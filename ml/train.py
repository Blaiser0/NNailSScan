"""
Entrenamiento CNN multiclase para enfermedades de uñas + exportación TFLite.
Configuración base: sin data augmentation, solo preprocess_input de MobileNetV2.

Dataset esperado (subcarpetas por clase):
  Dataset/entrenamiento/  o  Dataset/Train/
  Dataset/validacion/     o  Dataset/Val/
  Dataset/prueba/         o  Dataset/Test/

Salidas en ml/output/:
  nail_model.tflite, labels.txt, class_indices.json, best_model.keras, confusion_matrix.png
"""

from __future__ import annotations

import json
import os
from pathlib import Path

import matplotlib.pyplot as plt
import numpy as np
import seaborn as sns
import tensorflow as tf
from sklearn.metrics import classification_report, confusion_matrix
from tensorflow import keras
from tensorflow.keras.applications.mobilenet_v2 import MobileNetV2, preprocess_input
from tensorflow.keras.callbacks import EarlyStopping, ModelCheckpoint, ReduceLROnPlateau
from tensorflow.keras.layers import Dense, GlobalAveragePooling2D
from tensorflow.keras.models import Model

# ---------------------------------------------------------------------------
# Configuración
# ---------------------------------------------------------------------------
PROJECT_ROOT = Path(__file__).resolve().parent.parent
DATASET_ROOT = PROJECT_ROOT / "Dataset"
OUTPUT_DIR = Path(__file__).resolve().parent / "output"

SPLIT_FOLDER_CANDIDATES = {
    "train": ("entrenamiento", "Train"),
    "val": ("validacion", "Val"),
    "test": ("prueba", "Test"),
}

IMG_SIZE = (224, 224)  # MobileNetV2
BATCH_SIZE = 32
EPOCHS = 40
LEARNING_RATE = 1e-4
FINE_TUNE_EPOCHS = 15
FINE_TUNE_LR = 1e-5
SEED = 42
AUTOTUNE = tf.data.AUTOTUNE


def resolve_split_dir(split_key: str) -> Path:
    """Devuelve la primera carpeta existente entre candidatos (es/en)."""
    for name in SPLIT_FOLDER_CANDIDATES[split_key]:
        path = DATASET_ROOT / name
        if path.is_dir():
            return path
    candidates = ", ".join(SPLIT_FOLDER_CANDIDATES[split_key])
    raise FileNotFoundError(
        f"No se encontró carpeta para '{split_key}' en {DATASET_ROOT}. "
        f"Probado: {candidates}"
    )


def build_generators(train_dir: Path, val_dir: Path, test_dir: Path):
    """
    Carga imágenes a 224x224 sin augmentation.
    Solo aplica preprocess_input de MobileNetV2: (pixel / 127.5) - 1.0
    """
    common_kwargs = dict(
        labels="inferred",
        label_mode="categorical",
        color_mode="rgb",
        batch_size=BATCH_SIZE,
        image_size=IMG_SIZE,
    )

    train_ds = keras.utils.image_dataset_from_directory(
        train_dir,
        shuffle=True,
        seed=SEED,
        **common_kwargs,
    )

    class_names = train_ds.class_names
    class_indices = {name: index for index, name in enumerate(class_names)}
    labels = list(class_names)

    val_ds = keras.utils.image_dataset_from_directory(
        val_dir,
        class_names=class_names,
        shuffle=False,
        **common_kwargs,
    )

    test_ds = keras.utils.image_dataset_from_directory(
        test_dir,
        class_names=class_names,
        shuffle=False,
        **common_kwargs,
    )

    def preprocess(image, label):
        image = tf.cast(image, tf.float32)
        return preprocess_input(image), label

    train_ds = train_ds.map(preprocess, num_parallel_calls=AUTOTUNE).prefetch(AUTOTUNE)
    val_ds = val_ds.map(preprocess, num_parallel_calls=AUTOTUNE).prefetch(AUTOTUNE)
    test_ds = test_ds.map(preprocess, num_parallel_calls=AUTOTUNE).prefetch(AUTOTUNE)

    return train_ds, val_ds, test_ds, class_indices, labels


def build_model(num_classes: int) -> tuple[Model, MobileNetV2]:
    """MobileNetV2 (ImageNet) + GlobalAveragePooling + Dense + softmax."""
    base = MobileNetV2(
        input_shape=(*IMG_SIZE, 3),
        include_top=False,
        weights="imagenet",
    )
    base.trainable = False

    x = GlobalAveragePooling2D()(base.output)
    x = Dense(128, activation="relu")(x)
    outputs = Dense(num_classes, activation="softmax")(x)

    model = Model(inputs=base.input, outputs=outputs, name="nail_mobilenetv2")
    model.compile(
        optimizer=keras.optimizers.Adam(learning_rate=LEARNING_RATE),
        loss="categorical_crossentropy",
        metrics=["accuracy"],
    )
    return model, base


def fine_tune(model: Model, base: MobileNetV2) -> None:
    """Descongela las últimas capas del backbone para afinar."""
    base.trainable = True
    for layer in base.layers[:-30]:
        layer.trainable = False

    model.compile(
        optimizer=keras.optimizers.Adam(learning_rate=FINE_TUNE_LR),
        loss="categorical_crossentropy",
        metrics=["accuracy"],
    )


def save_labels(class_indices: dict, labels: list[str]) -> None:
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    with open(OUTPUT_DIR / "class_indices.json", "w", encoding="utf-8") as f:
        json.dump(class_indices, f, indent=2, ensure_ascii=False)
    with open(OUTPUT_DIR / "labels.txt", "w", encoding="utf-8") as f:
        f.write("\n".join(labels) + "\n")


def plot_training(
    history: keras.callbacks.History,
    fine_tune_history: keras.callbacks.History | None,
) -> None:
    """Guarda gráficas de accuracy y loss."""
    for metric in ("accuracy", "loss"):
        plt.figure(figsize=(8, 5))
        plt.plot(history.history[metric], label=f"train_{metric}")
        plt.plot(history.history[f"val_{metric}"], label=f"val_{metric}")
        if fine_tune_history:
            offset = len(history.history[metric])
            plt.plot(
                range(offset, offset + len(fine_tune_history.history[metric])),
                fine_tune_history.history[metric],
                label=f"ft_train_{metric}",
            )
            plt.plot(
                range(offset, offset + len(fine_tune_history.history[f"val_{metric}"])),
                fine_tune_history.history[f"val_{metric}"],
                label=f"ft_val_{metric}",
            )
        plt.xlabel("Época")
        plt.ylabel(metric)
        plt.legend()
        plt.title(f"Historial de {metric}")
        plt.tight_layout()
        plt.savefig(OUTPUT_DIR / f"history_{metric}.png", dpi=120)
        plt.close()


def plot_confusion_matrix(y_true: np.ndarray, y_pred: np.ndarray, labels: list[str]) -> Path:
    """Genera, muestra y guarda la matriz de confusión con seaborn."""
    cm = confusion_matrix(y_true, y_pred)

    print("\nMatriz de confusión (filas=real, columnas=predicho):")
    print("Etiquetas:", labels)
    print(cm)

    fig, ax = plt.subplots(figsize=(11, 9))
    sns.heatmap(
        cm,
        annot=True,
        fmt="d",
        cmap="Blues",
        xticklabels=labels,
        yticklabels=labels,
        linewidths=0.5,
        cbar_kws={"label": "Número de imágenes"},
        ax=ax,
    )
    ax.set_xlabel("Clase predicha")
    ax.set_ylabel("Clase real")
    ax.set_title("Matriz de confusión — conjunto de prueba")
    plt.setp(ax.get_xticklabels(), rotation=45, ha="right", rotation_mode="anchor")
    plt.tight_layout()

    out_path = OUTPUT_DIR / "confusion_matrix.png"
    plt.savefig(out_path, dpi=150, bbox_inches="tight")
    plt.show()
    plt.close()

    print(f"\nMatriz de confusión guardada en: {out_path}")
    return out_path


def evaluate_model(model: Model, test_ds: tf.data.Dataset, labels: list[str]) -> None:
    """Evalúa en Test y muestra matriz de confusión + classification report."""
    print("\n--- Evaluación en conjunto de prueba ---")
    loss, acc = model.evaluate(test_ds, verbose=1)
    print(f"Test loss: {loss:.4f} | Test accuracy: {acc:.4f}")

    y_true: list[int] = []
    y_pred: list[int] = []
    for images, batch_labels in test_ds:
        probabilities = model.predict(images, verbose=0)
        y_pred.extend(np.argmax(probabilities, axis=1))
        y_true.extend(np.argmax(batch_labels.numpy(), axis=1))

    y_true_arr = np.array(y_true)
    y_pred_arr = np.array(y_pred)

    plot_confusion_matrix(y_true_arr, y_pred_arr, labels)

    print("\nReporte de clasificación:")
    print(
        classification_report(
            y_true_arr,
            y_pred_arr,
            target_names=labels,
            digits=4,
        )
    )


def export_tflite(model: Model) -> Path:
    """Exporta a TFLite con cuantización float16."""
    converter = tf.lite.TFLiteConverter.from_keras_model(model)
    converter.optimizations = [tf.lite.Optimize.DEFAULT]
    converter.target_spec.supported_types = [tf.float16]

    tflite_model = converter.convert()
    out_path = OUTPUT_DIR / "nail_model.tflite"
    out_path.write_bytes(tflite_model)
    size_mb = out_path.stat().st_size / (1024 * 1024)
    print(f"\nModelo TFLite guardado: {out_path} ({size_mb:.2f} MB)")
    return out_path


def main() -> None:
    os.environ["TF_CPP_MIN_LOG_LEVEL"] = "2"
    tf.random.set_seed(SEED)
    np.random.seed(SEED)

    train_dir = resolve_split_dir("train")
    val_dir = resolve_split_dir("val")
    test_dir = resolve_split_dir("test")
    print(f"Train: {train_dir}\nVal:   {val_dir}\nTest:  {test_dir}")

    train_ds, val_ds, test_ds, class_indices, labels = build_generators(
        train_dir, val_dir, test_dir
    )
    num_classes = len(labels)
    print(f"\nClases ({num_classes}): {labels}")
    save_labels(class_indices, labels)

    model, base = build_model(num_classes)
    model.summary()

    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    checkpoint_path = OUTPUT_DIR / "best_model.keras"

    callbacks = [
        EarlyStopping(
            monitor="val_loss",
            patience=6,
            restore_best_weights=True,
            verbose=1,
        ),
        ModelCheckpoint(
            filepath=str(checkpoint_path),
            monitor="val_loss",
            save_best_only=True,
            verbose=1,
        ),
        ReduceLROnPlateau(
            monitor="val_loss",
            factor=0.5,
            patience=3,
            min_lr=1e-7,
            verbose=1,
        ),
    ]

    print("\n--- Fase 1: entrenamiento con backbone congelado ---")
    history = model.fit(
        train_ds,
        epochs=EPOCHS,
        validation_data=val_ds,
        callbacks=callbacks,
        verbose=1,
    )

    print("\n--- Fase 2: fine-tuning (últimas capas de MobileNetV2) ---")
    fine_tune(model, base)
    fine_tune_history = model.fit(
        train_ds,
        epochs=FINE_TUNE_EPOCHS,
        validation_data=val_ds,
        callbacks=callbacks,
        verbose=1,
    )

    plot_training(history, fine_tune_history)

    if checkpoint_path.exists():
        model = keras.models.load_model(checkpoint_path)
        print(f"\nModelo final cargado desde {checkpoint_path}")

    evaluate_model(model, test_ds, labels)
    export_tflite(model)

    from export_tflite import sync_to_android_assets

    sync_to_android_assets()
    print("\n--- Android ---")
    print("Modelo copiado a app/src/main/assets/. Abre Android Studio y Run.")


if __name__ == "__main__":
    main()

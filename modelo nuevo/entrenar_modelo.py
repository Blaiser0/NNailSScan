import hashlib
import json
import os
from pathlib import Path

import cv2
import numpy as np
import tensorflow as tf
from keras import layers
from keras.applications.efficientnet import EfficientNetB1, preprocess_input
from sklearn.utils.class_weight import compute_class_weight

# ─────────────────────────────────────────────
# 1. CONFIGURACIÓN Y RUTAS LOCALES
# ─────────────────────────────────────────────
PROYECTO = Path(r"C:\Users\snake\AndroidStudioProjects\NNailScan")
CARPETA_SCRIPT = Path(__file__).resolve().parent

RUTA_ENTRENAMIENTO = str(PROYECTO / "Dataset" / "entrenamiento")
RUTA_VALIDACION = str(PROYECTO / "Dataset" / "validacion")
RUTA_MODELO = str(CARPETA_SCRIPT / "modelo_nailscan.keras")
RUTA_CLASES = str(CARPETA_SCRIPT / "clases.json")
RUTA_TFLITE = str(CARPETA_SCRIPT / "nail_model_nuevo.tflite")

TAMANO_IMAGEN = (240, 240)  # Más detalle para lesiones en uña
TAMANO_LOTE = 16            # B1 es más pesado; lote menor evita OOM
SEED = 42
WEIGHT_DECAY = 1e-4
FOCAL_GAMMA = 2.0
EJECUTAR_LIMPIEZA = False

# 3 fases: cabeza → fine-tuning parcial → fine-tuning profundo (BN congelado)
FASES = (
    {"nombre": "Cabeza densa", "epochs": 35, "lr": 8e-4, "capas_descongeladas": 0},
    {"nombre": "Fine-tuning medio", "epochs": 25, "lr": 2e-4, "capas_descongeladas": 35},
    {"nombre": "Fine-tuning fino", "epochs": 20, "lr": 4e-5, "capas_descongeladas": 70},
)

AUTOTUNE = tf.data.AUTOTUNE


# ─────────────────────────────────────────────
# 2. FILTRADO, PURGA Y RECORTE INTELIGENTE
# ─────────────────────────────────────────────
def calcular_hash_archivo(ruta_archivo):
    hash_md5 = hashlib.md5()
    with open(ruta_archivo, "rb") as f:
        for bloque in iter(lambda: f.read(4096), b""):
            hash_md5.update(bloque)
    return hash_md5.hexdigest()


def limpiar_y_eliminar_imagenes(ruta_base):
    print(f"\n[Fase 1/2] Limpieza y Purga en: {ruta_base}")
    if not os.path.exists(ruta_base):
        print(f"La ruta {ruta_base} no existe.")
        return

    hashes_conocidos = set()
    contadores = {"duplicadas": 0, "corruptas": 0, "sesgo_color": 0}

    for raiz, _, archivos in os.walk(ruta_base):
        nombre_carpeta = os.path.basename(raiz)
        for archivo in archivos:
            if archivo.lower().endswith((".png", ".jpg", ".jpeg", ".webp")):
                ruta_completa = os.path.join(raiz, archivo)
                try:
                    hash_actual = calcular_hash_archivo(ruta_completa)
                    if hash_actual in hashes_conocidos:
                        os.remove(ruta_completa)
                        contadores["duplicadas"] += 1
                        continue
                    hashes_conocidos.add(hash_actual)
                except Exception:
                    pass

                try:
                    img = cv2.imread(ruta_completa)
                    if img is None:
                        os.remove(ruta_completa)
                        contadores["corruptas"] += 1
                        continue

                    h, w, _ = img.shape
                    if h < 10 or w < 10 or np.std(img) < 2.0:
                        os.remove(ruta_completa)
                        contadores["corruptas"] += 1
                        continue

                    media_azul = np.mean(img[:, :, 0])
                    media_rojo = np.mean(img[:, :, 2])
                    if nombre_carpeta != "dedo_azul" and (media_azul - media_rojo) > 25:
                        os.remove(ruta_completa)
                        contadores["sesgo_color"] += 1
                        continue

                except Exception:
                    if os.path.exists(ruta_completa):
                        os.remove(ruta_completa)
                    contadores["corruptas"] += 1

    print(
        f"Purga finalizada: {contadores['corruptas']} corruptas | "
        f"{contadores['duplicadas']} repetidas | {contadores['sesgo_color']} sesgo azul."
    )


def aplicar_recorte_inteligente(ruta_base):
    print(f"\n[Fase 2/2] Recortando bordes negros en: {ruta_base}")
    if not os.path.exists(ruta_base):
        return

    contador_recortes = 0
    for raiz, _, archivos in os.walk(ruta_base):
        for archivo in archivos:
            if archivo.lower().endswith((".png", ".jpg", ".jpeg", ".webp")):
                ruta_completa = os.path.join(raiz, archivo)
                try:
                    img = cv2.imread(ruta_completa)
                    if img is None:
                        continue

                    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
                    _, thresh = cv2.threshold(gray, 15, 255, cv2.THRESH_BINARY)
                    kernel = cv2.getStructuringElement(cv2.MORPH_ELLIPSE, (5, 5))
                    thresh = cv2.morphologyEx(thresh, cv2.MORPH_CLOSE, kernel, iterations=2)
                    thresh = cv2.morphologyEx(thresh, cv2.MORPH_OPEN, kernel, iterations=1)
                    contornos, _ = cv2.findContours(
                        thresh, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE
                    )

                    if contornos:
                        c = max(contornos, key=cv2.contourArea)
                        ratio = cv2.contourArea(c) / (img.shape[0] * img.shape[1])
                        if 0.10 < ratio < 0.95:
                            x, y, w, h = cv2.boundingRect(c)
                            margen_x, margen_y = int(w * 0.05), int(h * 0.05)
                            recorte = img[
                                max(0, y - margen_y) : min(img.shape[0], y + h + margen_y),
                                max(0, x - margen_x) : min(img.shape[1], x + w + margen_x),
                            ]
                            if recorte.size > 0:
                                cv2.imwrite(ruta_completa, recorte)
                                contador_recortes += 1
                except Exception:
                    pass

    print(f"Bisturí aplicado: {contador_recortes} imágenes recortadas.")


def ejecutar_limpieza_dataset():
    limpiar_y_eliminar_imagenes(RUTA_ENTRENAMIENTO)
    aplicar_recorte_inteligente(RUTA_ENTRENAMIENTO)
    limpiar_y_eliminar_imagenes(RUTA_VALIDACION)
    aplicar_recorte_inteligente(RUTA_VALIDACION)


# ─────────────────────────────────────────────
# 3. PÉRDIDA FOCAL (mejor en clases difíciles/confusas)
# ─────────────────────────────────────────────
def sparse_focal_loss(y_true, y_pred):
    """Focal loss para etiquetas enteras; reduce error en clases difíciles."""
    y_true = tf.reshape(tf.cast(y_true, tf.int32), [-1])
    y_pred = tf.clip_by_value(y_pred, 1e-7, 1.0 - 1e-7)
    num_classes = tf.shape(y_pred)[-1]
    y_one_hot = tf.one_hot(y_true, depth=num_classes)
    ce = -y_one_hot * tf.math.log(y_pred)
    focal_weight = tf.pow(1.0 - y_pred, FOCAL_GAMMA)
    return tf.reduce_mean(tf.reduce_sum(focal_weight * ce, axis=-1))


# ─────────────────────────────────────────────
# 4. PIPELINE DE DATOS
# ─────────────────────────────────────────────
def crear_augmentacion():
    return tf.keras.Sequential(
        [
            layers.RandomFlip("horizontal"),
            layers.RandomRotation(0.06),
            layers.RandomTranslation(0.10, 0.10),
            layers.RandomZoom((-0.12, 0.12), (-0.12, 0.12)),
            layers.RandomBrightness(0.10),
            layers.RandomContrast(0.10),
        ],
        name="data_augmentation",
    )


def build_generators(augmentacion):
    print("\nCargando datasets...")
    common = dict(
        image_size=TAMANO_IMAGEN,
        batch_size=TAMANO_LOTE,
        color_mode="rgb",
    )

    train_raw = tf.keras.utils.image_dataset_from_directory(
        RUTA_ENTRENAMIENTO,
        shuffle=True,
        seed=SEED,
        **common,
    )
    class_names = train_raw.class_names

    val_raw = tf.keras.utils.image_dataset_from_directory(
        RUTA_VALIDACION,
        class_names=class_names,
        shuffle=False,
        **common,
    )

    def pipeline(image, label, entrenar=False):
        image = tf.cast(image, tf.float32)
        if entrenar:
            image = augmentacion(image, training=True)
        return preprocess_input(image), label

    train_ds = train_raw.map(
        lambda x, y: pipeline(x, y, True), num_parallel_calls=AUTOTUNE
    ).prefetch(AUTOTUNE)
    val_ds = val_raw.map(
        lambda x, y: pipeline(x, y, False), num_parallel_calls=AUTOTUNE
    ).prefetch(AUTOTUNE)

    return train_ds, val_ds, train_raw, class_names


def calcular_pesos_clase(train_raw):
    print("\nCalculando pesos por clase...")
    etiquetas = np.concatenate([y for _, y in train_raw], axis=0)
    pesos = compute_class_weight(
        class_weight="balanced",
        classes=np.unique(etiquetas),
        y=etiquetas,
    )
    return dict(enumerate(pesos))


def crear_callbacks():
    return [
        tf.keras.callbacks.EarlyStopping(
            monitor="val_loss",
            patience=10,
            restore_best_weights=True,
            mode="min",
            verbose=1,
        ),
        tf.keras.callbacks.ModelCheckpoint(
            RUTA_MODELO,
            monitor="val_loss",
            save_best_only=True,
            mode="min",
            verbose=1,
        ),
        tf.keras.callbacks.ReduceLROnPlateau(
            monitor="val_loss",
            factor=0.4,
            patience=4,
            min_lr=1e-7,
            verbose=1,
        ),
    ]


# ─────────────────────────────────────────────
# 5. MODELO EfficientNetB1 + cabeza profunda
# ─────────────────────────────────────────────
def build_model(num_classes):
    base = EfficientNetB1(
        input_shape=(*TAMANO_IMAGEN, 3),
        include_top=False,
        weights="imagenet",
    )
    base.trainable = False

    inputs = tf.keras.Input(shape=(*TAMANO_IMAGEN, 3), name="entrada")
    x = base(inputs, training=False)
    x = layers.GlobalAveragePooling2D()(x)

    x = layers.Dense(512, use_bias=False)(x)
    x = layers.BatchNormalization()(x)
    x = layers.Activation("relu")(x)
    x = layers.Dropout(0.45)(x)

    x = layers.Dense(256, use_bias=False)(x)
    x = layers.BatchNormalization()(x)
    x = layers.Activation("relu")(x)
    x = layers.Dropout(0.30)(x)

    outputs = layers.Dense(num_classes, activation="softmax", name="salida")(x)
    model = tf.keras.Model(inputs, outputs, name="NailScan_EfficientNetB1")
    return model, base


def configurar_capas_entrenables(base, capas_descongeladas, congelar_bn=True):
    base.trainable = capas_descongeladas > 0
    if capas_descongeladas > 0:
        for layer in base.layers[:-capas_descongeladas]:
            layer.trainable = False
        if congelar_bn:
            for layer in base.layers:
                if isinstance(layer, layers.BatchNormalization):
                    layer.trainable = False
    else:
        for layer in base.layers:
            layer.trainable = False


def compilar_modelo(model, learning_rate):
    model.compile(
        optimizer=tf.keras.optimizers.AdamW(
            learning_rate=learning_rate,
            weight_decay=WEIGHT_DECAY,
        ),
        loss=sparse_focal_loss,
        metrics=["accuracy"],
    )


def exportar_tflite():
    print(f"\nExportando modelo a TFLite: {RUTA_TFLITE}")
    modelo_final = tf.keras.models.load_model(
        RUTA_MODELO,
        custom_objects={"sparse_focal_loss": sparse_focal_loss},
    )
    converter = tf.lite.TFLiteConverter.from_keras_model(modelo_final)
    converter.optimizations = [tf.lite.Optimize.DEFAULT]
    converter.target_spec.supported_types = [tf.float16]
    tflite_bytes = converter.convert()
    Path(RUTA_TFLITE).write_bytes(tflite_bytes)
    size_mb = Path(RUTA_TFLITE).stat().st_size / (1024 * 1024)
    print(f"TFLite guardado ({size_mb:.2f} MB): {RUTA_TFLITE}")

    assets_dir = PROYECTO / "app" / "src" / "main" / "assets"
    assets_dir.mkdir(parents=True, exist_ok=True)
    destino = assets_dir / "nail_model_nuevo.tflite"
    destino.write_bytes(tflite_bytes)
    print(f"Copiado a Android: {destino}")


def main():
    os.environ["TF_CPP_MIN_LOG_LEVEL"] = "2"
    tf.random.set_seed(SEED)
    np.random.seed(SEED)

    if EJECUTAR_LIMPIEZA:
        ejecutar_limpieza_dataset()
    else:
        print("\nLimpieza omitida (EJECUTAR_LIMPIEZA=False).")

    augmentacion = crear_augmentacion()
    train_ds, val_ds, train_raw, nombres_clases = build_generators(augmentacion)
    num_classes = len(nombres_clases)

    with open(RUTA_CLASES, "w", encoding="utf-8") as f:
        json.dump(nombres_clases, f, indent=2, ensure_ascii=False)
    print(f"Clases ({num_classes}): {nombres_clases}")

    pesos_clase = calcular_pesos_clase(train_raw)
    print(f"Pesos por clase: {pesos_clase}")

    modelo, base = build_model(num_classes)
    callbacks = crear_callbacks()

    for i, fase in enumerate(FASES, start=1):
        configurar_capas_entrenables(
            base,
            fase["capas_descongeladas"],
            congelar_bn=(fase["capas_descongeladas"] > 0),
        )
        compilar_modelo(modelo, fase["lr"])
        entrenables = sum(int(w.shape.num_elements()) for w in modelo.trainable_weights)
        print(
            f"\n=== FASE {i}: {fase['nombre']} | "
            f"{fase['epochs']} épocas max | lr={fase['lr']} | "
            f"params entrenables={entrenables:,} ==="
        )
        modelo.fit(
            train_ds,
            validation_data=val_ds,
            epochs=fase["epochs"],
            class_weight=pesos_clase,
            callbacks=callbacks,
        )

    if Path(RUTA_MODELO).exists():
        modelo = tf.keras.models.load_model(
            RUTA_MODELO,
            custom_objects={"sparse_focal_loss": sparse_focal_loss},
        )
        print(f"\nMejor modelo cargado desde: {RUTA_MODELO}")

    loss, acc = modelo.evaluate(val_ds, verbose=1)
    print(f"\nValidación final — loss: {loss:.4f} | accuracy: {acc * 100:.2f}%")

    exportar_tflite()
    print("\nEntrenamiento completado. Ejecuta: python evaluar_modelo.py")


if __name__ == "__main__":
    main()

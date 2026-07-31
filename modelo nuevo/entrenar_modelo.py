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
# 1. CONFIGURATION AND LOCAL PATHS
# ─────────────────────────────────────────────
PROJECT = Path(r"C:\Users\snake\AndroidStudioProjects\NNailScan")
SCRIPT_DIR = Path(__file__).resolve().parent

TRAIN_DIR = str(PROJECT / "Dataset" / "entrenamiento")
VAL_DIR = str(PROJECT / "Dataset" / "validacion")
MODEL_PATH = str(SCRIPT_DIR / "modelo_nailscan.keras")
CLASSES_PATH = str(SCRIPT_DIR / "clases.json")
TFLITE_PATH = str(SCRIPT_DIR / "nail_model_nuevo.tflite")

IMAGE_SIZE = (240, 240)  # More detail for nail lesions
BATCH_SIZE = 16          # B1 is heavier; smaller batch avoids OOM
SEED = 42
WEIGHT_DECAY = 1e-4
FOCAL_GAMMA = 2.0
RUN_CLEANUP = False

# 3 phases: head -> partial fine-tuning -> deep fine-tuning (BN frozen)
PHASES = (
    {"name": "Dense head", "epochs": 35, "lr": 8e-4, "unfrozen_layers": 0},
    {"name": "Medium fine-tuning", "epochs": 25, "lr": 2e-4, "unfrozen_layers": 35},
    {"name": "Fine fine-tuning", "epochs": 20, "lr": 4e-5, "unfrozen_layers": 70},
)

AUTOTUNE = tf.data.AUTOTUNE


# ─────────────────────────────────────────────
# 2. FILTERING, PURGE, AND SMART CROPPING
# ─────────────────────────────────────────────
def compute_file_hash(file_path):
    hash_md5 = hashlib.md5()
    with open(file_path, "rb") as f:
        for chunk in iter(lambda: f.read(4096), b""):
            hash_md5.update(chunk)
    return hash_md5.hexdigest()


def clean_and_remove_images(base_path):
    print(f"\n[Phase 1/2] Cleaning and purging: {base_path}")
    if not os.path.exists(base_path):
        print(f"Path does not exist: {base_path}")
        return

    known_hashes = set()
    counters = {"corrupt": 0, "duplicates": 0, "color_bias": 0}

    for root, _, files in os.walk(base_path):
        folder_name = os.path.basename(root)
        for filename in files:
            if filename.lower().endswith((".png", ".jpg", ".jpeg", ".webp")):
                full_path = os.path.join(root, filename)
                try:
                    current_hash = compute_file_hash(full_path)
                    if current_hash in known_hashes:
                        os.remove(full_path)
                        counters["duplicates"] += 1
                        continue
                    known_hashes.add(current_hash)
                except Exception:
                    pass

                try:
                    img = cv2.imread(full_path)
                    if img is None:
                        os.remove(full_path)
                        counters["corrupt"] += 1
                        continue

                    h, w, _ = img.shape
                    if h < 10 or w < 10 or np.std(img) < 2.0:
                        os.remove(full_path)
                        counters["corrupt"] += 1
                        continue

                    mean_blue = np.mean(img[:, :, 0])
                    mean_red = np.mean(img[:, :, 2])
                    # Keep folder name as-is: it matches the dataset class folder
                    if folder_name != "dedo_azul" and (mean_blue - mean_red) > 25:
                        os.remove(full_path)
                        counters["color_bias"] += 1
                        continue

                except Exception:
                    if os.path.exists(full_path):
                        os.remove(full_path)
                    counters["corrupt"] += 1

    print(
        f"Purge complete: {counters['corrupt']} corrupt | "
        f"{counters['duplicates']} duplicates | {counters['color_bias']} blue bias."
    )


def apply_smart_crop(base_path):
    print(f"\n[Phase 2/2] Cropping black borders: {base_path}")
    if not os.path.exists(base_path):
        return

    crop_count = 0
    for root, _, files in os.walk(base_path):
        for filename in files:
            if filename.lower().endswith((".png", ".jpg", ".jpeg", ".webp")):
                full_path = os.path.join(root, filename)
                try:
                    img = cv2.imread(full_path)
                    if img is None:
                        continue

                    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
                    _, thresh = cv2.threshold(gray, 15, 255, cv2.THRESH_BINARY)
                    kernel = cv2.getStructuringElement(cv2.MORPH_ELLIPSE, (5, 5))
                    thresh = cv2.morphologyEx(thresh, cv2.MORPH_CLOSE, kernel, iterations=2)
                    thresh = cv2.morphologyEx(thresh, cv2.MORPH_OPEN, kernel, iterations=1)
                    contours, _ = cv2.findContours(
                        thresh, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE
                    )

                    if contours:
                        c = max(contours, key=cv2.contourArea)
                        ratio = cv2.contourArea(c) / (img.shape[0] * img.shape[1])
                        if 0.10 < ratio < 0.95:
                            x, y, w, h = cv2.boundingRect(c)
                            margin_x, margin_y = int(w * 0.05), int(h * 0.05)
                            cropped = img[
                                max(0, y - margin_y) : min(img.shape[0], y + h + margin_y),
                                max(0, x - margin_x) : min(img.shape[1], x + w + margin_x),
                            ]
                            if cropped.size > 0:
                                cv2.imwrite(full_path, cropped)
                                crop_count += 1
                except Exception:
                    pass

    print(f"Smart crop applied: {crop_count} images cropped.")


def run_dataset_cleanup():
    clean_and_remove_images(TRAIN_DIR)
    apply_smart_crop(TRAIN_DIR)
    clean_and_remove_images(VAL_DIR)
    apply_smart_crop(VAL_DIR)


# ─────────────────────────────────────────────
# 3. FOCAL LOSS (better for hard/confusing classes)
# ─────────────────────────────────────────────
def sparse_focal_loss(y_true, y_pred):
    """Focal loss for integer labels; reduces error on hard classes."""
    y_true = tf.reshape(tf.cast(y_true, tf.int32), [-1])
    y_pred = tf.clip_by_value(y_pred, 1e-7, 1.0 - 1e-7)
    num_classes = tf.shape(y_pred)[-1]
    y_one_hot = tf.one_hot(y_true, depth=num_classes)
    ce = -y_one_hot * tf.math.log(y_pred)
    focal_weight = tf.pow(1.0 - y_pred, FOCAL_GAMMA)
    return tf.reduce_mean(tf.reduce_sum(focal_weight * ce, axis=-1))


# ─────────────────────────────────────────────
# 4. DATA PIPELINE
# ─────────────────────────────────────────────
def create_augmentation():
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


def build_generators(augmentation):
    print("\nLoading datasets...")
    common = dict(
        image_size=IMAGE_SIZE,
        batch_size=BATCH_SIZE,
        color_mode="rgb",
    )

    train_raw = tf.keras.utils.image_dataset_from_directory(
        TRAIN_DIR,
        shuffle=True,
        seed=SEED,
        **common,
    )
    class_names = train_raw.class_names

    val_raw = tf.keras.utils.image_dataset_from_directory(
        VAL_DIR,
        class_names=class_names,
        shuffle=False,
        **common,
    )

    def pipeline(image, label, training=False):
        image = tf.cast(image, tf.float32)
        if training:
            image = augmentation(image, training=True)
        return preprocess_input(image), label

    train_ds = train_raw.map(
        lambda x, y: pipeline(x, y, True), num_parallel_calls=AUTOTUNE
    ).prefetch(AUTOTUNE)
    val_ds = val_raw.map(
        lambda x, y: pipeline(x, y, False), num_parallel_calls=AUTOTUNE
    ).prefetch(AUTOTUNE)

    return train_ds, val_ds, train_raw, class_names


def compute_class_weights(train_raw):
    print("\nComputing class weights...")
    labels = np.concatenate([y for _, y in train_raw], axis=0)
    weights = compute_class_weight(
        class_weight="balanced",
        classes=np.unique(labels),
        y=labels,
    )
    return dict(enumerate(weights))


def create_callbacks():
    return [
        tf.keras.callbacks.EarlyStopping(
            monitor="val_loss",
            patience=10,
            restore_best_weights=True,
            mode="min",
            verbose=1,
        ),
        tf.keras.callbacks.ModelCheckpoint(
            MODEL_PATH,
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
# 5. EfficientNetB1 model + deep head
# ─────────────────────────────────────────────
def build_model(num_classes):
    base = EfficientNetB1(
        input_shape=(*IMAGE_SIZE, 3),
        include_top=False,
        weights="imagenet",
    )
    base.trainable = False

    inputs = tf.keras.Input(shape=(*IMAGE_SIZE, 3), name="input")
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

    outputs = layers.Dense(num_classes, activation="softmax", name="output")(x)
    model = tf.keras.Model(inputs, outputs, name="NailScan_EfficientNetB1")
    return model, base


def configure_trainable_layers(base, unfrozen_layers, freeze_bn=True):
    base.trainable = unfrozen_layers > 0
    if unfrozen_layers > 0:
        for layer in base.layers[:-unfrozen_layers]:
            layer.trainable = False
        if freeze_bn:
            for layer in base.layers:
                if isinstance(layer, layers.BatchNormalization):
                    layer.trainable = False
    else:
        for layer in base.layers:
            layer.trainable = False


def compile_model(model, learning_rate):
    model.compile(
        optimizer=tf.keras.optimizers.AdamW(
            learning_rate=learning_rate,
            weight_decay=WEIGHT_DECAY,
        ),
        loss=sparse_focal_loss,
        metrics=["accuracy"],
    )


def export_tflite():
    print(f"\nExporting model to TFLite: {TFLITE_PATH}")
    final_model = tf.keras.models.load_model(
        MODEL_PATH,
        custom_objects={"sparse_focal_loss": sparse_focal_loss},
    )
    converter = tf.lite.TFLiteConverter.from_keras_model(final_model)
    converter.optimizations = [tf.lite.Optimize.DEFAULT]
    converter.target_spec.supported_types = [tf.float16]
    tflite_bytes = converter.convert()
    Path(TFLITE_PATH).write_bytes(tflite_bytes)
    size_mb = Path(TFLITE_PATH).stat().st_size / (1024 * 1024)
    print(f"TFLite saved ({size_mb:.2f} MB): {TFLITE_PATH}")

    assets_dir = PROJECT / "app" / "src" / "main" / "assets"
    assets_dir.mkdir(parents=True, exist_ok=True)
    destination = assets_dir / "nail_model_nuevo.tflite"
    destination.write_bytes(tflite_bytes)
    print(f"Copied to Android: {destination}")


def main():
    os.environ["TF_CPP_MIN_LOG_LEVEL"] = "2"
    tf.random.set_seed(SEED)
    np.random.seed(SEED)

    if RUN_CLEANUP:
        run_dataset_cleanup()
    else:
        print("\nCleanup skipped (RUN_CLEANUP=False).")

    augmentation = create_augmentation()
    train_ds, val_ds, train_raw, class_names = build_generators(augmentation)
    num_classes = len(class_names)

    with open(CLASSES_PATH, "w", encoding="utf-8") as f:
        json.dump(class_names, f, indent=2, ensure_ascii=False)
    print(f"Classes ({num_classes}): {class_names}")

    class_weights = compute_class_weights(train_raw)
    print(f"Class weights: {class_weights}")

    model, base = build_model(num_classes)
    callbacks = create_callbacks()

    for i, phase in enumerate(PHASES, start=1):
        configure_trainable_layers(
            base,
            phase["unfrozen_layers"],
            freeze_bn=(phase["unfrozen_layers"] > 0),
        )
        compile_model(model, phase["lr"])
        trainable_params = sum(int(w.shape.num_elements()) for w in model.trainable_weights)
        print(
            f"\n=== PHASE {i}: {phase['name']} | "
            f"{phase['epochs']} max epochs | lr={phase['lr']} | "
            f"trainable params={trainable_params:,} ==="
        )
        model.fit(
            train_ds,
            validation_data=val_ds,
            epochs=phase["epochs"],
            class_weight=class_weights,
            callbacks=callbacks,
        )

    if Path(MODEL_PATH).exists():
        model = tf.keras.models.load_model(
            MODEL_PATH,
            custom_objects={"sparse_focal_loss": sparse_focal_loss},
        )
        print(f"\nBest model loaded from: {MODEL_PATH}")

    loss, acc = model.evaluate(val_ds, verbose=1)
    print(f"\nFinal validation — loss: {loss:.4f} | accuracy: {acc * 100:.2f}%")

    export_tflite()
    print("\nTraining complete. Run: python evaluar_modelo.py")


if __name__ == "__main__":
    main()

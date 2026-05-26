"""
Exporta nail_model.tflite desde best_model.keras (si el entrenamiento no generó el .tflite).

Uso:
  python export_tflite.py
"""

from pathlib import Path

import tensorflow as tf
from tensorflow import keras

OUTPUT_DIR = Path(__file__).resolve().parent / "output"
KERAS_MODEL = OUTPUT_DIR / "best_model.keras"
TFLITE_MODEL = OUTPUT_DIR / "nail_model.tflite"
ASSETS_DIR = Path(__file__).resolve().parent.parent / "app" / "src" / "main" / "assets"


def main() -> None:
    if not KERAS_MODEL.exists():
        raise FileNotFoundError(f"No existe {KERAS_MODEL}. Entrena primero con train.py.")

    print(f"Cargando {KERAS_MODEL}...")
    model = keras.models.load_model(KERAS_MODEL)

    print("Convirtiendo a TFLite (float16)...")
    converter = tf.lite.TFLiteConverter.from_keras_model(model)
    converter.optimizations = [tf.lite.Optimize.DEFAULT]
    converter.target_spec.supported_types = [tf.float16]
    tflite_bytes = converter.convert()

    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    TFLITE_MODEL.write_bytes(tflite_bytes)
    size_mb = TFLITE_MODEL.stat().st_size / (1024 * 1024)
    print(f"Guardado: {TFLITE_MODEL} ({size_mb:.2f} MB)")

    sync_to_android_assets(tflite_bytes)


def sync_to_android_assets(tflite_bytes: bytes | None = None) -> None:
    """Copia modelo y etiquetas a app/src/main/assets/."""
    ASSETS_DIR.mkdir(parents=True, exist_ok=True)

    if tflite_bytes is not None:
        (ASSETS_DIR / "nail_model.tflite").write_bytes(tflite_bytes)
    elif TFLITE_MODEL.exists():
        (ASSETS_DIR / "nail_model.tflite").write_bytes(TFLITE_MODEL.read_bytes())
    else:
        raise FileNotFoundError(f"No existe {TFLITE_MODEL}. Ejecuta train.py o export_tflite.py.")

    labels_src = OUTPUT_DIR / "labels.txt"
    if labels_src.exists():
        (ASSETS_DIR / "labels.txt").write_text(
            labels_src.read_text(encoding="utf-8"), encoding="utf-8"
        )

    print(f"Copiado a: {ASSETS_DIR / 'nail_model.tflite'}")
    print(f"Copiado a: {ASSETS_DIR / 'labels.txt'}")
    print("En Android Studio: Build > Rebuild Project, luego Run.")


if __name__ == "__main__":
    main()

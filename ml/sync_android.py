"""
Copia el modelo entrenado a la app Android sin re-exportar.

Uso (tras train.py):
  python sync_android.py
"""

from export_tflite import sync_to_android_assets


def main() -> None:
    sync_to_android_assets()
    print("Listo para compilar la app movil.")


if __name__ == "__main__":
    main()

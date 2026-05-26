import json
from pathlib import Path

import numpy as np
import tensorflow as tf
from keras.applications.efficientnet import preprocess_input
from sklearn.metrics import classification_report, confusion_matrix

# ─────────────────────────────────────────────
# CONFIGURACIÓN Y RUTAS LOCALES
# ─────────────────────────────────────────────
PROYECTO = Path(r"C:\Users\snake\AndroidStudioProjects\NNailScan")
CARPETA_SCRIPT = Path(__file__).resolve().parent

RUTA_PRUEBA = str(PROYECTO / "Dataset" / "prueba")
RUTA_MODELO = str(CARPETA_SCRIPT / "modelo_nailscan.keras")
RUTA_CLASES = str(CARPETA_SCRIPT / "clases.json")
TAMANO_IMAGEN = (240, 240)

if not Path(RUTA_MODELO).exists():
    raise FileNotFoundError(
        f"No se encontró el modelo en {RUTA_MODELO}. Ejecuta primero entrenar_modelo.py."
    )
if not Path(RUTA_CLASES).exists():
    raise FileNotFoundError(
        f"No se encontró {RUTA_CLASES}. Ejecuta primero entrenar_modelo.py."
    )
if not Path(RUTA_PRUEBA).exists():
    raise FileNotFoundError(f"No se encontró el dataset de prueba en {RUTA_PRUEBA}")

print("Cargando modelo y etiquetas guardadas...")

def sparse_focal_loss(y_true, y_pred):
    y_true = tf.reshape(tf.cast(y_true, tf.int32), [-1])
    y_pred = tf.clip_by_value(y_pred, 1e-7, 1.0 - 1e-7)
    num_classes = tf.shape(y_pred)[-1]
    y_one_hot = tf.one_hot(y_true, depth=num_classes)
    ce = -y_one_hot * tf.math.log(y_pred)
    focal_weight = tf.pow(1.0 - y_pred, 2.0)
    return tf.reduce_mean(tf.reduce_sum(focal_weight * ce, axis=-1))

modelo = tf.keras.models.load_model(
    RUTA_MODELO,
    custom_objects={"sparse_focal_loss": sparse_focal_loss},
)

with open(RUTA_CLASES, "r", encoding="utf-8") as f:
    CLASES = json.load(f)

print(f"Evaluando imágenes de prueba desde: {RUTA_PRUEBA}")
dataset_prueba_raw = tf.keras.utils.image_dataset_from_directory(
    RUTA_PRUEBA,
    image_size=TAMANO_IMAGEN,
    batch_size=32,
    shuffle=False,
    class_names=CLASES,
)

def preparar_imagen(image, label):
    image = tf.cast(image, tf.float32)
    return preprocess_input(image), label

dataset_prueba = dataset_prueba_raw.map(preparar_imagen).prefetch(tf.data.AUTOTUNE)

etiquetas_reales = []
for _, labels in dataset_prueba_raw:
    etiquetas_reales.extend(labels.numpy())
etiquetas_reales = np.array(etiquetas_reales)

perdida, precision = modelo.evaluate(dataset_prueba, verbose=0)

predicciones_raw = modelo.predict(dataset_prueba, verbose=0)
predicciones_indices = np.argmax(predicciones_raw, axis=1)

print("\n" + "=" * 60)
print("              REPORTE DE EVALUACIÓN LOCAL")
print("=" * 60)
print(f" Precisión global en datos de prueba: {precision * 100:.2f}%")
print(f" Pérdida (Loss) registrada: {perdida:.4f}")
print("=" * 60)

print("\nMATRIZ DE CONFUSIÓN:")
matriz = confusion_matrix(etiquetas_reales, predicciones_indices)

cabecera = "Real \\ Pred " + "".join([f"| {clase[:12]:^12} " for clase in CLASES])
print(cabecera)
print("-" * len(cabecera))

for idx, fila in enumerate(matriz):
    fila_texto = f"{CLASES[idx][:12]:<12}"
    for valor in fila:
        fila_texto += f"| {valor:^12} "
    print(fila_texto)

print("\nMÉTRICAS DETALLADAS POR CLASE:")
print(
    classification_report(
        etiquetas_reales,
        predicciones_indices,
        target_names=CLASES,
    )
)

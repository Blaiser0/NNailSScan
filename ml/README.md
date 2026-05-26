# Entrenamiento del modelo NNailScan

## Requisitos

- Python 3.10+
- GPU NVIDIA opcional (acelera el entrenamiento)

## Instalación

```bash
cd ml
python -m venv .venv
.venv\Scripts\activate
pip install -r requirements.txt
```

## Entrenar y exportar

```bash
python train.py
```

Salidas en `ml/output/`:

| Archivo | Uso |
|---------|-----|
| `nail_model.tflite` | Inferencia en Android |
| `labels.txt` | Etiquetas (orden alfabético) |
| `best_model.keras` | Mejor checkpoint Keras |
| `confusion_matrix.png` | Evaluación en Test |

## Exportar TFLite (si solo tienes best_model.keras)

```bash
python export_tflite.py
```

Genera `ml/output/nail_model.tflite` y lo copia automáticamente a `app/src/main/assets/`.

## Copiar a la app Android

`export_tflite.py` copia el modelo a assets. También puedes hacerlo manualmente:

```text
ml/output/nail_model.tflite  ->  app/src/main/assets/nail_model.tflite
ml/output/labels.txt         ->  app/src/main/assets/labels.txt
```

Sin `nail_model.tflite` en assets la app mostrará error al clasificar.

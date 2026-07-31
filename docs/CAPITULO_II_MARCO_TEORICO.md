# CAPÍTULO II: MARCO TEÓRICO

## 2.1. Introducción al capítulo

El presente capítulo expone la base conceptual del proyecto **NailScan**, aplicación móvil para clasificar afecciones ungueales mediante inteligencia artificial; aborda el aprendizaje profundo aplicado a imágenes, el despliegue del modelo en Android y el uso de Firebase como backend, e incluye un glosario de términos técnicos para facilitar la lectura del documento.

---

## 2.2. Marco teórico

### 2.2.1. Salud ungueal y necesidad de apoyo al diagnóstico

Las uñas pueden reflejar enfermedades dermatológicas y sistémicas —como onicomicosis, psoriasis ungueal, melanoma acral o acropaquia— mediante cambios en color, textura o forma; NailScan funciona como **sistema de apoyo informativo** que analiza fotos tomadas con el celular y orienta al usuario a consultar a un dermatólogo, sin sustituir el diagnóstico clínico profesional.

### 2.2.2. Inteligencia artificial y aprendizaje automático

La **inteligencia artificial (IA)** busca que las máquinas realicen tareas cognitivas como reconocer patrones, y el **aprendizaje automático (ML)** logra esto aprendiendo de datos en lugar de reglas fijas; NailScan emplea **aprendizaje supervisado**, entrenando el modelo con imágenes ya etiquetadas por clase diagnóstica.

### 2.2.3. Aprendizaje profundo y redes neuronales convolucionales (CNN)

El **aprendizaje profundo** usa redes con muchas capas para extraer características complejas, y las **CNN** aplican filtros convolucionales, reducción espacial (pooling) y capas densas para clasificar imágenes; en NailScan la red distingue ocho categorías: uña sana, acropaquia, dedo azul, melanoma acral, onicogrifosis, onicomicosis, picaduras y psoriasis ungueal.

### 2.2.4. Transfer learning y EfficientNet

El **transfer learning** reutiliza un modelo preentrenado (p. ej. en ImageNet) para evitar el **sobreajuste** con pocos datos, y **EfficientNetB1** equilibra precisión y eficiencia con imágenes de 240×240 px; el entrenamiento incluyó fine-tuning progresivo, augmentación de datos, ponderación por clase y focal loss.

### 2.2.5. Conjuntos de datos: entrenamiento, validación y prueba

Los datos se dividieron en **entrenamiento** (4 651 imágenes, para ajustar pesos), **validación** (884, para monitorear el entrenamiento) y **prueba** (304, para medir la generalización final); el desempeño se evalúa con accuracy, matriz de confusión y métricas por clase (precisión, recall, F1-score).

### 2.2.6. Despliegue móvil con TensorFlow Lite

**TensorFlow Lite (TFLite)** convierte el modelo Keras a formato `.tflite` optimizado para smartphones, ejecutándose con la API `Interpreter` en Android; la inferencia es **on-device** (local), lo que reduce latencia y protege la privacidad al no enviar la imagen a un servidor para clasificarla.

### 2.2.7. Desarrollo de aplicaciones móviles Android

NailScan está desarrollada en **Kotlin** con **Jetpack Compose** (UI declarativa), arquitectura **MVVM** (ViewModel + repositorios), **Navigation Compose** para pantallas, API de cámara/galería para capturar uñas y **Coil** para mostrar imágenes almacenadas en la nube.

### 2.2.8. Backend en la nube con Firebase

**Firebase** provee backend sin servidor propio: **Authentication** (registro y login), **Firestore** (perfiles, historial de escaneos y diccionario en tiempo real) y **Storage** (fotos de perfil e imágenes de escaneos), protegidos con **Security Rules** que limitan el acceso de cada usuario a sus propios datos.

### 2.2.9. Visión por computadora aplicada a imágenes dermatológicas

La **visión por computadora** interpreta imágenes automáticamente, pero en dermatología enfrenta variaciones de luz, posición de la uña y desbalance entre clases; NailScan mitiga esto con augmentación en entrenamiento, recorte de la región de interés y normalización a 240×240 px antes de la inferencia TFLite.

### 2.2.10. Consideraciones éticas y limitaciones

NailScan es **solo informativo**: depende de la calidad de la foto, puede reflejar sesgos del dataset y maneja datos sensibles de salud en Firebase bajo autenticación; por ello la app incluye descargos de responsabilidad médica y recomienda siempre la valoración de un especialista.

---

## 2.3. Definición de términos

A continuación se definen los acrónimos y conceptos técnicos más relevantes del proyecto NailScan.

| Término | Definición |
|---------|------------|
| **Accuracy** | Porcentaje de predicciones correctas sobre el total evaluado. |
| **API** | Interfaz que permite la comunicación entre componentes de software. |
| **Backend** | Servicios en la nube que procesan y almacenan datos (Firebase en NailScan). |
| **CNN** | Red neuronal convolucional para clasificación de imágenes. |
| **Dataset** | Conjunto de imágenes etiquetadas para entrenar y evaluar el modelo. |
| **Deep Learning** | Aprendizaje profundo con redes de muchas capas. |
| **EfficientNet** | Arquitectura CNN eficiente; NailScan usa la variante B1. |
| **Firebase** | Plataforma Google con Auth, Firestore y Storage para apps móviles. |
| **Fine-tuning** | Ajuste de capas de un modelo preentrenado a la tarea específica. |
| **Frontend** | Interfaz visible con la que interactúa el usuario. |
| **Focal Loss** | Función de pérdida que enfatiza ejemplos difíciles de clasificar. |
| **IA / ML** | Inteligencia artificial / aprendizaje automático a partir de datos. |
| **Inferencia** | Uso del modelo entrenado para predecir en imágenes nuevas. |
| **Kotlin** | Lenguaje principal de desarrollo Android en NailScan. |
| **MVVM** | Patrón que separa datos, lógica (ViewModel) e interfaz (View). |
| **Overfitting** | Sobreajuste: el modelo memoriza el entrenamiento y generaliza mal. |
| **TensorFlow Lite (TFLite)** | Versión de TensorFlow optimizada para inferencia en móviles. |
| **Transfer Learning** | Reutilizar un modelo preentrenado en una tarea nueva. |
| **Visión por computadora** | Rama de la IA que analiza e interpreta imágenes. |

---

## Referencias bibliográficas sugeridas

- Goodfellow, I., Bengio, Y., & Courville, A. (2016). *Deep Learning*. MIT Press.
- LeCun, Y., Bengio, Y., & Hinton, G. (2015). Deep learning. *Nature*, 521(7553), 436–444.
- Russell, S., & Norvig, P. (2020). *Artificial Intelligence: A Modern Approach* (4th ed.). Pearson.
- Tan, M., & Le, Q. (2019). EfficientNet. *Proceedings of ICML*.

---

*Proyecto NailScan — Clasificación de afecciones ungueales con IA móvil.*

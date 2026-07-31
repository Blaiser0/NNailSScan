# 2.2. Bases teóricas

## 2.2.1. Inteligencia artificial

Según Russell y Norvig (2020), "la inteligencia artificial es el campo de la
informática dedicado al diseño y construcción de agentes capaces de percibir
su entorno y ejecutar acciones que maximicen su probabilidad de éxito en
tareas que normalmente requieren inteligencia humana, como el reconocimiento
de patrones, la toma de decisiones y la comprensión del lenguaje". Por otro
lado, LeCun, Bengio y Hinton (2015) señalan que el avance más significativo de
la inteligencia artificial en la última década ha sido el desarrollo del aprendizaje
profundo, que ha permitido a los sistemas superar el desempeño humano en
tareas de reconocimiento visual y clasificación de imágenes.

## 2.2.2. Aprendizaje automático (Machine Learning)

Goodfellow, Bengio y Courville (2016) definen el aprendizaje automático como
"un subconjunto de la inteligencia artificial en el que se desarrollan algoritmos
que permiten a los sistemas aprender y mejorar su desempeño a partir de la
experiencia, sin ser programados explícitamente para cada tarea". En el
aprendizaje supervisado, una de sus variantes más utilizadas, el algoritmo
recibe un conjunto de datos etiquetados y ajusta sus parámetros internos para
minimizar el error entre la predicción generada y la etiqueta real conocida.

## 2.2.3. Redes neuronales convolucionales (CNN)

LeCun, Bengio y Hinton (2015) establecen que "las redes neuronales
convolucionales son arquitecturas de aprendizaje profundo diseñadas
específicamente para procesar datos con topología de cuadrícula, como las
imágenes, mediante la aplicación de filtros convolucionales que extraen
características locales en capas sucesivas de abstracción creciente". Cada
capa convolucional detecta patrones de complejidad mayor, desde bordes y
texturas en las primeras capas hasta formas y estructuras completas en las
capas más profundas, lo que las convierte en el estándar de facto para tareas
de clasificación de imágenes médicas y dermatológicas.

## 2.2.4. Transfer learning (aprendizaje por transferencia)

Tan y Le (2019) describen el transfer learning como "la técnica mediante la
cual un modelo previamente entrenado en un conjunto de datos de gran escala,
como ImageNet, es reutilizado como punto de partida para resolver una tarea
diferente pero relacionada, lo que reduce significativamente el tiempo de
entrenamiento y los datos necesarios para obtener un buen desempeño".
Esta técnica es especialmente relevante en aplicaciones de diagnóstico
asistido por inteligencia artificial, donde la cantidad de imágenes etiquetadas
disponibles suele ser limitada en comparación con los conjuntos de datos de
propósito general.

## 2.2.5. EfficientNet

Tan y Le (2019) proponen EfficientNet como "una familia de arquitecturas de
redes neuronales convolucionales que aplica un método de escalado compuesto
para ajustar de forma equilibrada la profundidad, la anchura y la resolución de
entrada de la red, logrando una mayor precisión con menor costo computacional
en comparación con arquitecturas de escala arbitraria". La variante EfficientNetB1
opera con imágenes de entrada de 240×240 píxeles y ofrece un equilibrio
adecuado entre capacidad de representación visual y viabilidad de despliegue
en dispositivos con recursos limitados, como los teléfonos inteligentes.

## 2.2.6. TensorFlow Lite

Según Google (2023), "TensorFlow Lite es un framework de inferencia de código
abierto diseñado para ejecutar modelos de aprendizaje automático en
dispositivos con recursos restringidos, como smartphones, tablets y
microcontroladores, mediante la conversión y optimización del modelo original
en un formato compacto (.tflite) que reduce el tamaño del modelo y la latencia
de inferencia". La ejecución local del modelo en el dispositivo, sin necesidad
de conectividad a internet en el momento del análisis, garantiza la privacidad
del usuario y elimina la dependencia de servicios externos durante la
clasificación.

## 2.2.7. Desarrollo de aplicaciones móviles nativas con Android y Kotlin

Según Google (2023), "Android es el sistema operativo móvil de código abierto
con mayor cuota de mercado mundial, y Kotlin es el lenguaje de programación
oficial recomendado para el desarrollo nativo de aplicaciones Android, diseñado
para ser conciso, seguro frente a errores de tipo nulo e interoperable con Java".
Jetpack Compose, también desarrollado por Google, complementa a Kotlin al
ofrecer un sistema de construcción de interfaces de usuario declarativo que
reduce el código repetitivo y facilita el mantenimiento y la escalabilidad de la
aplicación.

## 2.2.8. Firebase como Backend as a Service (BaaS)

Firebase es definido por Google (2023) como "una plataforma de desarrollo de
aplicaciones en la nube que proporciona una suite de servicios de backend
listos para usar, incluyendo autenticación de usuarios, bases de datos en tiempo
real, almacenamiento de archivos y reglas de seguridad, eliminando la necesidad
de administrar servidores propios". Cloud Firestore, uno de sus componentes
principales, es una base de datos NoSQL orientada a documentos que sincroniza
datos en tiempo real entre el cliente y la nube, lo que permite reflejar
inmediatamente en la interfaz los cambios registrados en el historial de
análisis del usuario.

## 2.2.9. Visión por computadora en dermatología

Esteva et al. (2017) demostraron que "los sistemas de visión por computadora
basados en redes neuronales convolucionales profundas son capaces de
clasificar lesiones dermatológicas con una precisión comparable a la de
dermatólogos certificados cuando se entrenan con conjuntos de datos de
imágenes clínicas suficientemente grandes y representativos". No obstante, el
desempeño de estos sistemas está sujeto a la calidad de la imagen capturada,
la variabilidad de iluminación y el balance entre las clases del conjunto de
entrenamiento, factores que deben abordarse mediante técnicas de
preprocesamiento y augmentación de datos.

## 2.2.10. Afecciones ungueales detectables mediante análisis de imagen

Haneke (2016) describe que "las uñas pueden presentar alteraciones
morfológicas, cromáticas y de textura que constituyen manifestaciones de
enfermedades dermatológicas locales o sistémicas, entre las que se destacan
la onicomicosis, caracterizada por engrosamiento y decoloración de origen
fúngico; la psoriasis ungueal, con depresiones puntiformes y separación de la
lámina; el melanoma acral subungueal, una forma de cáncer de piel de alta
gravedad; y la onicogrifosis, que produce una deformidad en espiral de la uña".
La identificación temprana de estas condiciones a través del análisis automatizado
de imágenes puede contribuir a orientar al paciente hacia la consulta médica
oportuna, sin que el sistema sustituya el criterio diagnóstico del especialista.

---

## Referencias

- Esteva, A., Kuprel, B., Novoa, R. A., Ko, J., Swetter, S. M., Blau, H. M., & Thrun, S. (2017). Dermatologist-level classification of skin cancer with deep neural networks. *Nature*, 542(7639), 115–118.
- Goodfellow, I., Bengio, Y., & Courville, A. (2016). *Deep Learning*. MIT Press.
- Google. (2023). *TensorFlow Lite Guide*. https://www.tensorflow.org/lite/guide
- Google. (2023). *Firebase Documentation*. https://firebase.google.com/docs
- Google. (2023). *Android Developers — Kotlin*. https://developer.android.com/kotlin
- Haneke, E. (2016). Nail disorders. En *Rook's Textbook of Dermatology* (9th ed.). Wiley-Blackwell.
- LeCun, Y., Bengio, Y., & Hinton, G. (2015). Deep learning. *Nature*, 521(7553), 436–444.
- Russell, S., & Norvig, P. (2020). *Artificial Intelligence: A Modern Approach* (4th ed.). Pearson.
- Tan, M., & Le, Q. V. (2019). EfficientNet: Rethinking model scaling for convolutional neural networks. *Proceedings of the 36th International Conference on Machine Learning (ICML)*.

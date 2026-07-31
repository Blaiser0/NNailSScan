# NailScan

App Android para **revisar uñas con la cámara del celular** y recibir una orientación sobre posibles alteraciones ungueales. Los resultados son **informativos** y no reemplazan una consulta médica.

## ¿Qué hace?

- Escanea una uña con **cámara o galería**
- Clasifica la imagen con **inteligencia artificial** (modelo local en el dispositivo)
- Muestra el **resultado**, nivel de confianza y recomendaciones
- Guarda tu **historial** de escaneos en la nube
- Ofrece un **diccionario** con descripción, síntomas y causas de cada condición
- Permite **registro e inicio de sesión** con correo

## ¿Cómo funciona?

1. **Regístrate** e inicia sesión.
2. En **Inicio**, toca el botón de cámara y toma una foto de la uña.
3. La app analiza la imagen con el modelo **TFLite** (`nail_model_nuevo.tflite`).
4. Ves el **diagnóstico orientativo** y puedes consultar más en el diccionario.
5. El escaneo queda guardado en tu **historial**.

## Roles

- **Usuario:** escanea, consulta historial y diccionario.
- **Administrador:** ve historial general de todos los usuarios y **estadísticas globales** de clasificaciones.

## Tecnologías

- Kotlin + Jetpack Compose
- TensorFlow Lite (clasificación on-device)
- Firebase (Auth, Firestore, Storage)

## Cómo ejecutar

1. Abre el proyecto en **Android Studio**.
2. Coloca `google-services.json` en `app/`.
3. Asegúrate de tener el modelo en `app/src/main/assets/`:
   - `nail_model_nuevo.tflite`
   - `labels.txt`
4. Compila e instala: **Run** o `./gradlew :app:assembleDebug`.

## Aviso

NailScan es una herramienta de apoyo. Ante cualquier duda o lesión persistente, consulta a un **dermatólogo**.

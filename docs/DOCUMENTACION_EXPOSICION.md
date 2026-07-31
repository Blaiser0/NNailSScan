# Documentación para Exposición — NailScan

Guía sencilla para explicar el código del proyecto en dos expositores.

---

## Visión general del flujo

```
Usuario abre la app
    → NailScanNavGraph (decide qué pantalla mostrar)
    → ScanScreen (elige foto o cámara)
    → NailClassifier (analiza la imagen con IA)
    → ScanRecord (guarda el resultado)
    → AuthRepository (valida que el usuario esté logueado)
    → build.gradle.kts (permite que todo compile e integre las librerías)
```

---

# Expositor 1 — Lo que ve y usa el usuario

## 1. ScanScreen.kt — UI (Interfaz)

**Ubicación:** `app/src/main/java/.../ui/screens/ScanScreen.kt`

**Qué es:** La pantalla donde el usuario analiza su uña.

**Qué hace en palabras simples:**
- Muestra dos botones: **elegir imagen de galería** o **tomar foto con cámara**.
- Cuando el usuario elige una imagen, la convierte en `Bitmap` (formato que entiende Android).
- Verifica que el usuario haya iniciado sesión.
- Muestra un **indicador de carga** mientras se procesa la imagen.
- Si todo sale bien, lleva al usuario a la pantalla de **resultado**.

**Partes clave para mostrar:**

| Parte | Para qué sirve |
|-------|----------------|
| `pickImageLauncher` | Abre la galería del celular |
| `takePictureLauncher` | Abre la cámara |
| `processScan()` | Coordina el análisis completo |
| `isProcessing` | Controla el spinner de “Analizando…” |

**Frase para exponer:**
> “Esta pantalla es la puerta de entrada del análisis: el usuario solo elige la foto y la app hace el resto.”

---

## 2. NailScanNavGraph.kt — UX (Navegación)

**Ubicación:** `app/src/main/java/.../navigation/NailScanNavGraph.kt`

**Qué es:** El mapa de rutas de la aplicación. Define **cómo el usuario se mueve** entre pantallas.

**Qué hace en palabras simples:**
- Decide por dónde empieza la app:
  - Si **no** hay sesión → pantalla de bienvenida / login.
  - Si **sí** hay sesión → pantalla principal.
- Conecta todas las pantallas: login, registro, inicio, escaneo, resultado, etc.
- Maneja el flujo del escaneo:

```
Main (Inicio) → Scan (Analizar) → ScanResult (Resultado) → Main
```

**Rutas importantes:**

| Ruta | Pantalla |
|------|----------|
| `welcome` | Bienvenida |
| `login` | Iniciar sesión |
| `main` | Inicio (tabs) |
| `scan` | Analizar uña |
| `scan_result` | Ver resultado |

**Partes clave para mostrar:**

| Parte | Para qué sirve |
|-------|----------------|
| `NailScanRoutes` | Nombres de todas las rutas |
| `startDestination` | Pantalla inicial según sesión |
| `composable(NailScanRoutes.Scan)` | Registra la pantalla de escaneo |
| `onNavigateToResult` | Pasa de Scan a ScanResult |

**Frase para exponer:**
> “Este archivo es el GPS de la app: define el camino que recorre el usuario desde que abre NailScan hasta que ve su diagnóstico.”

---

## 3. NailClassifier.kt — Backend IA (Inteligencia Artificial)

**Ubicación:** `app/src/main/java/.../NailClassifier.kt`

**Qué es:** El “cerebro” de la app. Ejecuta el modelo de inteligencia artificial **directamente en el celular**, sin internet.

**Qué hace en palabras simples:**
1. Carga el modelo `nail_model_nuevo.tflite` desde la carpeta `assets`.
2. Carga las etiquetas de enfermedades desde `labels.txt`.
3. Recibe una imagen de uña.
4. La redimensiona a **240×240 píxeles** (tamaño que espera el modelo).
5. Ejecuta la predicción con **TensorFlow Lite**.
6. Devuelve la enfermedad detectada y el **porcentaje de confianza**.

**Archivos que necesita (en `assets/`):**

| Archivo | Contenido |
|---------|-----------|
| `nail_model_nuevo.tflite` | Modelo entrenado (EfficientNetB1) |
| `labels.txt` | Lista de clases (ej. onicomicosis, unas_sanas…) |

**Partes clave para mostrar:**

| Parte | Para qué sirve |
|-------|----------------|
| `Interpreter` | Motor de TensorFlow Lite |
| `classifyImage()` | Función principal: imagen → diagnóstico |
| `bitmapToByteBuffer()` | Convierte la imagen al formato numérico |
| `loadLabels()` | Lee los nombres de las enfermedades |

**Frase para exponer:**
> “Aquí ocurre la magia: la foto entra, el modelo la analiza en el propio celular y sale el nombre de la enfermedad con su nivel de confianza.”

---

### Conexión Expositor 1

```
ScanScreen          →  pide la imagen al usuario
       ↓
NailScanNavGraph    →  lleva al usuario a Scan y luego a Resultado
       ↓
NailClassifier      →  analiza la imagen con IA on-device
```

---

# Expositor 2 — Datos, servicios e infraestructura

## 4. ScanRecord.kt — Datos (Modelo)

**Ubicación:** `app/src/main/java/.../data/model/ScanRecord.kt`

**Qué es:** La “ficha” o registro de cada escaneo. Define **qué información se guarda** cuando el usuario analiza una uña.

**Campos del registro:**

| Campo | Significado | Ejemplo |
|-------|-------------|---------|
| `id` | Identificador único del escaneo | `"abc-123"` |
| `userId` | Usuario que hizo el escaneo | Firebase UID |
| `result` | Resultado legible | `"Onicomicosis"` |
| `rawLabel` | Etiqueta original del modelo | `"onicomicosis"` |
| `confidence` | Confianza del modelo | `87.3` |
| `imageUrl` | URL de la foto en Firebase Storage | `https://…` |
| `dictionaryTermId` | Enlace al diccionario | `"onicomicosis"` |
| `createdAt` | Fecha y hora | Timestamp Firebase |

**Frase para exponer:**
> “Es como la hoja de un historial clínico digital: cada escaneo queda registrado con su foto, resultado y fecha.”

---

## 5. AuthRepository.kt — API (Autenticación)

**Ubicación:** `app/src/main/java/.../firebase/AuthRepository.kt`

**Qué es:** La capa que habla con **Firebase Authentication**. Gestiona usuarios y sesiones.

**Qué hace en palabras simples:**

| Función | Acción |
|---------|--------|
| `signIn()` | Iniciar sesión con correo y contraseña |
| `signUp()` | Crear cuenta nueva |
| `signOut()` | Cerrar sesión |
| `getUserProfile()` | Obtener datos del perfil |
| `updateUserProfile()` | Cambiar nombre |
| `updateUserProfilePhoto()` | Cambiar foto de perfil |
| `sendAccountVerificationEmail()` | Enviar enlace de recuperación |
| `currentUser` | Usuario activo en este momento |

**Por qué importa en el escaneo:**
- `ScanScreen` usa `authRepository.currentUser` para saber **quién** está analizando.
- Sin sesión activa, no se puede guardar el escaneo en el historial del usuario.

**Frase para exponer:**
> “Este archivo es el portero de la app: verifica quién entra, quién sale y mantiene la sesión del usuario conectada a Firebase.”

---

## 6. build.gradle.kts — Infraestructura

**Ubicación:** `app/build.gradle.kts`

**Qué es:** El archivo de configuración del módulo Android. Define **cómo se construye la app** y **qué librerías usa**.

**Datos principales de la app:**

| Configuración | Valor |
|---------------|-------|
| `applicationId` | `com.example.nnailscan` |
| `minSdk` | 24 (Android 7.0+) |
| `targetSdk` | 36 |
| `versionName` | 1.2 |

**Librerías importantes:**

| Librería | Para qué |
|----------|----------|
| Jetpack Compose | Interfaces modernas (UI) |
| Navigation Compose | Navegación entre pantallas |
| TensorFlow Lite | Ejecutar el modelo de IA |
| Firebase Auth | Login y registro |
| Firebase Firestore | Base de datos (historial, perfiles) |
| Firebase Storage | Guardar fotos |
| Coil | Cargar imágenes en pantalla |
| Coroutines | Operaciones en segundo plano |

**Detalle técnico útil:**
```kotlin
androidResources {
    noCompress += "tflite"
}
```
> Evita que Android comprima el modelo `.tflite` al empaquetar el APK (si se comprime, deja de funcionar).

**Frase para exponer:**
> “Este archivo es la lista de ingredientes y herramientas: sin él, la app no compilaría ni tendría acceso a Firebase, Compose ni TensorFlow Lite.”

---

### Conexión Expositor 2

```
ScanRecord        →  define QUÉ se guarda de cada escaneo
       ↓
AuthRepository    →  valida QUIÉN puede escanear y guardar
       ↓
build.gradle.kts  →  provee las HERRAMIENTAS para que todo funcione
```

---

# Flujo completo (para cerrar la exposición)

```
1. Usuario logueado (AuthRepository)
2. Toca "Escanear" en Inicio
3. NailScanNavGraph abre ScanScreen
4. Usuario elige foto o cámara (ScanScreen)
5. NailClassifier analiza la imagen → "onicomicosis" 87%
6. Se crea un ScanRecord con resultado + foto + fecha
7. Se guarda en Firestore (historial del usuario)
8. NavGraph lleva a ScanResultScreen
9. Usuario ve el diagnóstico y puede ir al diccionario
```

---

# Guion rápido por expositor (2–3 min cada uno)

## Expositor 1 — Experiencia del usuario

1. **NavGraph:** “La app sabe si mostrar login o inicio, y conecta escaneo con resultado.”
2. **ScanScreen:** “El usuario solo elige la imagen; la app valida sesión y muestra carga.”
3. **NailClassifier:** “La IA corre en el celular con TensorFlow Lite; no necesita internet para analizar.”

## Expositor 2 — Detrás de escena

1. **ScanRecord:** “Cada escaneo queda como un registro con foto, enfermedad y confianza.”
2. **AuthRepository:** “Firebase Auth protege el acceso y vincula cada escaneo a un usuario.”
3. **build.gradle.kts:** “Aquí están Compose, Firebase, TFLite y todo lo necesario para compilar la app.”

---

# Preguntas frecuentes en exposición

**¿La IA necesita internet?**
No para analizar. Sí para guardar el historial en Firebase.

**¿Dónde está el modelo entrenado?**
En `app/src/main/assets/nail_model_nuevo.tflite`, generado con `entrenar_modelo.py`.

**¿Qué pasa si el usuario no está logueado?**
ScanScreen muestra error y no procesa la imagen.

**¿Cuántas enfermedades detecta?**
Las que estén en `labels.txt`, alineadas con las carpetas del dataset de entrenamiento.

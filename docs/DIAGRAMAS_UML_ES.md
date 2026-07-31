# Diagramas UML en Espanol - NailScan

> Para usar estos diagramas en Mermaid Live, copia solo el contenido interno de cada bloque, empezando por `graph`, `classDiagram`, `sequenceDiagram` o `stateDiagram-v2`.

---

## 1. Diagrama de Casos de Uso

```mermaid
graph LR
    USUARIO((Usuario))
    ADMIN((Administrador))

    subgraph AUTENTICACION[Autenticacion]
        CU1[Registrarse]
        CU2[Iniciar sesion]
        CU3[Recuperar contrasena]
        CU4[Verificar correo electronico]
        CU5[Cambiar contrasena]
    end

    subgraph ESCANEO[Analisis de unas]
        CU6[Capturar o seleccionar imagen]
        CU7[Clasificar enfermedad]
        CU8[Ver resultado del escaneo]
    end

    subgraph PERFIL[Perfil de usuario]
        CU9[Ver perfil]
        CU10[Editar nombre y foto]
    end

    subgraph HISTORIAL[Historial]
        CU11[Ver historial personal]
    end

    subgraph DICCIONARIO[Diccionario informativo]
        CU12[Explorar diccionario]
        CU13[Ver detalle de enfermedad]
    end

    subgraph ADMINISTRACION[Administracion]
        CU14[Ver estadisticas globales]
        CU15[Ver historial global]
        CU16[Gestionar solicitudes de administrador]
        CU17[Ver perfiles de usuarios]
        CU18[Solicitar rol de administrador]
        CU19[Activar o desactivar modo administrador]
    end

    USUARIO --> CU1
    USUARIO --> CU2
    USUARIO --> CU3
    USUARIO --> CU4
    USUARIO --> CU5
    USUARIO --> CU6
    CU6 --> CU7
    CU7 --> CU8
    USUARIO --> CU9
    CU9 --> CU10
    USUARIO --> CU11
    USUARIO --> CU12
    CU12 --> CU13
    USUARIO --> CU18

    ADMIN --> CU14
    ADMIN --> CU15
    ADMIN --> CU16
    ADMIN --> CU17
    ADMIN --> CU19
    ADMIN --> CU6
    ADMIN --> CU9
    ADMIN --> CU11
    ADMIN --> CU12
```

---

## 2. Diagrama de Clases

```mermaid
classDiagram
    direction TB

    class PerfilUsuario {
        +idUsuario: String
        +nombreCompleto: String
        +correo: String
        +urlFoto: String
        +rol: RolUsuario
    }

    class RolUsuario {
        <<enumeracion>>
        USUARIO
        ADMINISTRADOR
    }

    class RegistroEscaneo {
        +id: String
        +idUsuario: String
        +resultado: String
        +etiquetaOriginal: String
        +confianza: Float
        +urlImagen: String
        +idTerminoDiccionario: String
        +fechaCreacion: Fecha
    }

    class SolicitudAdministrador {
        +id: String
        +idUsuario: String
        +correo: String
        +nombreCompleto: String
        +estado: EstadoSolicitud
        +fechaCreacion: Fecha
    }

    class EstadoSolicitud {
        <<enumeracion>>
        PENDIENTE
        APROBADA
        DENEGADA
    }

    class TerminoDiccionario {
        +id: String
        +titulo: String
        +descripcion: String
        +urlImagen: String
    }

    class DetalleTerminoDiccionario {
        +id: String
        +titulo: String
        +descripcion: String
        +sintomas: String
        +causas: String
        +recomendaciones: String
        +urlImagen: String
    }

    class EstadisticaClasificacion {
        +etiqueta: String
        +cantidad: Int
    }

    class RepositorioAutenticacion {
        +usuarioActual: UsuarioFirebase
        +iniciarSesion(correo, contrasena): Resultado
        +registrar(correo, contrasena, nombre): Resultado
        +obtenerPerfilUsuario(): PerfilUsuario
        +actualizarPerfil(nombre): Resultado
        +actualizarFotoPerfil(bytes): Resultado
        +enviarRecuperacionContrasena(correo): Resultado
        +cerrarSesion()
    }

    class RepositorioFirestore {
        +guardarPerfilUsuario(perfil): Resultado
        +obtenerPerfilUsuario(idUsuario): PerfilUsuario
        +actualizarRolUsuario(idUsuario, rol): Resultado
        +observarEscaneos(idUsuario): Flujo
        +guardarEscaneo(): Resultado
        +observarTodosLosUsuarios(): Flujo
        +observarEscaneosGlobales(): Flujo
        +observarEstadisticasGlobales(): Flujo
        +crearSolicitudAdministrador(solicitud): Resultado
        +observarSolicitudesPendientes(): Flujo
        +resolverSolicitud(id, estado): Resultado
    }

    class RepositorioAlmacenamiento {
        +subirImagenEscaneo(idUsuario, idEscaneo, bytes): Resultado
        +subirFotoPerfil(idUsuario, bytes): Resultado
    }

    class RepositorioEscaneo {
        +procesarYGuardarEscaneo(contexto, idUsuario, imagen): Resultado
    }

    class RepositorioRoles {
        +cargarRolActual(): RolUsuario
        +enviarSolicitudAdministrador(): Resultado
        +tieneSolicitudPendiente(): Boolean
        +aprobarSolicitud(id, idUsuario): Resultado
        +denegarSolicitud(id): Resultado
        +prepararAccesoAdministrador(): Resultado
    }

    class RepositorioDiccionario {
        +obtenerTerminos(): Lista
        +obtenerDetalle(id): DetalleTerminoDiccionario
    }

    class ModeloInicio {
        +estadoUi: FlujoEstado
        +vincularModoAdministrador(esAdmin)
        +actualizar()
    }

    class ModeloHistorial {
        +estadoUi: FlujoEstado
        +vincularModoAdministrador(esAdmin)
        +actualizar()
    }

    class ModeloPerfil {
        +estadoUi: FlujoEstado
        +actualizar()
        +actualizarPerfil(nombre)
        +subirFotoPerfil(bytes)
        +cerrarSesion()
    }

    class ModeloRoles {
        +estadoRol: FlujoEstado
        +alternarModoAdministrador()
        +solicitarAccesoAdministrador()
        +actualizar()
    }

    class ModeloSolicitudesAdmin {
        +estadoUi: FlujoEstado
        +vincularModoAdministrador(esAdmin)
        +aprobar(solicitud)
        +denegar(solicitud)
    }

    class ModeloUsuariosAdmin {
        +estadoUi: FlujoEstado
        +vincularModoAdministrador(esAdmin)
        +actualizar()
    }

    class ClasificadorUnas {
        +clasificarImagen(imagen): Par
        +cerrar()
    }

    PerfilUsuario --> RolUsuario
    SolicitudAdministrador --> EstadoSolicitud
    RegistroEscaneo --> TerminoDiccionario

    RepositorioAutenticacion --> RepositorioFirestore
    RepositorioAutenticacion --> RepositorioAlmacenamiento
    RepositorioEscaneo --> RepositorioFirestore
    RepositorioEscaneo --> RepositorioAlmacenamiento
    RepositorioEscaneo --> ClasificadorUnas
    RepositorioRoles --> RepositorioFirestore

    ModeloInicio --> RepositorioAutenticacion
    ModeloInicio --> RepositorioFirestore
    ModeloInicio --> RepositorioRoles
    ModeloHistorial --> RepositorioFirestore
    ModeloHistorial --> RepositorioRoles
    ModeloPerfil --> RepositorioAutenticacion
    ModeloRoles --> RepositorioRoles
    ModeloSolicitudesAdmin --> RepositorioRoles
    ModeloUsuariosAdmin --> RepositorioRoles
```

---

## 3. Diagrama de Componentes

```mermaid
graph TD
    subgraph APP[Aplicacion Android NailScan]
        subgraph CAPA_UI[Capa de interfaz]
            PANTALLAS[Pantallas Jetpack Compose]
            COMPONENTES[Componentes visuales]
            NAVEGACION[Navegacion]
        end

        subgraph CAPA_VM[Capa ViewModel]
            VM_INICIO[Modelo de Inicio]
            VM_HISTORIAL[Modelo de Historial]
            VM_PERFIL[Modelo de Perfil]
            VM_ROLES[Modelo de Roles]
            VM_ADMIN_SOL[Modelo de Solicitudes Admin]
            VM_ADMIN_USU[Modelo de Usuarios Admin]
            VM_DICCIONARIO[Modelo de Diccionario]
        end

        subgraph CAPA_REPO[Capa de repositorios]
            REPO_AUTH[Repositorio de Autenticacion]
            REPO_FIRESTORE[Repositorio Firestore]
            REPO_STORAGE[Repositorio de Almacenamiento]
            REPO_ESCANEO[Repositorio de Escaneo]
            REPO_ROLES[Repositorio de Roles]
            REPO_DICCIONARIO[Repositorio de Diccionario]
        end

        subgraph IA[Modulo de inteligencia artificial]
            CLASIFICADOR[Clasificador de unas]
            MODELO_TFLITE[Modelo TensorFlow Lite]
        end
    end

    subgraph FIREBASE[Servicios Firebase]
        AUTH[Firebase Authentication]
        DB[Cloud Firestore]
        STORAGE[Cloud Storage]
    end

    PANTALLAS --> COMPONENTES
    PANTALLAS --> NAVEGACION
    PANTALLAS --> VM_INICIO
    PANTALLAS --> VM_HISTORIAL
    PANTALLAS --> VM_PERFIL
    PANTALLAS --> VM_ROLES
    PANTALLAS --> VM_ADMIN_SOL
    PANTALLAS --> VM_ADMIN_USU
    PANTALLAS --> VM_DICCIONARIO

    VM_INICIO --> REPO_AUTH
    VM_INICIO --> REPO_FIRESTORE
    VM_INICIO --> REPO_ROLES
    VM_HISTORIAL --> REPO_FIRESTORE
    VM_HISTORIAL --> REPO_ROLES
    VM_PERFIL --> REPO_AUTH
    VM_ROLES --> REPO_ROLES
    VM_ADMIN_SOL --> REPO_ROLES
    VM_ADMIN_USU --> REPO_ROLES
    VM_DICCIONARIO --> REPO_DICCIONARIO

    REPO_ESCANEO --> CLASIFICADOR
    CLASIFICADOR --> MODELO_TFLITE
    REPO_AUTH --> AUTH
    REPO_AUTH --> REPO_FIRESTORE
    REPO_FIRESTORE --> DB
    REPO_STORAGE --> STORAGE
    REPO_ESCANEO --> REPO_FIRESTORE
    REPO_ESCANEO --> REPO_STORAGE
    REPO_ROLES --> REPO_FIRESTORE
```

---

## 4. Diagrama de Secuencia - Escaneo de Una

```mermaid
sequenceDiagram
    actor Usuario
    participant Pantalla as Pantalla de Escaneo
    participant RepoEscaneo as Repositorio de Escaneo
    participant Clasificador as Clasificador de Unas
    participant Almacenamiento as Repositorio de Almacenamiento
    participant Firestore as Repositorio Firestore
    participant Firebase as Firebase

    Usuario->>Pantalla: Selecciona o captura una imagen
    Pantalla->>RepoEscaneo: Procesar y guardar escaneo
    RepoEscaneo->>Clasificador: Clasificar imagen
    Clasificador-->>RepoEscaneo: Devuelve enfermedad y confianza
    RepoEscaneo->>Almacenamiento: Subir imagen del escaneo
    Almacenamiento->>Firebase: Guardar imagen en Cloud Storage
    Firebase-->>Almacenamiento: Devuelve URL de imagen
    Almacenamiento-->>RepoEscaneo: URL de imagen
    RepoEscaneo->>Firestore: Guardar resultado del escaneo
    Firestore->>Firebase: Registrar datos en Cloud Firestore
    Firebase-->>Firestore: Confirmacion
    Firestore-->>RepoEscaneo: Resultado correcto
    RepoEscaneo-->>Pantalla: Datos del resultado
    Pantalla-->>Usuario: Muestra resultado del analisis
```

---

## 5. Diagrama de Secuencia - Solicitud de Rol Administrador

```mermaid
sequenceDiagram
    actor Usuario as Usuario general
    actor Admin as Administrador
    participant Acerca as Pantalla Acerca de la App
    participant ModeloRol as Modelo de Roles
    participant RepoRol as Repositorio de Roles
    participant Firestore as Repositorio Firestore

    Usuario->>Acerca: Hace triple clic en el logo
    Acerca->>ModeloRol: Solicitar acceso administrador
    ModeloRol->>RepoRol: Enviar solicitud
    RepoRol->>Firestore: Crear solicitud pendiente
    Firestore-->>RepoRol: Solicitud registrada
    RepoRol-->>ModeloRol: Resultado correcto
    ModeloRol-->>Acerca: Mostrar mensaje de solicitud enviada

    Admin->>ModeloRol: Revisar modulo de peticiones
    ModeloRol->>RepoRol: Aprobar solicitud
    RepoRol->>Firestore: Marcar solicitud como aprobada
    Firestore-->>RepoRol: Solicitud actualizada
    RepoRol->>Firestore: Actualizar rol del usuario a administrador
    Firestore-->>RepoRol: Rol actualizado
    RepoRol-->>ModeloRol: Resultado correcto
    ModeloRol-->>Admin: Actualizar interfaz
```

---

## 6. Diagrama de Estado - Modo Administrador

```mermaid
stateDiagram-v2
    [*] --> ModoUsuario: Inicio de sesion correcto

    ModoUsuario --> SolicitudPendiente: Solicita rol administrador
    SolicitudPendiente --> ModoUsuario: Solicitud pendiente o denegada
    ModoUsuario --> ModoAdministrador: Solicitud aprobada o cuenta admin por defecto

    ModoAdministrador --> VistaAdministradorActiva: Activa modo administrador
    VistaAdministradorActiva --> ModoAdministrador: Desactiva modo administrador

    VistaAdministradorActiva --> VistaAdministradorActiva: Consulta estadisticas historial usuarios y solicitudes

    ModoAdministrador --> [*]: Cerrar sesion
    ModoUsuario --> [*]: Cerrar sesion
```

---

## 7. Diagrama de Despliegue

```mermaid
graph TD
    subgraph DISPOSITIVO[Dispositivo Android]
        APK[APK NailScan]
        TFLITE[Motor TensorFlow Lite]
        MODELO[Modelo entrenado de clasificacion]
    end

    subgraph NUBE[Google Firebase]
        AUTENTICACION[Firebase Authentication]
        BASE_DATOS[Cloud Firestore]
        ARCHIVOS[Cloud Storage]
    end

    APK --> TFLITE
    TFLITE --> MODELO
    APK --> AUTENTICACION
    APK --> BASE_DATOS
    APK --> ARCHIVOS
```


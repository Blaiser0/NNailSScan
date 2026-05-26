# NailScan — Branding

Recursos de marca centralizados para la app y documentación de diseño.

## Estructura

```text
branding/
├── logo/
│   └── logo.png              # Icono circular oficial (uña + círculo gris)
├── guias/
│   ├── pantalla_inicio.png   # Referencia UI — pantalla de bienvenida
│   └── iniciar_sesion.png    # Referencia UI — login
└── README.md
```

## Uso en Android

| Archivo fuente | Destino en la app |
|----------------|-------------------|
| `logo/logo.png` | `app/src/main/res/drawable/nailscan_logo.png` |
| Marca vectorial | `app/src/main/res/drawable/ic_nailscan_mark.xml` |

## Lockup “logo + nombre”

El diseño **NailScan + eslogan** se implementa en Compose:

- `NailScanBrandHeader` → pantalla de inicio y login
- Colores: fondo `#F5F5F5`, texto `#333333`, círculo `#D9D9D9`

## Actualizar assets

Tras cambiar el logo, copia de nuevo:

```powershell
Copy-Item branding\logo\logo.png app\src\main\res\drawable\nailscan_logo.png
```

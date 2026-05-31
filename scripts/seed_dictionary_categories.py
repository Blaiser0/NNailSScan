"""
Sube las imágenes de categorías a Firebase Storage y guarda los metadatos
en Firestore (colección dictionary_terms).

Requisitos:
  pip install firebase-admin

Autenticación (obligatoria — elige UNA opción):

  Opción A — Cuenta de servicio (recomendada):
    1. Firebase Console → Configuración del proyecto → Cuentas de servicio
    2. Generar nueva clave privada (descarga el JSON)
    3. Ejecutar:
       python scripts/seed_dictionary_categories.py --service-account ruta\\clave.json

  Opción B — Variable de entorno:
       set GOOGLE_APPLICATION_CREDENTIALS=ruta\\clave.json
       python scripts/seed_dictionary_categories.py

  Opción C — Google Cloud SDK (si lo tienes instalado):
       gcloud auth application-default login
       python scripts/seed_dictionary_categories.py

Nota: La app también puede poblar Firestore automáticamente al abrir el
Diccionario estando logueado (no necesitas este script si usas la app).
"""

from __future__ import annotations

import argparse
import mimetypes
import os
import sys
import uuid
from pathlib import Path
from urllib.parse import quote

import firebase_admin
from firebase_admin import credentials, firestore, storage

PROJECT_ID = "nailscan-65b49"
STORAGE_BUCKET = "nailscan-65b49.firebasestorage.app"
ROOT = Path(__file__).resolve().parents[1]
IMAGES_DIR = ROOT / "imagenes_categoria"
ASSETS_DIR = ROOT / "app" / "src" / "main" / "assets" / "categories"

TERM_FILES = {
    "acropaquia": ("Acropaquia.png", "Acropaquia"),
    "dedo_azul": ("Dedo Azul.png", "Dedo Azul"),
    "melanoma_acral": ("Melanoma.png", "Melanoma Acral"),
    "onicogrifosis": ("Onicogrifosis.png", "Onicogrifosis"),
    "onicomicosis": ("Onicomicosis.png", "Onicomicosis"),
    "picaduras": ("Picaduras.png", "Picaduras / Trauma"),
    "psoriasis_unas": ("Pitting Ungueal.png", "Psoriasis Ungueal"),
    "unas_sanas": (None, "Uña Sana"),
}

TERM_CONTENT = {
    "melanoma_acral": {
        "title": "Melanoma Acral",
        "description": "Lesión pigmentada maligna en la matriz o lecho ungueal.",
    },
    "onicogrifosis": {
        "title": "Onicogrifosis",
        "description": "Engrosamiento y curvatura anormal de la lámina ungueal.",
    },
    "onicomicosis": {
        "title": "Onicomicosis",
        "description": "Infección fúngica que afecta color, grosor y textura de la uña.",
    },
    "dedo_azul": {
        "title": "Dedo Azul",
        "description": "Cianosis o hematoma que oscurece la uña por falta de oxígeno o trauma.",
    },
    "acropaquia": {
        "title": "Acropaquia",
        "description": "Ensanchamiento de falanges distales con uñas en forma de reloj de arena.",
    },
    "psoriasis_unas": {
        "title": "Psoriasis Ungueal",
        "description": "Alteraciones como pitting, manchas y engrosamiento por psoriasis.",
    },
    "picaduras": {
        "title": "Picaduras / Trauma",
        "description": "Cambios ungueales por mordeduras, golpes o lesiones repetidas.",
    },
    "unas_sanas": {
        "title": "Uña Sana",
        "description": "Lámina ungueal sin signos evidentes de patología detectable.",
    },
}


def resolve_image_path(term_id: str, file_name: str | None) -> Path:
    asset_path = ASSETS_DIR / f"{term_id}.png"
    if asset_path.exists():
        return asset_path

    if file_name:
        direct = IMAGES_DIR / file_name
        if direct.exists():
            return direct

    if term_id == "unas_sanas":
        matches = list(IMAGES_DIR.glob("*Sana*.png"))
        if matches:
            return matches[0]

    raise FileNotFoundError(f"No se encontró imagen para {term_id}")


def init_firebase(service_account_path: str | None) -> None:
    if firebase_admin._apps:
        return

    env_path = os.environ.get("GOOGLE_APPLICATION_CREDENTIALS")
    json_path = service_account_path or env_path

    if json_path:
        path = Path(json_path)
        if not path.is_file():
            print(f"ERROR: No existe el archivo de credenciales: {path}", file=sys.stderr)
            sys.exit(1)
        cred = credentials.Certificate(str(path))
        firebase_admin.initialize_app(cred, {"storageBucket": STORAGE_BUCKET})
        return

    try:
        cred = credentials.ApplicationDefault()
        firebase_admin.initialize_app(cred, {"storageBucket": STORAGE_BUCKET})
    except Exception as error:
        print(
            "\nERROR: No se encontraron credenciales de Firebase.\n\n"
            "Descarga una clave de cuenta de servicio desde:\n"
            "  Firebase Console → Configuración → Cuentas de servicio → Generar nueva clave\n\n"
            "Luego ejecuta:\n"
            "  python scripts/seed_dictionary_categories.py --service-account ruta\\clave.json\n\n"
            "Alternativa: abre la app, inicia sesión y entra al Diccionario;\n"
            "la app subirá las imágenes automáticamente.\n",
            file=sys.stderr,
        )
        raise SystemExit(1) from error


def upload_image(term_id: str, image_path: Path) -> str:
    bucket = storage.bucket()
    blob_path = f"dictionary/{term_id}.png"
    blob = bucket.blob(blob_path)
    content_type = mimetypes.guess_type(image_path.name)[0] or "image/png"
    token = str(uuid.uuid4())
    blob.metadata = {"firebaseStorageDownloadTokens": token}
    blob.upload_from_filename(str(image_path), content_type=content_type)
    encoded_path = quote(blob_path, safe="")
    return (
        f"https://firebasestorage.googleapis.com/v0/b/{bucket.name}/o/"
        f"{encoded_path}?alt=media&token={token}"
    )


def main() -> None:
    parser = argparse.ArgumentParser(description="Sube categorías del diccionario a Firebase.")
    parser.add_argument(
        "--service-account",
        help="Ruta al JSON de cuenta de servicio de Firebase",
    )
    args = parser.parse_args()

    init_firebase(args.service_account)
    db = firestore.client()

    print(f"Proyecto: {PROJECT_ID}")
    print(f"Imágenes: {IMAGES_DIR}\n")

    for term_id, (file_name, _label) in TERM_FILES.items():
        image_path = resolve_image_path(term_id, file_name)
        image_url = upload_image(term_id, image_path)
        content = TERM_CONTENT[term_id]

        doc = {
            "id": term_id,
            "title": content["title"],
            "description": content["description"],
            "imageUrl": image_url,
        }

        db.collection("dictionary_terms").document(term_id).set(doc, merge=True)
        print(f"OK {term_id}: {image_url}")

    print("\nListo. Colección dictionary_terms actualizada en Firestore.")


if __name__ == "__main__":
    main()

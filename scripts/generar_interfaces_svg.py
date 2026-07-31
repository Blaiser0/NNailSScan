"""Genera SVGs responsivos de todas las interfaces de NailScan."""
import re
from pathlib import Path

OUT = Path(__file__).resolve().parent.parent / "docs" / "interfaces_svg"
W, H = 360, 800
ASPECT = f"{W} / {H}"

C = {
    "bg": "#FFF8F0",
    "surface": "#FFFFFF",
    "primary": "#8B5E3C",
    "light": "#F5D1B3",
    "accent": "#D4A373",
    "accent_dark": "#A0714F",
    "border": "#E8C4A8",
    "secondary": "#9C7355",
    "placeholder": "#C4A484",
    "disclaimer_bg": "#FFF3E8",
}


def esc(s: str) -> str:
    return (
        s.replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace('"', "&quot;")
    )


def wrap_lines(body: str, max_chars: int = 40) -> list[str]:
    words = body.split()
    lines: list[str] = []
    current: list[str] = []
    for word in words:
        candidate = " ".join(current + [word])
        if len(candidate) > max_chars and current:
            lines.append(" ".join(current))
            current = [word]
        else:
            current.append(word)
    if current:
        lines.append(" ".join(current))
    return lines


def multiline_text(
    x: int,
    y: int,
    body: str,
    size: int = 13,
    cls: str = "ts",
    line_height: int = 18,
    max_chars: int = 40,
) -> str:
    lines = wrap_lines(body, max_chars=max_chars)
    parts = [f'<text x="{x}" y="{y}" class="{cls}" font-size="{size}">']
    for index, line in enumerate(lines):
        dy = 0 if index == 0 else line_height
        parts.append(f'<tspan x="{x}" dy="{dy}">{esc(line)}</tspan>')
    parts.append("</text>")
    return "".join(parts)


def slug(title: str) -> str:
    value = re.sub(r"[^a-z0-9]+", "-", title.lower()).strip("-")
    return value or "screen"


def svg_open(title: str) -> list[str]:
    clip_id = f"clip-{slug(title)}"
    return [
        (
            f'<svg xmlns="http://www.w3.org/2000/svg" '
            f'viewBox="0 0 {W} {H}" '
            f'preserveAspectRatio="xMidYMid meet" '
            f'overflow="hidden" '
            f'role="img" aria-label="{esc(title)}" '
            f'width="100%" height="100%" '
            f'style="display:block;width:100%;height:100%;max-width:100%;max-height:100%;">'
        ),
        "<defs>",
        f'<clipPath id="{clip_id}"><rect x="0" y="0" width="{W}" height="{H}"/></clipPath>',
        "</defs>",
        "<style>",
        "svg { display: block; width: 100%; height: 100%; max-width: 100%; max-height: 100%; }",
        ".t { font-family: Arial, Helvetica, sans-serif; fill: #8B5E3C; }",
        ".ts { font-family: Arial, Helvetica, sans-serif; fill: #9C7355; }",
        ".tp { font-family: Arial, Helvetica, sans-serif; fill: #C4A484; }",
        ".tw { font-family: Arial, Helvetica, sans-serif; fill: #FFFFFF; }",
        ".tl { font-family: Arial, Helvetica, sans-serif; fill: #A0714F; }",
        "</style>",
        f'<g id="screen" clip-path="url(#{clip_id})">',
        f'<rect x="0" y="0" width="100%" height="100%" fill="{C["bg"]}"/>',
    ]


def svg_close() -> str:
    return "</g></svg>"


def rect(x, y, w, h, fill, rx=0, stroke=None, sw=1):
    s = f' stroke="{stroke}" stroke-width="{sw}"' if stroke else ""
    return f'<rect x="{x}" y="{y}" width="{w}" height="{h}" rx="{rx}" fill="{fill}"{s}/>'


def text(x, y, content, size=14, cls="t", weight="normal", anchor="start"):
    return (
        f'<text x="{x}" y="{y}" class="{cls}" font-size="{size}" '
        f'font-weight="{weight}" text-anchor="{anchor}">{esc(content)}</text>'
    )


def circle(cx, cy, r, fill, stroke=None):
    s = f' stroke="{stroke}" stroke-width="1"' if stroke else ""
    return f'<circle cx="{cx}" cy="{cy}" r="{r}" fill="{fill}"{s}/>'


def header(title: str, back: bool = True) -> list[str]:
    lines = [rect(0, 0, W, 56, C["surface"])]
    if back:
        lines.append(text(20, 36, "←", 22, weight="bold"))
    lines.append(text(48 if back else 20, 36, title, 22, weight="bold"))
    return lines


def bottom_bar(tabs: list[str], active: int = 0) -> list[str]:
    y = H - 64
    lines = [
        rect(0, y, W, 64, C["surface"], stroke=C["border"]),
    ]
    n = len(tabs)
    slot = W // n
    for i, label in enumerate(tabs):
        cx = slot * i + slot // 2
        color = C["primary"] if i == active else C["placeholder"]
        lines.append(circle(cx, y + 22, 10, color))
        lines.append(
            text(cx, y + 48, label, 10, cls="t" if i == active else "tp", anchor="middle")
        )
    return lines


def primary_btn(y: int, label: str, x: int = 24, w: int = 312):
    return [
        rect(x, y, w, 52, C["primary"], rx=14),
        text(x + w // 2, y + 32, label, 15, cls="tw", weight="bold", anchor="middle"),
    ]


def text_field(y: int, label: str, placeholder: str, x: int = 24, w: int = 312):
    return [
        text(x, y, label, 16, weight="bold"),
        rect(x, y + 10, w, 52, C["surface"], rx=10, stroke=C["border"]),
        text(x + 16, y + 42, placeholder, 16, cls="tp"),
    ]


def brand(y: int, tagline: bool = True, logo: int = 100) -> list[str]:
    cx = W // 2
    lines = [
        circle(cx, y + logo // 2, logo // 2, C["light"], stroke=C["border"]),
        f'<text x="{cx}" y="{y + logo + 28}" text-anchor="middle" class="t" font-size="28" font-weight="bold">'
        f'<tspan fill="{C["primary"]}">Nail</tspan><tspan fill="{C["light"]}">Scan</tspan></text>',
    ]
    if tagline:
        lines.append(
            text(cx, y + logo + 52, "Tu salud ungueal, al instante", 14, cls="ts", anchor="middle")
        )
    return lines


def menu_card(y: int, title: str, subtitle: str, x: int = 20, w: int = 320):
    return [
        rect(x, y, w, 72, C["surface"], rx=14),
        circle(x + 38, y + 36, 22, C["light"]),
        text(x + 72, y + 32, title, 16, weight="bold"),
        text(x + 72, y + 52, subtitle, 13, cls="ts"),
    ]


def history_card(y: int, result: str, date: str, x: int = 20, w: int = 320):
    return [
        rect(x, y, w, 88, C["surface"], rx=14),
        rect(x + 14, y + 14, 60, 60, C["light"], rx=12),
        text(x + 86, y + 38, result, 16, weight="bold"),
        text(x + 86, y + 58, date, 13, cls="ts"),
    ]


def content_card(y: int, title: str, body: str, x: int = 24, w: int = 312, h: int = 160):
    lines = [rect(x, y, w, h, C["surface"], rx=16)]
    body_y = y + 28 if title else y + 24
    if title:
        lines.append(text(x + 20, y + 28, title, 18, weight="bold"))
        body_y = y + 52
    lines.append(multiline_text(x + 20, body_y, body, max_chars=42))
    return lines


def welcome() -> str:
    lines = svg_open("WelcomeScreen")
    lines.extend(brand(260, tagline=True, logo=120))
    lines.append(svg_close())
    return "\n".join(lines)


def login() -> str:
    lines = svg_open("LoginScreen")
    lines.extend(brand(80, tagline=False, logo=90))
    lines.extend(text_field(220, "Correo Electronico", "tu@correo.com"))
    lines.extend(text_field(300, "Contrasena", "********"))
    lines.extend(primary_btn(390, "Iniciar Sesion"))
    lines.append(text(W // 2, 470, "Olvidaste tu contrasena?", 14, cls="tl", anchor="middle"))
    lines.append(
        text(W // 2, 500, "No tienes cuenta? Registrate", 14, cls="ts", anchor="middle")
    )
    lines.append(svg_close())
    return "\n".join(lines)


def register() -> str:
    lines = svg_open("RegisterScreen")
    lines.extend(header("Registro"))
    lines.extend(brand(70, tagline=False, logo=70))
    y = 180
    for lbl, ph in [
        ("Nombre Completo", "Tu nombre completo"),
        ("Correo Electronico", "tu@correo.com"),
        ("Contrasena", "********"),
        ("Confirmar Contrasena", "********"),
    ]:
        lines.extend(text_field(y, lbl, ph))
        y += 78
    lines.append(text(24, y + 10, "Acepto los terminos y condiciones", 13, cls="ts"))
    lines.extend(primary_btn(y + 24, "Registrarse"))
    lines.append(svg_close())
    return "\n".join(lines)


def forgot_password() -> str:
    lines = svg_open("ForgotPasswordScreen")
    lines.extend(header("Recuperar contrasena"))
    lines.append(
        text(24, 90, "Ingresa el correo asociado a tu cuenta.", 15, cls="ts")
    )
    lines.extend(text_field(130, "Correo Electronico", "tu@correo.com"))
    lines.extend(primary_btn(H - 100, "Enviar enlace de verificacion"))
    lines.append(svg_close())
    return "\n".join(lines)


def check_email() -> str:
    lines = svg_open("CheckEmailScreen")
    lines.extend(header("Revisa tu correo"))
    lines.append(text(24, 90, "Revisa tu bandeja de entrada.", 15, cls="ts"))
    lines.append(circle(W // 2, 200, 48, C["light"]))
    lines.append(text(W // 2, 210, "@", 32, weight="bold", anchor="middle"))
    lines.append(
        text(W // 2, 280, "Abre el enlace en este dispositivo", 14, cls="ts", anchor="middle")
    )
    lines.append(text(W // 2, H - 80, "Esperando verificacion...", 13, cls="t", anchor="middle"))
    lines.append(svg_close())
    return "\n".join(lines)


def email_verified() -> str:
    lines = svg_open("EmailVerifiedScreen")
    lines.extend(header("Verificacion"))
    lines.append(circle(W // 2, 200, 48, C["light"]))
    lines.append(text(W // 2, 210, "✓", 36, weight="bold", anchor="middle"))
    lines.append(
        text(W // 2, 280, "Correo verificado correctamente", 18, weight="bold", anchor="middle")
    )
    lines.append(
        text(W // 2, 310, "Crea tu nueva contrasena en la app", 14, cls="ts", anchor="middle")
    )
    lines.extend(primary_btn(H - 100, "Continuar"))
    lines.append(svg_close())
    return "\n".join(lines)


def change_password() -> str:
    lines = svg_open("ChangePasswordScreen")
    lines.extend(header("Cambiar contrasena"))
    lines.append(text(24, 90, "Minimo 8 caracteres con mayuscula, numero y simbolo.", 13, cls="ts"))
    lines.extend(text_field(130, "Nueva Contrasena", "********"))
    lines.extend(text_field(220, "Confirmar Contrasena", "********"))
    lines.extend(primary_btn(H - 100, "Cambiar Contrasena"))
    lines.append(svg_close())
    return "\n".join(lines)


def terms() -> str:
    lines = svg_open("TermsScreen")
    lines.extend(header("Terminos y condiciones"))
    lines.extend(
        content_card(
            72,
            "",
            "Al usar NailScan aceptas los terminos del servicio. La app ofrece analisis informativo de imagenes de unas mediante inteligencia artificial.",
        )
    )
    lines.append(svg_close())
    return "\n".join(lines)


def home(admin: bool = False) -> str:
    lines = svg_open("HomeScreen" + ("_Admin" if admin else ""))
    lines.append(rect(0, 0, W, 84, C["surface"]))
    lines.append(circle(44, 42, 24, C["light"]))
    lines.append(text(78, 34, "Hola,", 14))
    name = "Usuario Admin" if admin else "Maria Lopez"
    lines.append(text(78, 54, name, 16, weight="bold"))
    if admin:
        lines.append(text(210, 54, "✓", 14, weight="bold"))
    lines.append(circle(W // 2, 200, 66, C["accent"]))
    lines.append(
        f'<path d="M{W//2-18} 200 h36 M{W//2} 182 v36" stroke="#FFFFFF" stroke-width="4" stroke-linecap="round"/>'
    )
    lines.append(
        f'<rect x="{(W//2)+8}" y="188" width="14" height="10" rx="2" fill="none" stroke="#FFFFFF" stroke-width="3"/>'
    )
    y = 290
    if admin:
        lines.append(rect(20, y, 320, 150, C["surface"], rx=16))
        lines.append(text(36, y + 28, "Estadisticas globales (12 escaneos)", 14, weight="bold"))
        lines.append(circle(90, y + 90, 50, C["accent"]))
        lines.append(circle(90, y + 90, 30, C["light"]))
        lines.append(text(170, y + 70, "Unas sanas: 4", 13, cls="ts"))
        lines.append(text(170, y + 92, "Onicomicosis: 3", 13, cls="ts"))
        lines.append(text(170, y + 114, "Psoriasis: 2", 13, cls="ts"))
        y += 166
    lines.append(text(24, y, "Actividad Reciente", 18, weight="bold"))
    lines.extend(history_card(y + 16, "Unas sanas", "15/06/2026 10:30"))
    lines.extend(history_card(y + 116, "Onicomicosis", "14/06/2026 18:45"))
    lines.append(text(W // 2, y + 230, "Ver historial completo", 14, cls="tl", weight="bold", anchor="middle"))
    tabs = ["Inicio", "Diccionario", "Perfil"]
    if admin:
        tabs += ["Peticiones", "Usuarios"]
    lines.extend(bottom_bar(tabs, 0))
    lines.append(svg_close())
    return "\n".join(lines)


def history() -> str:
    lines = svg_open("HistoryScreen")
    lines.extend(header("Historial"))
    lines.extend(history_card(72, "Unas sanas", "15/06/2026"))
    lines.extend(history_card(172, "Melanoma acral", "10/06/2026"))
    lines.extend(history_card(272, "Onicogrifosis", "05/06/2026"))
    lines.append(svg_close())
    return "\n".join(lines)


def dictionary() -> str:
    lines = svg_open("DictionaryScreen")
    lines.append(rect(0, 0, W, 64, C["surface"]))
    lines.append(text(24, 40, "Diccionario de salud ungueal", 18, weight="bold"))
    terms = [
        ("Melanoma Acral", "Lesion pigmentada maligna"),
        ("Onicogrifosis", "Engrosamiento de la una"),
        ("Onicomicosis", "Infeccion fungica"),
        ("Unas sanas", "Estado normal de la una"),
    ]
    y = 80
    for t, d in terms:
        lines.append(rect(20, y, 320, 80, C["surface"], rx=14))
        lines.append(rect(34, y + 14, 52, 52, C["light"], rx=12))
        lines.append(text(98, y + 34, t, 16, weight="bold"))
        lines.append(text(98, y + 56, d, 13, cls="ts"))
        y += 92
    lines.extend(bottom_bar(["Inicio", "Diccionario", "Perfil"], 1))
    lines.append(svg_close())
    return "\n".join(lines)


def term_detail() -> str:
    lines = svg_open("TermDetailScreen")
    lines.extend(header("Onicomicosis"))
    lines.append(rect(20, 68, 320, 100, C["light"], rx=18))
    lines.append(text(40, 120, "Onicomicosis", 22, weight="bold"))
    lines.extend(content_card(182, "Descripcion", "Infeccion fungica que afecta las unas.", h=100))
    lines.extend(content_card(296, "Sintomas", "Engrosamiento, decoloracion y fragilidad.", h=100))
    lines.append(rect(20, 410, 320, 90, C["disclaimer_bg"], rx=12, stroke=C["light"]))
    lines.append(text(36, 440, "Descargo medico", 14, weight="bold"))
    lines.append(text(36, 462, "Solo informativo. Consulte a un dermatologo.", 12, cls="ts"))
    lines.append(svg_close())
    return "\n".join(lines)


def scan() -> str:
    lines = svg_open("ScanScreen")
    lines.extend(header("Analizar una"))
    lines.extend(primary_btn(200, "Seleccionar imagen de galeria"))
    lines.extend(primary_btn(270, "Tomar foto con camara"))
    lines.append(svg_close())
    return "\n".join(lines)


def scan_result() -> str:
    lines = svg_open("ScanResultScreen")
    lines.extend(header("Resultado"))
    lines.append(rect(20, 68, 320, 180, C["light"], rx=18, stroke=C["accent"], sw=2))
    lines.append(rect(20, 262, 320, 100, C["surface"], rx=14, stroke=C["accent"], sw=2))
    lines.append(text(36, 290, "Diagnostico IA", 14, weight="bold"))
    lines.append(text(36, 316, "Enfermedad: Onicomicosis", 15, cls="ts"))
    lines.append(text(36, 340, "Confianza: 87.3%", 13, cls="ts"))
    lines.extend(content_card(378, "Descripcion", "Patron compatible con infeccion fungica.", h=90))
    lines.extend(primary_btn(H - 80, "Saber mas en el diccionario"))
    lines.append(svg_close())
    return "\n".join(lines)


def profile(admin: bool = False) -> str:
    lines = svg_open("ProfileScreen")
    lines.append(circle(48, 48, 28, C["light"]))
    name = "Admin Usuario" if admin else "Maria Lopez"
    lines.append(text(92, 40, name, 18, weight="bold"))
    if admin:
        lines.append(text(230, 40, "✓", 14, weight="bold"))
    lines.append(text(92, 62, "maria@correo.com", 14, cls="ts"))
    lines.append(rect(260, 30, 70, 36, C["primary"], rx=10))
    lines.append(text(295, 52, "Editar", 13, cls="tw", anchor="middle"))
    menus = [
        ("Soporte Tecnico", "Preguntas frecuentes / Contacto"),
        ("Retroalimentacion", "Calificar App / Sugerir"),
        ("Acerca de NailScan", "Informacion de la app"),
        ("Terminos y condiciones", "Legal"),
        ("Politica de Privacidad", "Datos personales"),
    ]
    y = 100
    for t, s in menus:
        lines.extend(menu_card(y, t, s))
        y += 84
    lines.extend(primary_btn(y + 8, "Cerrar sesion"))
    lines.extend(bottom_bar(["Inicio", "Diccionario", "Perfil"], 2))
    lines.append(svg_close())
    return "\n".join(lines)


def edit_profile() -> str:
    lines = svg_open("EditProfileScreen")
    lines.extend(header("Editar perfil"))
    lines.append(circle(W // 2, 130, 48, C["light"]))
    lines.append(text(W // 2, 200, "Toca para cambiar foto", 13, cls="ts", anchor="middle"))
    lines.extend(text_field(240, "Nombre Completo", "Maria Lopez"))
    lines.extend(text_field(330, "Correo Electronico", "maria@correo.com (bloqueado)"))
    lines.extend(primary_btn(H - 100, "Guardar cambios"))
    lines.append(svg_close())
    return "\n".join(lines)


def support() -> str:
    lines = svg_open("TechnicalSupportScreen")
    lines.extend(header("Soporte Tecnico"))
    lines.extend(content_card(72, "", "Preguntas frecuentes sobre el uso de NailScan y analisis de imagenes.", h=200))
    lines.extend(primary_btn(300, "Contactar soporte"))
    lines.append(svg_close())
    return "\n".join(lines)


def feedback() -> str:
    lines = svg_open("FeedbackScreen")
    lines.extend(header("Retroalimentacion"))
    lines.extend(content_card(72, "", "Tu opinion nos ayuda a mejorar NailScan.", h=120))
    lines.extend(primary_btn(220, "Calificar en Play Store"))
    lines.extend(primary_btn(290, "Enviar sugerencia"))
    lines.extend(primary_btn(360, "Reportar problema"))
    lines.append(svg_close())
    return "\n".join(lines)


def about() -> str:
    lines = svg_open("AboutAppScreen")
    lines.extend(header("Acerca de NailScan"))
    lines.extend(brand(70, tagline=True, logo=80))
    lines.extend(content_card(230, "Version", "NailScan v1.0 - Analisis de unas con IA.", h=80))
    lines.extend(content_card(326, "Proposito", "Herramienta informativa para detectar patrones en unas.", h=100))
    lines.append(svg_close())
    return "\n".join(lines)


def privacy() -> str:
    lines = svg_open("PrivacyPolicyScreen")
    lines.extend(header("Politica de Privacidad"))
    lines.extend(
        content_card(
            72,
            "",
            "NailScan protege tus datos personales e imagenes. La informacion se almacena de forma segura en Firebase.",
            h=220,
        )
    )
    lines.append(svg_close())
    return "\n".join(lines)


def admin_requests() -> str:
    lines = svg_open("AdminRequestsScreen")
    lines.append(text(24, 40, "Peticiones de administrador", 20, weight="bold"))
    lines.append(rect(20, 60, 320, 130, C["surface"], rx=14))
    lines.append(text(36, 92, "Juan Perez", 16, weight="bold"))
    lines.append(text(36, 114, "juan@correo.com", 13, cls="ts"))
    lines.append(rect(36, 140, 120, 36, C["primary"], rx=10))
    lines.append(text(96, 162, "Permitir", 13, cls="tw", anchor="middle"))
    lines.append(rect(170, 140, 120, 36, C["surface"], rx=10, stroke=C["border"]))
    lines.append(text(230, 162, "Denegar", 13, cls="t", anchor="middle"))
    lines.extend(bottom_bar(["Inicio", "Diccionario", "Perfil", "Peticiones", "Usuarios"], 3))
    lines.append(svg_close())
    return "\n".join(lines)


def admin_users() -> str:
    lines = svg_open("AdminUsersScreen")
    lines.append(text(24, 40, "Perfiles de usuarios", 20, weight="bold"))
    users = [("Admin Usuario", "snakercher@gmail.com", "Administrador", True), ("Maria Lopez", "maria@correo.com", "Usuario", False)]
    y = 60
    for name, email, role, is_admin in users:
        lines.append(rect(20, y, 320, 88, C["surface"], rx=14))
        lines.append(circle(52, y + 44, 22, C["light"]))
        lines.append(text(84, y + 34, name, 16, weight="bold"))
        if is_admin:
            lines.append(text(250, y + 34, "✓", 14, weight="bold"))
        lines.append(text(84, y + 54, email, 12, cls="ts"))
        lines.append(text(84, y + 72, role, 12, cls="tl"))
        y += 100
    lines.extend(bottom_bar(["Inicio", "Diccionario", "Perfil", "Peticiones", "Usuarios"], 4))
    lines.append(svg_close())
    return "\n".join(lines)


SCREENS = {
    "01_welcome": welcome,
    "02_login": login,
    "03_register": register,
    "04_forgot_password": forgot_password,
    "05_check_email": check_email,
    "06_email_verified": email_verified,
    "07_change_password": change_password,
    "08_terms": terms,
    "09_home": lambda: home(False),
    "09_home_admin": lambda: home(True),
    "10_history": history,
    "11_dictionary": dictionary,
    "12_term_detail": term_detail,
    "13_scan": scan,
    "14_scan_result": scan_result,
    "15_profile": lambda: profile(False),
    "15_profile_admin": lambda: profile(True),
    "16_edit_profile": edit_profile,
    "17_support": support,
    "18_feedback": feedback,
    "19_about": about,
    "20_privacy": privacy,
    "21_admin_requests": admin_requests,
    "22_admin_users": admin_users,
}


def index_html() -> str:
    items = "\n".join(
        f"""    <a class="card" href="{name}.svg">
      <span class="num">{name[:2]}</span>
      <span class="title">{name[3:].replace("_", " ").title()}</span>
      <div class="preview">
        <object data="{name}.svg" type="image/svg+xml" aria-label="{name}"></object>
      </div>
    </a>"""
        for name in SCREENS
    )
    return f"""<!doctype html>
<html lang="es">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>NailScan - Interfaces SVG</title>
  <style>
    body {{ margin:0; background:#f8fafc; font-family:Arial,sans-serif; color:#0f172a; }}
    header {{ padding:32px; background:#fff; border-bottom:1px solid #e2e8f0; }}
    h1 {{ margin:0 0 8px; color:#8B5E3C; }}
    .grid {{ display:grid; grid-template-columns:repeat(auto-fill,minmax(220px,1fr)); gap:16px; padding:32px; }}
    .card {{ display:block; background:#fff; border:1px solid #e2e8f0; border-radius:16px; padding:16px; text-decoration:none; color:inherit; }}
    .card:hover {{ border-color:#8B5E3C; }}
    .num {{ color:#D4A373; font-weight:700; font-size:12px; }}
    .title {{ display:block; margin-top:8px; font-weight:700; }}
    .preview {{
      width:100%;
      aspect-ratio:{ASPECT};
      margin-top:12px;
      border-radius:12px;
      overflow:hidden;
      border:1px solid #E8C4A8;
      background:#FFF8F0;
    }}
    .preview object {{
      display:block;
      width:100%;
      height:100%;
      pointer-events:none;
    }}
  </style>
</head>
<body>
  <header>
    <h1>NailScan - Interfaces SVG</h1>
    <p>SVG responsivos 360x800. Se adaptan al contenedor en web, Figma y presentaciones.</p>
  </header>
  <section class="grid">
{items}
  </section>
</body>
</html>"""


def main():
    OUT.mkdir(parents=True, exist_ok=True)
    for name, builder in SCREENS.items():
        (OUT / f"{name}.svg").write_text(builder(), encoding="utf-8")
    (OUT / "index.html").write_text(index_html(), encoding="utf-8")
    print(f"Generados {len(SCREENS)} SVG en {OUT}")


if __name__ == "__main__":
    main()

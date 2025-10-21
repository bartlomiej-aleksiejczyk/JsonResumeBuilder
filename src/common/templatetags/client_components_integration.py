import json
import os

from django import template
from django.conf import settings
from django.utils.safestring import mark_safe

register = template.Library()

CLIENT_COMPONENT_SETTINGS = getattr(settings, "CLIENT_COMPONENT_SETTINGS", {})


@register.simple_tag
def load_client_components():
    """Load JS & CSS assets dynamically, supporting both Vite Dev Mode and production."""

    # **Vite Dev Mode Settings**
    is_debug = settings.DEBUG

    # **Production Mode Settings**
    client_components_static_url = getattr(settings, "STATIC_URL", "/static/")
    manifest_path = CLIENT_COMPONENT_SETTINGS.get(
        "MANIFEST_FILE_PATH", os.path.join(settings.BASE_DIR, "dist", ".vite", "manifest.json")
    )
    vite_dev_url = CLIENT_COMPONENT_SETTINGS.get("DEV_URL", "http://localhost:5173/")
    client_components_path = CLIENT_COMPONENT_SETTINGS.get("CLIENT_COMPONENTS_PATH", "client_components/")

    tags = []

    # **If in Development Mode, inject Vite Dev Server links**
    if is_debug:
        return mark_safe(
            f'''
            <script type="module" src="{vite_dev_url}@vite/client"></script>
            <script type="module" src="{vite_dev_url}{client_components_path}main.js"></script>
            '''
        )

    # **If in Production Mode, load from manifest.json**
    if not os.path.exists(manifest_path):
        raise template.TemplateSyntaxError(f"Vite manifest.json not found at {manifest_path}")

    try:
        with open(manifest_path, "r", encoding="utf-8") as f:
            manifest = json.load(f)
    except Exception as e:
        raise template.TemplateSyntaxError(f"Error loading Vite manifest.json: {str(e)}")

    # **Extract JS & CSS files**
    js_files = {k: v for k, v in manifest.items() if k.endswith(".js")}
    css_files = {k: v for k, v in manifest.items() if k.endswith(".css")}

    for file in js_files.values():
        tags.append(
            f'<script type="module" src="{client_components_static_url}{file["file"]}"></script>'
        )

    for file in css_files.values():
        tags.append(
            f'<link rel="stylesheet" href="{client_components_static_url}{file["file"]}">'
        )

    return mark_safe("\n".join(tags))
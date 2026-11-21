"""Reglas de entrada compartidas por toda la aplicación (tipo de dato y límites)."""
import re

MAX_ITEMS = 12          # límite de datos por carga guiada, definido por el grupo
MIN_VALUE = -9999       # tipo de dato permitido: enteros entre -9999 y 9999
MAX_VALUE = 9999


def parse_count(text, label, maximum=MAX_ITEMS):
    """Valida una cantidad: entero positivo y no superior al límite."""
    text = (text or '').strip()
    if not re.fullmatch(r'[+-]?[0-9]+', text):
        raise ValueError(f'{label} debe ser un número entero positivo.')
    number = int(text)
    if number < 1:
        raise ValueError(f'{label} debe ser mayor que cero.')
    if number > maximum:
        raise ValueError(f'{label} no puede superar {maximum}.')
    return number


def parse_number(text, label='El valor'):
    """Valida un dato: entero entre MIN_VALUE y MAX_VALUE (sin decimales ni letras)."""
    text = (text or '').strip()
    if not text:
        raise ValueError('Escribe un valor.')
    if not re.fullmatch(r'[+-]?[0-9]+', text):
        raise ValueError(f'{label} debe ser un número entero (sin letras ni decimales).')
    number = int(text)
    if not MIN_VALUE <= number <= MAX_VALUE:
        raise ValueError(f'{label} debe estar entre {MIN_VALUE} y {MAX_VALUE}.')
    return number

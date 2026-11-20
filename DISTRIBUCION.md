# DataStructures Lab para Windows y Linux

Descarga el archivo de tu sistema en Releases y extrae la carpeta completa. El paquete incluye dos aplicaciones independientes.

| Sistema | Python | Java |
| --- | --- | --- |
| Windows x64 | Abre `DataStructuresLab-Python/DataStructuresLab-Python.exe` | Abre `DataStructuresLab-Java/DataStructuresLab-Java.exe` |
| Linux x64 | Ejecuta `./DataStructuresLab-Python/DataStructuresLab-Python` | Ejecuta `./DataStructuresLab-Java/bin/DataStructuresLab-Java` |

Cada aplicación incluye su propio runtime. Linux requiere un escritorio con X11 o XWayland y bibliotecas gráficas GTK para JavaFX. Mantén junto al ejecutable sus subcarpetas. En Linux se recomienda extraer con `tar -xzf nombre-del-paquete.tar.gz` para preservar los permisos.

Desde código fuente, Java requiere JDK y Maven. Python necesita Python 3.12 con Tk y se ejecuta con `python python/main.py`. Las pruebas son `mvn test` y, en la carpeta `python`, `python -m unittest discover -s tests -v`. La opción `--smoke-test` abre la interfaz y realiza una comprobación automatizada.

El flujo de GitHub Actions publica un paquete por sistema. Las etiquetas `cross-v*` publican una versión después de que aprueben los dos runners.

# DataStructures Lab en Windows

Para desarrollo desde el código fuente, instala JDK 17+ y Maven 3.9+ y ejecuta `run-windows.cmd`; `test-windows.cmd` corre JUnit. Estos scripts requieren Java y Maven.

El workflow de GitHub Actions compila una aplicación Windows x64 con `jpackage`, que incluye su runtime Java, y publica un ZIP descargable en los artefactos de la ejecución. Descarga y descomprime `DataStructures-Lab-Windows-x64.zip` y abre `DataStructures-Lab.exe`. La descarga del workflow es una app-image autocontenida, no un instalador MSI.

Para Linux x64, usa el artefacto `DataStructures-Lab-Linux-x64.tar.gz`; descomprímelo y ejecuta `DataStructures-Lab/bin/DataStructures-Lab`.

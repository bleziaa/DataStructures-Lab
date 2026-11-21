# DataStructures Lab

Laboratorio de escritorio visual e interactivo para estudiar siete estructuras de datos. Los datos se manipulan mediante implementaciones Java reales; JavaFX se encarga de representar nodos, enlaces, posiciones y animaciones.

## Tecnologías

- Java 17 como versión objetivo de compilación.
- JavaFX 21.0.6.
- Maven.
- JUnit 5.11.4.

## Requisitos

- JDK 17 o superior. El proyecto compila con `--release 17` para conservar compatibilidad con Java 17.
- Maven 3.9 o superior.
- Linux, Windows o macOS con entorno gráfico compatible con JavaFX.

## Instalación y ejecución

Clona el repositorio y entra en la carpeta:

```bash
git clone https://github.com/bleziaa/DataStructures-Lab.git
cd DataStructures-Lab
```

Compilar y ejecutar:

```bash
mvn clean javafx:run
```

Ejecutar las pruebas:

```bash
mvn clean test
```

## Laboratorios disponibles

### 1. Arreglos

- Crear un arreglo de tamaño definido.
- Registrar valores en posiciones vacías.
- Consultar y modificar por índice.
- Recorrer las posiciones en orden.
- Animación de aparición, inserción, modificación y recorrido.
- Los índices comienzan en cero; no se sobrescribe una posición mediante Registrar.

### 2. Matrices

- Crear matrices con filas y columnas.
- Registrar, consultar y modificar celdas.
- Recorrer por filas.
- Mostrar coordenadas y total de elementos.
- Animación de aparición de las celdas y recorrido secuencial.

### 3. Listas enlazadas

- Lista simplemente enlazada con nodos y referencias reales.
- Insertar al inicio y al final.
- Buscar y eliminar la primera aparición de un valor.
- Recorrer desde HEAD hasta TAIL.
- Nodos circulares conectados mediante flechas.

### 4. Pilas

- Implementación enlazada real.
- PUSH, POP, PEEK, mostrar elementos y reiniciar.
- Representación vertical desde TOP hacia el fondo.
- Animación de caída al insertar y salida al extraer.
- Política LIFO: último en entrar, primero en salir.

### 5. Colas

- Implementación enlazada real con FRONT y REAR.
- ENQUEUE, DEQUEUE, PEEK FRONT y recorrido.
- Representación horizontal.
- Animación de entrada por REAR y salida por FRONT.
- Política FIFO: primero en entrar, primero en salir.

### 6. Árboles binarios de búsqueda

- Inserción, búsqueda y eliminación.
- Recorridos inorden, preorden y postorden.
- Visualización jerárquica con nodos y ramas.
- Búsqueda animada siguiendo las comparaciones desde la raíz.
- Regla: valores menores a la izquierda y mayores a la derecha.
- Los duplicados se rechazan.

### 7. Grafos

- Representación mediante listas de adyacencia.
- Crear vértices y aristas.
- Grafos dirigidos y no dirigidos.
- Consultar vecinos.
- Recorridos BFS y DFS con estados de visitados y pendientes.
- No se permiten vértices duplicados, aristas duplicadas ni lazos.

## Validaciones

Los formularios controlan campos vacíos, valores que no sean enteros, cantidades no válidas, límites de visualización, índices y coordenadas fuera de rango, operaciones sobre estructuras vacías, duplicados no permitidos y vértices inexistentes. Los mensajes se muestran en español dentro del módulo y la aplicación conserva su ejecución ante entradas incorrectas.

## Organización del proyecto

```text
src/
├── main/
│   ├── java/com/datastructureslab/
│   │   ├── app/             # Inicio y navegación
│   │   ├── animations/      # Transiciones reutilizables de JavaFX
│   │   ├── controllers/     # Controladores de cada laboratorio
│   │   ├── models/          # Validaciones y pasos de recorrido
│   │   ├── structures/      # Implementaciones reales de las estructuras
│   │   └── views/           # Nodos, flechas, enlaces y panel visual
│   └── resources/styles/    # Tema visual morado, negro y blanco
└── test/java/               # Pruebas unitarias JUnit 5
```

## Pruebas

Las pruebas cubren arreglos y matrices, listas, pilas, colas, árboles binarios de búsqueda, grafos y validaciones de entrada.

Verificación automatizada:
- Compilación y ejecución de pruebas Java con Maven: **28 pruebas exitosas, 0 fallidas**.
- Pruebas de la edición Python: **4 pruebas exitosas, 0 fallidas**.
- GitHub Actions compila y empaqueta ambas ediciones para Windows y Linux.
- Las interfaces JavaFX y Tkinter pasaron comprobaciones de inicio gráfico en runners Windows y Linux.
- Los paquetes autocontenidos están en [GitHub Releases](https://github.com/bleziaa/DataStructures-Lab/releases/tag/cross-v1.0.0).

## Diseño

Interfaz oscura con acentos morados, negro y blanco. Cada laboratorio presenta el área de visualización, panel de operaciones, información, resultados y bitácora. Las animaciones se implementan con JavaFX Transitions y Timeline.

## Edición Python

El repositorio también incluye una edición gráfica independiente en Python con Tkinter. Conserva los siete laboratorios e incorpora grafos dirigidos y no dirigidos, recorridos BFS y DFS, listas enlazadas, operaciones de borrado y animaciones visuales. Sus implementaciones están en `python/`.

Ejecuta las pruebas con:

```bash
cd python
python -m unittest discover -s tests -v
```

Para usarla sin instalar Python ni Maven, descarga el paquete correspondiente a tu sistema desde [Releases](https://github.com/bleziaa/DataStructures-Lab/releases/tag/cross-v1.0.0). La edición Java y la edición Python son aplicaciones independientes.

## Paquetes sin instalar Java, Maven ni Python

Consulta [la guía de distribución](DISTRIBUCION.md).

## Ejemplos interactivos

Cada uno de los siete módulos incluye el botón **Cargar ejemplo**, que prepara datos reales para explorar sus operaciones y animaciones: arreglo `[10, 20, 30, 40, 50]`, matriz `3×3`, lista enlazada, pila LIFO, cola FIFO, árbol binario de búsqueda y grafo no dirigido.

## Modo de consola

El laboratorio también se puede usar desde la terminal sin Maven ni JavaFX:

```bash
./run-console.sh
```

El script compila el modo de consola con Java 17 y abre el menú de las siete estructuras. Solicita los datos durante la ejecución, valida enteros y cantidades, permite realizar operaciones y volver al menú principal. Las estructuras reutilizan las implementaciones reales del proyecto.

En CachyOS, si el archivo no tiene permiso de ejecución, se puede iniciar con:

```bash
bash run-console.sh
```

La interfaz de escritorio con nodos y animaciones se ejecuta con `mvn javafx:run` cuando Maven está instalado. El modo de consola es independiente de esa dependencia.

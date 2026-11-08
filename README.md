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

Verificación local registrada durante el desarrollo:
- Compilación de todos los fuentes Java mediante `javac --release 17`: correcta.
- Ejecución de pruebas con JUnit Platform: **28 pruebas exitosas, 0 fallidas**.
- La ejecución con `mvn clean test` y `mvn javafx:run` queda pendiente de disponer de Maven en el equipo.

## Diseño

Interfaz oscura con acentos morados, negro y blanco. Cada laboratorio presenta el área de visualización, panel de operaciones, información, resultados y bitácora. Las animaciones se implementan con JavaFX Transitions y Timeline.

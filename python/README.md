# DataStructures Lab en Python

Aplicación gráfica con Tkinter. La [guía de distribución](../DISTRIBUCION.md) indica cómo iniciar el ejecutable en Windows o Linux. Desde código fuente, usa `python main.py` con Python 3.12 y Tk.

- Arreglos con inserción por índice, búsqueda y recorrido.
- Matrices con coordenadas de base cero, asignación, consulta y recorrido por filas.
- Listas enlazadas, pilas LIFO y colas FIFO.
- Árbol binario de búsqueda con eliminación y recorridos inorden, preorden y postorden.
- Grafos dirigidos y no dirigidos, con BFS, DFS y lista de vértices inalcanzables. Elige el tipo antes de crear aristas. Para cambiarlo, elimina primero las aristas o reinicia el grafo.

Las operaciones resaltan las posiciones afectadas. En los recorridos, un punto se mueve entre nodos siguiendo la conexión usada; las eliminaciones muestran un nodo que se contrae. El lienzo se puede desplazar para ver estructuras grandes.

Ejecuta `python -m unittest discover -s tests -v` para probar los modelos o `python main.py --smoke-test` para la comprobación gráfica.

package com.datastructureslab.console;

import com.datastructureslab.structures.*;
import java.util.*;

public final class ConsoleApp {
    private static final Scanner IN = new Scanner(System.in);
    private static final int MAX = 100;

    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println("       DATASTRUCTURES LAB · CONSOLA");
        System.out.println("==============================================");
        while (true) {
            System.out.println("\nMENÚ PRINCIPAL");
            System.out.println("1. Arreglo unidimensional");
            System.out.println("2. Matriz");
            System.out.println("3. Lista enlazada");
            System.out.println("4. Pila (LIFO)");
            System.out.println("5. Cola (FIFO)");
            System.out.println("6. Árbol binario de búsqueda");
            System.out.println("7. Grafo");
            System.out.println("0. Salir");
            int option = askInt("Selecciona una opción: ", 0, 7);
            if (option == 0) {
                System.out.println("¡Hasta luego!");
                return;
            }
            try {
                switch (option) {
                    case 1 -> arrayLab();
                    case 2 -> matrixLab();
                    case 3 -> listLab();
                    case 4 -> stackLab();
                    case 5 -> queueLab();
                    case 6 -> treeLab();
                    case 7 -> graphLab();
                    default -> { }
                }
            } catch (RuntimeException ex) {
                System.out.println("No se pudo completar la operación: " + ex.getMessage());
            }
        }
    }
    private static void arrayLab() {
        int n = askInt("¿Cuántos elementos? (1-" + MAX + "): ", 1, MAX);
        ArrayStructure a = new ArrayStructure(n);
        for (int i = 0; i < n; i++) a.set(i, askValue("Valor para índice " + i + ": "));
        showArray(a);
        while (true) {
            System.out.println("\n1 Consultar índice  2 Modificar  3 Recorrer  0 Menú");
            int op = askInt("Opción: ", 0, 3);
            if (op == 0) return;
            if (op == 1 || op == 2) {
                int i = askInt("Índice (0-" + (n - 1) + "): ", 0, n - 1);
                if (op == 1) System.out.println("índice " + i + " → " + a.get(i));
                else {
                    int v = askValue("Nuevo valor: ");
                    a.set(i, v);
                    System.out.println("Posición actualizada.");
                }
            }
            showArray(a);
        }
    }

    private static void showArray(ArrayStructure a) {
        System.out.println("ARREGLO (índice → valor)");
        for (int i = 0; i < a.length(); i++) System.out.println("[" + i + "] → " + a.get(i));
    }

    private static void matrixLab() {
        int rows = askInt("Número de filas (1-20): ", 1, 20);
        int cols = askInt("Número de columnas (1-20): ", 1, 20);
        if (rows * cols > MAX) {
            System.out.println("El total no puede superar " + MAX + " celdas.");
            return;
        }
        MatrixStructure m = new MatrixStructure(rows, cols);
        System.out.println("Total de elementos: " + m.totalElements());
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                m.set(r, c, askValue("Valor para fila " + r + ", columna " + c + ": "));
        showMatrix(m);
        while (true) {
            System.out.println("\n1 Consultar celda  2 Modificar celda  3 Recorrer  0 Menú");
            int op = askInt("Opción: ", 0, 3);
            if (op == 0) return;
            if (op == 1 || op == 2) {                int r = askInt("Fila (0-" + (rows - 1) + "): ", 0, rows - 1);
                int c = askInt("Columna (0-" + (cols - 1) + "): ", 0, cols - 1);
                if (op == 1) System.out.println("(" + r + ", " + c + ") → " + m.get(r, c));
                else m.set(r, c, askValue("Nuevo valor: "));
            }
            showMatrix(m);
        }
    }

    private static void showMatrix(MatrixStructure m) {
        System.out.println("MATRIZ · índices desde 0");
        for (int r = 0; r < m.rows(); r++) {
            System.out.print("Fila " + r + ": ");
            for (int c = 0; c < m.cols(); c++) System.out.print("[" + m.get(r, c) + "] ");
            System.out.println();
        }
    }

    private static void listLab() {
        int n = askInt("¿Cuántos elementos iniciales? (1-" + MAX + "): ", 1, MAX);
        SinglyLinkedList list = new SinglyLinkedList();
        for (int i = 0; i < n; i++) list.insertLast(askValue("Valor " + (i + 1) + ": "));
        showList(list);
        while (true) {
            System.out.println("\n1 Insertar al inicio  2 Insertar al final  3 Buscar");
            System.out.println("4 Eliminar primera aparición  5 Recorrer  0 Menú");
            int op = askInt("Opción: ", 0, 5);
            if (op == 0) return;
            if (op == 1) list.insertFirst(askValue("Valor: "));
            if (op == 2) list.insertLast(askValue("Valor: "));
            if (op == 3) {
                int v = askValue("Valor a buscar: ");
                int i = list.indexOf(v);
                System.out.println(i < 0 ? "El dato no existe." : "Encontrado en índice " + i + ".");
            }
            if (op == 4) {
                int v = askValue("Valor a eliminar: ");
                int i = list.remove(v);
                System.out.println(i < 0 ? "El dato no existe." : "Eliminado del índice " + i + ".");
            }
            showList(list);
        }
    }

    private static void showList(SinglyLinkedList list) {        System.out.println("LISTA ENLAZADA (se permiten repetidos)");
        List<Integer> values = list.toList();
        for (int i = 0; i < values.size(); i++)
            System.out.print("[" + i + ":" + values.get(i) + "]" + (i + 1 < values.size() ? " → " : ""));
        System.out.println(values.isEmpty() ? "(vacía)" : " → null");
    }

    private static void stackLab() {
        int n = askInt("¿Cuántos elementos iniciales? (1-" + MAX + "): ", 1, MAX);
        LinkedStack stack = new LinkedStack();
        for (int i = 0; i < n; i++) stack.push(askValue("Valor " + (i + 1) + ": "));
        showStack(stack);
        while (true) {
            System.out.println("\n1 Apilar (PUSH)  2 Desapilar (POP)  3 Consultar cima (PEEK)  0 Menú");
            int op = askInt("Opción: ", 0, 3);
            if (op == 0) return;
            try {
                if (op == 1) stack.push(askValue("Valor: "));
                if (op == 2) System.out.println("Sale primero (LIFO): " + stack.pop());
                if (op == 3) System.out.println("Cima (TOP): " + stack.peek());
            } catch (EmptyStructureException ex) { System.out.println(ex.getMessage()); }
            showStack(stack);
        }
    }

    private static void showStack(LinkedStack stack) {
        System.out.println("PILA · TOP está arriba (LIFO)");
        List<Integer> values = stack.toList();
        if (values.isEmpty()) System.out.println("(vacía)");
        for (int i = 0; i < values.size(); i++)
            System.out.println((i == 0 ? "TOP → " : "       ") + "[" + values.get(i) + "]");
    }

    private static void queueLab() {
        int n = askInt("¿Cuántos elementos iniciales? (1-" + MAX + "): ", 1, MAX);
        LinkedQueue queue = new LinkedQueue();
        for (int i = 0; i < n; i++) queue.enqueue(askValue("Valor " + (i + 1) + ": "));
        showQueue(queue);
        while (true) {
            System.out.println("\n1 Encolar  2 Desencolar  3 Consultar frente  0 Menú");
            int op = askInt("Opción: ", 0, 3);
            if (op == 0) return;
            try {
                if (op == 1) queue.enqueue(askValue("Valor: "));
                if (op == 2) System.out.println("Sale primero (FIFO): " + queue.dequeue());                if (op == 3) System.out.println("Frente (FRONT): " + queue.peek());
            } catch (EmptyStructureException ex) { System.out.println(ex.getMessage()); }
            showQueue(queue);
        }
    }

    private static void showQueue(LinkedQueue queue) {
        System.out.println("COLA · primero en entrar, primero en salir (FIFO)");
        List<Integer> values = queue.toList();
        if (values.isEmpty()) { System.out.println("(vacía)"); return; }
        System.out.print("FRONT → ");
        for (int v : values) System.out.print("[" + v + "] → ");
        System.out.println("REAR");
    }

    private static void treeLab() {
        int n = askInt("¿Cuántos valores iniciales? (1-" + MAX + "): ", 1, MAX);
        BinarySearchTree tree = new BinarySearchTree();
        for (int i = 0; i < n; i++) {
            int v = askValue("Valor " + (i + 1) + ": ");
            if (!tree.insert(v)) System.out.println("Repetido: " + v + " no se insertó.");
        }
        showTree(tree);
        while (true) {
            System.out.println("\n1 Insertar  2 Buscar  3 Recorridos  0 Menú");
            int op = askInt("Opción: ", 0, 3);
            if (op == 0) return;
            if (op == 1) {
                int v = askValue("Valor: ");
                System.out.println(tree.insert(v) ? "Insertado." : "Duplicado rechazado.");
            }
            if (op == 2) {
                int v = askValue("Valor a buscar: ");
                System.out.println(tree.contains(v) ? "Encontrado; camino: " + tree.searchPath(v) : "No existe; camino: " + tree.searchPath(v));
            }
            if (op == 3) {
                System.out.println("Inorden:   " + tree.inorder());
                System.out.println("Preorden:  " + tree.preorder());
                System.out.println("Postorden: " + tree.postorder());
            }
            showTree(tree);
        }
    }

    private static void showTree(BinarySearchTree tree) {
        System.out.println("ÁRBOL BINARIO · niveles y ramas · menores a la izquierda");
        if (tree.isEmpty()) { System.out.println("(vacío)"); return; }
        int width = tree.size() * 8 + 6;
        char[][] canvas = new char[tree.height() * 2 - 1][width];
        for (char[] row : canvas) Arrays.fill(row, ' ');
        Map<BinarySearchTree.Node, Integer> x = new IdentityHashMap<>();
        Map<BinarySearchTree.Node, Integer> level = new IdentityHashMap<>();
        int[] next = {0};
        placeInorder(tree.getRoot(), 0, x, level, next, canvas);
        drawBranches(tree.getRoot(), x, level, canvas);
        for (char[] row : canvas) {
            int end = row.length;
            while (end > 0 && row[end - 1] == ' ') end--;
            if (end > 0) System.out.println(new String(row, 0, end));
        }
        System.out.println("Cada rama indica izquierda o derecha respecto al padre.");
    }

    private static void placeInorder(BinarySearchTree.Node node, int depth,
            Map<BinarySearchTree.Node, Integer> x, Map<BinarySearchTree.Node, Integer> level,
            int[] next, char[][] canvas) {
        if (node == null) return;
        placeInorder(node.getLeft(), depth + 1, x, level, next, canvas);
        int center = 3 + next[0]++ * 8;
        x.put(node, center);
        level.put(node, depth);
        String value = String.valueOf(node.getValue());
        int start = center - value.length() / 2;
        for (int i = 0; i < value.length(); i++) canvas[depth * 2][start + i] = value.charAt(i);
        placeInorder(node.getRight(), depth + 1, x, level, next, canvas);
    }

    private static void drawBranches(BinarySearchTree.Node node,
            Map<BinarySearchTree.Node, Integer> x, Map<BinarySearchTree.Node, Integer> level,
            char[][] canvas) {
        if (node == null) return;
        int parent = x.get(node);
        int row = level.get(node) * 2 + 1;
        BinarySearchTree.Node left = node.getLeft();
        if (left != null) {
            int child = x.get(left);
            for (int i = child + 1; i < parent - 1; i++) canvas[row][i] = '─';
            canvas[row][parent - 1] = '╱';
        }
        BinarySearchTree.Node right = node.getRight();
        if (right != null) {
            int child = x.get(right);
            canvas[row][parent + 1] = '╲';
            for (int i = parent + 2; i < child; i++) canvas[row][i] = '─';
        }
        drawBranches(left, x, level, canvas);
        drawBranches(right, x, level, canvas);
    }

    private static void graphLab() {
        int n = askInt("Cantidad de vértices (1-" + MAX + "): ", 1, MAX);
        Graph graph = new Graph(false);
        for (int i = 0; i < n; i++) {
            int id = askValue("Identificador único del vértice " + (i + 1) + ": ");
            while (graph.hasVertex(id)) {
                System.out.println("Ese identificador ya existe.");
                id = askValue("Escribe otro identificador: ");
            }
            graph.addVertex(id);
        }
        int edges = askInt("Cantidad de aristas (0-" + MAX + "): ", 0, MAX);
        for (int i = 0; i < edges; i++) addEdgePrompt(graph, i + 1);
        showGraph(graph);
        while (true) {
            System.out.println("\n1 Agregar vértice  2 Agregar arista  3 Consultar vecinos");
            System.out.println("4 Recorrido BFS  5 Recorrido DFS  0 Menú");
            int op = askInt("Opción: ", 0, 5);
            if (op == 0) return;
            try {
                if (op == 1) {
                    int v = askValue("Nuevo identificador: ");
                    graph.addVertex(v);
                    System.out.println("Vértice agregado.");
                }
                if (op == 2) addEdgePrompt(graph, graph.edges().size() + 1);
                if (op == 3) {
                    int v = askValue("Vértice: ");
                    System.out.println("Vecinos de " + v + ": " + graph.neighbors(v));
                }
                if (op == 4 || op == 5) {
                    int start = askValue("Vértice inicial: ");
                    List<Integer> visited = op == 4 ? graph.bfs(start) : graph.dfs(start);                    System.out.println((op == 4 ? "BFS" : "DFS") + " desde " + start + ": " + visited);
                    List<Integer> unreachable = new ArrayList<>(graph.vertices());
                    unreachable.removeAll(visited);
                    System.out.println("No alcanzables desde " + start + ": " + (unreachable.isEmpty() ? "ninguno" : unreachable));
                }
            } catch (IllegalArgumentException ex) { System.out.println(ex.getMessage()); }
            showGraph(graph);
        }
    }

    private static void addEdgePrompt(Graph graph, int number) {
        while (true) {
            int from = askValue("Arista " + number + " · desde: ");
            int to = askValue("hasta: ");
            try {
                graph.addEdge(from, to);
                return;
            } catch (IllegalArgumentException ex) { System.out.println(ex.getMessage() + " Intenta de nuevo."); }
        }
    }

    private static void showGraph(Graph graph) {
        System.out.println("GRAFO NO DIRIGIDO · lista de adyacencia · no permite lazos");
        for (int v : graph.vertices()) System.out.println(v + " → " + graph.neighbors(v));
    }

    private static int askValue(String prompt) {
        return askInt(prompt + "(-9999 a 9999): ", -9999, 9999);
    }

    private static int askInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String text = IN.nextLine().trim();
            try {
                int value = Integer.parseInt(text);
                if (value < min || value > max) {
                    System.out.println("Ingresa un entero entre " + min + " y " + max + ".");
                    continue;
                }
                return value;
            } catch (NumberFormatException ex) {
                System.out.println("Entrada inválida. Escribe un número entero y vuelve a intentar.");
            }
        }    }
}

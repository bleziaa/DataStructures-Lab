package com.datastructureslab.structures;

import java.util.ArrayList;
import java.util.List;

/**
 * Árbol binario de búsqueda real.
 * Regla de comparación: menores a la izquierda, mayores a la derecha.
 * Regla de duplicados: NO se permiten; insertar un valor existente se rechaza.
 */
public class BinarySearchTree {

    public static final class Node {
        private final int value;
        private Node left;
        private Node right;

        private Node(int value) { this.value = value; }

        public int getValue() { return value; }

        public Node getLeft() { return left; }

        public Node getRight() { return right; }
    }

    private Node root;
    private int size;

    public Node getRoot() { return root; }

    public int size() { return size; }

    public boolean isEmpty() { return root == null; }

    /** Inserta el valor. Devuelve false si ya existía (duplicado rechazado). */
    public boolean insert(int value) {
        if (root == null) {
            root = new Node(value);
            size++;
            return true;
        }
        Node cur = root;
        while (true) {
            if (value == cur.value) return false;
            if (value < cur.value) {
                if (cur.left == null) { cur.left = new Node(value); break; }
                cur = cur.left;
            } else {
                if (cur.right == null) { cur.right = new Node(value); break; }
                cur = cur.right;
            }
        }
        size++;
        return true;
    }

    /**
     * Valores comparados desde la raíz. Si el valor existe, el último elemento
     * del camino es el propio valor; si no existe, es el último nodo visitado.
     */
    public List<Integer> searchPath(int value) {
        List<Integer> path = new ArrayList<>();
        Node cur = root;
        while (cur != null) {
            path.add(cur.value);
            if (value == cur.value) break;
            cur = value < cur.value ? cur.left : cur.right;
        }
        return path;
    }

    public boolean contains(int value) {
        List<Integer> path = searchPath(value);
        return !path.isEmpty() && path.get(path.size() - 1) == value;
    }

    /** Elimina el valor. Devuelve false si no existe. */
    public boolean delete(int value) {
        if (root == null) {
            throw new EmptyStructureException("El árbol está vacío: no hay nada que eliminar.");
        }
        if (!contains(value)) return false;
        root = delete(root, value);
        size--;
        return true;
    }

    private Node delete(Node node, int value) {
        if (node == null) return null;
        if (value < node.value) {
            node.left = delete(node.left, value);
        } else if (value > node.value) {
            node.right = delete(node.right, value);
        } else {
            if (node.left == null) return node.right;
            if (node.right == null) return node.left;
            Node successor = node.right;
            while (successor.left != null) successor = successor.left;
            Node replacement = new Node(successor.value);
            replacement.right = delete(node.right, successor.value);
            replacement.left = node.left;
            return replacement;
        }
        return node;
    }

    public List<Integer> inorder() {
        List<Integer> out = new ArrayList<>();
        inorder(root, out);
        return out;
    }

    private void inorder(Node n, List<Integer> out) {
        if (n == null) return;
        inorder(n.left, out);
        out.add(n.value);
        inorder(n.right, out);
    }

    public List<Integer> preorder() {
        List<Integer> out = new ArrayList<>();
        preorder(root, out);
        return out;
    }

    private void preorder(Node n, List<Integer> out) {
        if (n == null) return;
        out.add(n.value);
        preorder(n.left, out);
        preorder(n.right, out);
    }

    public List<Integer> postorder() {
        List<Integer> out = new ArrayList<>();
        postorder(root, out);
        return out;
    }

    private void postorder(Node n, List<Integer> out) {
        if (n == null) return;
        postorder(n.left, out);
        postorder(n.right, out);
        out.add(n.value);
    }

    /** Altura en niveles (árbol vacío = 0, solo raíz = 1). */
    public int height() { return height(root); }

    private int height(Node n) {
        return n == null ? 0 : 1 + Math.max(height(n.left), height(n.right));
    }

    public void clear() {
        root = null;
        size = 0;
    }
}

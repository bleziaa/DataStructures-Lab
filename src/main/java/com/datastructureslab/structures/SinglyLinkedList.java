package com.datastructureslab.structures;

import java.util.ArrayList;
import java.util.List;

/** Lista simplemente enlazada real (nodos y referencias). Admite valores repetidos. */
public class SinglyLinkedList {

    public static final class Node {
        private final int value;
        private Node next;

        private Node(int value) { this.value = value; }

        public int getValue() { return value; }

        public Node getNext() { return next; }
    }

    private Node head;
    private Node tail;
    private int size;

    public int size() { return size; }

    public boolean isEmpty() { return size == 0; }

    public Node getHead() { return head; }

    public void insertFirst(int value) {
        Node node = new Node(value);
        node.next = head;
        head = node;
        if (tail == null) tail = node;
        size++;
    }

    public void insertLast(int value) {
        Node node = new Node(value);
        if (tail == null) {
            head = node;
        } else {
            tail.next = node;
        }
        tail = node;
        size++;
    }

    /** Posición de la primera aparición del valor, o -1 si no existe. */
    public int indexOf(int value) {
        int i = 0;
        for (Node cur = head; cur != null; cur = cur.next, i++) {
            if (cur.value == value) return i;
        }
        return -1;
    }

    /** Elimina la primera aparición del valor. Devuelve su posición original o -1 si no existe. */
    public int remove(int value) {
        if (head == null) {
            throw new EmptyStructureException("La lista está vacía: no hay nada que eliminar.");
        }
        Node prev = null;
        Node cur = head;
        int i = 0;
        while (cur != null && cur.value != value) {
            prev = cur;
            cur = cur.next;
            i++;
        }
        if (cur == null) return -1;
        if (prev == null) {
            head = cur.next;
        } else {
            prev.next = cur.next;
        }
        if (cur == tail) tail = prev;
        size--;
        return i;
    }

    public List<Integer> toList() {
        List<Integer> list = new ArrayList<>();
        for (Node cur = head; cur != null; cur = cur.next) list.add(cur.value);
        return list;
    }

    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }
}

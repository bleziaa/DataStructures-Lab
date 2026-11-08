package com.datastructureslab.structures;

import java.util.ArrayList;
import java.util.List;

/** Pila (LIFO) implementada con nodos enlazados. */
public class LinkedStack {

    private static final class Node {
        final int value;
        final Node below;

        Node(int value, Node below) {
            this.value = value;
            this.below = below;
        }
    }

    private Node top;
    private int size;

    public void push(int value) {
        top = new Node(value, top);
        size++;
    }

    public int pop() {
        if (top == null) {
            throw new EmptyStructureException("La pila está vacía: no se puede hacer POP.");
        }
        int value = top.value;
        top = top.below;
        size--;
        return value;
    }

    public int peek() {
        if (top == null) {
            throw new EmptyStructureException("La pila está vacía: no hay elemento superior.");
        }
        return top.value;
    }

    public int size() { return size; }

    public boolean isEmpty() { return size == 0; }

    public void clear() {
        top = null;
        size = 0;
    }

    /** Elementos desde el TOP hacia el fondo. */
    public List<Integer> toList() {
        List<Integer> list = new ArrayList<>();
        for (Node cur = top; cur != null; cur = cur.below) list.add(cur.value);
        return list;
    }
}

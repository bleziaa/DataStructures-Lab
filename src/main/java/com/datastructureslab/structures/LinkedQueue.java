package com.datastructureslab.structures;

import java.util.ArrayList;
import java.util.List;

/** Cola (FIFO) implementada con nodos enlazados. */
public class LinkedQueue {

    private static final class Node {
        final int value;
        Node next;

        Node(int value) { this.value = value; }
    }

    private Node front;
    private Node rear;
    private int size;

    public void enqueue(int value) {
        Node node = new Node(value);
        if (rear == null) {
            front = node;
        } else {
            rear.next = node;
        }
        rear = node;
        size++;
    }

    public int dequeue() {
        if (front == null) {
            throw new EmptyStructureException("La cola está vacía: no se puede hacer DEQUEUE.");
        }
        int value = front.value;
        front = front.next;
        if (front == null) rear = null;
        size--;
        return value;
    }

    public int peek() {
        if (front == null) {
            throw new EmptyStructureException("La cola está vacía: no hay elemento al frente.");
        }
        return front.value;
    }

    public int rear() {
        if (rear == null) {
            throw new EmptyStructureException("La cola está vacía: no hay elemento al final.");
        }
        return rear.value;
    }

    public int size() { return size; }

    public boolean isEmpty() { return size == 0; }

    public void clear() {
        front = null;
        rear = null;
        size = 0;
    }

    /** Elementos desde el frente hacia el final. */
    public List<Integer> toList() {
        List<Integer> list = new ArrayList<>();
        for (Node cur = front; cur != null; cur = cur.next) list.add(cur.value);
        return list;
    }
}

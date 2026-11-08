package com.datastructureslab.structures;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class LinearStructuresTest {

    @Test
    void listInsertFirstAndLast() {
        SinglyLinkedList l = new SinglyLinkedList();
        assertTrue(l.isEmpty());
        l.insertLast(2);
        l.insertLast(3);
        l.insertFirst(1);
        assertEquals(List.of(1, 2, 3), l.toList());
        assertEquals(3, l.size());
        assertEquals(1, l.getHead().getValue());
        assertEquals(2, l.getHead().getNext().getValue());
    }

    @Test
    void listSearchAndRemove() {
        SinglyLinkedList l = new SinglyLinkedList();
        for (int v : new int[]{5, 6, 7, 8}) l.insertLast(v);
        assertEquals(2, l.indexOf(7));
        assertEquals(-1, l.indexOf(99));
        assertEquals(0, l.remove(5));      // cabeza
        assertEquals(2, l.remove(8));      // cola
        assertEquals(-1, l.remove(42));    // inexistente
        assertEquals(List.of(6, 7), l.toList());
        l.insertLast(9);                   // la cola debe seguir bien enlazada
        assertEquals(List.of(6, 7, 9), l.toList());
    }

    @Test
    void listRemoveOnEmptyThrowsAndSingleElement() {
        SinglyLinkedList l = new SinglyLinkedList();
        assertThrows(EmptyStructureException.class, () -> l.remove(1));
        l.insertFirst(1);
        assertEquals(0, l.remove(1));
        assertTrue(l.isEmpty());
        l.insertLast(4);
        assertEquals(List.of(4), l.toList());
    }

    @Test
    void stackIsLifo() {
        LinkedStack s = new LinkedStack();
        s.push(1); s.push(2); s.push(3);
        assertEquals(3, s.peek());
        assertEquals(3, s.size());
        assertEquals(List.of(3, 2, 1), s.toList());
        assertEquals(3, s.pop());
        assertEquals(2, s.pop());
        assertEquals(1, s.pop());
        assertTrue(s.isEmpty());
    }

    @Test
    void stackEmptyOperationsThrow() {
        LinkedStack s = new LinkedStack();
        assertThrows(EmptyStructureException.class, s::pop);
        assertThrows(EmptyStructureException.class, s::peek);
        s.push(1);
        s.clear();
        assertEquals(0, s.size());
    }

    @Test
    void queueIsFifo() {
        LinkedQueue q = new LinkedQueue();
        q.enqueue(1); q.enqueue(2); q.enqueue(3);
        assertEquals(1, q.peek());
        assertEquals(3, q.rear());
        assertEquals(List.of(1, 2, 3), q.toList());
        assertEquals(1, q.dequeue());
        assertEquals(2, q.dequeue());
        assertEquals(3, q.dequeue());
        assertTrue(q.isEmpty());
    }

    @Test
    void queueEmptyOperationsThrowAndRecovers() {
        LinkedQueue q = new LinkedQueue();
        assertThrows(EmptyStructureException.class, q::dequeue);
        assertThrows(EmptyStructureException.class, q::peek);
        assertThrows(EmptyStructureException.class, q::rear);
        q.enqueue(1);
        q.dequeue();
        q.enqueue(2);                      // tras vaciarse debe seguir funcionando
        assertEquals(2, q.peek());
        assertEquals(2, q.rear());
        q.clear();
        assertEquals(0, q.size());
    }
}

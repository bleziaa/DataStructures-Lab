package com.datastructureslab.structures;

import static org.junit.jupiter.api.Assertions.*;

import com.datastructureslab.models.TraversalStep;
import java.util.List;
import org.junit.jupiter.api.Test;

class TreeAndGraphTest {

    private BinarySearchTree sample() {
        BinarySearchTree t = new BinarySearchTree();
        for (int v : new int[]{50, 30, 70, 20, 40, 60, 80}) t.insert(v);
        return t;
    }

    @Test
    void treeTraversals() {
        BinarySearchTree t = sample();
        assertEquals(List.of(20, 30, 40, 50, 60, 70, 80), t.inorder());
        assertEquals(List.of(50, 30, 20, 40, 70, 60, 80), t.preorder());
        assertEquals(List.of(20, 40, 30, 60, 80, 70, 50), t.postorder());
        assertEquals(3, t.height());
        assertEquals(7, t.size());
    }

    @Test
    void treeRejectsDuplicates() {
        BinarySearchTree t = sample();
        assertFalse(t.insert(40));
        assertEquals(7, t.size());
    }

    @Test
    void treeSearchPath() {
        BinarySearchTree t = sample();
        assertEquals(List.of(50, 30, 40), t.searchPath(40));
        assertTrue(t.contains(40));
        assertEquals(List.of(50, 70, 80), t.searchPath(90));
        assertFalse(t.contains(90));
    }

    @Test
    void treeDeleteLeafOneChildTwoChildrenAndRoot() {
        BinarySearchTree t = sample();
        assertTrue(t.delete(20));                       // hoja
        assertTrue(t.delete(30));                       // un hijo
        assertEquals(List.of(40, 50, 60, 70, 80), t.inorder());
        assertTrue(t.delete(70));                       // dos hijos
        assertEquals(List.of(40, 50, 60, 80), t.inorder());
        assertTrue(t.delete(50));                       // raíz
        assertEquals(List.of(40, 60, 80), t.inorder());
        assertFalse(t.delete(999));                     // inexistente
    }

    @Test
    void treeEmptyAndSingleNode() {
        BinarySearchTree t = new BinarySearchTree();
        assertThrows(EmptyStructureException.class, () -> t.delete(1));
        assertTrue(t.searchPath(1).isEmpty());
        assertEquals(0, t.height());
        t.insert(5);
        assertEquals(1, t.height());
        assertTrue(t.delete(5));
        assertTrue(t.isEmpty());
    }

    @Test
    void treeSkewedDeepLevels() {
        BinarySearchTree t = new BinarySearchTree();
        for (int i = 1; i <= 6; i++) t.insert(i);
        assertEquals(6, t.height());
        assertEquals(List.of(1, 2, 3, 4, 5, 6), t.preorder());
    }

    private Graph sampleGraph(boolean directed) {
        Graph g = new Graph(directed);
        for (int v = 1; v <= 6; v++) g.addVertex(v);
        g.addEdge(1, 2);
        g.addEdge(1, 3);
        g.addEdge(2, 4);
        g.addEdge(3, 4);
        return g;                                       // 5 y 6 quedan desconectados
    }

    @Test
    void graphBfsAndDfsUndirected() {
        Graph g = sampleGraph(false);
        assertEquals(List.of(1, 2, 3, 4), g.bfs(1));
        assertEquals(List.of(1, 2, 4, 3), g.dfs(1));
        assertEquals(List.of(5), g.bfs(5));             // grafo desconectado
        assertEquals(List.of(1, 4), g.neighbors(2));
    }

    @Test
    void graphDirectedRespectsDirection() {
        Graph g = sampleGraph(true);
        assertEquals(List.of(4), g.bfs(4));
        assertEquals(List.of(4), g.neighbors(2).stream().filter(n -> n == 4).toList());
        assertTrue(g.neighbors(4).isEmpty());
        assertEquals(4, g.edges().size());
    }

    @Test
    void graphValidations() {
        Graph g = sampleGraph(false);
        assertThrows(IllegalArgumentException.class, () -> g.addVertex(1));
        assertThrows(IllegalArgumentException.class, () -> g.addEdge(1, 99));
        assertThrows(IllegalArgumentException.class, () -> g.addEdge(1, 2));
        assertThrows(IllegalArgumentException.class, () -> g.addEdge(2, 1));
        assertThrows(IllegalArgumentException.class, () -> g.addEdge(3, 3));
        assertThrows(IllegalArgumentException.class, () -> g.neighbors(99));
        assertThrows(IllegalArgumentException.class, () -> g.bfs(99));
        assertEquals(4, g.edges().size());
    }

    @Test
    void graphTraversalStepsReportPending() {
        Graph g = sampleGraph(false);
        List<TraversalStep> steps = g.bfsSteps(1);
        assertEquals(List.of(2, 3), steps.get(0).pending());
        assertEquals(List.of(1), steps.get(0).visited());
        List<TraversalStep> dfs = g.dfsSteps(1);
        assertEquals(List.of(2, 3), dfs.get(0).pending());
        assertTrue(dfs.get(dfs.size() - 1).pending().isEmpty());
    }

    @Test
    void emptyGraph() {
        Graph g = new Graph(false);
        assertEquals(0, g.vertexCount());
        assertThrows(IllegalArgumentException.class, () -> g.bfs(1));
    }
}

package com.datastructureslab.structures;

import com.datastructureslab.models.TraversalStep;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Grafo con listas de adyacencia, dirigido o no dirigido.
 * Reglas: vértices únicos, no se permiten aristas duplicadas ni lazos (a -> a).
 */
public class Graph {
    private final Map<Integer, List<Integer>> adjacency = new LinkedHashMap<>();
    private final boolean directed;

    public Graph(boolean directed) {
        this.directed = directed;
    }

    public boolean isDirected() { return directed; }

    public int vertexCount() { return adjacency.size(); }

    public boolean hasVertex(int v) { return adjacency.containsKey(v); }

    public void addVertex(int v) {
        if (adjacency.containsKey(v)) {
            throw new IllegalArgumentException("El vértice " + v + " ya existe.");
        }
        adjacency.put(v, new ArrayList<>());
    }

    public boolean hasEdge(int from, int to) {
        return hasVertex(from) && adjacency.get(from).contains(to);
    }

    public void addEdge(int from, int to) {
        if (!hasVertex(from) || !hasVertex(to)) {
            int missing = !hasVertex(from) ? from : to;
            throw new IllegalArgumentException("El vértice " + missing + " no existe. Créalo antes de conectarlo.");
        }
        if (from == to) {
            throw new IllegalArgumentException("No se permiten conexiones de un vértice consigo mismo.");
        }
        if (hasEdge(from, to) || (!directed && hasEdge(to, from))) {
            throw new IllegalArgumentException("La conexión " + from + (directed ? " → " : " — ") + to + " ya existe.");
        }
        adjacency.get(from).add(to);
        if (!directed) adjacency.get(to).add(from);
    }

    public List<Integer> neighbors(int v) {
        if (!hasVertex(v)) {
            throw new IllegalArgumentException("El vértice " + v + " no existe.");
        }
        return new ArrayList<>(adjacency.get(v));
    }

    public List<Integer> vertices() { return new ArrayList<>(adjacency.keySet()); }

    /** Aristas únicas; en no dirigido cada par aparece una sola vez. */
    public List<int[]> edges() {
        List<int[]> result = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (var entry : adjacency.entrySet()) {
            for (int to : entry.getValue()) {
                int from = entry.getKey();
                String key = directed ? from + ">" + to : Math.min(from, to) + "-" + Math.max(from, to);
                if (seen.add(key)) result.add(new int[]{from, to});
            }
        }
        return result;
    }

    public List<TraversalStep> bfsSteps(int start) {
        requireVertex(start);
        List<TraversalStep> steps = new ArrayList<>();
        List<Integer> visited = new ArrayList<>();
        Set<Integer> discovered = new HashSet<>();
        Deque<Integer> queue = new ArrayDeque<>();
        queue.add(start);
        discovered.add(start);
        while (!queue.isEmpty()) {
            int v = queue.poll();
            visited.add(v);
            for (int n : adjacency.get(v)) {
                if (discovered.add(n)) queue.add(n);
            }
            steps.add(new TraversalStep(v, new ArrayList<>(visited), new ArrayList<>(queue)));
        }
        return steps;
    }

    public List<TraversalStep> dfsSteps(int start) {
        requireVertex(start);
        List<TraversalStep> steps = new ArrayList<>();
        List<Integer> visited = new ArrayList<>();
        Set<Integer> done = new HashSet<>();
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(start);
        while (!stack.isEmpty()) {
            int v = stack.pop();
            if (!done.add(v)) continue;
            visited.add(v);
            List<Integer> neighbors = adjacency.get(v);
            for (int i = neighbors.size() - 1; i >= 0; i--) {
                if (!done.contains(neighbors.get(i))) stack.push(neighbors.get(i));
            }
            Set<Integer> pending = new LinkedHashSet<>();
            for (int p : stack) if (!done.contains(p)) pending.add(p);
            steps.add(new TraversalStep(v, new ArrayList<>(visited), new ArrayList<>(pending)));
        }
        return steps;
    }

    public List<Integer> bfs(int start) {
        return bfsSteps(start).stream().map(TraversalStep::current).toList();
    }

    public List<Integer> dfs(int start) {
        return dfsSteps(start).stream().map(TraversalStep::current).toList();
    }

    private void requireVertex(int v) {
        if (!hasVertex(v)) {
            throw new IllegalArgumentException("El vértice " + v + " no existe.");
        }
    }

    public void clear() { adjacency.clear(); }
}

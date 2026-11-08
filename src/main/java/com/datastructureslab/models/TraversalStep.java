package com.datastructureslab.models;

import java.util.List;

/**
 * Un paso de un recorrido de grafo: vértice que se visita ahora,
 * vértices ya visitados (incluye el actual) y vértices pendientes en cola/pila.
 */
public record TraversalStep(int current, List<Integer> visited, List<Integer> pending) {
}

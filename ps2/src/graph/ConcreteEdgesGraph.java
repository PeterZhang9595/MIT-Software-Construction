/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package graph;

import java.util.*;
import java.util.stream.Collectors;

/**
 * An implementation of Graph.
 * 
 * <p>PS2 instructions: you MUST use the provided rep.
 */
public class ConcreteEdgesGraph<L> implements Graph<L> {
    
    private final Set<L> vertices = new HashSet<>();
    private final List<Edge<L>> edges = new ArrayList<>();
    
    // Abstraction function:
    //   TODO Set<String> vertices + List<Edge> edges -> graph with vertices and edges in the list, in short 'g'
    // Representation invariant:
    //   TODO 1. All the edges in the list should have their source and target in the vertices set; 2. No duplicate edge (same source and target) in the list;
    // Safety from rep exposure:
    //   TODO 1. private final constraint 2. return a defensive copy of vertices when calling vertices()
    
    // TODO constructor
    public ConcreteEdgesGraph() {

    }
    // TODO checkRep
    private void checkRep()
    {
        for (Edge<L> e : edges) {
            assert vertices.contains(e.getSource());
            assert vertices.contains(e.getTarget());
        }
        for (int i = 0; i < edges.size(); i++) {
            for (int j = i + 1; j < edges.size(); j++) {
                Edge<L> e1 = edges.get(i);
                Edge<L> e2 = edges.get(j);
                assert !(e1.getSource().equals(e2.getSource()) && e1.getTarget().equals(e2.getTarget()));
            }
        }
    }

    @Override public boolean add(L vertex) {
        boolean added = !vertices.contains(vertex);
        vertices.add(vertex);
        checkRep();
        return added;
    }
    
    @Override public int set(L source, L target, int weight) {
        if (weight < 0) {
            throw new IllegalArgumentException("weight should be non-negative");
        }
        
        int prevWeight = 0;
        
        // Find and remove existing edge, capture its weight
        java.util.Iterator<Edge<L>> iterator = edges.iterator();
        while (iterator.hasNext()) {
            Edge<L> e = iterator.next();
            if (e.getSource().equals(source) && e.getTarget().equals(target)) {
                prevWeight = e.getWeight();
                iterator.remove();
                break;
            }
        }
        
        // If weight > 0, add new edge
        if (weight > 0) {
            vertices.add(source);
            vertices.add(target);
            edges.add(new Edge<>(source, target, weight));
        }
        
        checkRep();
        return prevWeight;
    }
    
    @Override public boolean remove(L vertex) {
        boolean removed = vertices.contains(vertex);
        vertices.remove(vertex);
        edges.removeIf(e -> e.getSource().equals(vertex) || e.getTarget().equals(vertex));
        checkRep();
        return removed;
    }
    
    @Override public Set<L> vertices() {
        return new HashSet<>(vertices);
    }
    
    @Override public Map<L, Integer> sources(L target) {
        Map<L, Integer> sources = new HashMap<>();
        for (Edge<L> e : edges) {
            if (e.getTarget().equals(target)) {
                sources.put(e.getSource(), e.getWeight());
            }
        }
        return Collections.unmodifiableMap(sources);
    }
    
    @Override public Map<L, Integer> targets(L source) {
        Map<L, Integer> targets = new HashMap<>();
        for (Edge<L> e : edges) {
            if (e.getSource().equals(source)) {
                targets.put(e.getTarget(), e.getWeight());
            }
        }
        return Collections.unmodifiableMap(targets);
    }
    
    // TODO toString()
    @Override
    public String toString() {
        if (vertices.isEmpty()) {
            return "Empty Graph";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Graph Structure:\n");

        for (L vertex : vertices) { 
            sb.append("  ").append(vertex).append(": ");

            // 找到从该顶点出发的所有边
            Map<L, Integer> targets = targets(vertex);

            if (targets.isEmpty()) {
                sb.append("(no outgoing edges)");
            } else {
                // 拼接每一条边
                String edgesString = targets.entrySet().stream()
                        .map(e -> "-> " + e.getKey() + " (" + e.getValue() + ")")
                        .collect(Collectors.joining(", "));
                sb.append(edgesString);
            }
            sb.append("\n");
        }
        return sb.toString();
    }
    
}

/**
 * TODO specification
 * Immutable.
 * This class is internal to the rep of ConcreteEdgesGraph.
 * 
 * <p>PS2 instructions: the specification and implementation of this class is
 * up to you.
 */
class Edge<L> {

    // TODO fields
    private final L source;
    private final L target;
    private final int weight;
    // Abstraction function:
    //   TODO String target + String source + int weight -> edge from source to target with weight, in short 'e'
    // Representation invariant:
    //   TODO the source and target should be non-null and distinct; weight should be positive integer
    // Safety from rep exposure:
    //   TODO 1. private+final constraint on 3 fields; 2. String and integer class are both immutable 3. No mutator methods

    
    // TODO constructor
    public Edge(L source, L target, int weight) {
        this.source = source;
        this.target = target;
        this.weight = weight;
        checkRep();
    }
    // TODO checkRep
    private void checkRep()
    {
        assert source != null;
        assert target != null;
        assert weight > 0;
        assert !source.equals(target);
    }
    
    // TODO methods
    public L getSource() { return source; }
    public L getTarget() { return target; }
    public int getWeight() { return weight; }
    
    // TODO toString()
    @Override
    public String toString() {
        checkRep();
        return source + " -> " + target + " (" + weight + ")";
    }
}

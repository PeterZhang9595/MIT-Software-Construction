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
public class ConcreteVerticesGraph<L> implements Graph<L> {
    
    private final List<Vertex<L>> vertices = new ArrayList<>();
    private final Set<L> verticesLabels = new HashSet<>();
    
    // Abstraction function:
    //   TODO The vertex list represents the graph.
    // Representation invariant:
    //   TODO There shouldn't be duplicate vertices;each vertex satisfies its ri
    // Safety from rep exposure:
    //   TODO 1. private final constraint 2. return a defensive copy of vertices when calling vertices()
    
    // TODO constructor
    public ConcreteVerticesGraph() {

    }
    // TODO checkRep
    private void checkRep(){
        Set<L> labelsFromVertices = new HashSet<>();
        for (Vertex<L> v : vertices) labelsFromVertices.add(v.getLabel());
        assert verticesLabels.equals(labelsFromVertices) : "verticesLabels must equal actual vertex labels";
    }

    @Override public boolean add(L vertex) {
        if (verticesLabels.contains(vertex)){
            return false;
        }
        Vertex<L> v = new Vertex<>(vertex);
        verticesLabels.add(vertex);
        vertices.add(v);
        checkRep();
        return true;
    }
    
    @Override public int set(L source, L target, int weight) {
        if(weight<0){
            throw new IllegalArgumentException("Weight must >= 0");
        }
        int prevWeight = 0;
        if (source != null && source.equals(target)) {
            // current Vertex invariant forbids self-loop; disallow here
            if (weight == 0) return 0;
            throw new IllegalArgumentException("Self-loop not allowed");
        }

        // find existing vertices
        Vertex<L> sV = null;
        Vertex<L> tV = null;
        for (Vertex<L> v : vertices) {
            if (v.getLabel().equals(source)) sV = v;
            if (v.getLabel().equals(target)) tV = v;
        }

        // create missing vertices if weight > 0
        if (weight > 0) {
            if (sV == null) {
                sV = new Vertex<>(source);
                vertices.add(sV);
                verticesLabels.add(source);
            }
            if (tV == null) {
                tV = new Vertex<>(target);
                vertices.add(tV);
                verticesLabels.add(target);
            }
            // add/update edge on both ends
            prevWeight = sV.addTarget(target, weight);
            tV.addSource(source, weight);
        } else {
            // weight == 0 : remove edge if present
            if (sV != null) prevWeight = sV.removeTarget(target);
            if (tV != null) tV.removeSource(source);
        }

        checkRep();
        return prevWeight;
    }
    
    @Override public boolean remove(L vertex) {
        if(!verticesLabels.contains(vertex)){
            return false;
        }
        // remove edges referencing this vertex from other vertices
        for (Vertex<L> v : vertices) {
            if (!v.getLabel().equals(vertex)) {
                v.removeTarget(vertex);
                v.removeSource(vertex);
            }
        }
        // remove the vertex object itself
        Iterator<Vertex<L>> iterator = vertices.iterator();
        while (iterator.hasNext()){
            Vertex<L> v = iterator.next();
            if (v.getLabel().equals(vertex)){
                iterator.remove();
                break;
            }
        }
        verticesLabels.remove(vertex);
        checkRep();
        return true;
    }
    
    @Override public Set<L> vertices() {
        return new HashSet<>(verticesLabels);
    }
    
    @Override public Map<L, Integer> sources(L target) {
        for (Vertex<L> v : vertices){
            if (v.getLabel().equals(target)){
                return Collections.unmodifiableMap(v.getSources());
            }
        }
        return Collections.emptyMap();
    }
    
    @Override public Map<L, Integer> targets(L source) {
        for (Vertex<L> v : vertices){
            if (v.getLabel().equals(source)){
                return Collections.unmodifiableMap(v.getTargets());
            }
        }
        return Collections.emptyMap();
    }
    
    // toString(): provide a readable representation of the graph
    @Override public String toString(){
        if (vertices.isEmpty()) {
            return "Empty Graph";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Graph Structure:\n");

        for (Vertex<L> v : vertices) {
            sb.append("  ").append(v.getLabel()).append(": ");

            Map<L, Integer> targets = v.getTargets();
            if (targets.isEmpty()) {
                sb.append("(no outgoing edges)");
            } else {
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
 * Mutable.
 * This class is internal to the rep of ConcreteVerticesGraph.
 * 
 * <p>PS2 instructions: the specification and implementation of this class is
 * up to you.
 */
class Vertex<L> {
    
    // TODO fields
    private final L label;
    private final Map<L, Integer> targets;
    private final Map<L, Integer> sources;
    
    // Abstraction function:
    //   TODO A structure of vertex name, and a map of the target vertex and the weight of the edge to that vertex.
    // Representation invariant:
    //   TODO The map shouldn't contain the vertex itself as a target, and the weight of the edge should be positve
    //   TODO The label should be non-null, no duplicate target vertex in targets.(This is solved by map itself)
    // Safety from rep exposure:
    //   TODO We need to keep each vertex mutable, but we can implement the methods to abstractly make changes to the map.
    //   TODO The label is immutable, so we can safely return it.
    //   TODO Return defensive copy of map when needed.
    
    // TODO constructor
    public Vertex(L label) {
        this.label = label;
        this.targets = new HashMap<>();
        this.sources = new HashMap<>();
        checkRep();
    }
    // TODO checkRep
    private void checkRep(){
        assert this.label != null;
        assert this.targets != null;
        assert this.sources != null;
        assert !this.targets.containsKey(this.label);
        assert !this.sources.containsKey(this.label);
        for (Integer weight : this.targets.values()) {
            assert weight > 0;
        }
        for(Integer weight : this.sources.values()) {
            assert weight > 0;
        }
    }
    // TODO methods
    public L getLabel() {
        return this.label;
    }

    public Map<L, Integer> getTargets() {
        return new HashMap<>(this.targets);
    }
    public Map<L, Integer> getSources() {
        return new HashMap<>(this.sources);
    }
    public int addTarget(L target, int weight) {
        Integer old = this.targets.put(target, weight);
        checkRep();
        return old == null ? 0 : old;
    }
    public int addSource(L source, int weight) {
        Integer old = this.sources.put(source, weight);
        checkRep();
        return old == null ? 0 : old;
    }
    public int removeTarget(L target)
    {
        Integer old = this.targets.remove(target);
        checkRep();
        return old == null ? 0 : old;
    }
    public int removeSource(L source)
    {
        Integer old = this.sources.remove(source);
        checkRep();
        return old == null ? 0 : old;
    }
    // TODO toString()
    @Override public String toString() {
        return String.valueOf(this.label);
    }
}

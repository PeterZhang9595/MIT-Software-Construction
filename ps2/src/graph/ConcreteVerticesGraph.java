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
public class ConcreteVerticesGraph implements Graph<String> {
    
    private final List<Vertex> vertices = new ArrayList<>();
    private final Set<String> verticesLabels = new HashSet<>();
    
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
        assert verticesLabels.size() == vertices.size();
    }

    @Override public boolean add(String vertex) {
        if (verticesLabels.contains(vertex)){
            return false;
        }
        Vertex v = new Vertex(vertex);
        verticesLabels.add(vertex);
        vertices.add(v);
        checkRep();
        return true;
    }
    
    @Override public int set(String source, String target, int weight) {
        if(weight<0){
            throw new IllegalArgumentException("Weight must >= 0");
        }
        int prevWeight = 0;
        if(this.verticesLabels.contains(source)&&this.verticesLabels.contains(target))
        {
            if (weight ==0)
            {
                for(Vertex v : vertices){
                    if (v.getLabel().equals(source)){
                        prevWeight = v.removeTarget(target);
                    }
                    if(v.getLabel().equals(target)){
                        prevWeight = v.removeSource(source);
                    }
                }
            }
            else{
                for(Vertex v : vertices){
                    if (v.getLabel().equals(target)){
                        prevWeight = v.addSource(source,weight);
                    }
                    if(v.getLabel().equals(source)){
                        prevWeight = v.addTarget(target,weight);
                    }
                }
            }
            checkRep();
            return prevWeight;
        }

        if(weight > 0){
            if(!this.verticesLabels.contains(source)){
                Vertex newSource = new Vertex(source);
                newSource.addTarget(target,weight);
                vertices.add(newSource);
                verticesLabels.add(source);
            }
            if(!this.verticesLabels.contains(target)){
                Vertex newTarget = new Vertex(target);
                newTarget.addSource(source,weight);
                vertices.add(newTarget);
                verticesLabels.add(target);
            }
            for(Vertex v : vertices){
                if (v.getLabel().equals(source)){
                    v.addTarget(target,weight);
                }
                else if(v.getLabel().equals(target)){
                    v.addSource(source,weight);
                }
            }
        }
        checkRep();
        return prevWeight;
    }
    
    @Override public boolean remove(String vertex) {
        if(!verticesLabels.contains(vertex)){
            return false;
        }
        verticesLabels.remove(vertex);
        Iterator<Vertex> iterator = vertices.iterator();
        while (iterator.hasNext()){
            Vertex v = iterator.next();
            v.removeSource(vertex);
            v.removeTarget(vertex);
            if (v.getLabel().equals(vertex)){
                iterator.remove();
            }
        }
        checkRep();
        return true;
    }
    
    @Override public Set<String> vertices() {
        return new HashSet<>(verticesLabels);
    }
    
    @Override public Map<String, Integer> sources(String target) {
        for (Vertex v : vertices){
            if (v.getLabel().equals(target)){
                return Collections.unmodifiableMap(v.getSources());
            }
        }
        return Collections.emptyMap();
    }
    
    @Override public Map<String, Integer> targets(String source) {
        for (Vertex v : vertices){
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

        for (Vertex v : vertices) {
            sb.append("  ").append(v.getLabel()).append(": ");

            Map<String, Integer> targets = v.getTargets();
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
class Vertex {
    
    // TODO fields
    private final String label;
    private final Map<String, Integer> targets;
    private final Map<String, Integer> sources;
    
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
    public Vertex(String label) {
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
    public String getLabel() {
        return this.label;
    }

    public Map<String, Integer> getTargets() {
        return new HashMap<>(this.targets);
    }
    public Map<String, Integer> getSources() {
        return new HashMap<>(this.sources);
    }
    public int addTarget(String target, int weight) {
        Integer old = this.targets.put(target, weight);
        checkRep();
        return old == null ? 0 : old;
    }
    public int addSource(String source, int weight) {
        Integer old = this.sources.put(source, weight);
        checkRep();
        return old == null ? 0 : old;
    }
    public int removeTarget(String target)
    {
        Integer old = this.targets.remove(target);
        checkRep();
        return old == null ? 0 : old;
    }
    public int removeSource(String source)
    {
        Integer old = this.sources.remove(source);
        checkRep();
        return old == null ? 0 : old;
    }
    // TODO toString()
    @Override public String toString() {
        return this.label;
    }
}

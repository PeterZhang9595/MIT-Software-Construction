/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package graph;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests for ConcreteVerticesGraph.
 * 
 * This class runs the GraphInstanceTest tests against ConcreteVerticesGraph, as
 * well as tests for that particular implementation.
 * 
 * Tests against the Graph spec should be in GraphInstanceTest.
 */
public class ConcreteVerticesGraphTest extends GraphInstanceTest {
    
    /*
     * Provide a ConcreteVerticesGraph for tests in GraphInstanceTest.
     */
    @Override public Graph<String> emptyInstance() {
        return new ConcreteVerticesGraph();
    }
    
    /*
     * Testing ConcreteVerticesGraph...
     */
    
    // Testing strategy for ConcreteVerticesGraph.toString()
    //   1. empty graph - returns "Empty Graph"
    //   2. single vertex with no outgoing edges - returns vertex label with "(no outgoing edges)"
    //   3. single vertex with one target - returns vertex label with "-> target (weight)"
    //   4. multiple vertices with various edges - returns all vertices with their outgoing edges listed
    //   5. check format consistency (uses Collectors.joining with ", " separator)
    
    @Test
    public void testToString_EmptyGraph() {
        Graph<String> g = emptyInstance();
        String result = g.toString();
        assertEquals("Empty Graph", result);
    }
    
    @Test
    public void testToString_SingleVertexNoEdges() {
        Graph<String> g = emptyInstance();
        g.add("A");
        String result = g.toString();
        assertTrue(result.contains("A:"));
        assertTrue(result.contains("(no outgoing edges)"));
    }
    
    @Test
    public void testToString_SingleVertexWithOneEdge() {
        Graph<String> g = emptyInstance();
        g.set("A", "B", 5);
        String result = g.toString();
        assertTrue(result.contains("A:"));
        assertTrue(result.contains("-> B (5)"));
    }
    
    @Test
    public void testToString_MultipleVerticesWithEdges() {
        Graph<String> g = emptyInstance();
        g.set("X", "Y", 3);
        g.set("X", "Z", 2);
        g.add("W");
        String result = g.toString();
        // Check structure format
        assertTrue(result.contains("Graph Structure:"));
        // Check X has two outgoing edges
        assertTrue(result.contains("X:"));
        assertTrue(result.contains("-> Y (3)"));
        assertTrue(result.contains("-> Z (2)"));
        // Check W has no outgoing edges
        assertTrue(result.contains("W:"));
        assertTrue(result.contains("(no outgoing edges)"));
    }
    
    @Test
    public void testToString_ContainsAllVertices() {
        Graph<String> g = emptyInstance();
        g.add("V1");
        g.add("V2");
        g.set("V2", "V1", 1);
        String result = g.toString();
        assertTrue("should contain V1", result.contains("V1"));
        assertTrue("should contain V2", result.contains("V2"));
    }
    
    /*
     * Testing Vertex (internal class)...
     */
    
    // Testing strategy for Vertex
    //   - getLabel(): returns the label set in constructor
    //   - getTargets(): returns a defensive copy of targets map
    //   - getSources(): returns a defensive copy of sources map
    //   - addTarget(target, weight): adds or updates a target, returns old weight (0 if not existed)
    //   - addSources(source, weight): adds or updates a source, returns old weight (0 if not existed)
    //   - toString(): returns the label
    //   - representation invariant: no self-loops, positive weights, non-null label
    
    @Test
    public void testVertexGetLabel() {
        // Test through ConcreteVerticesGraph since Vertex is package-private
        Graph<String> g = emptyInstance();
        g.add("TestVertex");
        // If toString works, the label is stored correctly
        String result = g.toString();
        assertTrue(result.contains("TestVertex"));
    }
    
    @Test
    public void testVertexAddTargetNewTarget() {
        Graph<String> g = emptyInstance();
        g.set("A", "B", 10);
        // Verify B is in targets of A with weight 10
        assertEquals(10, (int) g.targets("A").get("B"));
    }
    
    @Test
    public void testVertexAddTargetUpdateExisting() {
        Graph<String> g = emptyInstance();
        g.set("A", "B", 5);
        int oldWeight = g.set("A", "B", 15);
        // set() should use addTarget internally and return old weight
        assertEquals(5, oldWeight);
        assertEquals(15, (int) g.targets("A").get("B"));
    }
    
    @Test
    public void testVertexGetTargetsDefensiveCopy() {
        Graph<String> g = emptyInstance();
        g.set("P", "Q", 3);
        java.util.Map<String, Integer> targets1 = g.targets("P");
        java.util.Map<String, Integer> targets2 = g.targets("P");
        // Should be equal but different objects
        assertEquals(targets1, targets2);
        // Verify immutability or modification doesn't affect graph
        try {
            targets1.put("R", 99);
            // If we get here, check that graph wasn't modified
            assertFalse("modifying returned targets should not affect graph", 
                    g.targets("P").containsKey("R"));
        } catch (UnsupportedOperationException e) {
            // Acceptable: returned map is unmodifiable
        }
    }
    
    @Test
    public void testVertexGetSourcesDefensiveCopy() {
        Graph<String> g = emptyInstance();
        g.set("P", "Q", 3);
        java.util.Map<String, Integer> sources1 = g.sources("Q");
        java.util.Map<String, Integer> sources2 = g.sources("Q");
        // Should be equal but different objects
        assertEquals(sources1, sources2);
        // Verify immutability or modification doesn't affect graph
        try {
            sources1.put("R", 99);
            assertFalse("modifying returned sources should not affect graph", 
                    g.sources("Q").containsKey("R"));
        } catch (UnsupportedOperationException e) {
            // Acceptable: returned map is unmodifiable
        }
    }
    
    @Test
    public void testVertexMultipleTargetsAndSources() {
        Graph<String> g = emptyInstance();
        // Create a vertex with multiple incoming and outgoing edges
        g.set("A", "B", 1);
        g.set("A", "C", 2);
        g.set("X", "A", 10);
        g.set("Y", "A", 20);
        
        // Check targets of A
        java.util.Map<String, Integer> targets = g.targets("A");
        assertEquals(2, targets.size());
        assertEquals(1, (int) targets.get("B"));
        assertEquals(2, (int) targets.get("C"));
        
        // Check sources of A
        java.util.Map<String, Integer> sources = g.sources("A");
        assertEquals(2, sources.size());
        assertEquals(10, (int) sources.get("X"));
        assertEquals(20, (int) sources.get("Y"));
    }
    
}

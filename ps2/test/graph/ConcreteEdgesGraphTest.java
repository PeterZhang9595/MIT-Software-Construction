/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package graph;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests for ConcreteEdgesGraph.
 * 
 * This class runs the GraphInstanceTest tests against ConcreteEdgesGraph, as
 * well as tests for that particular implementation.
 * 
 * Tests against the Graph spec should be in GraphInstanceTest.
 */
public class ConcreteEdgesGraphTest extends GraphInstanceTest {
    
    /*
     * Provide a ConcreteEdgesGraph for tests in GraphInstanceTest.
     */
    @Override public Graph<String> emptyInstance() {
        return new ConcreteEdgesGraph();
    }
    
    /*
     * Testing ConcreteEdgesGraph...
     */
    
    // Testing strategy for ConcreteEdgesGraph.toString()
    //   1. empty graph - return empty representation
    //   2. graph with vertices only - show vertices
    //   3. graph with vertices and edges - show both vertices and edges
    //   4. graph with multiple edges - show all edges
    
    @Test
    public void testToString_EmptyGraph() {
        Graph<String> g = emptyInstance();
        String str = g.toString();
        assertNotNull(str);
        assertTrue(str.length() > 0);
    }
    
    @Test
    public void testToString_GraphWithVerticesOnly() {
        Graph<String> g = emptyInstance();
        g.add("A");
        g.add("B");
        String str = g.toString();
        assertTrue(str.contains("A") || str.contains("B"));
    }
    
    @Test
    public void testToString_GraphWithEdges() {
        Graph<String> g = emptyInstance();
        g.set("A", "B", 5);
        g.set("B", "C", 3);
        String str = g.toString();
        assertNotNull(str);
        assertTrue(str.length() > 0);
    }
    
    /*
     * Testing Edge...
     */
    
    // Testing strategy for Edge
    //   1. constructor creates edge with correct source, target, weight
    //   2. getSource() returns the source vertex
    //   3. getTarget() returns the target vertex
    //   4. getWeight() returns the weight
    //   5. toString() produces a meaningful string representation
    //   6. immutability: Edge cannot be modified after creation
    
    @Test
    public void testEdgeConstructor() {
        Edge e = new Edge("A", "B", 5);
        assertEquals("A", e.getSource());
        assertEquals("B", e.getTarget());
        assertEquals(5, e.getWeight());
    }
    
    @Test
    public void testEdgeGetters() {
        Edge e = new Edge("X", "Y", 10);
        assertEquals("X", e.getSource());
        assertEquals("Y", e.getTarget());
        assertEquals(10, e.getWeight());
    }
    
    @Test
    public void testEdgeToString() {
        Edge e = new Edge("P", "Q", 7);
        String str = e.toString();
        assertTrue(str.contains("P"));
        assertTrue(str.contains("Q"));
        assertTrue(str.contains("7"));
    }
    
    @Test(expected = AssertionError.class)
    public void testEdgeImmutable_SourceNotNull() {
        new Edge(null, "B", 5);
    }
    
    @Test(expected = AssertionError.class)
    public void testEdgeImmutable_TargetNotNull() {
        new Edge("A", null, 5);
    }
    
    @Test(expected = AssertionError.class)
    public void testEdgeImmutable_SourceAndTargetDistinct() {
        new Edge("A", "A", 5);
    }
    
    @Test(expected = AssertionError.class)
    public void testEdgeImmutable_WeightPositive() {
        new Edge("A", "B", 0);
    }
    
}

/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package graph;

import static org.junit.Assert.*;

import java.util.Collections;

import org.junit.Test;

/**
 * Tests for static methods of Graph.
 * 
 * To facilitate testing multiple implementations of Graph, instance methods are
 * tested in GraphInstanceTest.
 */
public class GraphStaticTest {
    
    // Testing strategy
    //   empty()
    //     no inputs, only output is empty graph
    //     observe with vertices()
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    @Test
    public void testEmptyVerticesEmpty() {
        assertEquals("expected empty() graph to have no vertices",
                Collections.emptySet(), Graph.empty().vertices());
    }
    @Test
    public void testStringLabels() {
        Graph<String> graph = new ConcreteVerticesGraph<>();
        assertTrue(graph.add("A"));
        assertTrue(graph.add("B"));
        assertTrue(graph.vertices().contains("A"));
        assertTrue(graph.vertices().contains("B"));
    }
    @Test
    public void testIntegerLabels() {
        Graph<Integer> graph = new ConcreteVerticesGraph<>();
        assertTrue(graph.add(1));
        assertTrue(graph.add(2));
        assertTrue(graph.vertices().contains(1));
        assertTrue(graph.vertices().contains(2));
    }

    // TODO test other vertex label types in Problem 3.2
    
}

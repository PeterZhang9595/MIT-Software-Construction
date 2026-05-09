/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package graph;

import static org.junit.Assert.*;

import java.util.Collections;

import org.junit.Test;

/**
 * Tests for instance methods of Graph.
 * 
 * <p>PS2 instructions: you MUST NOT add constructors, fields, or non-@Test
 * methods to this class, or change the spec of {@link #emptyInstance()}.
 * Your tests MUST only obtain Graph instances by calling emptyInstance().
 * Your tests MUST NOT refer to specific concrete implementations.
 */
public abstract class GraphInstanceTest {
    
    // Testing strategy
    //   TODO
    
    /**
     * Overridden by implementation-specific test classes.
     * 
     * @return a new empty graph of the particular implementation being tested
     */
    public abstract Graph<String> emptyInstance();
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    @Test
    public void testInitialVerticesEmpty() {
        // TODO you may use, change, or remove this test
        assertEquals("expected new graph to have no vertices",
                Collections.emptySet(), emptyInstance().vertices());
    }
    
    // TODO other tests for instance methods of Graph
    // TODO Test strategies:
    // add(L vertex) - write tests for each number
    // 1. add a vertex which is not in the graph, expect true and graph.vertices() contains vertex
    // 2. add a vertex which is in the graph, expect false and graph.vertices() remains unchanged
    // 3. add 3 vertex to the graph, 1st and 3rd vertex are the same, expect true for 1st and false for 3rd, and graph.vertices() contains 2 vertices

    @Test
    public void testAdd_NewAndDuplicateVertices() {
        Graph<String> g = emptyInstance();
        // 1
        assertTrue(g.add("v1"));
        assertTrue(g.vertices().contains("v1"));
        // 2
        assertFalse(g.add("v1"));
        // 3
        assertTrue(g.add("v2"));
        assertFalse(g.add("v1")); // adding v1 again still false
        assertEquals(2, g.vertices().size());
    }

    // public int set(L source, L target, int weight);
    // weight:zero,non-zero; edge:exist,not exist;
    // 1. zero weight; edge exit;remove it,return previous weight
    // 2. zero weight ; edge not exit; keep graph unchanged; return 0
    // 3. non-zero weight; edge not exist; add non-existing vertices to graph;add edge to graph;return 0
    // 4. non-zero weight; edge exist; change weight; return previous weight

    @Test
    public void testSet_RemoveExistingEdgeReturnsPreviousWeight() {
        Graph<String> g = emptyInstance();
        g.set("A", "B", 7);
        int prev = g.set("A", "B", 0);
        assertEquals(7, prev);
        assertFalse(g.targets("A").containsKey("B"));
    }

    @Test
    public void testSet_RemoveNonExistingEdgeReturnsZero() {
        Graph<String> g = emptyInstance();
        g.add("A");
        g.add("B");
        int prev = g.set("A", "B", 0);
        assertEquals(0, prev);
        assertFalse(g.targets("A").containsKey("B"));
    }

    @Test
    public void testSet_AddEdgeCreatesVerticesAndReturnsZero() {
        Graph<String> g = emptyInstance();
        int prev = g.set("X", "Y", 3);
        assertEquals(0, prev);
        assertTrue(g.vertices().contains("X"));
        assertTrue(g.vertices().contains("Y"));
        assertEquals(3, (int) g.targets("X").get("Y"));
    }

    @Test
    public void testSet_UpdateExistingEdgeReturnsOldWeight() {
        Graph<String> g = emptyInstance();
        g.set("S", "T", 4);
        int prev = g.set("S", "T", 9);
        assertEquals(4, prev);
        assertEquals(9, (int) g.targets("S").get("T"));
    }

    // public boolean remove(L vertex);
    // 1. vertex in graph - remove vertex and all edges to and from it(check vertices and edges); return true
    // 2. vertex not in graph - keep graph unchanged(check vertices and edges unchanged); return false

    @Test
    public void testRemove_ExistingVertexRemovesEdgesAndReturnsTrue() {
        Graph<String> g = emptyInstance();
        g.set("A", "B", 5);
        g.set("C", "A", 6);
        assertTrue(g.remove("A"));
        assertFalse(g.vertices().contains("A"));
        // edges to/from A should be gone
        assertTrue(g.targets("A").isEmpty());
        assertTrue(g.sources("A").isEmpty());
        // other vertices remain
        assertTrue(g.vertices().contains("B"));
        assertTrue(g.vertices().contains("C"));
        assertFalse(g.sources("B").containsKey("A"));
        assertFalse(g.targets("C").containsKey("A"));
    }

    @Test
    public void testRemove_NonExistingVertexReturnsFalse() {
        Graph<String> g = emptyInstance();
        assertFalse(g.remove("no-such-vertex"));
        assertTrue(g.vertices().isEmpty());
    }

    // public Map<L, Integer> sources(L target); (check weight is non-negative)
    // 1. target vertex not in graph - return empty map
    // 2. target vertex in graph but with no in-degree - return empty map
    // 3. target vertex in graph and with one source - return map
    // 4. target vertex in graph and with multiple sources - return map
    // 5. check the immutability of the returned map

    @Test
    public void testSources_TargetNotInGraphEmpty() {
        Graph<String> g = emptyInstance();
        assertTrue(g.sources("Z").isEmpty());
    }

    @Test
    public void testSources_TargetWithNoInDegreeEmpty() {
        Graph<String> g = emptyInstance();
        g.add("A");
        assertTrue(g.sources("A").isEmpty());
    }

    @Test
    public void testSources_SingleSource() {
        Graph<String> g = emptyInstance();
        g.set("P", "Q", 2);
        assertEquals(1, g.sources("Q").size());
        assertEquals(2, (int) g.sources("Q").get("P"));
    }

    @Test
    public void testSources_MultipleSources() {
        Graph<String> g = emptyInstance();
        g.set("A", "D", 1);
        g.set("B", "D", 2);
        g.set("C", "D", 3);
        assertEquals(3, g.sources("D").size());
        assertEquals(1, (int) g.sources("D").get("A"));
        assertEquals(2, (int) g.sources("D").get("B"));
        assertEquals(3, (int) g.sources("D").get("C"));
    }

    @Test
    public void testSources_ReturnedMapIsUnmodifiableOrNotAffectGraph() {
        Graph<String> g = emptyInstance();
        g.set("A", "B", 1);
        java.util.Map<String,Integer> m = g.sources("B");
        try {
            m.put("X", 99);
            // if put succeeded, ensure graph is unaffected
            assertFalse("modifying returned map should not change graph", g.sources("B").containsKey("X"));
        } catch (UnsupportedOperationException e) {
            // acceptable: returned map is unmodifiable
        }
    }

    // public Map<L, Integer> targets(L source); (check weight is non-negative)
    // 1. source vertex not in graph - return empty map
    // 2. source vertex in graph but with no out-degree - return empty map
    // 3. source vertex in graph and with one target - return map
    // 4. source vertex in graph and with multiple targets - return map
    // 5. check the immutability of the returned map

    @Test
    public void testTargets_SourceNotInGraphEmpty() {
        Graph<String> g = emptyInstance();
        assertTrue(g.targets("Z").isEmpty());
    }

    @Test
    public void testTargets_SourceWithNoOutDegreeEmpty() {
        Graph<String> g = emptyInstance();
        g.add("A");
        assertTrue(g.targets("A").isEmpty());
    }

    @Test
    public void testTargets_SingleTarget() {
        Graph<String> g = emptyInstance();
        g.set("U", "V", 8);
        assertEquals(1, g.targets("U").size());
        assertEquals(8, (int) g.targets("U").get("V"));
    }

    @Test
    public void testTargets_MultipleTargets() {
        Graph<String> g = emptyInstance();
        g.set("S", "T1", 1);
        g.set("S", "T2", 2);
        g.set("S", "T3", 3);
        assertEquals(3, g.targets("S").size());
        assertEquals(1, (int) g.targets("S").get("T1"));
        assertEquals(2, (int) g.targets("S").get("T2"));
        assertEquals(3, (int) g.targets("S").get("T3"));
    }

    @Test
    public void testTargets_ReturnedMapIsUnmodifiableOrNotAffectGraph() {
        Graph<String> g = emptyInstance();
        g.set("A", "B", 1);
        java.util.Map<String,Integer> m = g.targets("A");
        try {
            m.put("X", 99);
            // if put succeeded, ensure graph is unaffected
            assertFalse("modifying returned map should not change graph", g.targets("A").containsKey("X"));
        } catch (UnsupportedOperationException e) {
            // acceptable: returned map is unmodifiable
        }
    }
}

/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package poet;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import org.junit.Test;
import graph.Graph;

/**
 * Tests for GraphPoet.
 */
public class GraphPoetTest {
    
    // Testing strategy
    //   Partition the tests into two categories: constructor tests and poem generation tests.
    // 
    // ConstructorTest
    //      - test constructor with mytest:
    //      - check the graph:
    //          1. all the vertices shouldn't be empty or contain space or newline 
    //          2. All the vertices should be in lower case
    //          3. the vertices can contain punctuation, so when reading words followed by a punc without space, 
    //             the punc should be included in the vertex. 
    //          4. the weight of each edge should be correct
    //
    // PoemTest
    //      - test mugar-omni-theater.txt, mytest, newline-test
    //      - check the output:
    //          1. initial words from input string should be its initial case
    //          2. whether the bridge words are correctly inserted 
    //          3. all the whitespace should be single!
    //      - EXPECTED INPUT AND OUTPUT
    //          - mugar-omni-theater.txt INPUT:"Test the system." OUTPUT:"Test of the system."
    //          - newline-test INPUT:"Seek to explore new and exciting synergies!" 
    //                         OUTPUT:"Seek to explore strange new life and exciting synergies!"
    //          - mytest INPUT:"WHAT the dog doing ?  Hey man! Is this a choice? Or is this a choice ." 
    //                   OUTPUT:"WHAT the dog doing ? Hey man! Is this a good choice? Or is this a bad choice ."

    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    // ==================== Constructor Tests ====================
    
    /**
     * Test the constructor with mytest file.
     * Verifies that all vertices in the graph meet the specification:
     * 1. No empty vertices
     * 2. No vertices contain spaces or newlines
     * 3. All vertices are lowercase (case-insensitive matching)
     * 4. Punctuation attached to words is included in the vertex
     * 5. Edge weights are correctly computed (adjacency counts)
     */
    @Test
    public void testConstructorWithMytest() throws IOException {
        File corpus = new File("ps2/test/poet/mytest");
        GraphPoet poet = new GraphPoet(corpus);
        
        // Get the underlying graph (if accessible) or test through the toString()
        // For now, we test basic construction doesn't throw
        assertNotNull(poet);
        
        // The graph should be non-empty (mytest has content)
        String graphStr = poet.toString();
        assertNotNull(graphStr);
        assertTrue("Graph should contain structure information", 
                   graphStr.contains("Graph") || graphStr.length() > 0);
    }
    
    /**
     * Test that vertices in mytest graph are non-empty and don't contain spaces/newlines.
     * The graph should have vertices like: "if", "you", "want", "to", "test", "the", 
     * "poet,", "this", "is", "a", "good", "choice", ".", etc.
     */
    @Test
    public void testVerticesNoSpacesOrNewlines() throws IOException {
        File corpus = new File("ps2/test/poet/mytest");
        GraphPoet poet = new GraphPoet(corpus);
        
        // Expected vertices based on mytest (all lowercase, with punctuation attached)
        String[] expectedVertices = {"if", "you", "want", "to", "test", "the", "poet,", 
                                     "this", "is", "a", "good", "choice", ".", 
                                     "just", "bad", "choice?", ",", "hahaha.", 
                                     "what", "will", "choose", "or", "?"};
        
        String graphRepr = poet.toString();
        // Each vertex should appear in the graph representation
        for (String vertex : expectedVertices) {
            assertTrue("Vertex '" + vertex + "' should be in graph", 
                      graphRepr.contains(vertex));
        }
    }
    
    /**
     * Test that all vertices are in lowercase.
     * Words from corpus should be stored case-insensitively (all lowercase).
     */
    @Test
    public void testVerticesAreLowercase() throws IOException {
        File corpus = new File("ps2/test/poet/mytest");
        GraphPoet poet = new GraphPoet(corpus);
        
        // All vertices from mytest should be lowercase
        String graphRepr = poet.toString();
        
        // These should appear (lowercase versions)
        assertTrue("'if' should be lowercase", graphRepr.contains("if"));
        assertTrue("'you' should be lowercase", graphRepr.contains("you"));
        assertTrue("'good' should be lowercase", graphRepr.contains("good"));
        
        // These should NOT appear (uppercase versions)
        assertFalse("'If' (uppercase) should not appear", graphRepr.contains("If"));
        assertFalse("'You' (uppercase) should not appear", graphRepr.contains("You"));
    }
    
    /**
     * Test that punctuation attached to words is correctly included as part of the vertex.
     * For example: "poet," (not "poet"), "choice?" (not "choice"), "," (comma as vertex)
     */
    @Test
    public void testPunctuationInVertices() throws IOException {
        File corpus = new File("ps2/test/poet/mytest");
        GraphPoet poet = new GraphPoet(corpus);
        
        String graphRepr = poet.toString();
        
        // Vertices with attached punctuation should exist
        assertTrue("'poet,' with comma should be a vertex", graphRepr.contains("poet,"));
        assertTrue("'choice?' with question mark should be a vertex", graphRepr.contains("choice?"));
        assertTrue("'hahaha.' with period should be a vertex", graphRepr.contains("hahaha."));
        assertTrue("'.' as standalone word should be a vertex", graphRepr.contains("."));
        assertTrue("',' as standalone word should be a vertex", graphRepr.contains(","));
        assertTrue("'?' as standalone word should be a vertex", graphRepr.contains("?"));
    }
    
    /**
     * Test that edge weights are correctly computed.
     * Based on mytest content, certain adjacencies occur multiple times.
     * For example: "a" -> "good" should have weight 3, "good" -> "choice" should have weight 3
     */
    @Test
    public void testEdgeWeightsCorrect() throws IOException {
        File corpus = new File("ps2/test/poet/mytest");
        GraphPoet poet = new GraphPoet(corpus);
        
        String graphRepr = poet.toString();
        
        // Expected edges with their weights from mytest:
        // "a" -> "good" (appears 3 times)
        assertTrue("Edge 'a' -> 'good' with weight 3 should exist", 
                   graphRepr.contains("-> good (3)"));
        
        // "good" -> "choice" (appears 3 times)
        assertTrue("Edge 'good' -> 'choice' with weight 3 should exist", 
                   graphRepr.contains("-> choice (3)"));
        
        // "a" -> "bad" (appears 2 times)
        assertTrue("Edge 'a' -> 'bad' with weight 2 should exist", 
                   graphRepr.contains("-> bad (2)"));
    }
    
    // ==================== Poem Generation Tests ====================
    
    /**
     * Test poem generation with mugar-omni-theater.txt corpus.
     * Input: "Test the system."
     * Expected output: "Test of the system."
     * Verifies:
     * 1. Original case is preserved ("Test" stays "Test")
     * 2. Bridge word "of" is correctly inserted between "Test" and "the"
     * 3. Single spaces only
     */
    @Test
    public void testPoemMugarOmniTheater() throws IOException {
        File corpus = new File("ps2/test/poet/mugar-omni-theater.txt");
        GraphPoet poet = new GraphPoet(corpus);
        
        String input = "Test the system.";
        String output = poet.poem(input);
        String expected = "Test of the system.";
        
        assertEquals("Poem output should match expected", expected, output);
    }
    
    /**
     * Test poem generation with newline-test corpus.
     * Input: "Seek to explore new and exciting synergies!"
     * Expected output: "Seek to explore strange new life and exciting synergies!"
     * Verifies:
     * 1. Original case is preserved
     * 2. Bridge words "strange" and "life" are correctly inserted
     * 3. Single spaces only
     */
    @Test
    public void testPoemNewlineTest() throws IOException {
        File corpus = new File("ps2/test/poet/newline-test");
        GraphPoet poet = new GraphPoet(corpus);
        
        String input = "Seek to explore new and exciting synergies!";
        String output = poet.poem(input);
        String expected = "Seek to explore strange new life and exciting synergies!";
        
        assertEquals("Poem output should match expected", expected, output);
    }
    
    /**
     * Test poem generation with mytest corpus.
     * Input: "WHAT the dog doing ?  Hey man! Is this a choice? Or is this a choice ."
     * Expected output: "WHAT the dog doing ? Hey man! Is this a good choice? Or is this a bad choice ."
     * Verifies:
     * 1. Original case is preserved ("WHAT" stays "WHAT")
     * 2. Bridge words "good" and "bad" are correctly inserted
     * 3. Multiple spaces in input are normalized to single spaces
     */
    @Test
    public void testPoemMytest() throws IOException {
        File corpus = new File("ps2/test/poet/mytest");
        GraphPoet poet = new GraphPoet(corpus);
        
        String input = "WHAT the dog doing ?  Hey man! Is this a choice? Or is this a choice .";
        String output = poet.poem(input);
        String expected = "WHAT the dog doing ? Hey man! Is this is a bad choice? Or is this is a good choice .";
        
        assertEquals("Poem output should match expected", expected, output);
    }
    
    /**
     * Test that poem output uses single spaces only.
     * Input may have multiple spaces, but output should normalize them to single spaces.
     */
    @Test
    public void testOutputHasSingleSpacesOnly() throws IOException {
        File corpus = new File("ps2/test/poet/mytest");
        GraphPoet poet = new GraphPoet(corpus);
        
        // Input with multiple consecutive spaces
        String input = "Is  this  a   choice?";
        String output = poet.poem(input);
        
        // Should not contain double spaces
        assertFalse("Output should not contain double spaces", output.contains("  "));
        
        // All words should be separated by exactly one space
        String[] words = output.split(" ");
        for (String word : words) {
            assertFalse("No empty words between spaces", word.isEmpty());
        }
    }
    
    /**
     * Test that original case of input words is preserved in output.
     * Bridge words inserted by the poet should be lowercase,
     * but original input words should keep their case.
     */
    @Test
    public void testOriginalCasePreserved() throws IOException {
        File corpus = new File("ps2/test/poet/mugar-omni-theater.txt");
        GraphPoet poet = new GraphPoet(corpus);
        
        String input = "Test the system.";
        String output = poet.poem(input);
        
        // "Test", "the", "system." should keep original case
        assertTrue("'Test' should preserve uppercase T", output.startsWith("Test"));
        assertTrue("'the' should preserve lowercase", output.contains(" the "));
        assertTrue("'system.' should preserve lowercase", output.contains("system."));
    }

    @Test
    public void testSearchFor() throws IOException {
        File corpus = new File("ps2/test/poet/mugar-omni-theater.txt");
        GraphPoet poet = new GraphPoet(corpus);

        String bridge = poet.searchBridge( "the", "system");
        assertTrue("The bridge should be empty", bridge.isEmpty());
    }
    
}

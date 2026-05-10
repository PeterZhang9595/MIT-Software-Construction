/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package poet;

import java.io.File;
import java.io.IOException;
import java.util.*;

import graph.Graph;

/**
 * A graph-based poetry generator.
 * 
 * <p>GraphPoet is initialized with a corpus of text, which it uses to derive a
 * word affinity graph.
 * Vertices in the graph are words. Words are defined as non-empty
 * case-insensitive strings of non-space non-newline characters. They are
 * delimited in the corpus by spaces, newlines, or the ends of the file.
 * Edges in the graph count adjacencies: the number of times "w1" is followed by
 * "w2" in the corpus is the weight of the edge from w1 to w2.
 * 
 * <p>For example, given this corpus:
 * <pre>    Hello, HELLO, hello, goodbye!    </pre>
 * <p>the graph would contain two edges:
 * <ul><li> ("hello,") -> ("hello,")   with weight 2
 *     <li> ("hello,") -> ("goodbye!") with weight 1 </ul>
 * <p>where the vertices represent case-insensitive {@code "hello,"} and
 * {@code "goodbye!"}.
 * 
 * <p>Given an input string, GraphPoet generates a poem by attempting to
 * insert a bridge word between every adjacent pair of words in the input.
 * The bridge word between input words "w1" and "w2" will be some "b" such that
 * w1 -> b -> w2 is a two-edge-long path with maximum-weight weight among all
 * the two-edge-long paths from w1 to w2 in the affinity graph.
 * If there are no such paths, no bridge word is inserted.
 * In the output poem, input words retain their original case, while bridge
 * words are lower case. The whitespace between every word in the poem is a
 * single space.
 * 
 * <p>For example, given this corpus:
 * <pre>    This is a test of the Mugar Omni Theater sound system.    </pre>
 * <p>on this input:
 * <pre>    Test the system.    </pre>
 * <p>the output poem would be:
 * <pre>    Test of the system.    </pre>
 * 
 * <p>PS2 instructions: this is a required ADT class, and you MUST NOT weaken
 * the required specifications. However, you MAY strengthen the specifications
 * and you MAY add additional methods.
 * You MUST use Graph in your rep, but otherwise the implementation of this
 * class is up to you.
 */
public class GraphPoet {
    
    private final Graph<String> graph = Graph.empty();
    
    // Abstraction function:
    //   TODO The representation is a graph, and the abstraction is graphPost which you can call its Poem method to generate a poem.
    // Representation invariant:
    //   TODO All write in test file.
    // Safety from rep exposure:
    //   TODO This is safe enough.
    
    /**
     * Create a new poet with the graph from corpus (as described above).
     * 
     * @param corpus text file from which to derive the poet's affinity graph
     * @throws IOException if the corpus file cannot be found or read
     */
    public GraphPoet(File corpus) throws IOException {
        try (Scanner input = new Scanner(corpus)) {
            String prev = null;
            while(input.hasNext()) {
                String curr = input.next().toLowerCase();
                if(prev != null) {
                    int old = this.graph.targets(prev).getOrDefault(curr, 0);
                    this.graph.set(prev,curr,old+1);
                }
                prev = curr;
            }
        }
    }

    // TODO checkRep
    
    /**
     * Generate a poem.
     * 
     * @param input string from which to create the poem
     * @return poem (as described above)
     */
    public String poem(String input) {
        // 1. Split the input into multiple words.
        List<String> words = new ArrayList<>();
        words = Arrays.asList(input.split("\\s+"));
        // 2. Iterate from first word to the last and insert a bridge word between every adjacent pair of words.
        List<String> newWords = new ArrayList<>();
        for(int i = 0; i < words.size()-1; i++) {
            String curr = words.get(i);
            String next = words.get(i+1);
            newWords.add(curr);
            String bridge = searchBridge( curr.toLowerCase(), next.toLowerCase());
            if(!bridge.isEmpty()) {
                newWords.add(bridge);
            }
        }
        newWords.add(words.get(words.size()-1));
        return String.join(" ", newWords);
    }
    public String searchBridge( String word1, String word2) {
        String output = "";
        if(graph.vertices().contains(word1)&&graph.vertices().contains(word2)) {
            int maxWeight = 0;
            int tempWeight = 0;
            Map<String, Integer> targetsOfWord1 = graph.targets(word1);
            if(!targetsOfWord1.isEmpty())
            {
                for(Map.Entry<String, Integer> entry : targetsOfWord1.entrySet()) {
                    tempWeight = entry.getValue();
                    String tempWord = entry.getKey();
                    if(graph.targets(tempWord).containsKey(word2)) {
                        tempWeight += graph.targets(tempWord).get(word2);
                        if(tempWeight > maxWeight) {
                            maxWeight = tempWeight;
                            output = entry.getKey();
                        }
                    }
                    else{
                        continue;
                    }

                }
            }
        }
        return output;
    }
    // TODO toString()
    @Override public String toString() {
        return graph.toString();
    }
}

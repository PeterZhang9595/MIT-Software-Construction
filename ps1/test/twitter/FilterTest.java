/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class FilterTest {

    /*
     * TODO: your testing strategies for these methods should go here.
     * See the ic03-testing exercise for examples of what a testing strategy comment looks like.
     * Make sure you have partitions.
     * writtenBy: 1. naive test just like now 2. check whether the returning order is same as the input list
     *          3. check whether the method is case-insensitive
     * inTimeSpan: 1. naive check just like now 2. check the border case: when the start time is just the time of a tweet
     * containing: 1. naive check just like now 2. check whether the method is case-insensitive
     *          3. check the border case: whether the method can detect a space character so that it will not treat
     *          "a boy" as "aboy" 4. check whether the containing can detect whole word or substring
     */
    
    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    private static final Instant d0 = Instant.parse("2016-02-17T09:00:00Z");
    private static final Instant d3 = Instant.parse("2016-02-17T12:00:00Z");
    
    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);
    private static final Tweet tweet3 = new Tweet(3, "ALYSSA", "before window", d0);
    private static final Tweet tweet4 = new Tweet(4, "charlie", "after window", d3);
    private static final Tweet tweet5 = new Tweet(5, "alice", "Talk is cheap", d1);
    private static final Tweet tweet6 = new Tweet(6, "bob", "I will talk", d2);
    private static final Tweet tweet7 = new Tweet(7, "eve", "talkative style", d2);
    private static final Tweet tweet8 = new Tweet(8, "dave", "talk? maybe", d2);
    private static final Tweet tweet9 = new Tweet(9, "erin", "we discuss security and crypto", d2);
    private static final Tweet tweet10 = new Tweet(10, "dave", "ta lk maybe", d2);
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    @Test
    public void testWrittenByMultipleTweetsSingleResult() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2), "alyssa");
        
        assertEquals("expected singleton list", 1, writtenBy.size());
        assertTrue("expected list to contain tweet", writtenBy.contains(tweet1));
    }

    @Test
    public void testWrittenByCaseInsensitive() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2, tweet3), "aLySsA");

        assertEquals("author matching should be case-insensitive", Arrays.asList(tweet1, tweet3), writtenBy);
    }

    @Test
    public void testWrittenByNoMatch() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2), "nobody");

        assertTrue("no matching author should return empty list", writtenBy.isEmpty());
    }

    @Test
    public void testWrittenByKeepsInputOrderForMatches() {
        Tweet firstMatch = new Tweet(20, "sam", "first", d1);
        Tweet nonMatch = new Tweet(21, "x", "middle", d1);
        Tweet secondMatch = new Tweet(22, "SAM", "last", d2);
        Tweet thirdMatch = new Tweet(23, "SAm", "third", d3);

        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(firstMatch, nonMatch, secondMatch,thirdMatch), "sam");
        assertEquals("matched tweets should keep original order", Arrays.asList(firstMatch, secondMatch,thirdMatch), writtenBy);
    }
    
    @Test
    public void testInTimespanMultipleTweetsMultipleResults() {
        Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T12:00:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), new Timespan(testStart, testEnd));
        
        assertFalse("expected non-empty list", inTimespan.isEmpty());
        assertTrue("expected list to contain tweets", inTimespan.containsAll(Arrays.asList(tweet1, tweet2)));
        assertEquals("expected same order", 0, inTimespan.indexOf(tweet1));
    }

    @Test
    public void testInTimespanIncludesEndpoints() {
        Timespan timespan = new Timespan(d1, d2);
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet3, tweet1, tweet2, tweet4), timespan);

        assertEquals("timespan endpoints are inclusive", Arrays.asList(tweet1, tweet2), inTimespan);
    }

    @Test
    public void testInTimespanEmptyInput() {
        Timespan timespan = new Timespan(d1, d2);
        List<Tweet> inTimespan = Filter.inTimespan(Collections.emptyList(), timespan);

        assertTrue("empty input should return empty output", inTimespan.isEmpty());
    }
    
    @Test
    public void testContaining() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("talk"));
        
        assertFalse("expected non-empty list", containing.isEmpty());
        assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet1, tweet2)));
        assertEquals("expected same order", 0, containing.indexOf(tweet1));
    }

    @Test
    public void testContainingCaseInsensitive() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet5, tweet2), Arrays.asList("talk"));

        assertEquals("word match should be case-insensitive", Arrays.asList(tweet5, tweet2), containing);
    }

    @Test
    public void testContainingWholeWordNotSubstring() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet6, tweet7), Arrays.asList("talk"));

        assertEquals("should match whole word only, not substring", Arrays.asList(tweet6), containing);
    }

    @Test
    public void testContainingSpaceBoundaries() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet6, tweet8,tweet10), Arrays.asList("talk"));

        assertEquals("words are bounded by spaces", Arrays.asList(tweet6), containing);
    }

    @Test
    public void testContainingAnyWordInList() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet2, tweet9, tweet1), Arrays.asList("crypto", "rivest"));

        assertEquals("tweet should be included if it contains at least one target word", Arrays.asList(tweet2,tweet9,tweet1), containing);
    }

    @Test
    public void testContainingEmptyWordsList() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Collections.emptyList());

        assertTrue("empty words list should return empty result", containing.isEmpty());
    }

    /*
     * Warning: all the tests you write here must be runnable against any Filter
     * class that follows the spec. It will be run against several staff
     * implementations of Filter, which will be done by overwriting
     * (temporarily) your version of Filter with the staff's version.
     * DO NOT strengthen the spec of Filter or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in Filter, because that means you're testing a stronger
     * spec than Filter says. If you need such helper methods, define them in a
     * different class. If you only need them in this test class, then keep them
     * in this test class.
     */

}

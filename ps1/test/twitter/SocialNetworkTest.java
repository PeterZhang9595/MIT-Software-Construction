/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.time.Instant;

import org.junit.Test;

public class SocialNetworkTest {

    /*
     * TODO: your testing strategies for these methods should go here.
     * See the ic03-testing exercise for examples of what a testing strategy comment looks like.
     * Make sure you have partitions.
     *
     * guessFollowsGraph: partition by empty vs non-empty tweet list; tweets with
     * no mentions vs one mention vs multiple mentions; repeated mentions of the
     * same username; case-insensitive usernames; and self-mentions, which must
     * not create self-follow edges.
     * influencers: partition by empty graph; users with 0 vs >0 followers;
     * unique maximum follower count vs ties for the maximum; and usernames that
     * appear only as followees (in value sets) and not as keys.
     */

    private static final Instant NOW = Instant.parse("2026-05-06T00:00:00Z");

    private static Tweet tweet(long id, String author, String text) {
        return new Tweet(id, author, text, NOW);
    }

    private static Set<String> setOf(String... usernames) {
        return new HashSet<>(Arrays.asList(usernames));
    }
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    @Test
    public void testGuessFollowsGraphEmpty() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(new ArrayList<>());
        
        assertTrue("expected empty graph", followsGraph.isEmpty());
    }

    @Test
    public void testGuessFollowsGraphSingleMention() {
        List<Tweet> tweets = Arrays.asList(tweet(1, "alice", "hello @bob"));

        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);

        assertTrue("expected alice to be in graph", followsGraph.containsKey("alice"));
        assertTrue("expected alice to follow bob", followsGraph.get("alice").contains("bob"));
    }

    @Test
    public void testGuessFollowsGraphCaseInsensitive() {
        List<Tweet> tweets = Arrays.asList(
                tweet(1, "ALIce", "hey @BoB"),
                tweet(2, "bob", "reply to @ALICE"));

        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);

        assertTrue("expected alice to be in graph", followsGraph.containsKey("alice"));
        assertTrue("expected bob to be in graph", followsGraph.containsKey("bob"));
        assertTrue("expected alice to follow bob", followsGraph.get("alice").contains("bob"));
        assertTrue("expected bob to follow alice", followsGraph.get("bob").contains("alice"));
    }

    @Test
    public void testGuessFollowsGraphMultipleMentionsAndDedup() {
        List<Tweet> tweets = Arrays.asList(
                tweet(1, "alice", "hi @bob and @charlie and @bob"),
                tweet(2, "alice", "also @charlie"));

        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);

        assertTrue("expected alice to follow bob", followsGraph.get("alice").contains("bob"));
        assertTrue("expected alice to follow charlie", followsGraph.get("alice").contains("charlie"));
    }

    @Test
    public void testGuessFollowsGraphSelfMentionIgnored() {
        List<Tweet> tweets = Arrays.asList(tweet(1, "alice", "talking to @alice and @bob"));

        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);

        assertTrue("expected alice to be in graph", followsGraph.containsKey("alice"));
        assertFalse("users cannot follow themselves", followsGraph.get("alice").contains("alice"));
        assertTrue("expected alice to follow bob", followsGraph.get("alice").contains("bob"));
    }
    
    @Test
    public void testInfluencersEmpty() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertTrue("expected empty list", influencers.isEmpty());
    }

    @Test
    public void testInfluencersUniqueTopInfluencer() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("alice", setOf("bob"));
        followsGraph.put("charlie", setOf("bob"));
        followsGraph.put("dave", new HashSet<String>());

        List<String> influencers = SocialNetwork.influencers(followsGraph);

        assertEquals("expected bob to be most influential", "bob", influencers.get(0));
        assertEquals("expected all distinct usernames", setOf("alice", "bob", "charlie", "dave"), new HashSet<>(influencers));
    }

    @Test
    public void testInfluencersTieAtTop() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("alice", setOf("charlie"));
        followsGraph.put("bob", setOf("alice"));
        followsGraph.put("charlie", new HashSet<String>());

        List<String> influencers = SocialNetwork.influencers(followsGraph);

        assertEquals("expected 3 distinct usernames", 3, influencers.size());
        assertEquals("expected all usernames", setOf("alice", "bob", "charlie"), new HashSet<>(influencers));
        assertTrue("expected alice and charlie to be the top two in some order",
                setOf("alice", "charlie").contains(influencers.get(0))
                && setOf("alice", "charlie").contains(influencers.get(1)));
        assertEquals("expected bob to come after the tied top influencers", "bob", influencers.get(2));
    }

    @Test
    public void testInfluencersIncludesUsersOnlyAsFollowees() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("alice", setOf("bob"));
        followsGraph.put("charlie", setOf("bob"));

        List<String> influencers = SocialNetwork.influencers(followsGraph);

        assertEquals("expected bob to have the most followers", "bob", influencers.get(0));
        assertEquals("expected all distinct usernames", setOf("alice", "bob", "charlie"), new HashSet<>(influencers));
    }

    @Test
    public void testInfluencersAuthorManyTweetsCountsOnce() {
        List<Tweet> tweets = Arrays.asList(
                tweet(1, "alice", "@bob hi"),
                tweet(2, "alice", "@bob again"),
                tweet(3, "alice", "hey @bob"),
                tweet(4, "charlie", "@bob hello"));

        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);
        List<String> influencers = SocialNetwork.influencers(followsGraph);

        // alice mentions bob many times but still counts as one follower; charlie is another follower
        assertEquals("bob should be top influencer", "bob", influencers.get(0));
        assertEquals("there should be exactly 3 distinct users", setOf("alice", "bob", "charlie"), new HashSet<>(influencers));
    }

    /*
     * Warning: all the tests you write here must be runnable against any
     * SocialNetwork class that follows the spec. It will be run against several
     * staff implementations of SocialNetwork, which will be done by overwriting
     * (temporarily) your version of SocialNetwork with the staff's version.
     * DO NOT strengthen the spec of SocialNetwork or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in SocialNetwork, because that means you're testing a
     * stronger spec than SocialNetwork says. If you need such helper methods,
     * define them in a different class. If you only need them in this test
     * class, then keep them in this test class.
     */

}

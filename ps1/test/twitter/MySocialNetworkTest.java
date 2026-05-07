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
public class MySocialNetworkTest {
    /**
     * Tests for the optional SocialNetwork extensions: hashtags, triadic closure and awareness.
     * These tests call the public methods directly (they are part of the public API in
     * the student's SocialNetwork) and therefore are valid unit tests for the
     * behaviour described in the assignment TODOs.
     */

    private static final Instant NOW = Instant.parse("2026-05-06T00:00:00Z");

    private static Tweet tweet(long id, String author, String text) {
        return new Tweet(id, author, text, NOW);
    }

    private static Set<String> setOf(String... usernames) {
        return new HashSet<>(Arrays.asList(usernames));
    }

    @Test
    public void testAddUsersWithSameHashtag_caseInsensitiveAndBoundary() {
        List<Tweet> tweets = Arrays.asList(
                tweet(1, "alice", "I love #MIT"),
                tweet(2, "bob", "go #mit !!!"),
                tweet(3, "carol", "hello#mit should not count")
        );

        Map<String, Set<String>> follows = new HashMap<>();
        SocialNetwork.addUsersWithSameHashtag(tweets, follows);

        // alice and bob share hashtag (case-insensitive) -> mutual follow
        assertTrue(follows.containsKey("alice"));
        assertTrue(follows.containsKey("bob"));
        assertTrue(follows.get("alice").contains("bob"));
        assertTrue(follows.get("bob").contains("alice"));

        // carol's "hello#mit" should NOT be considered a hashtag (doesn't start with '#')
        assertFalse("carol should not follow or be followed because her token isn't a hashtag",
                follows.containsKey("carol") && (!follows.get("carol").isEmpty()));
    }

    @Test
    public void testAddUsersWithTriadicClosure_happyPathAndNegativeCases() {
        // Setup: a<->b and b<->c (mutuals), so after closure a<->c should be added
        Map<String, Set<String>> follows = new HashMap<>();
        follows.put("a", setOf("b"));
        follows.put("b", setOf("a", "c"));
        follows.put("c", setOf("b"));

        SocialNetwork.addUsersWithTriadicClosure(new ArrayList<>(), follows);

        // a should now follow c and c should follow a (mutual closure)
        assertTrue(follows.get("a").contains("c"));
        assertTrue(follows.get("c").contains("a"));

        // Negative case: if a and b are NOT mutual, no closure
        Map<String, Set<String>> follows2 = new HashMap<>();
        follows2.put("a", setOf("b")); // a->b but b does not follow a
        follows2.put("b", setOf("c", "d"));
        follows2.put("c", setOf("b")); // b<->c mutual

        SocialNetwork.addUsersWithTriadicClosure(new ArrayList<>(), follows2);
        assertFalse("a should NOT follow c because a and b are not mutual", follows2.get("a").contains("c"));
    }

    @Test
    public void testAddUsersWithAwareness_retweetCreatesFollow() {
        // A follows B, B follows C, and B retweets C -> A should follow C
        Map<String, Set<String>> follows = new HashMap<>();
        follows.put("a", setOf("b"));
        follows.put("b", setOf("c"));

        List<Tweet> tweets = Arrays.asList(
                tweet(1, "b", "RT @c: interesting thread")
        );

        SocialNetwork.addUsersWithAwareness(tweets, follows);

        assertTrue("a should follow c due to awareness via retweet", follows.get("a").contains("c"));
    }

    @Test
    public void testAddUsersWithAwareness_noRetweet_noChange() {
        // A follows B, B follows C but B does not retweet C -> A should NOT follow C
        Map<String, Set<String>> follows = new HashMap<>();
        follows.put("a", setOf("b"));
        follows.put("b", setOf("c"));

        List<Tweet> tweets = Arrays.asList(
                tweet(1, "b", "I like c's work")
        );

        SocialNetwork.addUsersWithAwareness(tweets, follows);

        assertFalse("a should not follow c because b did not retweet c", follows.get("a").contains("c"));
    }

    @Test
    public void testCombined_sequenceExample_individualSteps() {
        // This test demonstrates the intended sequence by calling helpers in order.
        List<Tweet> tweets = Arrays.asList(
                tweet(1, "a", "#coolstuff"), // a uses hashtag
                tweet(2, "b", "#CoolStuff"), // b uses same hashtag -> a<->b
                tweet(3, "b", "RT @c: wow"), // b retweets c
                tweet(4, "c", "#other")
        );

        Map<String, Set<String>> follows = new HashMap<>();

        // Start from mentions (none here)
        SocialNetwork.addMentionedUsers(tweets, follows);
        // Now connect users who use same hashtag
        SocialNetwork.addUsersWithSameHashtag(tweets, follows);
        // Awareness should make a follow c because a follows b and b retweets c
        SocialNetwork.addUsersWithAwareness(tweets, follows);

        assertTrue("a should follow b due to shared hashtag", follows.get("a").contains("b"));
        assertTrue("b should follow a due to shared hashtag", follows.get("b").contains("a"));
        assertTrue("a should follow c due to awareness via b's retweet", follows.get("a").contains("c"));
    }
}



/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * SocialNetwork provides methods that operate on a social network.
 * 
 * A social network is represented by a Map<String, Set<String>> where map[A] is
 * the set of people that person A follows on Twitter, and all people are
 * represented by their Twitter usernames. Users can't follow themselves. If A
 * doesn't follow anybody, then map[A] may be the empty set, or A may not even exist
 * as a key in the map; this is true even if A is followed by other people in the network.
 * Twitter usernames are not case-sensitive, so "ernie" is the same as "ERNie".
 * A username should appear at most once as a key in the map or in any given
 * map[A] set.
 * 
 * DO NOT change the method signatures and specifications of these methods, but
 * you should implement their method bodies, and you may add new public or
 * private methods or classes if you like.
 */
public class SocialNetwork {

    /**
     * Guess who might follow whom, from evidence found in tweets.
     * 
     * @param tweets
     *            a list of tweets providing the evidence, not modified by this
     *            method.
     * @return a social network (as defined above) in which Ernie follows Bert
     *         if and only if there is evidence for it in the given list of
     *         tweets.
     *         One kind of evidence that Ernie follows Bert is if Ernie
     *         @-mentions Bert in a tweet. This must be implemented. Other kinds
     *         of evidence may be used at the implementor's discretion.
     *         All the Twitter usernames in the returned social network must be
     *         either authors or @-mentions in the list of tweets.
     */
    public static Map<String, Set<String>> guessFollowsGraph(List<Tweet> tweets) {
        Map<String, Set<String>> followsGraph = new java.util.HashMap<>();
        addMentionedUsers(tweets, followsGraph);
        addUsersWithSameHashtag(tweets, followsGraph);
        addUsersWithAwareness(tweets, followsGraph);
        addUsersWithTriadicClosure(tweets, followsGraph);
        return followsGraph;
    }

    public static void addMentionedUsers(List<Tweet>tweets, Map<String, Set<String>> followsGraph) {
        for (Tweet tweet : tweets) {
            Set<String> mentionedUsers = Extract.getMentionedUsersInOneTweet(tweet);
            String author = tweet.getAuthor().toLowerCase();
            mentionedUsers.remove(author); // avoid self-mentioned

            if (followsGraph.containsKey(author)) {
                followsGraph.get(author).addAll(mentionedUsers);
            }
            else{
                followsGraph.put(author, mentionedUsers);
            }
        }
    }

    /**
     * Find users who use mention same hashtag(eg. #mit) and connect them(mutual follow) in the followsGraph.
     * Note that hashtag is case-insensitive.
     * @param tweets
     * @param followsGraph
     */
    public static void addUsersWithSameHashtag(List<Tweet> tweets, Map<String, Set<String>> followsGraph) {
        Map<String,Set<String>> hashtagWithUsers = new java.util.HashMap<>();
        for (Tweet tweet : tweets)
        {
            String author = tweet.getAuthor().toLowerCase();
            String text = tweet.getText().toLowerCase();
            String[] words = text.split("\\s+");
            for(String word : words) {
                if (word.startsWith("#")) {
                    if (!hashtagWithUsers.containsKey(word)) {
                        hashtagWithUsers.put(word,new HashSet<String>());
                    }
                    hashtagWithUsers.get(word).add(author);
                }
            }
        }
        for (Map.Entry<String, Set<String>> entry : hashtagWithUsers.entrySet()) {
            Set<String> users = entry.getValue();
            List<String> userList = new java.util.ArrayList<>(users);
            for (int i = 0; i < userList.size() - 1; i++) {
                String user1 = userList.get(i);
                String user2 = userList.get(i + 1);
                addToMap(user1, user2, followsGraph);
            }
        }
    }
    public static void addToMap(String user1,String user2,Map<String,Set<String>>followsGraph)
    {
        if (followsGraph.containsKey(user1)) {
            followsGraph.get(user1).add(user2);
        }
        else {
            Set<String> followSet = new HashSet<>();
            followSet.add(user2);
            followsGraph.put(user1, followSet);
        }
        if (followsGraph.containsKey(user2)) {
            followsGraph.get(user2).add(user1);
        }
        else {
            Set<String> followSet = new HashSet<>();
            followSet.add(user1);
            followsGraph.put(user2, followSet);
        }

    }

    /**
     * If A and B are mutual following relationship and if B and C are mutual following relationship then A and C are mutual following relationship.
     * This is based on the implementation of mentionedUsers and sameHashtag.
     * @param tweets
     * @param followsGraph
     */
    public static void addUsersWithTriadicClosure(List<Tweet> tweets, Map<String, Set<String>> followsGraph) {
        Map<String, Set<String>> edgesToAdd = new java.util.HashMap<>();
        for (Map.Entry<String, Set<String>> entry : followsGraph.entrySet()) {
            String userA = entry.getKey();
            Set<String> followsA = entry.getValue();
            for (String userB : followsA) {
                if (followsGraph.containsKey(userB) && followsGraph.get(userB).contains(userA)) { // A and B are mutual
                    Set<String> followsB = followsGraph.get(userB);
                    for (String userC : followsB) {
                        if (followsGraph.containsKey(userC) && followsGraph.get(userC).contains(userB)) { // B and C are mutual
                            edgesToAdd.computeIfAbsent(userA, k -> new HashSet<>()).add(userC);
                            edgesToAdd.computeIfAbsent(userC, k -> new HashSet<>()).add(userA);
                        }
                    }
                }
            }
        }
        for (String a : edgesToAdd.keySet()) {
            followsGraph.get(a).addAll(edgesToAdd.get(a));
        }
    }

    /**
     * If A follows B and B follows C, and B retweets C in his tweet, then A follows C.
     * @param tweets
     * @param followsGraph
     */
    public static void addUsersWithAwareness(List<Tweet> tweets, Map<String, Set<String>> followsGraph) {
        Map<String, Set<String>> edgesToAdd = new java.util.HashMap<>();
        Map<String, Set<String>> userMentioned  = new java.util.HashMap<>();
        for (Tweet tweet : tweets) {
            String author = tweet.getAuthor().toLowerCase();
            String text = tweet.getText().toLowerCase();
            Set<String> mentionedUsers = Extract.getMentionedUsersInOneTweet(tweet);
            if (!userMentioned.containsKey(author)) {
                userMentioned.put(author, mentionedUsers);
            }
            else {
                userMentioned.get(author).addAll(mentionedUsers);
            }
        }
        for (Map.Entry<String, Set<String>> entry : followsGraph.entrySet()) {
            String userA = entry.getKey();
            Set<String> followsA = entry.getValue();
            for (String userB : followsA) {
                if (followsGraph.containsKey(userB)) {
                    Set<String> followsB = userMentioned.get(userB);
                    for (String userC : followsB) {
                        edgesToAdd.computeIfAbsent(userA, k -> new HashSet<>()).add(userC);
                    }
                }
            }
        }
        for(String a : edgesToAdd.keySet()) {
            followsGraph.get(a).addAll(edgesToAdd.get(a));
        }
    }
    /**
     * Find the people in a social network who have the greatest influence, in
     * the sense that they have the most followers.
     * 
     * @param followsGraph
     *            a social network (as defined above)
     * @return a list of all distinct Twitter usernames in followsGraph, in
     *         descending order of follower count.
     */
    public static List<String> influencers(Map<String, Set<String>> followsGraph) {
        Map<String,Set<String>>  followers = new java.util.HashMap<>(); // a map records followers of the author(key)
        for(Map.Entry<String, Set<String>> entry : followsGraph.entrySet()){
            Set<String> follows = entry.getValue(); // a set records who the author(key) follows
            String author = entry.getKey();
            for (String follow : follows)
            {
                if (followers.containsKey(follow)){
                    followers.get(follow).add(author);
                }
                else{
                    followers.put(follow, new java.util.HashSet<>());
                    followers.get(follow).add(author);
                }
            }
        }

        // Make sure users who are authors but have no followers still appear
        for (String author : followsGraph.keySet()) {
            followers.putIfAbsent(author, new java.util.HashSet<>());
        }

        // Build a map of follower counts
        java.util.Map<String, Integer> counts = new java.util.HashMap<>();
        for (Map.Entry<String, Set<String>> e : followers.entrySet()) {
            counts.put(e.getKey(), e.getValue().size());
        }

        // Create a list of users and sort by follower count descending
        java.util.List<String> users = new java.util.ArrayList<>(counts.keySet());
        users.sort((a, b) -> {
            int cmp = counts.get(b).compareTo(counts.get(a)); // descending by count
            if (cmp != 0) return cmp;
            return a.compareTo(b); // tie-break lexicographically for determinism
        });

        return users;
    }

}

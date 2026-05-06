/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.sql.Time;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class ExtractTest {

    /*
     * TODO: your testing strategies for these methods should go here.
     * See the ic03-testing exercise for examples of what a testing strategy comment looks like.
     * Make sure you have partitions.
     */

    private static final Instant d0 = Instant.parse("2015-02-15T23:00:00Z");
    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    private static final Instant d3 = Instant.parse("2026-04-15T01:00:00Z");

    private static final Tweet tweet0 = new Tweet(0,"masami","Patience is key in life",d0);
    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);
    private static final Tweet tweet3 = new Tweet(3,"Kobe","What can i say man!",d3);
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }

    @Test
    public void testGetTimespanTwoTweets() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet2));
        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d2, timespan.getEnd());
    }
    @Test
    public void testGetTimespanFourTweets() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet0,tweet1, tweet2,tweet3));
        
        assertEquals("expected start", d0, timespan.getStart());
        assertEquals("expected end", d3, timespan.getEnd());
    }

    // Here in order to not strengthen the specification, we choose to ignore the test of dealing with
    // empty tweets list
    
    @Test
    public void testGetMentionedUsersNoMention() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet1));
        
        assertTrue("expected empty set", mentionedUsers.isEmpty());
    }

    private static final Tweet tweetForMentionedUserTest1 = new Tweet(5,"masami","I like @Zyy-9595_hahaha so much.",d1);
    private static final Tweet tweetForMentionedUserTest2 = new Tweet(6,"zz","@fuck you!",d1);
    private static final Tweet tweetForMentionedUserTest3 = new Tweet(7,"peter","man,@what can i say",d2);
    private static final Tweet tweetForMentionedUserTest4 = new Tweet(8,"zajuka","My roommate is @Zyy-9595_hahaha.",d2);
    private static final Tweet tweetForMentionedUserTest5 = new Tweet(9,"zajuka","@zYY-9595_haHaha is my roommate.",d2);
    private static final Tweet tweetForMentionedUserTest6 = new Tweet(10,"gakki","@pku.edu.stu.cn is my email name.",d2);
    private static final Tweet tweetForMentionedUserTest7 = new Tweet(11,"who_am_i","can@canneed haha",d3);
    private static final Tweet tweetForMentionedUserTest8 = new Tweet(12,"mmmm","Great work @Alice, @bob_42, and @charlie-dev! I also thanked @ALICE, but bitdiddle@mit.edu is just an email.",d3);
    @Test
    public void testGetMentionedUsersNormalCase()
    {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweetForMentionedUserTest1,tweetForMentionedUserTest2,tweetForMentionedUserTest3));
        Set<String> expected = new HashSet<>(Arrays.asList("zyy-9595_hahaha", "fuck", "what"));
        assertEquals("expected set",expected,mentionedUsers);
    }


    @Test
    public void testGetMentionedUsersSameMention(){
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweetForMentionedUserTest1,tweetForMentionedUserTest4,tweetForMentionedUserTest4));
        Set<String> expected = new HashSet<>(Arrays.asList("zyy-9595_hahaha"));
        assertEquals("expected set",expected,mentionedUsers);
    }

    @Test
    public void testGetMentionedUsersUpperAndLowerCase()
    {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweetForMentionedUserTest4,tweetForMentionedUserTest5));
        Set<String> expected = new HashSet<>(Arrays.asList("zyy-9595_hahaha"));
    }


    @Test
    public void testGetMentionedUsersExtractCorrectName()
    {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweetForMentionedUserTest6));
        Set<String> expected = new HashSet<>(Arrays.asList("pku"));
        assertEquals("expected set",expected,mentionedUsers);
    }

    @Test
    public void testGetMentionedUsersWithWrongFormat()
    {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweetForMentionedUserTest7));
        assertTrue("expected empty set", mentionedUsers.isEmpty());
    }

    @Test
    public void testGetMentionedUsersSeveralMentionsInTweet()
    {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweetForMentionedUserTest8));
        Set<String> expected = new HashSet<>(Arrays.asList("alice", "bob_42", "charlie-dev"));
        assertEquals("expected set",expected,mentionedUsers);
    }
    /*
     * Warning: all the tests you write here must be runnable against any
     * Extract class that follows the spec. It will be run against several staff
     * implementations of Extract, which will be done by overwriting
     * (temporarily) your version of Extract with the staff's version.
     * DO NOT strengthen the spec of Extract or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in Extract, because that means you're testing a
     * stronger spec than Extract says. If you need such helper methods, define
     * them in a different class. If you only need them in this test class, then
     * keep them in this test class.
     */

}

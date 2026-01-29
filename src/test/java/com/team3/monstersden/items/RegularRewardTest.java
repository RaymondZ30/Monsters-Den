package com.team3.monstersden.items;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Unit tests for {@link RegularReward}.
 */
public class RegularRewardTest {

    /**
     * default constructor should set the default score value and position.
     */
    @Test
    public void testDefaultConstructorSetsScoreAndPosition() {
        RegularReward reward = new RegularReward(2, 3);

        assertEquals(2, reward.getX());
        assertEquals(3, reward.getY());
        // According to implementation, default score is 10
        assertEquals(10, reward.getScoreValue());
        assertFalse(reward.isCollected());
    }

    /**
     * Custom constructor should use the provided score value.
     */
    @Test
    public void testCustomScoreConstructor() {
        RegularReward reward = new RegularReward(5, 6, 42);

        assertEquals(5, reward.getX());
        assertEquals(6, reward.getY());
        assertEquals(42, reward.getScoreValue());
    }

    /**
     * collect() should return the score once and mark the reward as collected.
     * Subsequent calls should return 0 and not change the state further.
     */
    @Test
    public void testCollectIsIdempotent() {
        RegularReward reward = new RegularReward(0, 0, 15);

        int first = reward.collect();
        int second = reward.collect();

        assertEquals(15, first);
        assertEquals(0, second);
        assertTrue(reward.isCollected());
    }

    /**
     * Regular rewards should always be required.
     */
    @Test
    public void testIsRequiredAlwaysTrue() {
        RegularReward reward = new RegularReward(1, 1);
        assertTrue(reward.isRequired());
    }
}

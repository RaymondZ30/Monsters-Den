package com.team3.monstersden.items;

import org.junit.*;
import static org.junit.Assert.*;

/**
 * Test suite for Reward classes (JUnit 4).
 * Tests RegularReward, BonusReward, and base Reward functionality.
 *
 * @author Team 3 - CMPT 276
 * @version 1.0
 */
public class RewardTest {

    // ==================== RegularReward Tests ====================

    @Test
    public void testRegularRewardDefaultConstructor() {
        RegularReward reward = new RegularReward(5, 10);

        assertEquals("X coordinate should be 5", 5, reward.getX());
        assertEquals("Y coordinate should be 10", 10, reward.getY());
        assertTrue("Score value should be positive", reward.getScoreValue() > 0);
        assertFalse("Reward should not be collected initially", reward.isCollected());
    }

    @Test
    public void testRegularRewardCustomScore() {
        RegularReward reward = new RegularReward(3, 7, 50);

        assertEquals("Score value should be 50", 50, reward.getScoreValue());
    }

    @Test
    public void testRegularRewardCollect() {
        RegularReward reward = new RegularReward(0, 0, 100);

        int points = reward.collect();

        assertEquals("Should return 100 points", 100, points);
        assertTrue("Reward should be marked as collected", reward.isCollected());
    }

    @Test
    public void testRegularRewardCollectTwice() {
        RegularReward reward = new RegularReward(0, 0, 100);

        reward.collect();
        int secondCollect = reward.collect();

        assertEquals("Second collect should return 0", 0, secondCollect);
    }

    @Test
    public void testRegularRewardIsRequired() {
        RegularReward reward = new RegularReward(0, 0);

        assertTrue("Regular reward should always be required", reward.isRequired());
    }

    // ==================== BonusReward Tests ====================

    @Test
    public void testBonusRewardDefaultConstructor() {
        BonusReward bonus = new BonusReward(5, 10);

        assertEquals("X coordinate should be 5", 5, bonus.getX());
        assertEquals("Y coordinate should be 10", 10, bonus.getY());
        assertFalse("Should not be expired initially", bonus.isExpired());
        assertTrue("Should be available initially", bonus.isAvailable());
    }

    @Test
    public void testBonusRewardCustomValues() {
        BonusReward bonus = new BonusReward(3, 7, 200, 15);

        assertEquals("Score value should be 200", 200, bonus.getScoreValue());
        assertEquals("Duration should be 15", 15, bonus.getDuration());
        assertEquals("Ticks remaining should be 15", 15, bonus.getTicksRemaining());
    }

    @Test
    public void testBonusRewardTick() {
        BonusReward bonus = new BonusReward(0, 0, 100, 5);

        int initialTicks = bonus.getTicksRemaining();
        boolean expired = bonus.tick();

        assertFalse("Should not expire after one tick", expired);
        assertEquals("Ticks should decrease by 1", initialTicks - 1, bonus.getTicksRemaining());
    }


    @Test
    public void testBonusRewardCollectBeforeExpiry() {
        BonusReward bonus = new BonusReward(0, 0, 150, 5);

        int points = bonus.collect();

        assertEquals("Should return 150 points", 150, points);
        assertTrue("Should be collected", bonus.isCollected());
        assertFalse("Should not be available after collection", bonus.isAvailable());
    }

    @Test
    public void testBonusRewardCollectAfterExpiry() {
        BonusReward bonus = new BonusReward(0, 0, 150, 1);

        bonus.tick();
        bonus.tick(); // Expired

        int points = bonus.collect();

        assertEquals("Expired bonus should return 0 points", 0, points);
    }

    @Test
    public void testBonusRewardToStringNotExpired() {
        BonusReward bonus = new BonusReward(5, 10, 200, 10);

        String str = bonus.toString();

        assertNotNull("toString should not return null", str);
        assertTrue("Should contain score value", str.contains("200"));
        assertTrue("Should contain coordinates", str.contains("5") && str.contains("10"));
    }

    @Test
    public void testBonusRewardToStringExpired() {
        BonusReward bonus = new BonusReward(5, 10, 200, 1);
        bonus.tick();
        bonus.tick();

        String str = bonus.toString();

        assertTrue("Should indicate expired status", str.toLowerCase().contains("expired"));
    }

    // ==================== Base Reward Tests ====================

    @Test
    public void testRewardSetPosition() {
        RegularReward reward = new RegularReward(5, 10, 100);

        reward.setPosition(15, 20);

        assertEquals("X should be updated to 15", 15, reward.getX());
        assertEquals("Y should be updated to 20", 20, reward.getY());
    }

    @Test
    public void testRewardToString() {
        RegularReward reward = new RegularReward(3, 7, 50);

        String str = reward.toString();

        assertNotNull("toString should not return null", str);
        assertTrue("Should contain class name", str.contains("RegularReward"));
        assertTrue("Should contain coordinates", str.contains("3") && str.contains("7"));
        assertTrue("Should contain score value", str.contains("50"));
    }

    // ==================== Edge Cases ====================

    @Test
    public void testZeroScoreReward() {
        RegularReward reward = new RegularReward(0, 0, 0);

        int points = reward.collect();

        assertEquals("Should handle zero score", 0, points);
    }

    @Test
    public void testNegativeCoordinates() {
        RegularReward reward = new RegularReward(-5, -10, 100);

        assertEquals("Should handle negative X", -5, reward.getX());
        assertEquals("Should handle negative Y", -10, reward.getY());
    }

    @Test
    public void testBonusRewardZeroDuration() {
        BonusReward bonus = new BonusReward(0, 0, 100, 0);

        assertEquals("Duration should be 0", 0, bonus.getDuration());
        assertEquals("Ticks remaining should be 0", 0, bonus.getTicksRemaining());
    }

    @Test
    public void testBonusRewardTickWhenCollected() {
        BonusReward bonus = new BonusReward(0, 0, 100, 5);
        bonus.collect();

        int ticksBefore = bonus.getTicksRemaining();
        bonus.tick();
        int ticksAfter = bonus.getTicksRemaining();

        assertEquals("Ticks should not decrease when collected", ticksBefore, ticksAfter);
    }

    @Test
    public void testBonusRewardMultipleTicks() {
        BonusReward bonus = new BonusReward(0, 0, 100, 10);

        for (int i = 0; i < 5; i++) {
            bonus.tick();
        }

        assertEquals("Should have 5 ticks remaining", 5, bonus.getTicksRemaining());
        assertFalse("Should not be expired", bonus.isExpired());
    }
}
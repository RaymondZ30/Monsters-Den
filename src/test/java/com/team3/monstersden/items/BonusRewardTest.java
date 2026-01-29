package com.team3.monstersden.items;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Unit tests for {@link BonusReward}.
 */
public class BonusRewardTest {

    /**
     * default constructor should initialize duration, ticksRemaining,
     * score value, and non-expired state.
     */
    @Test
    public void testDefaultConstructorInitializesFields() {
        BonusReward bonus = new BonusReward(1, 2);

        assertEquals(1, bonus.getX());
        assertEquals(2, bonus.getY());
        assertFalse(bonus.isExpired());
        assertTrue(bonus.isAvailable());
        assertEquals(bonus.getDuration(), bonus.getTicksRemaining());
        // From implementation, default score is 25
        assertEquals(25, bonus.getScoreValue());
    }

    /**
     * tick() should decrement ticksRemaining and eventually mark
     * the reward as expired when it reaches zero.
     */
    @Test
    public void testTickExpiresAfterDuration() {
        int duration = 3;
        BonusReward bonus = new BonusReward(0, 0, 50, duration);

        assertFalse(bonus.isExpired());
        assertEquals(duration, bonus.getTicksRemaining());

        // first tick: not yet expired
        assertFalse(bonus.tick());
        assertEquals(2, bonus.getTicksRemaining());
        assertFalse(bonus.isExpired());

        // second tick: not yet expired
        assertFalse(bonus.tick());
        assertEquals(1, bonus.getTicksRemaining());
        assertFalse(bonus.isExpired());

        // third tick: should expire here
        assertTrue(bonus.tick());
        assertTrue(bonus.isExpired());
        assertFalse(bonus.isAvailable());
        assertTrue(bonus.getTicksRemaining() <= 0);
    }

    /**
     * collect() should work normally before expiration
     * and become ineffective after expiration.
     */
    @Test
    public void testCollectBeforeAndAfterExpire() {
        BonusReward bonus = new BonusReward(0, 0, 40, 1);
    
        // before expiration and before collecting, it should be available
        assertTrue(bonus.isAvailable());
        assertFalse(bonus.isExpired());
        assertFalse(bonus.isCollected());
    
        // collect before expiration: should return score and mark as collected
        int collectedScore = bonus.collect();
        assertEquals(40, collectedScore);
        assertTrue(bonus.isCollected());
    
        // once collected, it is no longer "available"
        assertFalse(bonus.isAvailable());
        assertFalse(bonus.isExpired());
    
        // further collect calls should return 0
        assertEquals(0, bonus.collect());
    
        // after collected, tick() should not cause expiration anymore
        boolean expiredThisTick = bonus.tick();
        assertFalse(expiredThisTick);
        assertFalse(bonus.isExpired());
    }
    

    /**
     * if the bonus reward expires before collection, collect() should return 0
     * and not mark it as collected.
     */
    @Test
    public void testCollectReturnsZeroWhenExpired() {
        BonusReward bonus = new BonusReward(0, 0, 30, 1);

        // let it expire
        bonus.tick(); // duration 1 -> immediately expired

        assertTrue(bonus.isExpired());
        assertFalse(bonus.isCollected());
        assertFalse(bonus.isAvailable());

        int collectedScore = bonus.collect();
        assertEquals(0, collectedScore);
        assertFalse(bonus.isCollected()); // still not collected
    }

    /**
     * toString() should reflect expired state vs. active state.
     */
    @Test
    public void testToStringReflectsState() {
        BonusReward bonus = new BonusReward(1, 2, 30, 2);

        String active = bonus.toString();
        assertTrue(active.contains("Bonus Reward"));
        assertFalse(active.toLowerCase().contains("expired"));

        // force expiration
        bonus.tick();
        bonus.tick();

        String expired = bonus.toString();
        assertTrue(expired.toLowerCase().contains("expired"));
    }
}

package com.team3.monstersden.items;

/**
 * Bonus reward that appears temporarily on the board.
 * Disappears after a set number of game ticks if not collected.
 * Provides higher score values than regular rewards.
 * 
 * @author Team 3 - CMPT 276
 * @version 1.0
 */
public class BonusReward extends Reward {
    
    /** Default score value for bonus rewards */
    private static final int DEFAULT_BONUS_SCORE = 25;
    
    /** Default duration in game ticks */
    private static final int DEFAULT_DURATION = 10;
    
    /** Duration this bonus reward remains on the board (in game ticks) */
    private int duration;
    
    /** Remaining ticks before this reward disappears */
    private int ticksRemaining;
    
    /** Whether this reward has expired */
    private boolean expired;
    
    /**
     * Constructs a bonus reward at the specified position with default values.
     * 
     * @param x the x-coordinate position
     * @param y the y-coordinate position
     */
    public BonusReward(int x, int y) {
        this(x, y, DEFAULT_BONUS_SCORE, DEFAULT_DURATION);
    }
    
    /**
     * Constructs a bonus reward with custom score and duration.
     * 
     * @param x the x-coordinate position
     * @param y the y-coordinate position
     * @param scoreValue the score points this reward grants
     * @param duration the number of ticks before expiration
     */
    public BonusReward(int x, int y, int scoreValue, int duration) {
        super(x, y, scoreValue);
        this.duration = duration;
        this.ticksRemaining = duration;
        this.expired = false;
    }
    
    /**
     * Updates the timer for this bonus reward.
     * Called each game tick. Decrements the remaining time.
     * 
     * @return true if reward expired this tick, false otherwise
     */
    public boolean tick() {
        if (!expired && !collected) {
            ticksRemaining--;
            if (ticksRemaining <= 0) {
                expired = true;
                return true;
            }
        }
        return false;
    }
    
    /**
     * Collects the bonus reward if not expired.
     * 
     * @return the score value if collected, 0 if expired or already collected
     */
    @Override
    public int collect() {
        if (expired) {
            return 0;
        }
        return super.collect();
    }
    
    /**
     * Checks if this bonus reward has expired.
     * 
     * @return true if expired, false otherwise
     */
    public boolean isExpired() {
        return expired;
    }
    
    /**
     * Gets the number of ticks remaining before expiration.
     * 
     * @return remaining ticks
     */
    public int getTicksRemaining() {
        return ticksRemaining;
    }
    
    /**
     * Gets the initial duration of this bonus reward.
     * 
     * @return the duration in ticks
     */
    public int getDuration() {
        return duration;
    }
    
    /**
     * Checks if this reward is still available (not expired and not collected).
     * 
     * @return true if available, false otherwise
     */
    public boolean isAvailable() {
        return !expired && !collected;
    }
    
    @Override
    public String toString() {
        if (expired) {
            return "Expired Bonus Reward";
        }
        return "Bonus Reward at (" + x + "," + y + ") worth " + scoreValue + 
               " points (expires in " + ticksRemaining + " ticks)";
    }
}

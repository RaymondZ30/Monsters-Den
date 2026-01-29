package com.team3.monstersden.items;

/**
 * Regular reward that must be collected to win the game.
 * These rewards are permanent fixtures on the board until collected.
 * * @author Team 3 - CMPT 276
 * @version 1.0
 */
public class RegularReward extends Reward {
    
    /** Default score value for regular rewards */
    private static final int DEFAULT_SCORE = 10;
    
    /**
     * Constructs a regular reward at the specified position with default score.
     * * @param x the x-coordinate position
     * @param y the y-coordinate position
     */
    public RegularReward(int x, int y) {
        super(x, y, DEFAULT_SCORE);
    }
    
    /**
     * Constructs a regular reward at the specified position with custom score.
     * * @param x the x-coordinate position
     * @param y the y-coordinate position
     * @param scoreValue the score points this reward grants
     */
    public RegularReward(int x, int y, int scoreValue) {
        super(x, y, scoreValue);
    }
    
    /**
     * Checks if this is a required reward for winning the game.
     * * @return always returns true for regular rewards
     */
    public boolean isRequired() {
        return true;
    }
}
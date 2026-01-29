package com.team3.monstersden.items;

/**
 * Abstract base class representing a collectible reward in the game.
 * Rewards increase the player's score when collected.
 * 
 * @author Team 3 - CMPT 276
 * @version 1.0
 */
public abstract class Reward {
    
    /** Score value of this reward */
    protected int scoreValue;
    
    /** Position on the board (x-coordinate) */
    protected int x;
    
    /** Position on the board (y-coordinate) */
    protected int y;
    
    /** Whether this reward has been collected */
    protected boolean collected;
    
    /**
     * Constructs a reward with specified position and score value.
     * 
     * @param x the x-coordinate position
     * @param y the y-coordinate position
     * @param scoreValue the score points this reward grants
     */
    public Reward(int x, int y, int scoreValue) {
        this.x = x;
        this.y = y;
        this.scoreValue = scoreValue;
        this.collected = false;
    }
    
    /**
     * Called when the reward is collected by the player.
     * 
     * @return the score value to be added
     */
    public int collect() {
        if (!collected) {
            collected = true;
            return scoreValue;
        }
        return 0;
    }
    
    /**
     * Checks if this reward has been collected.
     * 
     * @return true if collected, false otherwise
     */
    public boolean isCollected() {
        return collected;
    }
    
    /**
     * Gets the score value of this reward.
     * 
     * @return the score value
     */
    public int getScoreValue() {
        return scoreValue;
    }
    
    /**
     * Gets the x-coordinate of this reward.
     * 
     * @return the x position
     */
    public int getX() {
        return x;
    }
    
    /**
     * Gets the y-coordinate of this reward.
     * 
     * @return the y position
     */
    public int getY() {
        return y;
    }
    
    /**
     * Sets the position of this reward.
     * 
     * @param x the new x-coordinate
     * @param y the new y-coordinate
     */
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + " at (" + x + "," + y + ") worth " + scoreValue + " points";
    }
}

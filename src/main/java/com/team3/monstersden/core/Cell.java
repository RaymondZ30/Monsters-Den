package com.team3.monstersden.core;

import com.team3.monstersden.characters.Enemy;
import com.team3.monstersden.items.Reward;
import com.team3.monstersden.items.Punishment;

/**
 * Represents a single cell in the game board grid.
 * Each cell has coordinates, can be a wall, and may contain game objects.
 * 
 * <p>Cells are the fundamental building blocks of the game board. They can contain:
 * <ul>
 *   <li>Walls (impassable barriers)</li>
 *   <li>The player character</li>
 *   <li>Enemies (monsters or humans)</li>
 *   <li>Rewards (regular or bonus)</li>
 *   <li>Punishments (score penalties)</li>
 *   <li>Start or exit positions</li>
 * </ul>
 * </p>
 * 
 * @author Team 3 - CMPT 276
 * @version 1.0
 * @since Phase 2
 */
public class Cell {
    
    /** X-coordinate of this cell in the grid */
    private final int x;
    
    /** Y-coordinate of this cell in the grid */
    private final int y;
    
    /** Indicates if this cell is a wall (impassable) */
    private boolean isWall;
    
    /** Indicates if this cell is the start position */
    private boolean isStart;
    
    /** Indicates if this cell is the exit position */
    private boolean isExit;
    
    /** The game object currently occupying this cell (if any) - legacy field */
    private Object content;
    
    /** Enemy occupying this cell (if any) */
    private Enemy enemy;
    
    /** Reward in this cell (if any) */
    private Reward reward;
    
    /** Punishment trap in this cell (if any) */
    private Punishment punishment;
    
    /**
     * Constructs a new Cell at the specified coordinates.
     * By default, the cell is empty and passable.
     * 
     * @param x the x-coordinate of the cell
     * @param y the y-coordinate of the cell
     */
    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
        this.isWall = false;
        this.isStart = false;
        this.isExit = false;
        this.content = null;
        this.enemy = null;
        this.reward = null;
        this.punishment = null;
    }
    
    /**
     * Returns the x-coordinate of this cell.
     * 
     * @return the x-coordinate
     */
    public int getX() {
        return x;
    }
    
    /**
     * Returns the y-coordinate of this cell.
     * 
     * @return the y-coordinate
     */
    public int getY() {
        return y;
    }
    
    /**
     * Checks if this cell is a wall.
     * 
     * @return true if this cell is a wall, false otherwise
     */
    public boolean isWall() {
        return isWall;
    }
    
    /**
     * Checks if this cell is walkable (not a wall).
     * Used by UI and movement logic.
     * 
     * @return true if this cell can be walked on, false if it's a wall
     */
    public boolean isWalkable() {
        return !isWall;
    }
    
    /**
     * Sets whether this cell is a wall.
     * 
     * @param isWall true to make this cell a wall, false to make it passable
     */
    public void setWall(boolean isWall) {
        this.isWall = isWall;
    }
    
    /**
     * Checks if this cell is the start position.
     * 
     * @return true if this is the start cell, false otherwise
     */
    public boolean isStart() {
        return isStart;
    }
    
    /**
     * Sets whether this cell is the start position.
     * 
     * @param isStart true to mark as start position, false otherwise
     */
    public void setStart(boolean isStart) {
        this.isStart = isStart;
    }
    
    /**
     * Checks if this cell is the exit position.
     * 
     * @return true if this is the exit cell, false otherwise
     */
    public boolean isExit() {
        return isExit;
    }
    
    /**
     * Sets whether this cell is the exit position.
     * 
     * @param isExit true to mark as exit position, false otherwise
     */
    public void setExit(boolean isExit) {
        this.isExit = isExit;
    }
    
    /**
     * Checks if this cell is empty (contains no content).
     * A wall cell is not considered empty even if it has no content.
     * 
     * @return true if the cell has no content and is not a wall, false otherwise
     */
    public boolean isEmpty() {
        return content == null && !isWall && enemy == null && reward == null && punishment == null;
    }
    
    /**
     * Gets the content object currently in this cell (legacy method).
     * 
     * @return the content object, or null if the cell is empty
     */
    public Object getContent() {
        return content;
    }
    
    /**
     * Sets the content object for this cell (legacy method).
     * 
     * @param content the object to place in this cell, or null to clear the cell
     */
    public void setContent(Object content) {
        this.content = content;
    }
    
    /**
     * Gets the enemy occupying this cell.
     * 
     * @return the Enemy object, or null if no enemy is present
     */
    public Enemy getEnemy() {
        return enemy;
    }
    
    /**
     * Sets the enemy occupying this cell.
     * 
     * @param enemy the Enemy to place in this cell, or null to remove enemy
     */
    public void setEnemy(Enemy enemy) {
        this.enemy = enemy;
    }
    
    /**
     * Gets the reward in this cell.
     * 
     * @return the Reward object, or null if no reward is present
     */
    public Reward getReward() {
        return reward;
    }
    
    /**
     * Sets the reward in this cell.
     * 
     * @param reward the Reward to place in this cell, or null to remove reward
     */
    public void setReward(Reward reward) {
        this.reward = reward;
    }
    
    /**
     * Gets the punishment trap in this cell.
     * 
     * @return the Punishment object, or null if no punishment is present
     */
    public Punishment getPunishment() {
        return punishment;
    }
    
    /**
     * Sets the punishment trap in this cell.
     * 
     * @param punishment the Punishment to place in this cell, or null to remove punishment
     */
    public void setPunishment(Punishment punishment) {
        this.punishment = punishment;
    }
    
    /**
     * Clears any content from this cell.
     */
    public void clearContent() {
        this.content = null;
    }
    
    /**
     * Clears all entities from this cell (enemy, reward, punishment).
     */
    public void clearAll() {
        this.content = null;
        this.enemy = null;
        this.reward = null;
        this.punishment = null;
    }
    
    /**
     * Checks if this cell contains content of the specified type.
     * 
     * @param clazz the class type to check for
     * @return true if the cell contains an object of the specified type, false otherwise
     */
    public boolean hasContentOfType(Class<?> clazz) {
        return content != null && clazz.isInstance(content);
    }
    
    /**
     * Checks if this cell has an enemy.
     * 
     * @return true if an enemy is present, false otherwise
     */
    public boolean hasEnemy() {
        return enemy != null;
    }
    
    /**
     * Checks if this cell has a reward.
     * 
     * @return true if a reward is present, false otherwise
     */
    public boolean hasReward() {
        return reward != null;
    }
    
    /**
     * Checks if this cell has a punishment.
     * 
     * @return true if a punishment is present, false otherwise
     */
    public boolean hasPunishment() {
        return punishment != null;
    }
    
    /**
     * Returns a string representation of this cell showing its coordinates and state.
     * 
     * @return a string in the format "Cell(x,y)[wall/start/exit/empty/occupied]"
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Cell(").append(x).append(",").append(y).append(")[");
        
        if (isWall) {
            sb.append("wall");
        } else if (isStart) {
            sb.append("start");
        } else if (isExit) {
            sb.append("exit");
        } else {
            boolean hasContent = false;
            if (enemy != null) {
                sb.append("enemy");
                hasContent = true;
            }
            if (reward != null) {
                if (hasContent) sb.append(", ");
                sb.append("reward");
                hasContent = true;
            }
            if (punishment != null) {
                if (hasContent) sb.append(", ");
                sb.append("punishment");
                hasContent = true;
            }
            if (!hasContent) {
                sb.append("empty");
            }
        }
        
        sb.append("]");
        return sb.toString();
    }
}
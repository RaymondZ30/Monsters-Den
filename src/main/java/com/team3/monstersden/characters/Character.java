package com.team3.monstersden.characters;

import com.team3.monstersden.core.Board;
import com.team3.monstersden.patterns.strategy.MovementStrategy;
import com.team3.monstersden.util.Direction;

/**
 * Abstract base class for a character in the game.
 * Holds common properties like position and health.
 * @author ProjectTeam3
 */
public abstract class Character {
    protected int x;
    protected int y;
    protected int health;
    protected MovementStrategy movementStrategy;

    /**
     * Constructor for a Character
     * @param x x-coordinate on board
     * @param y y-coordinate on board
     * @param health starting health of the character
     */
    public Character(int x, int y, int health) {
        this.x = x;
        this.y = y;
        this.health = health;
    }

    /**
     * Get the x-coordinate for the Character
     * @return x-coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Set the x-coordinate for the Character
     * @param x new x-coordinate
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Get the y-coordinate for the Character
     * @return y-coordinate
     */
    public int getY() {
        return y;
    }


    /**
     * Set the x=y-coordinate for the Character
     * @param y new x-coordinate
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Get the current health value for the Character
     * @return current health value
     */
    public int getHealth() {
        return health;
    }

    /**
     * Set the new health value for the Character
     * @param health new health value
     */
    public void setHealth(int health) {
        this.health = Math.max(0, health);
    }

    /**
     * Checks if the character is still alive.
     */
    public boolean isAlive() {
        return health > 0;
    }

    /**
     * Reduces health by the specified amount
     * @param damage amount of damage to reduce Character health by
     */
    public void takeDamage(int damage) {
        this.health -= damage;
        if (this.health < 0){
            this.health = 0;
        }
    }

    /**
     * Set MovementStrategy for the Character to use
     * @param movementStrategy MovementStrategy to set for Character
     */
    public void setMovementStrategy(MovementStrategy movementStrategy) {
        this.movementStrategy = movementStrategy;
    }

    /**
     * Get MovementStrategy that Character is using
     * @return MovementStrategy for Character
     */
    public MovementStrategy getMovementStrategy() {
        return movementStrategy;
    }

    /**
     * Abstract method to be implemented by other classes on how to move
     */
    public abstract Direction move(Board board);
}
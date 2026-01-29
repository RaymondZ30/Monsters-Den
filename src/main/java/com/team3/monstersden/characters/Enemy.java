package com.team3.monstersden.characters;

import com.team3.monstersden.core.Board;
import com.team3.monstersden.core.Cell;
import com.team3.monstersden.core.GameConfig;
import com.team3.monstersden.util.Direction;

/**
 * Abstract base class for an enemy in the game.
 * Handles damage, movement integration, and AI states.
 * @author ProjectTeam3
 */
public abstract class Enemy extends Character {
    protected int damage;
    protected AIState currentState;

    /**
     * Constructor for Enemy
     * @param x x-coordinate
     * @param y y-coordinate
     * @param damage amount of damage an Enemy can inflict
     * @param maxHealth health value for an Enemy
     */
    public Enemy(int x, int y, int damage, int maxHealth) {
        super(x, y, maxHealth);
        this.damage = damage;
        this.currentState = AIState.PATROL; // Default state
    }

    /**
     * Get the damage value that an Enemy can inflict
     * @return damage value
     */
    public int getDamage() {
        return damage;
    }

    /**
     * Set the damage value that an Enemy can inflict
     * @param damage new damage value
     */
    public void setDamage(int damage) {
        this.damage = damage;
    }

    /**
     * Get the state of the Enemy (IDLE, PATROL, CHASE, ATTACK)
     * @return current state for the Enemy
     */
    public AIState getCurrentState() {
        return currentState;
    }

    /**
     * Set the state of the Enemy (IDLE, PATROL, CHASE, ATTACK)
     * @param state new state for the Enemy
     */
    public void setCurrentState(AIState state) {
        this.currentState = state;
    }

    /**
     * Main update loop for the enemy, called every game tick.
     * Handles movement decision and action execution (attacking or moving).
     * @param board board game to reference from
     * @param player player instance that an Enemy would target
     */
    public void tick(Board board, Player player) {
        if (!isAlive()) return;

        // 1. Decide on move/action using strategy
        Direction dir = move(board, player);

        // 2. Execute Action based on state determined by strategy
        if (currentState == AIState.ATTACK) {
            // Attack player if in range (assumes strategy set state correctly)
             System.out.println(this.getClass().getSimpleName() + " attacks Player!");
             player.takeDamage(this.damage);
             // Optional: Add hit feedback here (observer notification)
        } else if (dir != Direction.NONE) {
            // Try to move
            int newX = this.x + dir.getDx();
            int newY = this.y + dir.getDy();

            // 3. Collision & Movement Robustness
            // Verify move is still valid (strategy might have chosen it, but maybe another enemy just moved there)
            if (board.isValidMove(newX, newY)) {
                Cell currentCell = board.getCell(this.x, this.y);
                Cell nextCell = board.getCell(newX, newY);

                if (!nextCell.hasEnemy() && (player.getX() != newX || player.getY() != newY)) {
                    // Perform move
                    currentCell.setEnemy(null);
                    this.x = newX;
                    this.y = newY;
                    nextCell.setEnemy(this);
                }
            }
        }
    }

    /**
     * Determines next movement direction for an Enemy
     * @param board board game to reference from
     * @return the direction that Enemy should move
     */
    @Override
    public Direction move(Board board) {
        return move(board, null);
    }

    /**
     * Abstract method to be implemented by other classes to determine movement direction for an Enemy
     * @param board board game to reference from
     * @param player player instance that an Enemy would target
     * @return direction that an Enemy should move to
     */
    public abstract Direction move(Board board, Player player);
}
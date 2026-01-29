package com.team3.monstersden.characters;

import com.team3.monstersden.core.Board;
import com.team3.monstersden.util.Direction;

/**
 * Class for a Monster enemy.
 */
public class Monster extends Enemy {

    /**
     * @param x X-coordinate
     * @param y Y-coordinate
     * @param damage Damage per hit
     * @param maxHealth Maximum HP
     */
    public Monster(int x, int y, int damage, int maxHealth) {
        super(x, y, damage, maxHealth); // was hardcoded 60 before — now using arg
    }

    /**
     * Determines next movement direction for a Monster to chase a Player
     * @param board board game to reference from
     * @param player player instance that a Monster would target
     * @return direction that a Monster should move to
     */
    @Override
    public Direction move(Board board, Player player) {
        if (movementStrategy != null) {
            return movementStrategy.chooseMove(board, this, player);
        }
        return Direction.NONE;
    }
}

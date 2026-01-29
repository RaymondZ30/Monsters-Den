package com.team3.monstersden.characters;

import com.team3.monstersden.core.Board;
import com.team3.monstersden.util.Direction;

/**
 * Class for a HumanEnemy.
 */
public class HumanEnemy extends Enemy {

    /**
     * Constructor for HumanEnemy
     * @param x x-coordinate
     * @param y y-coordinate
     * @param damage Damage per hit
     * @param maxHealth Maximum HP
     */
    public HumanEnemy(int x, int y, int damage, int maxHealth) {
        super(x, y, damage, maxHealth); // was hardcoded 40 before — now using arg
    }

    /**
     * Determines next movement direction for a HumanEnemy to chase a Player
     * @param board board game to reference from
     * @param player player instance that a HumanEnemy would target
     * @return direction that a HumanEnemy should move to
     */
    @Override
    public Direction move(Board board, Player player) {
        if (movementStrategy != null) {
            return movementStrategy.chooseMove(board, this, player);
        }
        return Direction.NONE;
    }
}

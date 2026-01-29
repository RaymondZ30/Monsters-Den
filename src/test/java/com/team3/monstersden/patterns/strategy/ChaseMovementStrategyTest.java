package com.team3.monstersden.patterns.strategy;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import com.team3.monstersden.characters.Character;
import com.team3.monstersden.characters.Player;
import com.team3.monstersden.core.Board;
import com.team3.monstersden.util.Direction;

/**
 * Unit tests for {@link ChaseMovementStrategy}.
 *
 * <p>These tests verify that the strategy chooses a movement direction
 * that moves the character toward the player based solely on their
 * relative positions.</p>
 */
public class ChaseMovementStrategyTest {

    private final ChaseMovementStrategy strategy = new ChaseMovementStrategy();

    /**
     * helper fcn to call chooseMove without needing a real Board.
     */
    private Direction chooseMove(Character self, Player player) {
        Board dummyBoard = null; // Board is not used in the implementation
        return strategy.chooseMove(dummyBoard, self, player);
    }

    /**
     * when the player is to the right of self on the same row,
     * the strategy should return {@link Direction#RIGHT}.
     */
    @Test
    public void testMovesRightWhenPlayerIsToTheRight() {
        Player self = new Player(0, 0);
        Player player = new Player(2, 0);

        Direction dir = chooseMove(self, player);
        assertEquals(Direction.RIGHT, dir);
    }

    /**
     * when the player is to the left of self on the same row,
     * the strategy should return {@link Direction#LEFT}.
     */
    @Test
    public void testMovesLeftWhenPlayerIsToTheLeft() {
        Player self = new Player(3, 0);
        Player player = new Player(0, 0);

        Direction dir = chooseMove(self, player);
        assertEquals(Direction.LEFT, dir);
    }

    /**
     * when the player is below self in the same column,
     * the strategy should return {@link Direction#DOWN}.
     */
    @Test
    public void testMovesDownWhenPlayerIsBelow() {
        Player self = new Player(0, 0);
        Player player = new Player(0, 5);

        Direction dir = chooseMove(self, player);
        assertEquals(Direction.DOWN, dir);
    }

    /**
     * when the player is above self in the same column,
     * the strategy should return {@link Direction#UP}.
     */
    @Test
    public void testMovesUpWhenPlayerIsAbove() {
        Player self = new Player(0, 5);
        Player player = new Player(0, 0);

        Direction dir = chooseMove(self, player);
        assertEquals(Direction.UP, dir);
    }

    /**
     * when the player is diagonally pos, both dx and dy are nonzero.
     * given the current implementation (which uses Integer.compare and
     * compares abs(dx) and abs(dy) where both are 1), the tie is broken by the
     * vertical component (dy), so we expect UP or DOWN.
     */
    @Test
    public void testDiagonalPrefersVerticalMovement() {
        Player self = new Player(0, 0);
        Player player = new Player(3, 3); // diagonally down-right

        Direction dir = chooseMove(self, player);
        assertEquals(Direction.DOWN, dir);
    }
}

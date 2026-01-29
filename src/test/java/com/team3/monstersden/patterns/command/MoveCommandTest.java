package com.team3.monstersden.patterns.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import org.junit.Test;

import com.team3.monstersden.core.Game;
import com.team3.monstersden.patterns.strategy.PlayerMovementStrategy;
import com.team3.monstersden.util.Direction;

/**
 * Unit tests for {@link MoveCommand}.
 *
 * <p>These tests verify that the command correctly delegates to
 * {@link PlayerMovementStrategy#addMove(Game, Direction)} when both the
 * strategy and direction are non-null, and that it safely does nothing
 * when either dependency is null.</p>
 */
public class MoveCommandTest {

    /**
     * Simple test double for {@link PlayerMovementStrategy} that records
     * the arguments passed to {@link #addMove(Game, Direction)}.
     */
    private static class TestPlayerMovementStrategy extends PlayerMovementStrategy {
        Game capturedGame;
        Direction capturedDirection;
        int callCount = 0;

        @Override
        public void addMove(Game game, Direction dir) {
            this.capturedGame = game;
            this.capturedDirection = dir;
            this.callCount++;
        }
    }

    /**
     * When both strategy and direction are non-null, execute() should
     * delegate once to addMove with the same game and direction.
     */
    @Test
    public void testExecuteDelegatesToStrategyWithSameArguments() {
        TestPlayerMovementStrategy strategy = new TestPlayerMovementStrategy();
        Direction direction = Direction.RIGHT;
        Game game = null; // MoveCommand doesn't require a non-null Game

        MoveCommand command = new MoveCommand(strategy, direction);
        command.execute(game);

        assertEquals(1, strategy.callCount);
        assertSame(game, strategy.capturedGame);
        assertEquals(direction, strategy.capturedDirection);
    }

    /**
     * When the strategy is null, execute() should perform no action and
     * must not throw any exceptions.
     */
    @Test
    public void testExecuteDoesNothingWhenStrategyIsNull() {
        Direction direction = Direction.LEFT;
        Game game = null;

        MoveCommand command = new MoveCommand(null, direction);
        // Should not throw
        command.execute(game);
    }

    /**
     * When the direction is null, execute() should not call addMove()
     * on the strategy and should complete without errors.
     */
    @Test
    public void testExecuteDoesNothingWhenDirectionIsNull() {
        TestPlayerMovementStrategy strategy = new TestPlayerMovementStrategy();
        Game game = null;

        MoveCommand command = new MoveCommand(strategy, null);
        command.execute(game);

        assertEquals(0, strategy.callCount);
        assertNull(strategy.capturedDirection);
        assertNull(strategy.capturedGame);
    }
}

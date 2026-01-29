package com.team3.monstersden.patterns.strategy;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.team3.monstersden.characters.Enemy;
import com.team3.monstersden.characters.Player;
import com.team3.monstersden.core.Board;
import com.team3.monstersden.core.Cell;
import com.team3.monstersden.core.Game;
import com.team3.monstersden.items.BonusReward;
import com.team3.monstersden.items.Punishment;
import com.team3.monstersden.items.RegularReward;
import com.team3.monstersden.util.Direction;

/**
 * Unit tests for {@link PlayerMovementStrategy}.
 *
 * <p>These tests focus on the movement logic (valid and invalid moves),
 * and exercise combat, reward, and trap branches. A simple test board
 * is injected into the singleton {@link Game} via reflection.</p>
 */
public class PlayerMovementStrategyTest {

    /**
     * Simple board backed by a fixed-size grid of {@link Cell}s.
     * Only the methods needed by PlayerMovementStrategy are overridden.
     */
    private static class TestBoard extends Board {
        private final int width;
        private final int height;
        private final Cell[][] cells;

        public TestBoard() {
            this(8, 8);
        }

        public TestBoard(int width, int height) {
            this.width = width;
            this.height = height;
            this.cells = new Cell[height][width];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    cells[y][x] = new Cell(x, y);
                }
            }
        }

        @Override
        public boolean isValidMove(int x, int y) {
            // For tests we treat any in-bounds cell as valid.
            return x >= 0 && x < width && y >= 0 && y < height;
        }

        @Override
        public Cell getCell(int x, int y) {
            return cells[y][x];
        }
    }

    /** Board which rejects all moves (for invalid-move branch). */
    private static class AlwaysInvalidBoard extends TestBoard {
        @Override
        public boolean isValidMove(int x, int y) {
            return false;
        }
    }

    /**
     * Enemy that is always considered dead (isAlive == false), so that the
     * "enemy defeated" branch in addMove is taken regardless of damage.
     */
    private static class DeadEnemy extends Enemy {
        public DeadEnemy(int x, int y) {
            super(x, y, 0, 1);
        }

        @Override
        public Direction move(Board board, Player player) {
            return Direction.NONE;
        }

        @Override
        public boolean isAlive() {
            return false;
        }
    }

    /**
     * Enemy that always survives (isAlive == true) to exercise the
     * "enemy survives and counterattacks" branch.
     */
    private static class AliveEnemy extends Enemy {
        public AliveEnemy(int x, int y, int damage) {
            super(x, y, damage, 999);
        }

        @Override
        public Direction move(Board board, Player player) {
            return Direction.NONE;
        }

        @Override
        public boolean isAlive() {
            return true;
        }
    }

    /**
     * Helper to configure the singleton Game instance with a custom board
     * and a player at a specific position.
     */
    private Game setupGameAt(int x, int y, Board board) throws Exception {
        Game game = Game.getInstance();

        // Use reflection to replace the private 'board' field
        Field boardField = Game.class.getDeclaredField("board");
        boardField.setAccessible(true);
        boardField.set(game, board);

        // Public setter exists for the player
        game.setPlayer(new Player(x, y));

        return game;
    }

    /**
     * When the target cell is valid, the player should move one tile to the right.
     */
    @Test
    public void testPlayerMovesRightOnValidMove() throws Exception {
        PlayerMovementStrategy strat = new PlayerMovementStrategy();
        TestBoard board = new TestBoard();
        Game game = setupGameAt(1, 1, board);

        strat.addMove(game, Direction.RIGHT);

        Player p = game.getPlayer();
        assertEquals(2, p.getX());
        assertEquals(1, p.getY());
    }

    /**
     * When the board rejects the move, the player should not change position.
     */
    @Test
    public void testInvalidMoveDoesNotChangePosition() throws Exception {
        PlayerMovementStrategy strat = new PlayerMovementStrategy();

        Board badBoard = new AlwaysInvalidBoard();
        Game game = setupGameAt(1, 1, badBoard);
        Player p = game.getPlayer();

        strat.addMove(game, Direction.LEFT);

        assertEquals(1, p.getX());
        assertEquals(1, p.getY());
    }

    // ---------------------------------------------------------------------
    // Null-guard and switch default branches
    // ---------------------------------------------------------------------

    /** game == null short-circuits safely. */
    @Test
    public void testNullGameDoesNothing() {
        PlayerMovementStrategy strat = new PlayerMovementStrategy();
        strat.addMove(null, Direction.UP);
        // Just verifying no exception and branch is covered.
    }

    /** game.getBoard() == null short-circuits safely. */
    @Test
    public void testNullBoardDoesNothing() throws Exception {
        PlayerMovementStrategy strat = new PlayerMovementStrategy();

        Game game = Game.getInstance();
        Field boardField = Game.class.getDeclaredField("board");
        boardField.setAccessible(true);
        boardField.set(game, null);

        game.setPlayer(new Player(1, 1));

        strat.addMove(game, Direction.UP);
    }

    /** game.getPlayer() == null short-circuits safely. */
    @Test
    public void testNullPlayerDoesNothing() throws Exception {
        PlayerMovementStrategy strat = new PlayerMovementStrategy();
        Game game = Game.getInstance();

        // Give it some board
        Field boardField = Game.class.getDeclaredField("board");
        boardField.setAccessible(true);
        boardField.set(game, new TestBoard());

        game.setPlayer(null);

        strat.addMove(game, Direction.UP);
    }

    /** dir == null short-circuits safely. */
    @Test
    public void testNullDirectionDoesNothing() throws Exception {
        PlayerMovementStrategy strat = new PlayerMovementStrategy();
        Game game = setupGameAt(1, 1, new TestBoard());

        strat.addMove(game, null);

        assertEquals(1, game.getPlayer().getX());
        assertEquals(1, game.getPlayer().getY());
    }

    /** Direction.NONE hits the default branch of the switch and does not move. */
    @Test
    public void testDirectionNoneDoesNothing() throws Exception {
        PlayerMovementStrategy strat = new PlayerMovementStrategy();
        Game game = setupGameAt(1, 1, new TestBoard());

        strat.addMove(game, Direction.NONE);

        assertEquals(1, game.getPlayer().getX());
        assertEquals(1, game.getPlayer().getY());
    }

    // ---------------------------------------------------------------------
    // Combat branches (enemy in the destination cell)
    // ---------------------------------------------------------------------

    /** Enemy present and considered dead: player moves into that cell. */
    @Test
    public void testEnemyDiesPlayerMovesIntoCell() throws Exception {
        PlayerMovementStrategy strat = new PlayerMovementStrategy();

        TestBoard board = new TestBoard();
        Game game = setupGameAt(1, 1, board);

        // Put a "dead" enemy in the cell to the right of the player.
        Cell dest = board.getCell(2, 1);
        dest.setEnemy(new DeadEnemy(2, 1));

        strat.addMove(game, Direction.RIGHT);

        Player p = game.getPlayer();
        assertEquals(2, p.getX());
        assertEquals(1, p.getY());
        // Cell should now have no enemy after being "defeated".
        assertFalse(dest.hasEnemy());
    }

    /** Enemy present and always alive: player should not move into that cell. */
    @Test
    public void testEnemySurvivesPlayerDoesNotMove() throws Exception {
        PlayerMovementStrategy strat = new PlayerMovementStrategy();

        TestBoard board = new TestBoard();
        Game game = setupGameAt(1, 1, board);

        Cell dest = board.getCell(2, 1);
        dest.setEnemy(new AliveEnemy(2, 1, 5));

        strat.addMove(game, Direction.RIGHT);

        Player p = game.getPlayer();
        // Still at original position
        assertEquals(1, p.getX());
        assertEquals(1, p.getY());
        assertTrue(dest.hasEnemy());
    }

    // ---------------------------------------------------------------------
    // Reward-related branches
    // ---------------------------------------------------------------------

    /** Reward present but already collected: inner body should be skipped. */
    @Test
    public void testRewardAlreadyCollectedDoesNothing() throws Exception {
        PlayerMovementStrategy strat = new PlayerMovementStrategy();

        TestBoard board = new TestBoard();
        Game game = setupGameAt(1, 1, board);

        Cell dest = board.getCell(2, 1);
        RegularReward reward = new RegularReward(2, 1, 5);
        // Mark as collected so isCollected() returns true
        reward.collect();
        dest.setReward(reward);

        strat.addMove(game, Direction.RIGHT);

        // Reward still present but already collected; important thing is that
        // we exercised the collected == true branch without exception.
        assertTrue(dest.hasReward());
    }

    /** RegularReward branch: reward is collected and removed from the cell. */
    @Test
    public void testRegularRewardCollectedAndCleared() throws Exception {
        PlayerMovementStrategy strat = new PlayerMovementStrategy();

        TestBoard board = new TestBoard();
        Game game = setupGameAt(1, 1, board);

        Cell dest = board.getCell(2, 1);
        dest.setReward(new RegularReward(2, 1, 10));

        strat.addMove(game, Direction.RIGHT);

        Cell cellAfterMove = board.getCell(2, 1);
        assertFalse(cellAfterMove.hasReward());
    }

    /** BonusReward branch: reward is collected and removed from the cell. */
    @Test
    public void testBonusRewardCollectedAndCleared() throws Exception {
        PlayerMovementStrategy strat = new PlayerMovementStrategy();

        TestBoard board = new TestBoard();
        Game game = setupGameAt(1, 1, board);

        Cell dest = board.getCell(2, 1);
        dest.setReward(new BonusReward(2, 1, 20, 3));

        strat.addMove(game, Direction.RIGHT);

        Cell cellAfterMove = board.getCell(2, 1);
        assertFalse(cellAfterMove.hasReward());
    }

    // ---------------------------------------------------------------------
    // Punishment / trap logic branches
    // ---------------------------------------------------------------------

    /**
     * Inactive trap: isActive() == false so inner block is skipped.
     * We force inactivity by triggering the trap before moving.
     */
    @Test
    public void testInactiveTrapDoesNothing() throws Exception {
        PlayerMovementStrategy strat = new PlayerMovementStrategy();

        TestBoard board = new TestBoard();
        Game game = setupGameAt(1, 1, board);

        // Custom trap that can safely report respawnable without throwing.
        Punishment trap = new Punishment(2, 1, 10) {
            @Override
            public boolean isRespawnable() {
                return true;
            }
        };

        // Trigger once so that isActive() becomes false.
        trap.trigger();

        Cell dest = board.getCell(2, 1);
        dest.setPunishment(trap);

        strat.addMove(game, Direction.RIGHT);

        // Trap should still be present; we mainly wanted the inactive branch.
        assertTrue(dest.hasPunishment());
    }

    /**
     * Active, respawnable trap: isActive() true, trigger() called,
     * and isRespawnable() == true so punishment remains in the cell.
     */
    @Test
    public void testActiveRespawnableTrapRemains() throws Exception {
        PlayerMovementStrategy strat = new PlayerMovementStrategy();

        TestBoard board = new TestBoard();
        Game game = setupGameAt(1, 1, board);

        Punishment trap = new Punishment(2, 1, 10) {
            @Override
            public boolean isRespawnable() {
                return true;
            }
        };

        Cell dest = board.getCell(2, 1);
        dest.setPunishment(trap);

        strat.addMove(game, Direction.RIGHT);

        Cell cellAfterMove = board.getCell(2, 1);
        assertTrue(cellAfterMove.hasPunishment());
    }

    /**
     * Active, non-respawnable trap: after trigger() and isRespawnable() == false,
     * the punishment should be cleared from the cell.
     */
    @Test
    public void testActiveNonRespawnableTrapIsRemoved() throws Exception {
        PlayerMovementStrategy strat = new PlayerMovementStrategy();

        TestBoard board = new TestBoard();
        Game game = setupGameAt(1, 1, board);

        Punishment trap = new Punishment(2, 1, 10) {
            @Override
            public boolean isRespawnable() {
                return false;
            }
        };

        Cell dest = board.getCell(2, 1);
        dest.setPunishment(trap);

        strat.addMove(game, Direction.RIGHT);

        Cell cellAfterMove = board.getCell(2, 1);
        assertFalse(cellAfterMove.hasPunishment());
    }
}

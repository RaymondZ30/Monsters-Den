package com.team3.monstersden.patterns.strategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

import com.team3.monstersden.characters.AIState;
import com.team3.monstersden.characters.Enemy;
import com.team3.monstersden.characters.Monster;
import com.team3.monstersden.characters.Player;
import com.team3.monstersden.core.Board;
import com.team3.monstersden.core.Cell;
import com.team3.monstersden.util.Direction;

/**
 * Unit tests for {@link SmartEnemyStrategy}.
 *
 * These tests focus on the AI state transitions (ATTACK, CHASE, PATROL)
 * and the fact that a movement direction is produced when appropriate.
 */
public class SmartEnemyStrategyTest {

    /**
     * minimal fake board: every coordinate is a valid move and cells are empty.
     * This keeps the test focused on AI logic and not board collisions.
     */
    private static class FakeBoard extends Board {

        @Override
        public boolean isValidMove(int x, int y) {
            return true;
        }

        @Override
        public Cell getCell(int x, int y) {
            return new Cell(x, y);
        }
    }

    /**
     * when the player is adjacent (Manhattan distance 1), the enemy
     * should enter ATTACK state. In this state, SmartEnemyStrategy
     * returns Direction.NONE because the enemy attacks in place.
     */
    @Test
    public void testStateBecomesAttackWhenPlayerAdjacent() {
        SmartEnemyStrategy strat = new SmartEnemyStrategy();
        Board board = new FakeBoard();
        Enemy enemy = new Monster(0, 0, 10, 10);
        Player player = new Player(0, 1); // distance 1

        Direction d = strat.chooseMove(board, enemy, player);

        assertEquals(AIState.ATTACK, enemy.getCurrentState());
        assertEquals(Direction.NONE, d);
    }

    /**
     * when the player is within chase range but not adjacent
     * (1 < distance <= ENEMY_CHASE_RANGE), the enemy should enter
     * CHASE state and SmartEnemyStrategy should delegate to A* to
     * produce some movement direction.
     */
    @Test
    public void testStateBecomesChaseWhenPlayerInChaseRange() {
        SmartEnemyStrategy strat = new SmartEnemyStrategy();
        Board board = new FakeBoard();
        Enemy enemy = new Monster(0, 0, 10, 10);
        Player player = new Player(0, 3); // distance 3

        Direction d = strat.chooseMove(board, enemy, player);

        assertEquals(AIState.CHASE, enemy.getCurrentState());
        assertNotNull(d); // don't assert a specific direction, A* decides
    }

    /**
     * When the player is far away (distance > ENEMY_CHASE_RANGE),
     * the enemy should patrol randomly: state PATROL and a non-NONE
     * direction returned by getPatrolMove.
     */
    @Test
    public void testStateBecomesPatrolWhenPlayerFar() {
        SmartEnemyStrategy strat = new SmartEnemyStrategy();
        Board board = new FakeBoard();
        Enemy enemy = new Monster(0, 0, 10, 10);
        Player player = new Player(20, 20); // distance >> chase range

        Direction d = strat.chooseMove(board, enemy, player);

        assertEquals(AIState.PATROL, enemy.getCurrentState());
        assertNotNull(d);
        assertNotEquals(Direction.NONE, d);
    }

    /**
     * when the player is null (e.g., dead or not present), the enemy
     * should also patrol: state PATROL and a non-NONE direction.
     */
    @Test
    public void testPlayerNullResultsInPatrolMovement() {
        SmartEnemyStrategy strat = new SmartEnemyStrategy();
        Board board = new FakeBoard();
        Enemy enemy = new Monster(0, 0, 10, 10);

        Direction d = strat.chooseMove(board, enemy, null);

        assertEquals(AIState.PATROL, enemy.getCurrentState());
        assertNotNull(d);
        assertNotEquals(Direction.NONE, d);
    }
}

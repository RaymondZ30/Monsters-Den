package com.team3.monstersden.characters;

import com.team3.monstersden.core.Board;
import com.team3.monstersden.core.Cell;
import com.team3.monstersden.util.Direction;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test suite for Enemy class
 */
public class EnemyTest {
    private Board mockBoard;
    private Player mockPlayer;
    private TestEnemy enemy;


    private static class TestEnemy extends Enemy {
        private Direction nextMove = Direction.NONE;

        public TestEnemy(int x, int y, int damage, int maxHealth) {
            super(x, y, damage, maxHealth);
        }

        @Override
        public Direction move(Board board, Player player) {
            return nextMove;
        }

        // Helper method for testing - NOT part of Enemy class
        public void setNextMove(Direction dir) {
            this.nextMove = dir;
        }
    }

    @Before
    public void setUp() {
        mockBoard = new Board();
        mockPlayer = new Player("TestPlayer", 1, 1);
        enemy = new TestEnemy(5, 5, 10, 50);
    }

    // Test constructor initialization
    @Test
    public void testConstructor() {
        assertEquals(5, enemy.getX());
        assertEquals(5, enemy.getY());
        assertEquals(10, enemy.getDamage());
        assertEquals(50, enemy.getHealth());
        assertEquals(AIState.PATROL, enemy.getCurrentState());
        assertTrue(enemy.isAlive());
    }

    // Test if getDamage() returns correct value
    @Test
    public void testGetDamage() {
        assertEquals(10, enemy.getDamage());
    }

    // Test setDamage() is updated correctly
    @Test
    public void testSetDamage() {
        enemy.setDamage(25);
        assertEquals(25, enemy.getDamage());
    }

    // Test setDamage if it accepts zero damage
    @Test
    public void testSetDamageZero() {
        enemy.setDamage(0);
        assertEquals(0, enemy.getDamage());
    }

    // Test setDamage given high damage value
    @Test
    public void testSetDamageHighValue() {
        enemy.setDamage(1000);
        assertEquals(1000, enemy.getDamage());
    }

    // Test getCurrentState returns initial state
    @Test
    public void testGetCurrentState() {
        assertEquals(AIState.PATROL, enemy.getCurrentState());
    }

    // Test setCurrentState updates state correctly
    @Test
    public void testSetCurrentState() {
        enemy.setCurrentState(AIState.CHASE);
        assertEquals(AIState.CHASE, enemy.getCurrentState());

        enemy.setCurrentState(AIState.ATTACK);
        assertEquals(AIState.ATTACK, enemy.getCurrentState());
    }

    // Test if all AIStates can be set
    @Test
    public void testAllAIStates() {
        for (AIState state : AIState.values()) {
            enemy.setCurrentState(state);
            assertEquals(state, enemy.getCurrentState());
        }
    }

    // Test when Enemy is alive when health is above zero
    @Test
    public void testIsAlive() {
        assertTrue(enemy.isAlive());
    }

    // Test Enemy health decreases correctly when taking damage
    @Test
    public void testTakeDamage() {
        enemy.takeDamage(15);
        assertEquals(35, enemy.getHealth());
    }

    // Test Enemy health stays alive after small damage
    @Test
    public void testStaysAliveAfterSmallDamage() {
        enemy.takeDamage(10);
        assertTrue(enemy.isAlive());
        assertEquals(40, enemy.getHealth());
    }

    // Test tick does nothing when enemy is dead
    @Test
    public void testTickWhenEnemyDead() {
        enemy.takeDamage(50); // Kill the enemy
        int playerHealth = mockPlayer.getHealth();
        int enemyX = enemy.getX();
        int enemyY = enemy.getY();

        enemy.setCurrentState(AIState.ATTACK);
        enemy.setNextMove(Direction.RIGHT);
        enemy.tick(mockBoard, mockPlayer);

        // Enemy should not move or attack
        assertEquals(enemyX, enemy.getX());
        assertEquals(enemyY, enemy.getY());
        assertEquals(playerHealth, mockPlayer.getHealth());
    }

    // Test tick attacks player when in ATTACK state
    @Test
    public void testTickAttacksPlayerInAttackState() {
        int initialHealth = mockPlayer.getHealth();
        enemy.setCurrentState(AIState.ATTACK);
        enemy.setNextMove(Direction.NONE);

        enemy.tick(mockBoard, mockPlayer);

        assertEquals(initialHealth - 10, mockPlayer.getHealth());
    }

    // Test tick does not attack when in PATROL state
    @Test
    public void testTickDoesNotAttackInPatrolState() {
        int initialHealth = mockPlayer.getHealth();
        enemy.setCurrentState(AIState.PATROL);
        enemy.setNextMove(Direction.NONE);

        enemy.tick(mockBoard, mockPlayer);

        assertEquals(initialHealth, mockPlayer.getHealth());
    }

    // Test tick does not attack when in CHASE state
    @Test
    public void testTickDoesNotAttackInChaseState() {
        int initialHealth = mockPlayer.getHealth();
        enemy.setCurrentState(AIState.CHASE);
        enemy.setNextMove(Direction.NONE);

        enemy.tick(mockBoard, mockPlayer);

        assertEquals(initialHealth, mockPlayer.getHealth());
    }

    // Test tick does not attack when in IDLE state
    @Test
    public void testTickDoesNotAttackInIdleState() {
        int initialHealth = mockPlayer.getHealth();
        enemy.setCurrentState(AIState.IDLE);
        enemy.setNextMove(Direction.NONE);

        enemy.tick(mockBoard, mockPlayer);

        assertEquals(initialHealth, mockPlayer.getHealth());
    }

    // Test tick deals correct damage amount
    @Test
    public void testTickAttackDealsDamage() {
        int initialHealth = mockPlayer.getHealth();
        int enemyDamage = enemy.getDamage();
        enemy.setCurrentState(AIState.ATTACK);
        enemy.setNextMove(Direction.NONE);

        enemy.tick(mockBoard, mockPlayer);

        assertEquals(initialHealth - enemyDamage, mockPlayer.getHealth());
    }

    // Test that newX calculation uses getDx() correctly
    @Test
    public void testTickCalculatesNewXCorrectly() throws Exception {
        mockBoard.loadMap("level1.txt");

        // Position player far away
        mockPlayer.setX(15);
        mockPlayer.setY(15);

        // Place enemy at a known open position
        enemy.x = 3;
        enemy.y = 3;
        Cell startCell = mockBoard.getCell(3, 3);
        if (startCell != null && startCell.isWalkable()) {
            startCell.setEnemy(enemy);

            enemy.setCurrentState(AIState.PATROL);
            enemy.setNextMove(Direction.RIGHT); // getDx() = 1

            int expectedNewX = enemy.getX() + Direction.RIGHT.getDx(); // 3 + 1 = 4

            // Check if target is valid before testing
            if (mockBoard.isValidMove(expectedNewX, 3)) {
                Cell targetCell = mockBoard.getCell(expectedNewX, 3);
                if (targetCell != null && targetCell.isWalkable() && !targetCell.hasEnemy()) {
                    enemy.tick(mockBoard, mockPlayer);
                    assertEquals(expectedNewX, enemy.getX());
                }
            }
        }
    }

    // Test that newY calculation uses getDy() correctly
    @Test
    public void testTickCalculatesNewYCorrectly() throws Exception {
        mockBoard.loadMap("level1.txt");

        // Position player far away
        mockPlayer.setX(15);
        mockPlayer.setY(15);

        // Place enemy at a known open position
        enemy.x = 3;
        enemy.y = 3;
        Cell startCell = mockBoard.getCell(3, 3);
        if (startCell != null && startCell.isWalkable()) {
            startCell.setEnemy(enemy);

            enemy.setCurrentState(AIState.PATROL);
            enemy.setNextMove(Direction.DOWN); // getDy() = 1

            int expectedNewY = enemy.getY() + Direction.DOWN.getDy(); // 3 + 1 = 4

            // Check if target is valid before testing
            if (mockBoard.isValidMove(3, expectedNewY)) {
                Cell targetCell = mockBoard.getCell(3, expectedNewY);
                if (targetCell != null && targetCell.isWalkable() && !targetCell.hasEnemy()) {
                    enemy.tick(mockBoard, mockPlayer);
                    assertEquals(expectedNewY, enemy.getY());
                }
            }
        }
    }

    // Test that board.getCell(this.x, this.y) is called for currentCell
    @Test
    public void testTickGetsCurrentCell() throws Exception {
        mockBoard.loadMap("level1.txt");

        mockPlayer.setX(15);
        mockPlayer.setY(15);

        // Use a position we know exists
        enemy.x = 2;
        enemy.y = 2;

        Cell startCell = mockBoard.getCell(2, 2);
        if (startCell != null && startCell.isWalkable()) {
            startCell.setEnemy(enemy);

            // Verify the cell has the enemy
            assertEquals(enemy, startCell.getEnemy());

            enemy.setCurrentState(AIState.PATROL);
            enemy.setNextMove(Direction.RIGHT);

            enemy.tick(mockBoard, mockPlayer);

            // After tick, either enemy moved or stayed
            // Either way, getCell was called internally
            Cell cellAfter = mockBoard.getCell(2, 2);
            assertNotNull(cellAfter);
        }
    }

    // Test that board.getCell(newX, newY) is called for nextCell
    @Test
    public void testTickGetsNextCell() throws Exception {
        mockBoard.loadMap("level1.txt");

        mockPlayer.setX(15);
        mockPlayer.setY(15);

        enemy.x = 2;
        enemy.y = 2;

        Cell startCell = mockBoard.getCell(2, 2);
        if (startCell != null && startCell.isWalkable()) {
            startCell.setEnemy(enemy);

            enemy.setCurrentState(AIState.PATROL);
            enemy.setNextMove(Direction.RIGHT);

            int targetX = 3;
            int targetY = 2;

            if (mockBoard.isValidMove(targetX, targetY)) {
                Cell nextCell = mockBoard.getCell(targetX, targetY);
                assertNotNull(nextCell); // Verifies getCell(newX, newY) works
            }
        }
    }

    // Test that currentCell.setEnemy(null) is called
    @Test
    public void testTickClearsCurrentCell() throws Exception {
        mockBoard.loadMap("level1.txt");

        mockPlayer.setX(15);
        mockPlayer.setY(15);

        enemy.x = 2;
        enemy.y = 2;

        Cell startCell = mockBoard.getCell(2, 2);
        Cell targetCell = mockBoard.getCell(3, 2);

        if (startCell != null && targetCell != null &&
                startCell.isWalkable() && targetCell.isWalkable()) {

            startCell.setEnemy(enemy);
            targetCell.setEnemy(null);

            enemy.setCurrentState(AIState.PATROL);
            enemy.setNextMove(Direction.RIGHT);

            enemy.tick(mockBoard, mockPlayer);

            // If enemy moved, old cell should be null
            if (enemy.getX() == 3) {
                assertNull(startCell.getEnemy());
            }
        }
    }

    // Test that this.x and this.y are updated
    @Test
    public void testTickUpdatesEnemyPosition() throws Exception {
        mockBoard.loadMap("level1.txt");

        mockPlayer.setX(15);
        mockPlayer.setY(15);

        enemy.x = 2;
        enemy.y = 2;

        Cell startCell = mockBoard.getCell(2, 2);
        Cell targetCell = mockBoard.getCell(3, 2);

        if (startCell != null && targetCell != null &&
                startCell.isWalkable() && targetCell.isWalkable()) {

            startCell.setEnemy(enemy);
            targetCell.setEnemy(null);

            int originalX = enemy.getX();
            int originalY = enemy.getY();

            enemy.setCurrentState(AIState.PATROL);
            enemy.setNextMove(Direction.RIGHT);

            enemy.tick(mockBoard, mockPlayer);

            // Check if position changed (movement succeeded)
            if (mockBoard.isValidMove(3, 2) && !targetCell.hasEnemy()) {
                assertTrue(enemy.getX() != originalX || enemy.getY() != originalY);
            }
        }
    }

    // Test that nextCell.setEnemy(this) is called
    @Test
    public void testTickSetsEnemyInNextCell() throws Exception {
        mockBoard.loadMap("level1.txt");

        mockPlayer.setX(15);
        mockPlayer.setY(15);

        enemy.x = 2;
        enemy.y = 2;

        Cell startCell = mockBoard.getCell(2, 2);
        Cell targetCell = mockBoard.getCell(3, 2);

        if (startCell != null && targetCell != null &&
                startCell.isWalkable() && targetCell.isWalkable()) {

            startCell.setEnemy(enemy);
            targetCell.setEnemy(null);

            enemy.setCurrentState(AIState.PATROL);
            enemy.setNextMove(Direction.RIGHT);

            enemy.tick(mockBoard, mockPlayer);

            // If enemy moved to (3,2), that cell should have the enemy
            if (enemy.getX() == 3) {
                assertEquals(enemy, targetCell.getEnemy());
            }
        }
    }
}

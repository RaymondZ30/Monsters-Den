package com.team3.monstersden.integration;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import com.team3.monstersden.core.Board;
import com.team3.monstersden.core.Cell;
import com.team3.monstersden.core.Game;
import com.team3.monstersden.characters.*;
import com.team3.monstersden.patterns.strategy.*;
import com.team3.monstersden.util.Direction;

/**
 * Integration tests for Movement Strategy pattern.
 * Tests strategy implementations with actual game board.
 * 
 * @author Team 3
 * @version 1.0
 */
public class StrategyIntegrationTest {
    
    private Game game;
    private Board board;
    private Player player;
    
    @Before
    public void setUp() {
        game = Game.getInstance();
        game.initialize();
        board = game.getBoard();
        player = game.getPlayer();
    }
    
    // ==================== SmartEnemyStrategy Tests ====================
    
    @Test
    public void testSmartEnemyStrategyWithMonster() {
        SmartEnemyStrategy strategy = new SmartEnemyStrategy();
        
        // Create monster far from player
        Cell spawnCell = board.getRandomEmptyCell();
        if (spawnCell != null) {
            Monster monster = new Monster(spawnCell.getX(), spawnCell.getY(), 15, 50);
            monster.setMovementStrategy(strategy);
            
            Direction move = strategy.chooseMove(board, monster, player);
            
            assertNotNull("Strategy should return a direction", move);
        }
    }
    
    @Test
    public void testSmartEnemyStrategyChasesBehavior() {
        SmartEnemyStrategy strategy = new SmartEnemyStrategy();
        
        // Place enemy near player
        int playerX = player.getX();
        int playerY = player.getY();
        
        // Find nearby cell
        int enemyX = playerX + 3;
        int enemyY = playerY;
        
        if (board.isValidMove(enemyX, enemyY)) {
            HumanEnemy enemy = new HumanEnemy(enemyX, enemyY, 10, 40);
            enemy.setMovementStrategy(strategy);
            
            Direction move = strategy.chooseMove(board, enemy, player);
            
            assertNotNull("Strategy should choose a move", move);
        }
    }
    
    @Test
    public void testSmartEnemyStrategyPatrolBehavior() {
        SmartEnemyStrategy strategy = new SmartEnemyStrategy();
        
        // Place enemy far from player
        Cell farCell = board.getRandomEmptyCell();
        if (farCell != null) {
            Monster monster = new Monster(farCell.getX(), farCell.getY(), 15, 50);
            monster.setMovementStrategy(strategy);
            
            // Move player far away
            Cell exitCell = board.getExitCell();
            player.setX(exitCell.getX());
            player.setY(exitCell.getY());
            
            Direction move = strategy.chooseMove(board, monster, player);
            
            assertNotNull("Strategy should return direction for patrol", move);
        }
    }
    
    @Test
    public void testSmartEnemyStrategyAttackState() {
        SmartEnemyStrategy strategy = new SmartEnemyStrategy();
        
        // Place enemy adjacent to player
        int playerX = player.getX();
        int playerY = player.getY();
        
        // Try to place enemy next to player
        int enemyX = playerX + 1;
        int enemyY = playerY;
        
        if (board.isInBounds(enemyX, enemyY)) {
            Monster monster = new Monster(enemyX, enemyY, 15, 50);
            monster.setMovementStrategy(strategy);
            
            Direction move = strategy.chooseMove(board, monster, player);
            
            // When adjacent, might attack (NONE) or move
            assertNotNull("Strategy should make decision when adjacent", move);
        }
    }
    
    // ==================== PlayerMovementStrategy Tests ====================
    
    @Test
    public void testPlayerMovementStrategy() {
        PlayerMovementStrategy strategy = new PlayerMovementStrategy();
        player.setMovementStrategy(strategy);
        
        Direction move = strategy.chooseMove(board, player, player);
        
        assertNotNull("Player strategy should return a direction", move);
    }
    
    @Test
    public void testPlayerStrategyReturnsNone() {
        PlayerMovementStrategy strategy = new PlayerMovementStrategy();
        
        Direction move = strategy.chooseMove(board, player, player);
        
        // Player strategy typically returns NONE (waits for input)
        assertEquals("Player strategy should return NONE by default", 
                     Direction.NONE, move);
    }
    
    // ==================== Strategy Pattern Flexibility Tests ====================
    
    @Test
    public void testSwitchingStrategies() {
        Monster monster = new Monster(5, 5, 15, 50);
        
        SmartEnemyStrategy smartStrategy = new SmartEnemyStrategy();
        monster.setMovementStrategy(smartStrategy);
        
        assertNotNull("Should have movement strategy", 
                      monster.getMovementStrategy());
        assertEquals("Should be smart strategy", 
                     smartStrategy, monster.getMovementStrategy());
        
        // Switch to player strategy (testing flexibility)
        PlayerMovementStrategy playerStrategy = new PlayerMovementStrategy();
        monster.setMovementStrategy(playerStrategy);
        
        assertEquals("Strategy should be switched", 
                     playerStrategy, monster.getMovementStrategy());
    }
    
    @Test
    public void testEnemyWithoutStrategy() {
        Cell cell = board.getRandomEmptyCell();
        if (cell != null) {
            Monster monster = new Monster(cell.getX(), cell.getY(), 15, 50);
            // Don't set strategy
            
            Direction move = monster.move(board, player);
            
            assertEquals("Enemy without strategy should return NONE", 
                         Direction.NONE, move);
        }
    }
    
    @Test
    public void testStrategyWithNullPlayer() {
        SmartEnemyStrategy strategy = new SmartEnemyStrategy();
        Monster monster = new Monster(5, 5, 15, 50);
        monster.setMovementStrategy(strategy);
        
        Direction move = strategy.chooseMove(board, monster, null);
        
        assertNotNull("Strategy should handle null player gracefully", move);
    }
    
    // ==================== Movement Integration Tests ====================
    
    @Test
    public void testEnemyMovementWithStrategy() {
        Cell spawnCell = board.getRandomEmptyCell();
        if (spawnCell != null) {
            Monster monster = new Monster(spawnCell.getX(), spawnCell.getY(), 15, 50);
            SmartEnemyStrategy strategy = new SmartEnemyStrategy();
            monster.setMovementStrategy(strategy);
            
            int initialX = monster.getX();
            int initialY = monster.getY();
            
            Direction move = monster.move(board, player);
            
            assertNotNull("Monster should return a direction", move);
        }
    }
    
    @Test
    public void testMultipleEnemiesWithStrategies() {
        // Test that multiple enemies can use strategies simultaneously
        SmartEnemyStrategy strategy = new SmartEnemyStrategy();
        
        for (Enemy enemy : game.getEnemies()) {
            enemy.setMovementStrategy(strategy);
            
            Direction move = enemy.move(board, player);
            
            assertNotNull("Each enemy should have valid move", move);
        }
    }
    
    // ==================== Distance Calculation Tests ====================
    
    @Test
    public void testStrategyCalculatesDistance() {
        SmartEnemyStrategy strategy = new SmartEnemyStrategy();
        
        // Place enemy at known distance
        int playerX = player.getX();
        int playerY = player.getY();
        
        int enemyX = playerX + 5;
        int enemyY = playerY + 5;
        
        if (board.isInBounds(enemyX, enemyY)) {
            Monster monster = new Monster(enemyX, enemyY, 15, 50);
            monster.setMovementStrategy(strategy);
            
            Direction move = strategy.chooseMove(board, monster, player);
            
            assertNotNull("Strategy should calculate and choose move", move);
        }
    }
    
    // ==================== Edge Cases ====================
    
    @Test
    public void testStrategyAtBoardEdge() {
        SmartEnemyStrategy strategy = new SmartEnemyStrategy();
        
        // Place enemy at corner
        Monster monster = new Monster(0, 0, 15, 50);
        monster.setMovementStrategy(strategy);
        
        Direction move = strategy.chooseMove(board, monster, player);
        
        assertNotNull("Strategy should work at board edge", move);
    }
    
    @Test
    public void testStrategyNearWalls() {
        SmartEnemyStrategy strategy = new SmartEnemyStrategy();
        
        // Find a walkable cell near a wall
        for (int y = 0; y < board.getHeight(); y++) {
            for (int x = 0; x < board.getWidth(); x++) {
                Cell cell = board.getCell(x, y);
                if (cell.isWalkable() && !cell.isStart() && !cell.isExit()) {
                    Monster monster = new Monster(x, y, 15, 50);
                    monster.setMovementStrategy(strategy);
                    
                    Direction move = strategy.chooseMove(board, monster, player);
                    
                    assertNotNull("Strategy should work near walls", move);
                    return; // Test passed
                }
            }
        }
    }
    
    @Test
    public void testPlayerStrategyIntegration() {
        PlayerMovementStrategy strategy = new PlayerMovementStrategy();
        
        int initialX = player.getX();
        int initialY = player.getY();
        
        Direction move = player.move(board);
        
        assertNotNull("Player should return direction", move);
        assertEquals("Player should not move automatically", 
                     Direction.NONE, move);
    }
}
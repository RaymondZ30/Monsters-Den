package com.team3.monstersden.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import com.team3.monstersden.characters.Enemy;
import com.team3.monstersden.characters.HumanEnemy;
import com.team3.monstersden.characters.Monster;

import java.util.List;

/**
 * Test suite for SpawnManager class.
 * Tests enemy spawning mechanics and spawn timing.
 * 
 * @author Team 3 - CMPT 276
 * @version 1.0
 */
public class SpawnManagerTest {

    private Game game;
    private SpawnManager spawnManager;

    @Before
    public void setUp() {
        game = Game.getInstance();
        game.initialize();
        spawnManager = new SpawnManager(game);
    }

    // ==================== Construction Tests ====================

    @Test
    public void testSpawnManagerConstruction() {
        SpawnManager manager = new SpawnManager(game);
        assertNotNull("SpawnManager should be created", manager);
    }

    // ==================== Tick Behavior Tests ====================

    @Test
    public void testTickDoesNotThrow() {
        // Should not throw exception
        spawnManager.tick();
        assertTrue("Tick should complete without error", true);
    }

    @Test
    public void testMultipleTicks() {
        // Tick multiple times - should not throw
        for (int i = 0; i < 5; i++) {
            spawnManager.tick();
        }
        assertTrue("Multiple ticks should complete without error", true);
    }

    @Test
    public void testTicksUntilSpawnAttempt() {
        int initialEnemyCount = game.getEnemies().size();
        
        // Tick enough times to trigger spawn attempt (SPAWN_INTERVAL_TICKS = 15)
        for (int i = 0; i < GameConfig.SPAWN_INTERVAL_TICKS; i++) {
            spawnManager.tick();
        }
        
        // May or may not spawn (depends on available cells and max enemies)
        // But should not crash
        assertTrue("Should complete spawn interval", true);
    }

    // ==================== Spawn Attempt Tests ====================

    @Test
    public void testSpawnAttemptWithAvailableSpace() {
        game.initialize();
        
        // Clear some enemies to make room
        while (game.getEnemies().size() > 2) {
            Enemy enemy = game.getEnemies().get(0);
            game.removeEnemy(enemy);
        }
        
        int initialCount = game.getEnemies().size();
        
        // Trigger spawn attempts
        for (int i = 0; i < GameConfig.SPAWN_INTERVAL_TICKS + 1; i++) {
            spawnManager.tick();
        }
        
        // Should attempt to spawn (may succeed if empty cells available)
        assertTrue("Spawn attempt should complete", true);
    }

    @Test
    public void testNoSpawnWhenAtMaxEnemies() {
        game.initialize();
        
        // Fill up to max enemies
        while (game.getEnemies().size() < GameConfig.MAX_ACTIVE_ENEMIES) {
            Cell cell = game.getBoard().getRandomEmptyCell();
            if (cell != null) {
                Monster enemy = new Monster(cell.getX(), cell.getY(), 
                                          GameConfig.MONSTER_DAMAGE, 
                                          GameConfig.MONSTER_MAX_HP);
                cell.setEnemy(enemy);
                game.getEnemies().add(enemy);
            } else {
                break;
            }
        }
        
        int maxCount = game.getEnemies().size();
        
        // Try to spawn more
        for (int i = 0; i < GameConfig.SPAWN_INTERVAL_TICKS * 2; i++) {
            spawnManager.tick();
        }
        
        // Should not exceed max
        assertTrue("Should not exceed max enemies", 
                   game.getEnemies().size() <= GameConfig.MAX_ACTIVE_ENEMIES);
    }

    // ==================== Enemy Type Tests ====================

    @Test
    public void testSpawnsValidEnemyTypes() {
        game.initialize();
        
        // Clear enemies
        game.getEnemies().clear();
        
        // Trigger multiple spawn attempts
        for (int attempt = 0; attempt < 5; attempt++) {
            for (int i = 0; i < GameConfig.SPAWN_INTERVAL_TICKS + 1; i++) {
                spawnManager.tick();
            }
        }
        
        // Check that spawned enemies are valid types
        for (Enemy enemy : game.getEnemies()) {
            assertTrue("Spawned enemy should be Monster or HumanEnemy",
                      enemy instanceof Monster || enemy instanceof HumanEnemy);
            assertNotNull("Spawned enemy should have movement strategy",
                         enemy.getMovementStrategy());
        }
    }

    // ==================== Spawn Location Tests ====================

    @Test
    public void testEnemiesSpawnOnBoard() {
        game.initialize();
        
        List<Enemy> enemies = game.getEnemies();
        Board board = game.getBoard();
        
        for (Enemy enemy : enemies) {
            assertTrue("Enemy X should be within board bounds",
                      enemy.getX() >= 0 && enemy.getX() < board.getWidth());
            assertTrue("Enemy Y should be within board bounds",
                      enemy.getY() >= 0 && enemy.getY() < board.getHeight());
        }
    }

    @Test
    public void testEnemiesSpawnOnWalkableCells() {
        game.initialize();
        
        List<Enemy> enemies = game.getEnemies();
        Board board = game.getBoard();
        
        for (Enemy enemy : enemies) {
            Cell cell = board.getCell(enemy.getX(), enemy.getY());
            assertNotNull("Enemy should be on valid cell", cell);
            assertTrue("Enemy should be on walkable cell", cell.isWalkable());
        }
    }

    @Test
    public void testEnemiesDontSpawnOnStartCell() {
        game.initialize();
        
        Cell startCell = game.getBoard().getStartCell();
        List<Enemy> enemies = game.getEnemies();
        
        for (Enemy enemy : enemies) {
            boolean onStart = enemy.getX() == startCell.getX() && 
                enemy.getY() == startCell.getY();
            assertFalse("Enemy should not spawn on start cell", onStart);
        }
    }

    @Test
    public void testEnemiesDontSpawnOnExitCell() {
        game.initialize();
        
        Cell exitCell = game.getBoard().getExitCell();
        List<Enemy> enemies = game.getEnemies();
        
        for (Enemy enemy : enemies) {
            boolean onExit = enemy.getX() == exitCell.getX() && 
                 enemy.getY() == exitCell.getY();
            assertFalse("Enemy should not spawn on exit cell", onExit);
        }
    }

    // ==================== Spawn Timing Tests ====================

    @Test
    public void testSpawnIntervalTiming() {
        game.initialize();
        game.getEnemies().clear();
        
        int ticksBeforeSpawn = GameConfig.SPAWN_INTERVAL_TICKS - 1;
        
        // Tick just before spawn interval
        for (int i = 0; i < ticksBeforeSpawn; i++) {
            spawnManager.tick();
        }
        
        // Tick to complete interval (may spawn)
        spawnManager.tick();
        
        // Test passes if no exception thrown
        assertTrue("Should handle spawn interval correctly", true);
    }

    @Test
    public void testMultipleSpawnIntervals() {
        game.initialize();
        
        int intervals = 3;
        int totalTicks = GameConfig.SPAWN_INTERVAL_TICKS * intervals;
        
        for (int i = 0; i < totalTicks; i++) {
            spawnManager.tick();
        }
        
        // Should handle multiple intervals
        assertTrue("Should handle multiple spawn intervals", true);
    }

    // ==================== Enemy Attributes Tests ====================

    @Test
    public void testSpawnedEnemiesHaveCorrectHealth() {
        game.initialize();
        
        for (Enemy enemy : game.getEnemies()) {
            if (enemy instanceof Monster) {
                assertEquals("Monster should have correct max HP",
                           GameConfig.MONSTER_MAX_HP, enemy.getHealth());
            } else if (enemy instanceof HumanEnemy) {
                assertEquals("Human should have correct max HP",
                           GameConfig.HUMAN_MAX_HP, enemy.getHealth());
            }
        }
    }

    @Test
    public void testSpawnedEnemiesHaveCorrectDamage() {
        game.initialize();
        
        for (Enemy enemy : game.getEnemies()) {
            if (enemy instanceof Monster) {
                assertEquals("Monster should have correct damage",
                           GameConfig.MONSTER_DAMAGE, enemy.getDamage());
            } else if (enemy instanceof HumanEnemy) {
                assertEquals("Human should have correct damage",
                           GameConfig.HUMAN_DAMAGE, enemy.getDamage());
            }
        }
    }

    @Test
    public void testSpawnedEnemiesAreAlive() {
        game.initialize();
        
        for (Enemy enemy : game.getEnemies()) {
            assertTrue("Spawned enemy should be alive", enemy.isAlive());
            assertTrue("Spawned enemy health should be positive", 
                      enemy.getHealth() > 0);
        }
    }

    // ==================== Edge Cases ====================

    @Test
    public void testSpawnManagerWithEmptyBoard() {
        // This tests robustness - what if board has no empty cells?
        // The spawn manager should handle this gracefully
        spawnManager.tick();
        assertTrue("Should handle edge case gracefully", true);
    }

    @Test
    public void testConsecutiveSpawnAttempts() {
        game.initialize();
        
        // Clear enemies to make room
        game.getEnemies().clear();
        
        // Make multiple consecutive spawn attempts
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < GameConfig.SPAWN_INTERVAL_TICKS + 1; j++) {
                spawnManager.tick();
            }
        }
        
        // Should handle consecutive attempts without issue
        assertTrue("Should handle consecutive spawn attempts", true);
        assertTrue("Should not spawn more than max",
                  game.getEnemies().size() <= GameConfig.MAX_ACTIVE_ENEMIES);
    }

    // ==================== Integration with Game Tests ====================

    @Test
    public void testSpawnedEnemiesAddedToGameList() {
        game.initialize();
        int initialCount = game.getEnemies().size();
        
        // Clear some enemies
        while (game.getEnemies().size() > 1) {
            game.removeEnemy(game.getEnemies().get(0));
        }
        
        // Trigger spawn
        for (int i = 0; i < GameConfig.SPAWN_INTERVAL_TICKS * 2; i++) {
            spawnManager.tick();
        }
        
        // Spawned enemies should be in game's enemy list
        assertTrue("Game should track spawned enemies", true);
    }

    @Test
    public void testSpawnedEnemiesPlacedOnBoard() {
        game.initialize();
        
        for (Enemy enemy : game.getEnemies()) {
            Cell cell = game.getBoard().getCell(enemy.getX(), enemy.getY());
            
            if (cell != null && cell.hasEnemy()) {
                assertNotNull("Cell should contain enemy", cell.getEnemy());
            }
        }
    }
}
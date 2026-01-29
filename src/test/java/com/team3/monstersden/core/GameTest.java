package com.team3.monstersden.core;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import com.team3.monstersden.characters.Monster;
import com.team3.monstersden.characters.Player;
import com.team3.monstersden.patterns.observer.GameObserver;
import com.team3.monstersden.patterns.strategy.SmartEnemyStrategy;

/**
 * Comprehensive test suite for the Game class.
 * Tests singleton pattern, game state, scoring, win/lose conditions, and observer pattern.
 * 
 * @author Team 3 - CMPT 276
 * @version 1.0
 */
public class GameTest {

    private Game game;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @Before
    public void setUp() {
        // Get singleton instance and initialize
        game = Game.getInstance();
        game.initialize();
        
        // Capture console output for verification
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    @After
    public void tearDown() {
        // Restore console output
        System.setOut(originalOut);
    }

    // ==================== Singleton Pattern Tests ====================

    @Test
    public void testSingletonInstance() {
        Game instance1 = Game.getInstance();
        Game instance2 = Game.getInstance();
        
        assertSame("getInstance should always return the same instance", instance1, instance2);
    }

    // ==================== Initialization Tests ====================

    @Test
    public void testInitialize() {
        game.initialize();
        
        assertNotNull("Board should be initialized", game.getBoard());
        assertNotNull("Player should be initialized", game.getPlayer());
        assertEquals("Initial score should be 0", 0, game.getScore());
        assertEquals("Initial collected rewards should be 0", 0, game.getCollectedRewards());
    }

    @Test
    public void testPlayerAtStartCell() {
        game.initialize();
        Player player = game.getPlayer();
        Cell startCell = game.getBoard().getStartCell();
        
        assertEquals("Player X should match start cell X", startCell.getX(), player.getX());
        assertEquals("Player Y should match start cell Y", startCell.getY(), player.getY());
    }

    @Test
    public void testEnemiesSpawned() {
        game.initialize();
        
        assertNotNull("Enemies list should not be null", game.getEnemies());
        assertTrue("At least one enemy should be spawned", game.getEnemies().size() > 0);
    }

    // ==================== Score Management Tests ====================

    @Test
    public void testAddPositiveScore() {
        game.initialize();
        int initialScore = game.getScore();
        
        game.addScore(100);
        
        assertEquals("Score should increase by 100", initialScore + 100, game.getScore());
    }

    @Test
    public void testAddNegativeScore() {
        game.initialize();
        game.addScore(50);  // Start with positive score
        
        game.addScore(-20);
        
        assertEquals("Score should decrease by penalty", 30, game.getScore());
    }

    @Test
    public void testScoreCanBeNegative() {
        game.initialize();
        
        game.addScore(-50);
        
        assertTrue("Score should be negative", game.getScore() < 0);
    }

    // ==================== Reward Collection Tests ====================

    @Test
    public void testRegularRewardCollected() {
        game.initialize();
        int initialRewards = game.getCollectedRewards();
        
        game.regularRewardCollected();
        
        assertEquals("Collected rewards should increment by 1", initialRewards + 1, game.getCollectedRewards());
    }

    @Test
    public void testMultipleRewardCollections() {
        game.initialize();
        
        game.regularRewardCollected();
        game.regularRewardCollected();
        game.regularRewardCollected();
        
        assertEquals("Should have collected 3 rewards", 3, game.getCollectedRewards());
    }

    // ==================== Win Condition Tests ====================

    @Test
    public void testWinConditionWithoutAllRewards() {
        game.initialize();
        Player player = game.getPlayer();
        Cell exitCell = game.getBoard().getExitCell();
        
        // Move player to exit
        player.setX(exitCell.getX());
        player.setY(exitCell.getY());
        
        assertFalse("Should not win without collecting all rewards", game.checkWinCondition());
    }

    @Test
    public void testWinConditionWithRewardsNotAtExit() {
        game.initialize();
        
        // Collect all required rewards
        int totalRewards = game.getTotalRewards();
        for (int i = 0; i < totalRewards; i++) {
            game.regularRewardCollected();
        }
        
        assertFalse("Should not win if not at exit cell", game.checkWinCondition());
    }

    @Test
    public void testWinConditionMet() {
        game.initialize();
        Player player = game.getPlayer();
        Cell exitCell = game.getBoard().getExitCell();
        
        // Collect all rewards
        int totalRewards = game.getTotalRewards();
        for (int i = 0; i < totalRewards; i++) {
            game.regularRewardCollected();
        }
        
        // Move to exit
        player.setX(exitCell.getX());
        player.setY(exitCell.getY());
        
        assertTrue("Should win when at exit with all rewards", game.checkWinCondition());
    }

    // ==================== Game Over Tests ====================

    @Test
    public void testGameNotOverInitially() {
        game.initialize();
        
        assertFalse("Game should not be over initially", game.isGameOver());
    }

    // ==================== Enemy Management Tests ====================

    @Test
    public void testRemoveEnemy() {
        game.initialize();
        
        if (game.getEnemies().isEmpty()) {
            // Add an enemy for testing
            Cell cell = game.getBoard().getRandomEmptyCell();
            Monster enemy = new Monster(cell.getX(), cell.getY(), 20, 60);
            enemy.setMovementStrategy(new SmartEnemyStrategy());
            cell.setEnemy(enemy);
            game.getEnemies().add(enemy);
        }
        
        int initialCount = game.getEnemies().size();
        var enemy = game.getEnemies().get(0);
        
        game.removeEnemy(enemy);
        
        assertEquals("Enemy count should decrease by 1", initialCount - 1, game.getEnemies().size());
    }

    @Test
    public void testRemoveNullEnemy() {
        game.initialize();
        int initialCount = game.getEnemies().size();
        
        game.removeEnemy(null);
        
        assertEquals("Enemy count should remain unchanged", initialCount, game.getEnemies().size());
    }

    // ==================== Observer Pattern Tests ====================

    @Test
    public void testAddObserver() {
        game.initialize();
        TestObserver observer = new TestObserver();
        
        game.addObserver(observer);
        game.notifyObservers();
        
        assertTrue("Observer should be notified", observer.wasNotified);
    }

    @Test
    public void testScoreChangeNotification() {
        game.initialize();
        TestObserver observer = new TestObserver();
        game.addObserver(observer);
        
        game.addScore(100);
        
        assertTrue("Observer should be notified of score change", observer.scoreChanged);
        assertEquals("Observer should receive correct score", 100, observer.lastScore);
    }

    @Test
    public void testGameEndNotificationWin() {
        game.initialize();
        TestObserver observer = new TestObserver();
        game.addObserver(observer);
        
        game.notifyGameEnd(true, 500);
        
        assertTrue("Observer should be notified of game end", observer.gameEnded);
        assertTrue("Observer should know player won", observer.won);
        assertEquals("Observer should receive final score", 500, observer.finalScore);
    }

    @Test
    public void testGameEndNotificationLoss() {
        game.initialize();
        TestObserver observer = new TestObserver();
        game.addObserver(observer);
        
        game.notifyGameEnd(false, 200);
        
        assertTrue("Observer should be notified of game end", observer.gameEnded);
        assertFalse("Observer should know player lost", observer.won);
        assertEquals("Observer should receive final score", 200, observer.finalScore);
    }

    // ==================== Getter Tests ====================

    @Test
    public void testGetBoard() {
        game.initialize();
        
        assertNotNull("getBoard should return non-null board", game.getBoard());
        assertTrue("Board should have positive width", game.getBoard().getWidth() > 0);
        assertTrue("Board should have positive height", game.getBoard().getHeight() > 0);
    }

    @Test
    public void testGetPlayer() {
        game.initialize();
        
        assertNotNull("getPlayer should return non-null player", game.getPlayer());
        assertEquals("Player should start with configured health", 
                    GameConfig.PLAYER_START_HP, game.getPlayer().getHealth());
    }

    @Test
    public void testGetEnemies() {
        game.initialize();
        
        assertNotNull("getEnemies should return non-null list", game.getEnemies());
    }

    @Test
    public void testGetTotalRewards() {
        game.initialize();
        
        assertTrue("Total rewards should be positive", game.getTotalRewards() > 0);
    }

    // ==================== Edge Cases ====================

    @Test
    public void testMultipleInitializations() {
        game.initialize();
        game.addScore(500);
        
        game.initialize();
        
        assertEquals("Score should reset to 0 after re-initialization", 0, game.getScore());
        assertEquals("Collected rewards should reset", 0, game.getCollectedRewards());
    }

    @Test
    public void testSetPlayer() {
        game.initialize();
        Player newPlayer = new Player("Test", 5, 5);
        
        game.setPlayer(newPlayer);
        
        assertSame("Player should be updated", newPlayer, game.getPlayer());
        assertEquals("Player name should match", "Test", game.getPlayer().getName());
    }

    // ==================== Helper Classes ====================

    /**
     * Test observer implementation for verifying observer pattern.
     */
    private static class TestObserver implements GameObserver {
        boolean wasNotified = false;
        boolean scoreChanged = false;
        boolean gameEnded = false;
        boolean won = false;
        int lastScore = 0;
        int finalScore = 0;

        @Override
        public void update() {
            wasNotified = true;
        }

        @Override
        public void onScoreChanged(int newScore) {
            scoreChanged = true;
            lastScore = newScore;
        }

        @Override
        public void onBonusTimerUpdate(int ticksRemaining) {
            // Not tested here
        }

        @Override
        public void onGameEnd(boolean won, int finalScore) {
            gameEnded = true;
            this.won = won;
            this.finalScore = finalScore;
        }
    }
}
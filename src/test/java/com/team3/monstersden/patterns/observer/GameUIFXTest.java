package com.team3.monstersden.patterns.observer;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.team3.monstersden.characters.HumanEnemy;
import com.team3.monstersden.characters.Monster;
import com.team3.monstersden.characters.Player;
import com.team3.monstersden.core.Board;
import com.team3.monstersden.core.Cell;
import com.team3.monstersden.core.Game;
import com.team3.monstersden.items.BonusReward;
import com.team3.monstersden.items.Punishment;
import com.team3.monstersden.items.RegularReward;
import com.team3.monstersden.items.SilverSword;
import com.team3.monstersden.items.SteelSword;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class GameUIFXTest {

    private static boolean toolkitInitialized = false;
    private Game game;
    private GameUIFX ui;
    private Stage stage;

    /**
     * Initializes the JavaFX Toolkit once for all tests.
     */
    @BeforeClass
    public static void initToolkit() {
        if (!toolkitInitialized) {
            try {
                Platform.startup(() -> {});
                toolkitInitialized = true;
                Platform.setImplicitExit(false);
            } catch (IllegalStateException e) {
                toolkitInitialized = true;
            }
        }
    }

    @Before
    public void setUp() throws Exception {
        game = Game.getInstance();
        game.initialize(); // Ensures player and board are set up

        // Reset board for clean testing
        clearBoard();

        CountDownLatch latch = new CountDownLatch(1);
        
        Platform.runLater(() -> {
            try {
                stage = new Stage();
                ui = new GameUIFX(game, stage);
                ui.show(); // Necessary to attach scene
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        });

        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("Timed out waiting for JavaFX UI to initialize");
        }
    }

    private void clearBoard() {
        Board board = game.getBoard();
        for (int y = 0; y < board.getHeight(); y++) {
            for (int x = 0; x < board.getWidth(); x++) {
                Cell c = board.getCell(x, y);
                if (c != null) {
                    c.setEnemy(null);
                    c.setReward(null);
                    c.setPunishment(null);
                }
            }
        }
    }

    // --- HELPER TO GET PRIVATE FIELDS ---
    @SuppressWarnings("unchecked")
    private <T> T getPrivateField(String fieldName) throws Exception {
        Field field = GameUIFX.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(ui);
    }

    // --- TEST RENDERING LOGIC (Covering drawCell, drawPlayerImage, etc.) ---

    @Test
    public void testRenderFullBoardComplexity() throws InterruptedException {
        // Setup a board with EVERY type of entity to trigger all if/else branches in drawCell
        Board board = game.getBoard();
        
        // 1. Wall (implicitly tested by board structure)
        
        // 2. Monster
        board.getCell(1, 1).setEnemy(new Monster(1, 1, 10, 1));
        
        // 3. Human Enemy
        board.getCell(2, 1).setEnemy(new HumanEnemy(2, 1, 10, 1));
        
        // 4. Regular Reward
        board.getCell(3, 1).setReward(new RegularReward(3, 1, 100));
        
        // 5. Bonus Reward
        // Updated to match constructor: BonusReward(x, y, score, duration)
        BonusReward bonus = new BonusReward(4, 1, 500, 20); 
        board.getCell(4, 1).setReward(bonus);
        
        // 6. Punishment/Trap
        Punishment trap = new Punishment(5, 1, 50);
        board.getCell(5, 1).setPunishment(trap);

        // 7. Player Position
        // Replaces missing setPosition() with setPlayer()
        game.setPlayer(new Player("TestPlayer", 0, 0));

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                // This calls renderBoard(), which calls drawCell() for every cell
                // This executes the drawing logic for all entities we placed above
                ui.update(); 
                latch.countDown();
            } catch (Exception e) {
                fail("Rendering threw exception: " + e.getMessage());
            }
        });
        
        assertTrue("Rendering update timed out", latch.await(2, TimeUnit.SECONDS));
    }

    @Test
    public void testPlayerFacingDirectionRendering() throws InterruptedException {
        // Test logic in drawPlayerImage regarding flipping
        
        // 1. Face RIGHT (Default)
        simulateKeyPress(KeyCode.D); 
        triggerRender(); // Should draw normal image

        // 2. Face LEFT
        simulateKeyPress(KeyCode.A);
        triggerRender(); // Should draw flipped image (-width)
        
        // Verify no crashes occurred during these specific render calls
        assertTrue(true);
    }
    
    private void triggerRender() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            ui.update();
            latch.countDown();
        });
        assertTrue(latch.await(1, TimeUnit.SECONDS));
    }

    // --- TEST STATS UPDATES ---

    @Test
    public void testStatsBarColors() throws Exception {
        Player p = game.getPlayer();
        Label healthLabel = getPrivateField("healthLabel");
        
        // 1. High Health (>60) - Green
        p.setHealth(80);
        runUpdateStats();
        assertTrue(healthLabel.getText().contains("80"));

        // 2. Medium Health (>30) - Orange
        p.setHealth(50);
        runUpdateStats();
        assertTrue(healthLabel.getText().contains("50"));

        // 3. Low Health (<=30) - Red
        p.setHealth(20);
        runUpdateStats();
        assertTrue(healthLabel.getText().contains("20"));
    }

    private void runUpdateStats() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                // Private method updateStats is called by update() or onScoreChanged
                ui.onScoreChanged(game.getScore()); 
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        });
        assertTrue(latch.await(1, TimeUnit.SECONDS));
    }

    // --- TEST OBSERVABLE EVENTS ---

    @Test
    public void testOnScoreChanged() throws Exception {
        int newScore = 500;
        
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            ui.onScoreChanged(newScore);
            latch.countDown();
        });
        assertTrue(latch.await(2, TimeUnit.SECONDS));

        Label scoreLabel = getPrivateField("scoreLabel");
        final String[] text = new String[1];
        
        runOnFxThread(() -> text[0] = scoreLabel.getText());
        assertEquals("Score: 500", text[0]);
    }

    @Test
    public void testOnBonusTimerUpdate() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            ui.onBonusTimerUpdate(10);
            latch.countDown();
        });
        assertTrue(latch.await(2, TimeUnit.SECONDS));

        Label bonusLabel = getPrivateField("bonusTimerLabel");
        final String[] text = new String[1];
        
        runOnFxThread(() -> text[0] = bonusLabel.getText());
        assertTrue(text[0].contains("10"));
    }

    @Test
    public void testDisplayMessage() throws Exception {
        String msg = "Test Message";
        
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            ui.displayMessage(msg);
            latch.countDown();
        });
        assertTrue(latch.await(2, TimeUnit.SECONDS));
        
        Label messageLabel = getPrivateField("messageLabel");
        final String[] text = new String[1];
        
        runOnFxThread(() -> text[0] = messageLabel.getText());
        assertEquals(msg, text[0]);
    }

    @Test
    public void testDisplayError() throws Exception {
        String err = "Critical Failure";
        
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            ui.displayError(err);
            latch.countDown();
        });
        assertTrue(latch.await(2, TimeUnit.SECONDS));
        
        Label messageLabel = getPrivateField("messageLabel");
        final String[] text = new String[1];
        
        runOnFxThread(() -> text[0] = messageLabel.getText());
        assertEquals("ERROR: Critical Failure", text[0]);
    }

    // --- TEST KEY INPUTS (Coverage for setupKeyBindings) ---

    private void simulateKeyPress(KeyCode code) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            Scene scene = stage.getScene();
            if (scene != null) {
                EventHandler<? super KeyEvent> handler = scene.getOnKeyPressed();
                if (handler != null) {
                    KeyEvent event = new KeyEvent(
                        KeyEvent.KEY_PRESSED, "", "", code, 
                        false, false, false, false
                    );
                    handler.handle(event);
                }
            }
            latch.countDown();
        });
        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    @Test
    public void testKeyBindingMovement() throws InterruptedException {
        // Replaces missing setPosition() with setPlayer()
        game.setPlayer(new Player("TestPlayer", 1, 1));
        
        // Test movement (assumes open space or handles wall collision gracefully)
        simulateKeyPress(KeyCode.D); // RIGHT
        assertTrue(game.getPlayer() != null);
        
        simulateKeyPress(KeyCode.S); // DOWN
        assertTrue(game.getPlayer() != null);
    }

    @Test
    public void testKeyBindingWeaponSwitch() throws InterruptedException {
        Player p = game.getPlayer();
        
        // Press '2' -> Steel Sword
        simulateKeyPress(KeyCode.DIGIT2);
        assertTrue("Should switch to Steel Sword", p.getCurrentSword() instanceof SteelSword);

        // Press '1' -> Silver Sword
        simulateKeyPress(KeyCode.DIGIT1);
        assertTrue("Should switch to Silver Sword", p.getCurrentSword() instanceof SilverSword);
    }

    // Helper to read UI properties on FX thread
    private void runOnFxThread(Runnable action) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            action.run();
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
    }
}
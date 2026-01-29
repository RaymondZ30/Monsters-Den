package com.team3.monstersden.patterns.observer;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import com.team3.monstersden.core.Game;

public class GameUITest {

    private Game game;
    private GameUI ui;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @Before
    public void setUp() {
        // Redirect System.out to capture output
        System.setOut(new PrintStream(outContent));
        game = Game.getInstance();
        game.initialize();
        ui = new GameUI(game, false); // Disable colors for easier string matching
    }

    @After
    public void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    public void testRender() {
        ui.render();
        String output = outContent.toString();
        // Verify key UI elements are present
        assertTrue(output.contains("MONSTER'S DEN"));
        assertTrue(output.contains("Score:"));
        assertTrue(output.contains("Health:"));
    }

    @Test
    public void testDisplayStats() {
        game.addScore(500);
        // Manually trigger the observer method
        ui.onScoreChanged(500);
        
        String output = outContent.toString();
        assertTrue("Output should contain the score", output.contains("500"));
    }

    @Test
    public void testGameEndDisplay() {
        ui.onGameEnd(true, 1000);
        String output = outContent.toString();
        assertTrue(output.contains("VICTORY"));
        assertTrue(output.contains("1000"));
    }
}
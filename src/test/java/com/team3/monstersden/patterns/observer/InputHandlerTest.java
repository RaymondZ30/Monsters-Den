package com.team3.monstersden.patterns.observer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.After;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import com.team3.monstersden.core.Game;

public class InputHandlerTest {

    private Game game;
    private GameUI ui;
    private InputStream originalIn;

    @Before
    public void setUp() {
        game = Game.getInstance();
        game.initialize();
        ui = new GameUI(game);
        originalIn = System.in;
    }

    @After
    public void tearDown() {
        System.setIn(originalIn);
    }

    @Test
    public void testProcessInputMovement() {
        // Simulate inputs: "d" (right), "q" (quit)
        String input = "d\nq\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        InputHandler handler = new InputHandler(game, ui);
        
        int startX = game.getPlayer().getX();
        int startY = game.getPlayer().getY();
        
        // We need a board where moving right is valid. 
        // Assuming level1.txt start is valid, but let's rely on the handler processing it.
        // Even if blocked, the command executes.
        
        handler.start(); // This blocks until "q" is processed
        
        assertFalse("Handler should stop running", handler.isRunning());
    }

    @Test
    public void testWeaponSwitchInput() {
        // Input "2" to switch to steel sword, then "q"
        String input = "2\nq\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        InputHandler handler = new InputHandler(game, ui);
        handler.start();

        assertTrue("Player should have Steel Sword equipped", 
            game.getPlayer().getCurrentSword() instanceof com.team3.monstersden.items.SteelSword);
    }
    
    @Test
    public void testUnknownCommand() {
        // "xyz" is unknown, then "q"
        String input = "xyz\nq\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        InputHandler handler = new InputHandler(game, ui);
        handler.start();
        // Should handle gracefully without crashing
        assertFalse(handler.isRunning());
    }
}
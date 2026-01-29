package com.team3.monstersden.console;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

import com.team3.monstersden.core.Game;

public class TerminalControllerTest {

    @Test
    public void testRunExitsOnQ() {
        // Input 'Q' to quit immediately
        ByteArrayInputStream in = new ByteArrayInputStream("Q\n".getBytes());
        System.setIn(in);
        
        // Capture output
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        Game game = Game.getInstance();
        LocalMaze maze = new LocalMaze();
        TerminalController controller = new TerminalController(game, maze);
        
        controller.run();
        
        String output = out.toString();
        assertTrue(output.contains("Bye!"));
    }
    
    @Test
    public void testMoveCommands() {
        // Move Right (D), then Quit
        ByteArrayInputStream in = new ByteArrayInputStream("D\nQ\n".getBytes());
        System.setIn(in);
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        Game game = Game.getInstance();
        LocalMaze maze = new LocalMaze(); // Starts at (1,1)
        TerminalController controller = new TerminalController(game, maze);
        
        controller.run();
        
        // Check if maze rendered (contains walls)
        assertTrue(out.toString().contains("#"));
    }
}
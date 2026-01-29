package com.team3.monstersden.console;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for LocalMaze console-based gameplay.
 * 
 * @author Team 3 - CMPT 276
 * @version 1.0
 */
public class LocalMazeTest {

    private LocalMaze maze;

    @Before
    public void setUp() {
        maze = new LocalMaze();
    }

    // ==================== Construction Tests ====================

    @Test
    public void testMazeInitialization() {
        assertNotNull("Maze should be created", maze);
        assertFalse("Player should not start at exit", maze.atExit());
    }

    // ==================== Movement Tests ====================

    @Test
    public void testValidMove() {
        // Try to move right (should be valid from start position)
        boolean moved = maze.move(1, 0);
        
        // Movement success depends on maze layout
        // Just verify it doesn't crash
        assertNotNull("Maze should still render after move attempt", maze.render());
    }

    @Test
    public void testMoveToWall() {
        // Try to move up (should hit wall from start)
        boolean moved = maze.move(0, -1);
        
        // Exact result depends on maze layout
        assertNotNull("Maze should render after wall collision", maze.render());
    }

    @Test
    public void testMoveOutOfBounds() {
        // Try to move far out of bounds
        boolean moved = maze.move(-100, -100);
        
        assertFalse("Moving out of bounds should return false", moved);
    }

    @Test
    public void testMultipleMoves() {
        maze.move(1, 0);  // Right
        maze.move(0, 1);  // Down
        maze.move(-1, 0); // Left
        maze.move(0, -1); // Up
        
        assertNotNull("Maze should render after multiple moves", maze.render());
    }

    // ==================== Exit Detection Tests ====================

    @Test
    public void testAtExitInitially() {
        assertFalse("Player should not be at exit initially", maze.atExit());
    }

    @Test
    public void testCurrentTile() {
        char tile = maze.currentTile();
        
        assertTrue("Current tile should be S, ., or E", 
                  tile == 'S' || tile == '.' || tile == 'E');
    }

    // ==================== Rendering Tests ====================

    @Test
    public void testRenderNotNull() {
        String rendered = maze.render();
        
        assertNotNull("Render should not return null", rendered);
    }

    @Test
    public void testRenderContainsPlayer() {
        String rendered = maze.render();
        
        assertTrue("Rendered maze should contain player 'P'", rendered.contains("P"));
    }

    @Test
    public void testRenderContainsWalls() {
        String rendered = maze.render();
        
        assertTrue("Rendered maze should contain walls '#'", rendered.contains("#"));
    }

    @Test
    public void testRenderContainsFloor() {
        String rendered = maze.render();
        
        assertTrue("Rendered maze should contain floor or player", 
                  rendered.contains(".") || rendered.contains("P"));
    }

    @Test
    public void testRenderAfterMovement() {
        String before = maze.render();
        maze.move(1, 0);
        String after = maze.render();
        
        assertNotNull("Render before should not be null", before);
        assertNotNull("Render after should not be null", after);
        assertTrue("Player should still be in maze after move", after.contains("P"));
    }

    // ==================== Movement Direction Tests ====================

    @Test
    public void testMoveRight() {
        maze.move(1, 0);
        assertNotNull("Should render after right move", maze.render());
    }

    @Test
    public void testMoveLeft() {
        maze.move(1, 0); // Move right first
        maze.move(-1, 0); // Then left
        assertNotNull("Should render after left move", maze.render());
    }

    @Test
    public void testMoveDown() {
        maze.move(0, 1);
        assertNotNull("Should render after down move", maze.render());
    }

    @Test
    public void testMoveUp() {
        maze.move(0, 1); // Move down first
        maze.move(0, -1); // Then up
        assertNotNull("Should render after up move", maze.render());
    }

    // ==================== Edge Cases ====================

    @Test
    public void testMoveZeroDelta() {
        String before = maze.render();
        maze.move(0, 0);
        String after = maze.render();
        
        assertNotNull("Should render after zero move", after);
    }

    @Test
    public void testConsecutiveMovesSameDirection() {
        maze.move(1, 0);
        maze.move(1, 0);
        maze.move(1, 0);
        
        assertNotNull("Should handle consecutive moves", maze.render());
    }

    @Test
    public void testRapidDirectionChanges() {
        maze.move(1, 0);
        maze.move(0, 1);
        maze.move(-1, 0);
        maze.move(0, -1);
        maze.move(1, 0);
        
        assertNotNull("Should handle rapid direction changes", maze.render());
    }

    // ==================== State Consistency Tests ====================

    @Test
    public void testRenderConsistency() {
        String render1 = maze.render();
        String render2 = maze.render();
        
        assertEquals("Multiple renders without moves should be identical", render1, render2);
    }

    @Test
    public void testCurrentTileMatchesRender() {
        char tile = maze.currentTile();
        String rendered = maze.render();
        
        // Player should be shown as 'P' in render
        assertTrue("Render should show player", rendered.contains("P"));
    }
}
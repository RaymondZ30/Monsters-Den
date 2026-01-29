package com.team3.monstersden.core;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for Board class.
 * Tests map loading, cell access, and validation.
 * 
 * @author Team 3 - CMPT 276
 * @version 1.0
 */
public class BoardTest {

    private Board board;

    @Before
    public void setUp() {
        board = new Board();
    }

    // ==================== Map Loading Tests ====================

    @Test
    public void testLoadValidMap() {
        try {
            board.loadMap("level1.txt");
            assertTrue("Board width should be positive", board.getWidth() > 0);
            assertTrue("Board height should be positive", board.getHeight() > 0);
        } catch (IOException e) {
            fail("Should load valid map without exception: " + e.getMessage());
        }
    }

    @Test
    public void testMapHasStartCell() throws IOException {
        board.loadMap("level1.txt");
        
        assertNotNull("Board should have a start cell", board.getStartCell());
        assertTrue("Start cell should be marked as start", board.getStartCell().isStart());
    }

    @Test
    public void testMapHasExitCell() throws IOException {
        board.loadMap("level1.txt");
        
        assertNotNull("Board should have an exit cell", board.getExitCell());
        assertTrue("Exit cell should be marked as exit", board.getExitCell().isExit());
    }

    @Test
    public void testMapDimensionsConsistent() throws IOException {
        board.loadMap("level1.txt");
        
        Cell[][] grid = board.getGrid();
        assertNotNull("Grid should not be null", grid);
        assertEquals("Grid height should match board height", board.getHeight(), grid.length);
        assertEquals("Grid width should match board width", board.getWidth(), grid[0].length);
    }

    @Test(expected = IOException.class)
    public void testLoadNonExistentMap() throws IOException {
        board.loadMap("nonexistent.txt");
    }

    // ==================== Cell Access Tests ====================

    @Test
    public void testGetCellValid() throws IOException {
        board.loadMap("level1.txt");
        
        Cell cell = board.getCell(0, 0);
        assertNotNull("Cell at (0,0) should exist", cell);
        assertEquals("Cell X coordinate should be 0", 0, cell.getX());
        assertEquals("Cell Y coordinate should be 0", 0, cell.getY());
    }

    @Test
    public void testGetCellOutOfBounds() throws IOException {
        board.loadMap("level1.txt");
        
        Cell cell = board.getCell(-1, -1);
        assertNull("Cell at negative coordinates should be null", cell);
        
        cell = board.getCell(1000, 1000);
        assertNull("Cell at large coordinates should be null", cell);
    }

    @Test
    public void testGetCellAllPositions() throws IOException {
        board.loadMap("level1.txt");
        
        for (int y = 0; y < board.getHeight(); y++) {
            for (int x = 0; x < board.getWidth(); x++) {
                Cell cell = board.getCell(x, y);
                assertNotNull("Cell at (" + x + "," + y + ") should not be null", cell);
                assertEquals("Cell X should match", x, cell.getX());
                assertEquals("Cell Y should match", y, cell.getY());
            }
        }
    }

    // ==================== Bounds Checking Tests ====================

    @Test
    public void testIsInBoundsValid() throws IOException {
        board.loadMap("level1.txt");
        
        assertTrue("Origin should be in bounds", board.isInBounds(0, 0));
        assertTrue("Max coordinates should be in bounds", 
                  board.isInBounds(board.getWidth() - 1, board.getHeight() - 1));
        assertTrue("Center should be in bounds", 
                  board.isInBounds(board.getWidth() / 2, board.getHeight() / 2));
    }

    @Test
    public void testIsInBoundsInvalid() throws IOException {
        board.loadMap("level1.txt");
        
        assertFalse("Negative X should be out of bounds", board.isInBounds(-1, 0));
        assertFalse("Negative Y should be out of bounds", board.isInBounds(0, -1));
        assertFalse("X >= width should be out of bounds", board.isInBounds(board.getWidth(), 0));
        assertFalse("Y >= height should be out of bounds", board.isInBounds(0, board.getHeight()));
    }

    // ==================== Movement Validation Tests ====================

    @Test
    public void testIsValidMoveOnWalkableCell() throws IOException {
        board.loadMap("level1.txt");
        
        // Find a walkable cell (non-wall)
        Cell walkableCell = null;
        for (int y = 0; y < board.getHeight(); y++) {
            for (int x = 0; x < board.getWidth(); x++) {
                Cell cell = board.getCell(x, y);
                if (cell.isWalkable()) {
                    walkableCell = cell;
                    break;
                }
            }
            if (walkableCell != null) break;
        }
        
        assertNotNull("Should find at least one walkable cell", walkableCell);
        assertTrue("Move to walkable cell should be valid", 
                  board.isValidMove(walkableCell.getX(), walkableCell.getY()));
    }

    @Test
    public void testIsValidMoveOnWall() throws IOException {
        board.loadMap("level1.txt");
        
        // Find a wall cell
        Cell wallCell = null;
        for (int y = 0; y < board.getHeight(); y++) {
            for (int x = 0; x < board.getWidth(); x++) {
                Cell cell = board.getCell(x, y);
                if (cell.isWall()) {
                    wallCell = cell;
                    break;
                }
            }
            if (wallCell != null) break;
        }
        
        assertNotNull("Should find at least one wall cell", wallCell);
        assertFalse("Move to wall should be invalid", 
                   board.isValidMove(wallCell.getX(), wallCell.getY()));
    }

    @Test
    public void testIsValidMoveOutOfBounds() throws IOException {
        board.loadMap("level1.txt");
        
        assertFalse("Move to negative X should be invalid", board.isValidMove(-1, 0));
        assertFalse("Move to negative Y should be invalid", board.isValidMove(0, -1));
        assertFalse("Move beyond width should be invalid", board.isValidMove(board.getWidth(), 0));
        assertFalse("Move beyond height should be invalid", board.isValidMove(0, board.getHeight()));
    }

    // ==================== Random Empty Cell Tests ====================

    @Test
    public void testGetRandomEmptyCell() throws IOException {
        board.loadMap("level1.txt");
        
        Cell cell = board.getRandomEmptyCell();
        
        if (cell != null) {
            assertTrue("Random cell should be walkable", cell.isWalkable());
            assertFalse("Random cell should not be start", cell.isStart());
            assertFalse("Random cell should not be exit", cell.isExit());
            assertNull("Random cell should have no enemy", cell.getEnemy());
            assertNull("Random cell should have no reward", cell.getReward());
            assertNull("Random cell should have no punishment", cell.getPunishment());
        }
    }

    @Test
    public void testGetRandomEmptyCellMultipleTimes() throws IOException {
        board.loadMap("level1.txt");
        
        Cell cell1 = board.getRandomEmptyCell();
        Cell cell2 = board.getRandomEmptyCell();
        
        if (cell1 != null) {
            assertTrue("First random cell should be walkable", cell1.isWalkable());
        }
        if (cell2 != null) {
            assertTrue("Second random cell should be walkable", cell2.isWalkable());
        }
    }

    // ==================== Grid Access Tests ====================

    @Test
    public void testGetGrid() throws IOException {
        board.loadMap("level1.txt");
        
        Cell[][] grid = board.getGrid();
        assertNotNull("Grid should not be null", grid);
        assertTrue("Grid should have rows", grid.length > 0);
        assertTrue("Grid should have columns", grid[0].length > 0);
    }

    // ==================== Dimension Tests ====================

    @Test
    public void testGetWidth() throws IOException {
        board.loadMap("level1.txt");
        
        int width = board.getWidth();
        assertTrue("Width should be positive", width > 0);
        assertEquals("Width should match grid width", width, board.getGrid()[0].length);
    }

    @Test
    public void testGetHeight() throws IOException {
        board.loadMap("level1.txt");
        
        int height = board.getHeight();
        assertTrue("Height should be positive", height > 0);
        assertEquals("Height should match grid height", height, board.getGrid().length);
    }

    // ==================== toString Tests ====================

    @Test
    public void testToStringBeforeLoad() {
        String result = board.toString();
        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain 'Board'", result.contains("Board"));
    }

    @Test
    public void testToStringAfterLoad() throws IOException {
        board.loadMap("level1.txt");
        
        String result = board.toString();
        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain 'Board'", result.contains("Board"));
        assertTrue("toString should contain width", 
                  result.contains(String.valueOf(board.getWidth())));
        assertTrue("toString should contain height", 
                  result.contains(String.valueOf(board.getHeight())));
    }

    // ==================== Print Board Tests ====================

    @Test
    public void testPrintBoardBeforeInit() {
        board.printBoard(); // Should not throw
    }

    @Test
    public void testPrintBoardAfterLoad() throws IOException {
        board.loadMap("level1.txt");
        board.printBoard(); // Should not throw
    }

    // ==================== Edge Cases ====================

    @Test
    public void testBoardBeforeLoad() {
        assertEquals("Width should be 0 before loading", 0, board.getWidth());
        assertEquals("Height should be 0 before loading", 0, board.getHeight());
        assertNull("Start cell should be null before loading", board.getStartCell());
        assertNull("Exit cell should be null before loading", board.getExitCell());
    }

    @Test
    public void testGetCellBeforeLoad() {
        Cell cell = board.getCell(0, 0);
        assertNull("getCell should return null before map is loaded", cell);
    }

    @Test
    public void testIsInBoundsBeforeLoad() {
        assertFalse("No coordinates should be in bounds before loading", board.isInBounds(0, 0));
    }

    @Test
    public void testIsValidMoveBeforeLoad() {
        assertFalse("No move should be valid before loading", board.isValidMove(0, 0));
    }
}
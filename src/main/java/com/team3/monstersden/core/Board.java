package com.team3.monstersden.core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import java.util.Random;


/**
 * Represents the game board consisting of a 2D grid of cells.
 * Manages the dungeon layout, including walls, start position, and exit.
 * 
 * <p>The Board is responsible for:
 * <ul>
 *   <li>Loading map data from files</li>
 *   <li>Maintaining the grid structure</li>
 *   <li>Providing access to individual cells</li>
 *   <li>Validating movement within the grid</li>
 *   <li>Tracking start and exit positions</li>
 * </ul>
 * </p>
 * 
 * <p>Map file format:
 * <pre>
 * # = Wall
 * . = Empty space
 * S = Start position
 * E = Exit position
 * </pre>
 * </p>
 * 
 * @author Team 3 - CMPT 276
 * @version 1.0
 * @since Phase 2
 */
public class Board {
    
    /** 2D grid of cells representing the game board */
    private Cell[][] grid;
    
    /** Width of the board (number of columns) */
    private int width;
    
    /** Height of the board (number of rows) */
    private int height;
    
    /** The starting cell where the player begins */
    private Cell startCell;
    
    /** The exit cell that must be reached to win */
    private Cell exitCell;
    
    /**
     * Constructs an empty Board.
     * The board must be initialized by calling loadMap() before use.
     */

    private static final Random RANDOM = new Random();

    /**
     * Constructor to initialize board
     */
    public Board() {
        this.grid = null;
        this.width = 0;
        this.height = 0;
        this.startCell = null;
        this.exitCell = null;
    }
    
    /**
     * Loads a map from the specified file and initializes the board grid.
     * 
     * <p>The map file should contain ASCII characters representing the layout:
     * <ul>
     *   <li>'#' for walls</li>
     *   <li>'.' for empty spaces</li>
     *   <li>'S' for the start position</li>
     *   <li>'E' for the exit position</li>
     * </ul>
     * </p>
     * 
     * @param filename the name of the map file in the resources/maps directory
     * @throws IOException if the file cannot be read
     * @throws IllegalArgumentException if the map format is invalid
     */
    public void loadMap(String filename) throws IOException {
        List<String> lines = new ArrayList<>();
        
        // Try to load from resources first
        InputStream is = getClass().getClassLoader().getResourceAsStream("maps/" + filename);
        
        if (is != null) {
            // Load from resources (packaged in JAR)
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty() && !line.startsWith("//")) {
                        lines.add(line);
                    }
                }
            }
        } else {
            // Try to load from file system (development)
            try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resource/maps/" + filename))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty() && !line.startsWith("//")) {
                        lines.add(line);
                    }
                }
            }
        }
        
        if (lines.isEmpty()) {
            throw new IOException("Map file is empty or not found: " + filename);
        }
        
        // Determine board dimensions
        height = lines.size();
        width = lines.get(0).length();
        
        // Validate all rows have same width
        for (String line : lines) {
            if (line.length() != width) {
                throw new IllegalArgumentException("Map rows must have equal width");
            }
        }
        
        // Initialize grid
        grid = new Cell[height][width];
        
        // Parse map and create cells
        for (int y = 0; y < height; y++) {
            String line = lines.get(y);
            for (int x = 0; x < width; x++) {
                Cell cell = new Cell(x, y);
                char c = line.charAt(x);
                
                switch (c) {
                    case '#':
                        cell.setWall(true);
                        break;
                    case 'S':
                        cell.setStart(true);
                        startCell = cell;
                        break;
                    case 'E':
                        cell.setExit(true);
                        exitCell = cell;
                        break;
                    case '.':
                        // Empty cell, do nothing
                        break;
                    default:
                        // Treat unknown characters as empty
                        System.out.println("Warning: Unknown character '" + c + "' at (" + x + "," + y + "), treating as empty");
                        break;
                }
                
                grid[y][x] = cell;
            }
        }
        
        // Validate start and exit positions exist
        if (startCell == null) {
            throw new IllegalArgumentException("Map must contain a start position (S)");
        }
        if (exitCell == null) {
            throw new IllegalArgumentException("Map must contain an exit position (E)");
        }
        
        System.out.println("Map loaded successfully: " + width + "x" + height);
    }
    
    /**
     * Returns the cell at the specified coordinates.
     * 
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return the Cell at the specified position, or null if out of bounds
     */
    public Cell getCell(int x, int y) {
        if (!isInBounds(x, y)) {
            return null;
        }
        return grid[y][x];
    }
    
    /**
     * Checks if the specified coordinates are within the board boundaries.
     * 
     * @param x the x-coordinate to check
     * @param y the y-coordinate to check
     * @return true if coordinates are within bounds, false otherwise
     */
    public boolean isInBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
    
    /**
     * Validates if a move to the specified coordinates is allowed.
     * A move is valid if the target cell is within bounds and not a wall.
     * 
     * @param x the target x-coordinate
     * @param y the target y-coordinate
     * @return true if the move is valid, false otherwise
     */
    public boolean isValidMove(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height) {
            return false;
        }
        Cell cell = grid[y][x];
        return cell != null && cell.isWalkable();
    }

    
    /**
     * Returns the starting cell of the board.
     * 
     * @return the start Cell where the player begins
     */
    public Cell getStartCell() {
        return startCell;
    }

    /**
     * Returns a random empty walkable cell (not wall, start, exit, or occupied).
     * @return a random empty cell
     */
    public Cell getRandomEmptyCell() {
        for (int attempts = 0; attempts < 1000; attempts++) {
            int x = RANDOM.nextInt(width);
            int y = RANDOM.nextInt(height);
            Cell cell = grid[y][x];

            if (cell != null && cell.isWalkable() && !cell.isStart() && !cell.isExit()
                && cell.getReward() == null && cell.getEnemy() == null && cell.getPunishment() == null) {
                return cell;
            }
        }
        return null;
    }

    
    /**
     * Returns the exit cell of the board.
     * @return the exit Cell that must be reached to win
     */
    public Cell getExitCell() {
        return exitCell;
    }
    
    /**
     * Returns the width (number of columns) of the board.
     * @return the board width
     */
    public int getWidth() {
        return width;
    }
    
    /**
     * Returns the height (number of rows) of the board.
     * @return the board height
     */
    public int getHeight() {
        return height;
    }
    
    /**
     * Returns the entire grid of cells.

     * @return 2D array of all cells in the board
     */
    public Cell[][] getGrid() {
        return grid;
    }
    
    /**
     * Prints a text representation of the board to the console.
     * Useful for debugging and testing.
     */
    public void printBoard() {
        if (grid == null) {
            System.out.println("Board not initialized");
            return;
        }
        
        System.out.println("\nBoard Layout (" + width + "x" + height + "):");
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Cell cell = grid[y][x];
                if (cell.isWall()) {
                    System.out.print("# ");
                } else if (cell.isStart()) {
                    System.out.print("S ");
                } else if (cell.isExit()) {
                    System.out.print("E ");
                } else if (cell.getContent() != null) {
                    System.out.print("? ");
                } else {
                    System.out.print(". ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }
    
    /**
     * Returns a string representation of the board.
     * 
     * @return string describing the board dimensions
     */
    @Override
    public String toString() {
        return "Board[" + width + "x" + height + "]";
    }
}
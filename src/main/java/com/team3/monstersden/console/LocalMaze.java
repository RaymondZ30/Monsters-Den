package com.team3.monstersden.console;

import java.util.Arrays;

/**
 * Represents a simple local maze for console-based gameplay
 */
public class LocalMaze {
    private final char[][] grid;
    private int px, py; // player pos

    /**
     * LocalMaze constructor with a layout
     */
    public LocalMaze() {
        // simple fallback map (S = start, E = exit, # = wall, . = floor)
        String[] rows = {
            "##########",
            "#S....#..#",
            "#.##..#..#",
            "#....##..#",
            "#..#.....#",
            "#..#.##..#",
            "#..#...E.#",
            "##########"
        };
        grid = new char[rows.length][];
        for (int i = 0; i < rows.length; i++) grid[i] = rows[i].toCharArray();

        // locate S
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                if (grid[y][x] == 'S') { px = x; py = y; return; }
            }
        }
        // fallback if no S found
        px = 1; py = 1;
    }

    /**
     * Attempts to move the player by the specified relative coordinates
     * @param dx change in x-direction
     * @param dy change in y-direction
     * @return true if the player successfully moved
     */
    public boolean move(int dx, int dy) {
        int nx = px + dx, ny = py + dy;
        if (ny < 0 || ny >= grid.length || nx < 0 || nx >= grid[0].length) return false;
        char cell = grid[ny][nx];
        if (cell == '#') return false;
        // move: restore previous cell as '.' unless it was 'S'
        if (grid[py][px] != 'S') grid[py][px] = '.';
        // update pos (do not overwrite target cell )
        px = nx; 
        py = ny;
        return true;
    }

    /**
     * Checks whether the player is currently on the exit tile
     * @return true if player is at exit
     */
    public boolean atExit() {
        return grid[py][px] == 'E';
    }

    /**
     * Helper function showing underlying tile under player
     * @return underlying tile under player
     */
    public char currentTile() { 
        return grid[py][px]; 
    }

    /**
     * Renders the maze as a string, showing the player's position as 'P'
     * @return string representation of the maze with the player position
     */
    public String render() {
        // draw player as 'P' over the grid (without mutating exit/start)
        char[][] copy = new char[grid.length][];
        for (int i = 0; i < grid.length; i++) copy[i] = Arrays.copyOf(grid[i], grid[i].length);
        copy[py][px] = 'P';
        StringBuilder sb = new StringBuilder();
        for (char[] row : copy) sb.append(row).append('\n');
        return sb.toString();
    }
}

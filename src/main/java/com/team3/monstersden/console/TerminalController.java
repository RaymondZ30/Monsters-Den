package com.team3.monstersden.console;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.team3.monstersden.core.Game;

/**
 * Handles terminal-based input and output for controlling the player in a LocalMaze
 */
public class TerminalController implements Runnable {
    private final LocalMaze maze;
    private final Game game;

    /**
     * Creates a new TerminalController for the given game and maze.
     *
     * @param game the game instance to update (e.g., score, HUD)
     * @param maze the local maze in which the player moves
     */
    public TerminalController(Game game, LocalMaze maze) {
        this.game = game;
        this.maze = maze;
    }

    /**
     * Starts the terminal input loop.
     */
    @Override
    public void run() {
        System.out.println("\nControls: W/A/S/D to move, Q to quit");
        redraw();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                String line = br.readLine();
                if (line == null) break;
                if (line.isEmpty()) { prompt(); continue; }

                char c = Character.toUpperCase(line.charAt(0));
                if (c == 'Q') { System.out.println("Bye!"); break; }

                int dx = 0, dy = 0;
                if (c == 'W') dy = -1;
                else if (c == 'S') dy = 1;
                else if (c == 'A') dx = -1;
                else if (c == 'D') dx = 1;
                else {
                    System.out.println("(Use W/A/S/D or Q)");
                    prompt();
                    continue;
                }

                boolean moved = maze.move(dx, dy);
                if (moved) {
                    game.addScore(1);      // nudge Game/Observer so HUD updates
                    redraw();
                    if (maze.atExit()) {
                        System.out.println("You reached the exit! 🎉");
                        break;
                    }
                } else {
                    // blocked by wall or bounds
                    System.out.println("Blocked.");
                    prompt();
                }
            }
        } catch (Exception e) {
            System.err.println("Input error: " + e.getMessage());
        }
    }

    /**
     * Clears the terminal and renders the maze with the player's current position.
     */
    private void redraw() {
        // ANSI clear + home (works in most terminals)
        System.out.print("\033[H\033[2J");
        System.out.flush();
        System.out.println(maze.render());
        prompt();
    }

    /**
     * Prints the user input prompt in the terminal.
     */
    private void prompt() {
        System.out.print("Move (W/A/S/D, Q to quit): ");
        System.out.flush();
    }
}

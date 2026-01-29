package com.team3.monstersden.patterns.observer;

import com.team3.monstersden.core.Game;
import com.team3.monstersden.patterns.command.Command;
import com.team3.monstersden.patterns.command.MoveCommand;
import com.team3.monstersden.patterns.strategy.PlayerMovementStrategy;
import com.team3.monstersden.util.Direction;
import com.team3.monstersden.items.SilverSword;
import com.team3.monstersden.items.SteelSword;

import java.util.Scanner;

/**
 * Handles user input and translates it to game commands.
 * 
 * @author Team 3
 * @version 1.0
 */
public class InputHandler {
    
    private Game game;
    private GameUI ui;
    private Scanner scanner;
    private boolean running;
    
    /**
     * Constructs an InputHandler for the game.
     * 
     * @param game the game instance
     * @param ui the UI instance
     */
    public InputHandler(Game game, GameUI ui) {
        this.game = game;
        this.ui = ui;
        this.scanner = new Scanner(System.in);
        this.running = true;
    }
    
    /**
     * Starts the input loop, processing user commands.
     */
    public void start() {
        ui.render();
        
        while (running && !game.isGameOver()) {
            System.out.print("Enter command: ");
            String input = scanner.nextLine().trim().toLowerCase();
            
            processInput(input);
            
            // Game tick after each action
            game.tick();
            
            // Update UI
            ui.render();
        }
    }
    
    /**
     * Processes a single input command.
     * 
     * @param input the user input string
     */
    private void processInput(String input) {
        switch (input) {
            // Movement
            case "w":
            case "up":
                movePlayer(Direction.UP);
                break;
            case "s":
            case "down":
                movePlayer(Direction.DOWN);
                break;
            case "a":
            case "left":
                movePlayer(Direction.LEFT);
                break;
            case "d":
            case "right":
                movePlayer(Direction.RIGHT);
                break;
                
            // Weapon switching
            case "1":
                game.getPlayer().switchWeapon(new SilverSword());
                ui.displayMessage("Switched to Silver Sword (effective vs Monsters)");
                break;
            case "2":
                game.getPlayer().switchWeapon(new SteelSword());
                ui.displayMessage("Switched to Steel Sword (effective vs Humans)");
                break;
                
            // Other commands
            case "q":
            case "quit":
                running = false;
                ui.displayMessage("Thanks for playing!");
                break;
            case "h":
            case "help":
                displayHelp();
                break;
            default:
                ui.displayError("Unknown command: " + input);
                break;
        }
    }
    
    /**
     * Moves the player in the specified direction.
     * 
     * @param direction the direction to move
     */
    private void movePlayer(Direction direction) {
        PlayerMovementStrategy strategy = (PlayerMovementStrategy) game.getPlayer().getMovementStrategy();
        Command moveCommand = new MoveCommand(strategy, direction);
        moveCommand.execute(game);
    }
    
    /**
     * Displays help information.
     */
    private void displayHelp() {
        System.out.println("\n=== HELP ===");
        System.out.println("Movement: W/A/S/D or arrow keys");
        System.out.println("Weapon Switch: 1 (Silver Sword) or 2 (Steel Sword)");
        System.out.println("Silver Sword: Full damage to Monsters, 50% to Humans");
        System.out.println("Steel Sword: Full damage to Humans, 50% to Monsters");
        System.out.println("Goal: Collect all regular rewards ($) and reach exit (E)");
        System.out.println("Avoid punishments (X) and defeat enemies!");
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }
    
    /**
     * Stops the input handler.
     */
    public void stop() {
        running = false;
        scanner.close();
    }
    
    /**
     * Checks if the input handler is running.
     * 
     * @return true if running, false otherwise
     */
    public boolean isRunning() {
        return running;
    }
}
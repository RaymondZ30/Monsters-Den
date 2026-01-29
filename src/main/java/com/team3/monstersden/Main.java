package com.team3.monstersden;

import com.team3.monstersden.core.Game;
import com.team3.monstersden.patterns.observer.GameUI;
import com.team3.monstersden.patterns.observer.InputHandler;

/**
 * Main entry point for Monster's Den game.
 * Initializes the game, UI, and input handling system.
 * 
 * @author Team 3
 * @version 1.0
 */
public class Main {
    
    public static void main(String[] args) {
        try {
            // Display welcome screen
            displayWelcome();
            
            // Wait for user to press Enter
            System.out.println("Press Enter to start your adventure...");
            new java.util.Scanner(System.in).nextLine();
            
            // Create game instance (Singleton pattern)
            Game game = Game.getInstance();
            
            // Initialize game world
            // This should:
            // - Load the map from resources/maps/level1.txt
            // - Spawn player at start position
            // - Place enemies, rewards, and punishments
            // - Initialize game state
            game.initialize();
            
            // Create UI with color support
            // Set to false if your terminal doesn't support ANSI colors
            boolean useColors = true;
            GameUI ui = new GameUI(game, useColors);
            
            // Attach UI as observer to game
            game.addObserver(ui);
            
            // Create input handler
            InputHandler inputHandler = new InputHandler(game, ui);
            
            // Display initial game state
            ui.displayMessage("Game initialized successfully!");
            ui.displayMessage("Collect all regular rewards ($) and reach the exit (E) to win!");
            ui.displayMessage("Use the correct sword for each enemy type:");
            ui.displayMessage("  - Silver Sword (1): Effective against Monsters");
            ui.displayMessage("  - Steel Sword (2): Effective against Humans");
            System.out.println();
            System.out.println("Press Enter to continue...");
            new java.util.Scanner(System.in).nextLine();
            
            // Start the game loop
            inputHandler.start();
            
            // Game has ended
            displayGoodbye();
            
        } catch (Exception e) {
            System.err.println("An error occurred while running the game:");
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Displays the welcome screen with game title and instructions.
     */
    private static void displayWelcome() {
        clearScreen();
        
        String border = "═".repeat(70);
        System.out.println(border);
        System.out.println("║" + centerText("", 68) + "║");
        System.out.println("║" + centerText("███╗   ███╗ ██████╗ ███╗   ██╗███████╗████████╗███████╗██████╗", 68) + "║");
        System.out.println("║" + centerText("████╗ ████║██╔═══██╗████╗  ██║██╔════╝╚══██╔══╝██╔════╝██╔══██╗", 68) + "║");
        System.out.println("║" + centerText("██╔████╔██║██║   ██║██╔██╗ ██║███████╗   ██║   █████╗  ██████╔╝", 68) + "║");
        System.out.println("║" + centerText("██║╚██╔╝██║██║   ██║██║╚██╗██║╚════██║   ██║   ██╔══╝  ██╔══██╗", 68) + "║");
        System.out.println("║" + centerText("██║ ╚═╝ ██║╚██████╔╝██║ ╚████║███████║   ██║   ███████╗██║  ██║", 68) + "║");
        System.out.println("║" + centerText("╚═╝     ╚═╝ ╚═════╝ ╚═╝  ╚═══╝╚══════╝   ╚═╝   ╚══════╝╚═╝  ╚═╝", 68) + "║");
        System.out.println("║" + centerText("", 68) + "║");
        System.out.println("║" + centerText("███████╗     ██████╗ ███████╗███╗   ██╗", 68) + "║");
        System.out.println("║" + centerText("██╔════╝     ██╔══██╗██╔════╝████╗  ██║", 68) + "║");
        System.out.println("║" + centerText("███████╗     ██║  ██║█████╗  ██╔██╗ ██║", 68) + "║");
        System.out.println("║" + centerText("╚════██║     ██║  ██║██╔══╝  ██║╚██╗██║", 68) + "║");
        System.out.println("║" + centerText("███████║     ██████╔╝███████╗██║ ╚████║", 68) + "║");
        System.out.println("║" + centerText("╚══════╝     ╚═════╝ ╚══════╝╚═╝  ╚═══╝", 68) + "║");
        System.out.println("║" + centerText("", 68) + "║");
        System.out.println(border);
        System.out.println();
        System.out.println(centerText("A Witcher-Inspired Dungeon Adventure", 70));
        System.out.println(centerText("By Team 3 - CMPT 276", 70));
        System.out.println();
        System.out.println(border);
        System.out.println();
        System.out.println("  THE STORY:");
        System.out.println("  You are Gerald, a skilled monster hunter, trapped in a mysterious dungeon");
        System.out.println("  filled with monsters and hostile humans. Armed with your silver and steel");
        System.out.println("  swords, you must navigate the treacherous maze, collect treasures, and");
        System.out.println("  find the exit before it's too late.");
        System.out.println();
        System.out.println("  OBJECTIVE:");
        System.out.println("  - Collect ALL regular rewards ($) scattered throughout the dungeon");
        System.out.println("  - Reach the exit (E) to escape");
        System.out.println("  - Keep your score above 0 (avoid punishments!)");
        System.out.println("  - Survive encounters with enemies");
        System.out.println();
        System.out.println("  GAMEPLAY TIPS:");
        System.out.println("  ✓ Use Silver Sword (1) against Monsters for full damage");
        System.out.println("  ✓ Use Steel Sword (2) against Human enemies for full damage");
        System.out.println("  ✓ Wrong weapon deals only 50% damage!");
        System.out.println("  ✓ Grab Bonus rewards (★) quickly - they expire!");
        System.out.println("  ✓ Avoid punishment traps (X) - they reduce your score");
        System.out.println();
        System.out.println(border);
        System.out.println();
    }
    
    /**
     * Displays goodbye message when game ends.
     */
    private static void displayGoodbye() {
        System.out.println();
        String border = "═".repeat(70);
        System.out.println(border);
        System.out.println("║" + centerText("Thank you for playing Monster's Den!", 68) + "║");
        System.out.println("║" + centerText("", 68) + "║");
        System.out.println("║" + centerText("Created by Team 3:", 68) + "║");
        System.out.println("║" + centerText("Simarjot Singh, Raymond Zhou,", 68) + "║");
        System.out.println("║" + centerText("Maria Leon Campos, Shahmeer Khan", 68) + "║");
        System.out.println("║" + centerText("", 68) + "║");
        System.out.println("║" + centerText("CMPT 276 - Software Engineering", 68) + "║");
        System.out.println("║" + centerText("Simon Fraser University", 68) + "║");
        System.out.println(border);
        System.out.println();
    }
    
    /**
     * Clears the console screen.
     */
    private static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
    
    /**
     * Centers text within the specified width.
     * 
     * @param text the text to center
     * @param width the total width
     * @return centered text with padding
     */
    private static String centerText(String text, int width) {
        if (text.length() >= width) {
            return text;
        }
        int padding = (width - text.length()) / 2;
        return " ".repeat(padding) + text + " ".repeat(width - text.length() - padding);
    }
}
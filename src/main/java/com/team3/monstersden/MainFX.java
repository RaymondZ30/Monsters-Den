package com.team3.monstersden;

import com.team3.monstersden.core.Game;
import com.team3.monstersden.patterns.observer.GameUIFX;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

/**
 * Main JavaFX application entry point for Monster's Den.
 * Initializes the game and launches the graphical UI.
 * 
 * @author Team 3
 * @version 2.0 (JavaFX)
 */
public class MainFX extends Application {
    
    private Game game;
    private GameUIFX ui;
    
    @Override
    
    public void start(Stage primaryStage) {
    try {
        // Show welcome dialog
        if (!showWelcomeDialog()) {
            return; // User cancelled
        }

        // Create game instance (Singleton pattern)
        game = Game.getInstance();

        // Initialize game world (loads map or fallback)
        game.initialize();

        // ✅ Create player at start cell
        var board = game.getBoard();
        if (board == null) {
            throw new IllegalStateException("Board failed to initialize — check map loading");
        }

        var startCell = board.getStartCell();
        if (startCell == null) {
            throw new IllegalStateException("No start cell found in board");
        }

        // Import this class: com.team3.monstersden.characters.Player
        com.team3.monstersden.characters.Player player =
                new com.team3.monstersden.characters.Player(startCell.getX(), startCell.getY());

        // Assign movement strategy so WASD works
        player.setMovementStrategy(new com.team3.monstersden.patterns.strategy.PlayerMovementStrategy());

        // Register player with game
        game.setPlayer(player);

        // ✅ Create JavaFX UI
        ui = new com.team3.monstersden.patterns.observer.GameUIFX(game, primaryStage);
        game.addObserver(ui);

        // Start the game logic (game loop, observer updates)
        game.startGameLoop();

        // Show the window
        ui.show();

        // Display welcome message
        ui.displayMessage("Welcome to Monster's Den! Collect all rewards and reach the exit!");

    } catch (Exception e) {
        showErrorDialog("Failed to start game", e.getMessage());
        e.printStackTrace();
    }
}

    
    /**
     * Shows the welcome dialog with game instructions.
     * 
     * @return true if user wants to start, false to cancel
     */
    private boolean showWelcomeDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Monster's Den");
        alert.setHeaderText("⚔️  WELCOME TO MONSTER'S DEN  ⚔️");
        
        String content = "A Witcher-Inspired Dungeon Adventure\n\n" +
            "THE STORY:\n" +
            "You are Gerald, a skilled monster hunter trapped in a mysterious dungeon.\n" +
            "Armed with your silver and steel swords, you must navigate the maze,\n" +
            "collect treasures, and find the exit.\n\n" +
            "OBJECTIVE:\n" +
            "• Collect ALL regular rewards (♦) scattered throughout\n" +
            "• Reach the exit (▣) to escape\n" +
            "• Keep your score above 0\n" +
            "• Survive encounters with enemies\n\n" +
            "CONTROLS:\n" +
            "• WASD or Arrow Keys: Move\n" +
            "• 1: Switch to Silver Sword (vs Monsters)\n" +
            "• 2: Switch to Steel Sword (vs Humans)\n" +
            "• Q/ESC: Quit Game\n\n" +
            "TIPS:\n" +
            "✓ Use the correct sword for each enemy type\n" +
            "✓ Wrong weapon deals only 50% damage\n" +
            "✓ Grab Bonus rewards (★) quickly - they expire!\n" +
            "✓ Avoid punishment traps (✖)\n\n" +
            "Good luck, Witcher!";
        
        alert.setContentText(content);
        alert.getDialogPane().setPrefWidth(500);
        
        ButtonType startButton = new ButtonType("Start Adventure");
        ButtonType cancelButton = new ButtonType("Exit");
        alert.getButtonTypes().setAll(startButton, cancelButton);
        
        return alert.showAndWait().orElse(cancelButton) == startButton;
    }
    
    /**
     * Shows an error dialog.
     */
    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    @Override
    public void stop() {
        // Clean up when application closes
        System.out.println("Thanks for playing Monster's Den!");
    }
    
    /**
     * Main method - launches the JavaFX application.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
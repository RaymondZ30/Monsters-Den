package com.team3.monstersden.patterns.observer;

import com.team3.monstersden.core.Game;
import com.team3.monstersden.core.Board;
import com.team3.monstersden.core.Cell;
import com.team3.monstersden.characters.Player;
import com.team3.monstersden.characters.Enemy;
import com.team3.monstersden.characters.Monster;
import com.team3.monstersden.characters.HumanEnemy;
import com.team3.monstersden.items.Reward;
import com.team3.monstersden.items.RegularReward;
import com.team3.monstersden.items.BonusReward;
import com.team3.monstersden.items.Punishment;

/**
 * Console-based UI implementation that observes game state changes.
 * Renders the game board, score, timer, and game status using ASCII art.
 * 
 * @author Maria Leon Campos & Team 3
 * @version 1.0
 */
public class GameUI implements GameObserver {
    
    private Game game;
    private int currentScore;
    private int bonusTimerTicks;
    
    // Display symbols
    private static final String PLAYER = " G ";      // Gerald (player)
    private static final String MONSTER = " M ";     // Monster
    private static final String HUMAN = " H ";       // Human enemy
    private static final String REWARD = " $ ";      // Regular reward
    private static final String BONUS = " ★ ";       // Bonus reward
    private static final String PUNISHMENT = " X ";  // Punishment/trap
    private static final String EMPTY = " · ";       // Empty cell
    private static final String WALL = "███";        // Wall
    private static final String EXIT = " E ";        // Exit
    private static final String START = " S ";       // Start
    
    // ANSI color codes (optional, remove if not supported)
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String PURPLE = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";
    private static final String WHITE = "\u001B[37m";
    
    private boolean useColors;
    
    /**
     * Constructs a GameUI for the specified game.
     * 
     * @param game the game to observe and render
     */
    public GameUI(Game game) {
        this.game = game;
        this.currentScore = 0;
        this.bonusTimerTicks = 0;
        this.useColors = true; // Set to false if colors not supported
    }
    
    /**
     * Constructs a GameUI with color option.
     * 
     * @param game the game to observe
     * @param useColors whether to use ANSI colors
     */
    public GameUI(Game game, boolean useColors) {
        this.game = game;
        this.currentScore = 0;
        this.bonusTimerTicks = 0;
        this.useColors = useColors;
    }
    
    @Override
    public void update() {
        render();
    }
    
    @Override
    public void onScoreChanged(int newScore) {
        this.currentScore = newScore;
        render();
    }
    
    @Override
    public void onBonusTimerUpdate(int ticksRemaining) {
        this.bonusTimerTicks = ticksRemaining;
        // Optional: only render if you want live timer updates
    }
    
    @Override
    public void onGameEnd(boolean won, int finalScore) {
        render();
        displayGameOver(won, finalScore);
    }
    
    /**
     * Renders the complete game UI to console.
     */
    public void render() {
        clearScreen();
        displayHeader();
        displayBoard();
        displayStats();
        displayLegend();
        displayControls();
    }
    
    /**
     * Clears the console screen.
     */
    private void clearScreen() {
        // ANSI escape code to clear screen
        System.out.print("\033[H\033[2J");
        System.out.flush();
        
        // Alternative for Windows (less effective):
        // for (int i = 0; i < 50; i++) System.out.println();
    }
    
    /**
     * Displays the game header/title.
     */
    private void displayHeader() {
        String border = "═".repeat(60);
        System.out.println(color(CYAN, border));
        System.out.println(color(CYAN, "║") + 
                          centerText("MONSTER'S DEN", 58) + 
                          color(CYAN, "║"));
        System.out.println(color(CYAN, "║") + 
                          centerText("A Witcher-Inspired Dungeon Adventure", 58) + 
                          color(CYAN, "║"));
        System.out.println(color(CYAN, border));
        System.out.println();
    }
    
    /**
     * Displays the game board with all entities.
     */
    private void displayBoard() {
        Board board = game.getBoard();
        if (board == null) {
            System.out.println("Board not initialized.");
            return;
        }
        
        int width = board.getWidth();
        int height = board.getHeight();
        Player player = (Player) game.getPlayer();
        
        // Top border
        System.out.print("  ┌");
        for (int x = 0; x < width; x++) {
            System.out.print("───");
        }
        System.out.println("┐");
        
        // Board content
        for (int y = 0; y < height; y++) {
            System.out.print(String.format("%2d│", y)); // Row number
            
            for (int x = 0; x < width; x++) {
                Cell cell = board.getCell(x, y);
                String symbol = getCellSymbol(cell, x, y, player);
                System.out.print(symbol);
            }
            
            System.out.println("│");
        }
        
        // Bottom border
        System.out.print("  └");
        for (int x = 0; x < width; x++) {
            System.out.print("───");
        }
        System.out.println("┘");
        
        // Column numbers
        System.out.print("   ");
        for (int x = 0; x < width; x++) {
            System.out.print(String.format(" %-2d", x));
        }
        System.out.println();
        System.out.println();
    }
    
    /**
     * Determines the display symbol for a cell.
     */
    private String getCellSymbol(Cell cell, int x, int y, Player player) {
        if (cell == null) {
            return color(WHITE, WALL);
        }
        
        // Check if player is on this cell
        if (player != null && player.getX() == x && player.getY() == y) {
            return color(GREEN, PLAYER);
        }
        
        // Check for enemies
        Enemy enemy = cell.getEnemy();
        if (enemy != null) {
            if (enemy instanceof Monster) {
                return color(RED, MONSTER);
            } else if (enemy instanceof HumanEnemy) {
                return color(YELLOW, HUMAN);
            }
        }
        
        // Check for rewards
        Reward reward = cell.getReward();
        if (reward != null && !reward.isCollected()) {
            if (reward instanceof BonusReward) {
                BonusReward bonus = (BonusReward) reward;
                if (!bonus.isExpired()) {
                    return color(PURPLE, BONUS);
                }
            } else if (reward instanceof RegularReward) {
                return color(YELLOW, REWARD);
            }
        }
        
        // Check for punishment
        Punishment punishment = cell.getPunishment();
        if (punishment != null && punishment.isActive()) {
            return color(RED, PUNISHMENT);
        }
        
        // Check for special cells
        if (cell.isExit()) {
            return color(CYAN, EXIT);
        }
        if (cell.isStart()) {
            return color(BLUE, START);
        }
        
        // Empty walkable cell
        if (cell.isWalkable()) {
            return color(WHITE, EMPTY);
        }
        
        // Wall
        return color(WHITE, WALL);
    }
    
    /**
     * Displays game statistics (score, health, weapon, etc.).
     */
    private void displayStats() {
        Player player = (Player) game.getPlayer();
        
        System.out.println("┌─────────────────────── STATS ───────────────────────┐");
        
        // Score
        System.out.printf("│ Score: %-42s │%n", 
                         color(YELLOW, String.format("%d", currentScore)));
        
        // Player stats
        if (player != null) {
            System.out.printf("│ Health: %-41s │%n", 
                             color(player.getHealth() > 50 ? GREEN : RED, 
                                   String.format("%d", player.getHealth())));
            
            System.out.printf("│ Position: %-39s │%n", 
                             String.format("(%d, %d)", player.getX(), player.getY()));
            
            System.out.printf("│ Current Weapon: %-34s │%n", 
                             color(CYAN, player.getCurrentSword().getName()));
        }
        
        // Bonus timer
        if (bonusTimerTicks > 0) {
            System.out.printf("│ Bonus Expires In: %-31s │%n", 
                             color(PURPLE, String.format("%d ticks", bonusTimerTicks)));
        }
        
        // Collected rewards
        System.out.printf("│ Regular Rewards Collected: %-23s │%n",
                         String.format("%d / %d", 
                                     game.getCollectedRewards(), 
                                     game.getTotalRewards()));
        
        System.out.println("└─────────────────────────────────────────────────────┘");
        System.out.println();
    }
    
    /**
     * Displays the legend explaining symbols.
     */
    private void displayLegend() {
        System.out.println("┌──────────────────── LEGEND ─────────────────────┐");
        System.out.printf("│ %s Gerald (Player)    %s Monster           │%n", 
                         color(GREEN, PLAYER), color(RED, MONSTER));
        System.out.printf("│ %s Human Enemy        %s Regular Reward    │%n", 
                         color(YELLOW, HUMAN), color(YELLOW, REWARD));
        System.out.printf("│ %s Bonus Reward       %s Punishment/Trap   │%n", 
                         color(PURPLE, BONUS), color(RED, PUNISHMENT));
        System.out.printf("│ %s Exit               %s Start Position    │%n", 
                         color(CYAN, EXIT), color(BLUE, START));
        System.out.println("└─────────────────────────────────────────────────┘");
        System.out.println();
    }
    
    /**
     * Displays game controls.
     */
    private void displayControls() {
        System.out.println("┌─────────────────── CONTROLS ────────────────────┐");
        System.out.println("│ W/↑ - Move Up       S/↓ - Move Down           │");
        System.out.println("│ A/← - Move Left     D/→ - Move Right           │");
        System.out.println("│ 1 - Silver Sword    2 - Steel Sword            │");
        System.out.println("│ Q - Quit Game       H - Help                   │");
        System.out.println("└─────────────────────────────────────────────────┘");
        System.out.println();
    }
    
    /**
     * Displays game over screen.
     */
    private void displayGameOver(boolean won, int finalScore) {
        System.out.println();
        String border = "═".repeat(60);
        System.out.println(color(won ? GREEN : RED, border));
        
        if (won) {
            System.out.println(color(GREEN, "║") + 
                              centerText("VICTORY!", 58) + 
                              color(GREEN, "║"));
            System.out.println(color(GREEN, "║") + 
                              centerText("You have escaped the Monster's Den!", 58) + 
                              color(GREEN, "║"));
        } else {
            System.out.println(color(RED, "║") + 
                              centerText("GAME OVER", 58) + 
                              color(RED, "║"));
            System.out.println(color(RED, "║") + 
                              centerText("You have been defeated...", 58) + 
                              color(RED, "║"));
        }
        
        System.out.println(color(won ? GREEN : RED, "║") + 
                          centerText(String.format("Final Score: %d", finalScore), 58) + 
                          color(won ? GREEN : RED, "║"));
        System.out.println(color(won ? GREEN : RED, border));
        System.out.println();
    }
    
    /**
     * Displays a message to the player.
     * 
     * @param message the message to display
     */
    public void displayMessage(String message) {
        System.out.println(color(YELLOW, ">>> " + message));
    }
    
    /**
     * Displays an error message.
     * 
     * @param error the error message
     */
    public void displayError(String error) {
        System.out.println(color(RED, "ERROR: " + error));
    }
    
    /**
     * Centers text within a given width.
     */
    private String centerText(String text, int width) {
        int padding = (width - text.length()) / 2;
        return " ".repeat(Math.max(0, padding)) + text + 
               " ".repeat(Math.max(0, width - text.length() - padding));
    }
    
    /**
     * Applies color to text if colors are enabled.
     */
    private String color(String colorCode, String text) {
        if (useColors) {
            return colorCode + text + RESET;
        }
        return text;
    }
    
    /**
     * Enables or disables color output.
     * 
     * @param enabled true to enable colors, false to disable
     */
    public void setColorEnabled(boolean enabled) {
        this.useColors = enabled;
    }
}
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
import com.team3.monstersden.items.SilverSword;
import com.team3.monstersden.items.SteelSword;
import com.team3.monstersden.patterns.command.MoveCommand;
import com.team3.monstersden.patterns.command.Command;
import com.team3.monstersden.patterns.strategy.PlayerMovementStrategy;
import com.team3.monstersden.util.Direction;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.util.HashMap;
import java.util.Map;

/**
 * JavaFX-based graphical UI with image support.
 * Renders the game board with images instead of text symbols.
 *
 * @author Team 3
 * @version 3.0 (JavaFX with Images)
 */
public class GameUIFX implements GameObserver {

    private Game game;
    private Stage stage;
    private Canvas canvas;
    private GraphicsContext gc;

    // Image cache
    private Map<String, Image> imageCache;

    // UI Components
    private Label scoreLabel;
    private Label healthLabel;
    private Label positionLabel;
    private Label weaponLabel;
    private Label bonusTimerLabel;
    private Label rewardsLabel;
    private Label messageLabel;
    private ProgressBar healthBar;

    // Display settings
    private static final int CELL_SIZE = 50;
    private static final int GRID_LINE_WIDTH = 2;

    // Colors
    private static final Color COLOR_EXIT = Color.rgb(0, 191, 255);
    private static final Color COLOR_START = Color.rgb(70, 130, 180);
    private static final Color COLOR_EMPTY = Color.rgb(200, 200, 200);
    private static final Color COLOR_WALL = Color.rgb(64, 64, 64);
    private static final Color COLOR_GRID = Color.rgb(128, 128, 128);
    private static final Color COLOR_PLAYER     = Color.rgb(50, 205, 50);   // Green
    private static final Color COLOR_MONSTER    = Color.rgb(220, 20, 60);   // Red
    private static final Color COLOR_HUMAN      = Color.rgb(255, 215, 0);   // Gold
    private static final Color COLOR_REWARD     = Color.rgb(255, 215, 0);   // Gold
    private static final Color COLOR_BONUS      = Color.rgb(138, 43, 226);  // Purple
    private static final Color COLOR_PUNISHMENT = Color.rgb(220, 20, 60);   // Red



    private int currentScore = 0;
    private int bonusTimerTicks = 0;
    private boolean playerFacingLeft = false;

    /**
     * Constructs a JavaFX UI for the game.
     */
    public GameUIFX(Game game, Stage stage) {
        this.game = game;
        this.stage = stage;
        this.imageCache = new HashMap<>();
        loadImages();
        initializeUI();
        setupKeyBindings();
    }

    /**
     * Loads all game images into cache.
     * Images are located in src/main/resources/images/
     */
    private void loadImages() {
        System.out.println("Loading game images...");

        // Load all entity images
        loadImage("player", "/images/player.png");
        loadImage("monster", "/images/monster.png");
        loadImage("human", "/images/human.png");
        loadImage("reward", "/images/reward.png");
        loadImage("bonus", "/images/bonus.png");
        loadImage("trap", "/images/trap.png");
        loadImage("exit", "/images/exit.png");
        loadImage("floor", "/images/floor.png");

        // Wall image (optional - will use floor.png if not found)
        loadImageOptional("wall", "/images/wall.png", "/images/floor.png");

        System.out.println("✓ Image loading complete! Loaded " + imageCache.size() + " images.");
        System.out.println("Missing images will display as colored shapes.");
    }

    /**
     * Loads an image with a fallback option.
     */
    private void loadImageOptional(String key, String primaryPath, String fallbackPath) {
        try {
            Image image = new Image(getClass().getResourceAsStream(primaryPath));
            if (!image.isError()) {
                imageCache.put(key, image);
                System.out.println("  ✓ " + key + " loaded");
                return;
            }
        } catch (Exception e) {
            // Try fallback
        }

        // Try fallback
        try {
            Image image = new Image(getClass().getResourceAsStream(fallbackPath));
            if (!image.isError()) {
                imageCache.put(key, image);
                System.out.println("  ✓ " + key + " loaded (using fallback)");
            }
        } catch (Exception e) {
            System.err.println("  ✗ " + key + " not found (will use colored shape)");
        }
    }

    /**
     * Loads a single image into the cache.
     */
    private void loadImage(String key, String path) {
        try {
            Image image = new Image(getClass().getResourceAsStream(path));
            if (image.isError()) {
                System.err.println("  ✗ Failed to load: " + key + " from " + path);
            } else {
                imageCache.put(key, image);
                System.out.println("  ✓ " + key + " loaded");
            }
        } catch (Exception e) {
            System.err.println("  ✗ Error loading " + key + ": " + e.getMessage());
        }
    }

    /**
     * Gets an image from cache, returns null if not found.
     */
    private Image getImage(String key) {
        return imageCache.get(key);
    }

    /**
     * Initializes the JavaFX UI components.
     */
    private void initializeUI() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #2b2b2b;");

        VBox topBar = createTitleBar();
        root.setTop(topBar);

        Board board = game.getBoard();
        canvas = new Canvas();
        gc = canvas.getGraphicsContext2D();

        StackPane canvasContainer = new StackPane(canvas);
        canvasContainer.setStyle("-fx-background-color: #1e1e1e;");
        root.setCenter(canvasContainer);

        // Make canvas resize to window
        canvas.widthProperty().bind(canvasContainer.widthProperty());
        canvas.heightProperty().bind(canvasContainer.heightProperty());

        // Re-render when resized
        canvas.widthProperty().addListener(observable -> update());
        canvas.heightProperty().addListener(observable -> update());


        VBox statsPanel = createStatsPanel();
        root.setRight(statsPanel);

        VBox bottomPanel = createBottomPanel();
        root.setBottom(bottomPanel);

        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add("data:text/css," + getCSS());

        stage.setScene(scene);
        stage.setTitle("Monster's Den - A Witcher Adventure");
        stage.setResizable(false);
    }

    private VBox createTitleBar() {
        VBox titleBar = new VBox(5);
        titleBar.setAlignment(Pos.CENTER);
        titleBar.setStyle("-fx-background-color: linear-gradient(to bottom, #3a3a3a, #2b2b2b);");
        titleBar.setPadding(new Insets(10));

        Label title = new Label("MONSTER'S DEN");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        title.setTextFill(Color.rgb(220, 220, 220));

        Label subtitle = new Label("A Witcher-Inspired Dungeon Adventure");
        subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        subtitle.setTextFill(Color.rgb(180, 180, 180));

        titleBar.getChildren().addAll(title, subtitle);
        return titleBar;
    }

    private VBox createStatsPanel() {
        VBox statsPanel = new VBox(15);
        statsPanel.setAlignment(Pos.TOP_LEFT);
        statsPanel.setPadding(new Insets(20));
        statsPanel.setStyle("-fx-background-color: #3a3a3a; -fx-border-color: #555; -fx-border-width: 2;");
        statsPanel.setPrefWidth(250);

        Label statsTitle = new Label("⚔ STATS ⚔");
        statsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        statsTitle.setTextFill(Color.rgb(255, 215, 0));

        scoreLabel = createStatLabel("Score: 0");
        healthLabel = createStatLabel("Health: 100");

        healthBar = new ProgressBar(1.0);
        healthBar.setPrefWidth(200);
        healthBar.setStyle("-fx-accent: #32cd32;");

        positionLabel = createStatLabel("Position: (0, 0)");
        weaponLabel = createStatLabel("Weapon: Silver Sword");
        bonusTimerLabel = createStatLabel("Bonus: --");
        rewardsLabel = createStatLabel("Rewards: 0 / 0");

        Separator sep1 = new Separator();
        Separator sep2 = new Separator();

        VBox legendBox = createLegend();

        statsPanel.getChildren().addAll(
                statsTitle,
                sep1,
                scoreLabel,
                healthLabel,
                healthBar,
                positionLabel,
                weaponLabel,
                bonusTimerLabel,
                rewardsLabel,
                sep2,
                legendBox
        );

        return statsPanel;
    }

    private Label createStatLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        label.setTextFill(Color.rgb(220, 220, 220));
        return label;
    }

    private VBox createLegend() {
        VBox legend = new VBox(8);
        legend.setAlignment(Pos.TOP_LEFT);

        Label legendTitle = new Label("LEGEND:");
        legendTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        legendTitle.setTextFill(Color.rgb(255, 215, 0));

        legend.getChildren().addAll(
                legendTitle,
                createLegendItem("Player", Color.rgb(50, 205, 50)),
                createLegendItem("Monster", Color.rgb(220, 20, 60)),
                createLegendItem("Human Enemy", Color.rgb(255, 215, 0)),
                createLegendItem("Reward", Color.rgb(255, 215, 0)),
                createLegendItem("Bonus", Color.rgb(138, 43, 226)),
                createLegendItem("Trap", Color.rgb(220, 20, 60)),
                createLegendItem("Exit", Color.rgb(0, 191, 255))
        );

        return legend;
    }

    private HBox createLegendItem(String description, Color color) {
        HBox item = new HBox(10);
        item.setAlignment(Pos.CENTER_LEFT);

        Label colorBox = new Label("  ");
        colorBox.setStyle("-fx-background-color: " + toHexString(color) + "; -fx-border-color: #999; -fx-border-width: 1;");
        colorBox.setPrefWidth(20);

        Label descLabel = new Label(description);
        descLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        descLabel.setTextFill(Color.rgb(200, 200, 200));

        item.getChildren().addAll(colorBox, descLabel);
        return item;
    }

    private String toHexString(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    private VBox createBottomPanel() {
        VBox bottomPanel = new VBox(10);
        bottomPanel.setAlignment(Pos.CENTER);
        bottomPanel.setPadding(new Insets(10));
        bottomPanel.setStyle("-fx-background-color: #3a3a3a; -fx-border-color: #555; -fx-border-width: 2 0 0 0;");

        Label controlsLabel = new Label("CONTROLS: WASD/Arrows = Move  |  1 = Silver Sword  |  2 = Steel Sword  |  Q = Quit");
        controlsLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        controlsLabel.setTextFill(Color.rgb(180, 180, 180));

        messageLabel = new Label("Welcome to Monster's Den! Collect all rewards and reach the exit!");
        messageLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        messageLabel.setTextFill(Color.rgb(255, 215, 0));
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(900);
        messageLabel.setAlignment(Pos.CENTER);

        bottomPanel.getChildren().addAll(controlsLabel, messageLabel);
        return bottomPanel;
    }

    private void setupKeyBindings() {
        Scene scene = stage.getScene();

        scene.setOnKeyPressed(event -> {
            if (game.isGameOver()) {
                return;
            }

            Direction direction = null;

            switch (event.getCode()) {
                case W:
                case UP:
                    direction = Direction.UP;
                    break;
                case S:
                case DOWN:
                    direction = Direction.DOWN;
                    break;
                case A:
                case LEFT:
                    direction = Direction.LEFT;
                    playerFacingLeft = true;
                    break;
                case D:
                case RIGHT:
                    direction = Direction.RIGHT;
                    playerFacingLeft = false;
                    break;

                case DIGIT1:
                case NUMPAD1:
                    ((Player) game.getPlayer()).switchWeapon(new SilverSword());
                    displayMessage("Switched to Silver Sword (effective vs Monsters)");
                    update();
                    break;
                case DIGIT2:
                case NUMPAD2:
                    ((Player) game.getPlayer()).switchWeapon(new SteelSword());
                    displayMessage("Switched to Steel Sword (effective vs Humans)");
                    update();
                    break;

                case Q:
                case ESCAPE:
                    if (showQuitConfirmation()) {
                        Platform.exit();
                    }
                    break;

                default:
                    break;
            }

            if (direction != null) {
                PlayerMovementStrategy strategy = (PlayerMovementStrategy) game.getPlayer().getMovementStrategy();
                Command moveCommand = new MoveCommand(strategy, direction);
                moveCommand.execute(game);
                game.tick();
            }
        });
    }

    /**
     * Renders the game board with images.
     */
    /**
 * Renders the game board using images that scale to the full window.
 */
    /**
     * Renders the game board using images that scale to the full window.
     */
    private void renderBoard() {
        Board board = game.getBoard();
        Player player = game.getPlayer();

        // Calculate cell size while maintaining square aspect ratio
        double cellWidth = canvas.getWidth() / board.getWidth();
        double cellHeight = canvas.getHeight() / board.getHeight();
        double cellSize = Math.min(cellWidth, cellHeight); // Use the smaller dimension to keep cells square

        // Center the board in the canvas
        double offsetX = (canvas.getWidth() - (cellSize * board.getWidth())) / 2;
        double offsetY = (canvas.getHeight() - (cellSize * board.getHeight())) / 2;

        // Clear the entire canvas
        gc.setFill(Color.rgb(30, 30, 30));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Draw each cell and its contents
        for (int y = 0; y < board.getHeight(); y++) {
            for (int x = 0; x < board.getWidth(); x++) {
                Cell cell = board.getCell(x, y);
                if (cell == null) continue;

                double pixelX = offsetX + (x * cellSize);
                double pixelY = offsetY + (y * cellSize);

                // Draw the base floor or wall image
                if (cell.isWall()) {
                    drawImageScaled("wall", pixelX, pixelY, cellSize, cellSize, COLOR_WALL);
                } else {
                    drawImageScaled("floor", pixelX, pixelY, cellSize, cellSize, COLOR_EMPTY);
                }

                // Overlay subtle tint for start/exit cells
                if (cell.isStart()) {
                    gc.setFill(COLOR_START.deriveColor(0, 1, 1, 0.35));
                    gc.fillRect(pixelX, pixelY, cellSize, cellSize);
                } else if (cell.isExit()) {
                    gc.setFill(COLOR_EXIT.deriveColor(0, 1, 1, 0.35));
                    gc.fillRect(pixelX, pixelY, cellSize, cellSize);
                }

                // 1️⃣ Draw trap (punishment)
                Punishment trap = cell.getPunishment();
                if (trap != null && trap.isActive()) {
                    drawImageScaled("trap", pixelX, pixelY, cellSize, cellSize, COLOR_PUNISHMENT);
                }

                // 2️⃣ Draw reward or bonus
                Reward reward = cell.getReward();
                if (reward != null && !reward.isCollected()) {
                    if (reward instanceof BonusReward) {
                        drawImageScaled("bonus", pixelX, pixelY, cellSize, cellSize, COLOR_BONUS);
                    } else {
                        drawImageScaled("reward", pixelX, pixelY, cellSize, cellSize, COLOR_REWARD);
                    }
                }

                // 3️⃣ Draw exit image (if applicable)
                if (cell.isExit()) {
                    drawImageScaled("exit", pixelX, pixelY, cellSize, cellSize, COLOR_EXIT);
                }

                // 4️⃣ Draw enemy (monster/human)
                Enemy enemy = cell.getEnemy();
                if (enemy != null) {
                    if (enemy instanceof Monster) {
                        drawImageScaled("monster", pixelX, pixelY, cellSize, cellSize, COLOR_MONSTER);
                    } else if (enemy instanceof HumanEnemy) {
                        drawImageScaled("human", pixelX, pixelY, cellSize, cellSize, COLOR_HUMAN);
                    }
                }
            }
        }

        // 5️⃣ Draw player last (always on top)
        if (player != null) {
            double px = offsetX + (player.getX() * cellSize);
            double py = offsetY + (player.getY() * cellSize);
            drawPlayerImageScaled(px, py, cellSize, cellSize);
        }

        // Draw grid lines for structure
        gc.setStroke(COLOR_GRID);
        gc.setLineWidth(1.0);
        for (int i = 0; i <= board.getWidth(); i++) {
            double x = offsetX + (i * cellSize);
            gc.strokeLine(x, offsetY, x, offsetY + (board.getHeight() * cellSize));
        }
        for (int i = 0; i <= board.getHeight(); i++) {
            double y = offsetY + (i * cellSize);
            gc.strokeLine(offsetX, y, offsetX + (board.getWidth() * cellSize), y);
        }
    }



    /**
     * Draws a single cell with images or colored rectangles.
     */
    private void drawCell(int x, int y, Cell cell, Player player) {
        int pixelX = x * CELL_SIZE;
        int pixelY = y * CELL_SIZE;

        // Draw wall
        if (cell == null || !cell.isWalkable()) {
            drawImageOrColor(pixelX, pixelY, "wall", COLOR_WALL);
            return;
        }

        // Draw floor
        drawImageOrColor(pixelX, pixelY, "floor", COLOR_EMPTY);

        // Draw special cell backgrounds
        if (cell.isExit()) {
            gc.setFill(COLOR_EXIT.deriveColor(0, 1, 1, 0.3));
            gc.fillRect(pixelX, pixelY, CELL_SIZE, CELL_SIZE);
        } else if (cell.isStart()) {
            gc.setFill(COLOR_START.deriveColor(0, 1, 1, 0.3));
            gc.fillRect(pixelX + 5, pixelY + 5, CELL_SIZE - 10, CELL_SIZE - 10);
        }

        // Draw entities in priority order (bottom to top)

        // 1. Draw punishment/trap
        Punishment punishment = cell.getPunishment();
        if (punishment != null && punishment.isActive()) {
            drawImageOrColor(pixelX, pixelY, "trap", Color.rgb(220, 20, 60));
        }

        // 2. Draw reward
        Reward reward = cell.getReward();
        if (reward != null && !reward.isCollected()) {
            if (reward instanceof BonusReward) {
                BonusReward bonus = (BonusReward) reward;
                if (!bonus.isExpired()) {
                    drawImageOrColor(pixelX, pixelY, "bonus", Color.rgb(138, 43, 226));
                }
            } else if (reward instanceof RegularReward) {
                drawImageOrColor(pixelX, pixelY, "reward", Color.rgb(255, 215, 0));
            }
        }

        // 3. Draw exit marker
        if (cell.isExit()) {
            drawImageOrColor(pixelX, pixelY, "exit", Color.WHITE);
        }

        // 4. Draw enemy
        Enemy enemy = cell.getEnemy();
        if (enemy != null) {
            if (enemy instanceof Monster) {
                drawImageOrColor(pixelX, pixelY, "monster", Color.rgb(220, 20, 60));
            } else if (enemy instanceof HumanEnemy) {
                drawImageOrColor(pixelX, pixelY, "human", Color.rgb(255, 215, 0));
            }
        }

        // 5. Draw player (always on top)
        if (x == player.getX() && y == player.getY()) {
            drawPlayerImage(pixelX, pixelY);
        }
    }

    /**
     * Draws the player image facing the correct direction.
     */
    private void drawPlayerImage(int x, int y) {
        Image playerImage = getImage("player");
        int padding = 4;
        double drawX = x + padding;
        double drawY = y + padding;
        double width = CELL_SIZE - (padding * 2);
        double height = CELL_SIZE - (padding * 2);

        if (playerImage != null && !playerImage.isError()) {
            if (playerFacingLeft) {
                // Flip horizontally by drawing with negative width
                gc.drawImage(playerImage,
                        drawX + width, drawY,  // start position shifted right
                        -width, height);       // negative width flips image
            } else {
                gc.drawImage(playerImage,
                        drawX, drawY,
                        width, height);
            }
        } else {
            // fallback if image not loaded
            gc.setFill(Color.rgb(50, 205, 50));
            gc.fillOval(drawX, drawY, width, height);
        }
    }


    /**
     * Draws an image if available, otherwise draws a colored rectangle.
     */
    private void drawImageOrColor(int x, int y, String imageKey, Color fallbackColor) {
        Image image = getImage(imageKey);

        if (image != null && !image.isError()) {
            // Draw image, scaled to fit cell with padding
            int padding = 4;
            gc.drawImage(image,
                    x + padding, y + padding,
                    CELL_SIZE - (padding * 2), CELL_SIZE - (padding * 2));
        } else {
            // Fallback: draw colored circle or rectangle
            gc.setFill(fallbackColor);
            int padding = 8;
            gc.fillOval(x + padding, y + padding,
                    CELL_SIZE - (padding * 2), CELL_SIZE - (padding * 2));
        }
    }

    /**
 * Draws an image scaled to a cell, falling back to a color if missing.
 */
private void drawImageScaled(String key, double x, double y, double w, double h, Color fallbackColor) {
    Image image = getImage(key);
    if (image != null && !image.isError()) {
        gc.drawImage(image, x + 2, y + 2, w - 4, h - 4);
    } else {
        gc.setFill(fallbackColor);
        gc.fillRect(x + 4, y + 4, w - 8, h - 8);
    }
}

/**
 * Draws the player image scaled to fit dynamic cell size (with left/right facing).
 */
private void drawPlayerImageScaled(double x, double y, double w, double h) {
    Image playerImage = getImage("player");
    if (playerImage != null && !playerImage.isError()) {
        if (playerFacingLeft) {
            gc.drawImage(playerImage, x + w, y, -w, h); // flipped
        } else {
            gc.drawImage(playerImage, x, y, w, h);
        }
    } else {
        gc.setFill(COLOR_PLAYER);
        gc.fillOval(x + 4, y + 4, w - 8, h - 8);
    }
}


    /**
 * Draws a symbol (entity) centered in a dynamically scaled cell.
 */
    private void drawEntity(double x, double y, double cellWidth, double cellHeight,
                            String symbol, Color color, double sizeFactor) {
        gc.setFill(color);

        // Scale font size based on available cell space
        double fontSize = Math.min(cellWidth, cellHeight) * sizeFactor;
        gc.setFont(Font.font("Arial", FontWeight.BOLD, fontSize));

        // Center the text
        double textWidth = symbol.length() * fontSize * 0.6;
        double textHeight = fontSize;
        double centerX = x + (cellWidth - textWidth) / 2;
        double centerY = y + (cellHeight + textHeight) / 2 - 4;

        gc.fillText(symbol, centerX, centerY);
    }


    private void updateStats() {
        Player player = game.getPlayer();

        if (player != null) {
            scoreLabel.setText("Score: " + currentScore);
            int health = player.getHealth();
            healthLabel.setText("Health: " + health);
            healthBar.setProgress(health / 100.0);

            if (health > 60) {
                healthBar.setStyle("-fx-accent: #32cd32;");
            } else if (health > 30) {
                healthBar.setStyle("-fx-accent: #ffa500;");
            } else {
                healthBar.setStyle("-fx-accent: #dc143c;");
            }

            positionLabel.setText(String.format("Position: (%d, %d)", player.getX(), player.getY()));
            weaponLabel.setText("Weapon: " + player.getCurrentSword().getName());
        }

        if (bonusTimerTicks > 0) {
            bonusTimerLabel.setText("Bonus Expires: " + bonusTimerTicks + " ticks");
            bonusTimerLabel.setTextFill(Color.rgb(138, 43, 226));
        } else {
            bonusTimerLabel.setText("Bonus: --");
            bonusTimerLabel.setTextFill(Color.rgb(180, 180, 180));
        }

        rewardsLabel.setText(String.format("Rewards: %d / %d",
                game.getCollectedRewards(),
                game.getTotalRewards()));
    }

    @Override
    public void update() {
        Platform.runLater(() -> {
            renderBoard();
            updateStats();
        });
    }

    @Override
    public void onScoreChanged(int newScore) {
        this.currentScore = newScore;
        Platform.runLater(this::updateStats);
    }

    @Override
    public void onBonusTimerUpdate(int ticksRemaining) {
        this.bonusTimerTicks = ticksRemaining;
        Platform.runLater(this::updateStats);
    }

    @Override
    public void onGameEnd(boolean won, int finalScore) {
        Platform.runLater(() -> {
            renderBoard();
            updateStats();
            showGameOverDialog(won, finalScore);
        });
    }

    public void displayMessage(String message) {
        Platform.runLater(() -> {
            messageLabel.setText(message);
            messageLabel.setTextFill(Color.rgb(255, 215, 0));
        });
    }

    public void displayError(String error) {
        Platform.runLater(() -> {
            messageLabel.setText("ERROR: " + error);
            messageLabel.setTextFill(Color.rgb(220, 20, 60));
        });
    }

    private void showGameOverDialog(boolean won, int finalScore) {
        Alert alert = new Alert(won ? Alert.AlertType.INFORMATION : Alert.AlertType.WARNING);
        alert.setTitle("Game Over");
        alert.setHeaderText(won ? "🎉 VICTORY! 🎉" : "💀 DEFEAT 💀");

        String content = won
                ? "Congratulations! You have escaped the Monster's Den!\n\nFinal Score: " + finalScore
                : "You have been defeated...\n\nFinal Score: " + finalScore;

        alert.setContentText(content);
        alert.showAndWait();

        Alert playAgain = new Alert(Alert.AlertType.CONFIRMATION);
        playAgain.setTitle("Play Again?");
        playAgain.setHeaderText("Would you like to play again?");
        playAgain.setContentText("Choose your option:");

        ButtonType buttonYes = new ButtonType("Play Again");
        ButtonType buttonNo = new ButtonType("Exit", ButtonBar.ButtonData.CANCEL_CLOSE);
        playAgain.getButtonTypes().setAll(buttonYes, buttonNo);

        playAgain.showAndWait().ifPresent(response -> {
        if (response == buttonYes) {
            // Restart game
            game.initialize();
            game.startGameLoop();   // 🔥 start ticking again
            update();
            displayMessage("New game started! Good luck!");
        } else {
            Platform.exit();
        }
});

    }

    private boolean showQuitConfirmation() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Quit Game");
        alert.setHeaderText("Are you sure you want to quit?");
        alert.setContentText("Your progress will be lost.");

        ButtonType buttonYes = new ButtonType("Yes, Quit");
        ButtonType buttonNo = new ButtonType("No, Continue", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonYes, buttonNo);

        return alert.showAndWait().orElse(buttonNo) == buttonYes;
    }

    public void show() {
        stage.show();
        update();
    }

    private String getCSS() {
        return ".label {" +
                "    -fx-font-family: 'Arial';" +
                "}" +
                ".progress-bar > .track {" +
                "    -fx-background-color: #555555;" +
                "    -fx-background-radius: 5;" +
                "}" +
                ".progress-bar > .bar {" +
                "    -fx-background-radius: 5;" +
                "    -fx-padding: 3px;" +
                "}" +
                ".separator {" +
                "    -fx-background-color: #555555;" +
                "}";
    }
}
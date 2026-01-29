package com.team3.monstersden.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.team3.monstersden.characters.Enemy;
import com.team3.monstersden.characters.HumanEnemy;
import com.team3.monstersden.characters.Monster;
import com.team3.monstersden.characters.Player;
import com.team3.monstersden.items.BonusReward;
import com.team3.monstersden.items.Punishment;
import com.team3.monstersden.items.RegularReward;
import com.team3.monstersden.patterns.observer.GameObserver;
import com.team3.monstersden.patterns.strategy.PlayerMovementStrategy;
import com.team3.monstersden.patterns.strategy.SmartEnemyStrategy;

/**
 * Main game controller implementing Singleton pattern.
 * Manages the game loop, world updates, and state.
 */
public class Game {
    private static Game instance;
    private Board board;
    private Player player;
    private List<Enemy> enemies;
    private List<Punishment> traps;
    private SpawnManager spawnManager;
    private int score;
    private GameState gameState;
    private List<GameObserver> observers;
    private Timer gameTimer;
    private int requiredRewards;
    private int rewardsCollected;
    private List<BonusReward> activeBonusRewards;

    private static final int TICK_INTERVAL = 500; 

    public enum GameState { NOT_STARTED, RUNNING, PAUSED, WON, LOST }

    /**
     * Constructor to initialize Game
     */
    private Game() {
        this.board = new Board();
        this.enemies = new ArrayList<>();
        this.traps = new ArrayList<>();
        this.observers = new ArrayList<>();
        this.activeBonusRewards = new ArrayList<>();
        this.gameState = GameState.NOT_STARTED;
    }

    /**
     * Returns the singleton instance of the Game
     * @return singleton Game instance
     */
    public static synchronized Game getInstance() {
        if (instance == null) instance = new Game();
        return instance;
    }

    /**
     * Initializes or resets the game state to start a new game.
     */
    public void initialize() {
        try {
            board.loadMap("level1.txt");
            score = 0;
            rewardsCollected = 0;
            enemies.clear();
            traps.clear();
            gameState = GameState.NOT_STARTED;
            spawnManager = new SpawnManager(this);

            // Init player
            if (player == null) {
                player = new Player("Gerald", board.getStartCell().getX(), board.getStartCell().getY());
                player.setMovementStrategy(new PlayerMovementStrategy());
            } else {
                player.setX(board.getStartCell().getX());
                player.setY(board.getStartCell().getY());
                player.setHealth(GameConfig.PLAYER_START_HP);
            }

            populateWorld();
            notifyObservers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateWorld() {
        Random rng = new Random();

        // 1) ENEMIES: mix of monsters and humans
        int monstersToSpawn = 3;
        int humansToSpawn   = 2;

        for (int i = 0; i < monstersToSpawn; i++) {
            Cell cell = board.getRandomEmptyCell();
            if (cell != null) {
                Monster m = new Monster(
                    cell.getX(),
                    cell.getY(),
                    GameConfig.MONSTER_DAMAGE,
                    GameConfig.MONSTER_MAX_HP
                );
                m.setMovementStrategy(new SmartEnemyStrategy());
                cell.setEnemy(m);
                enemies.add(m);
            }
        }

        for (int i = 0; i < humansToSpawn; i++) {
            Cell cell = board.getRandomEmptyCell();
            if (cell != null) {
                HumanEnemy h = new HumanEnemy(
                    cell.getX(),
                    cell.getY(),
                    GameConfig.HUMAN_DAMAGE,
                    GameConfig.HUMAN_MAX_HP
                );
                h.setMovementStrategy(new SmartEnemyStrategy());
                cell.setEnemy(h);
                enemies.add(h);
            }
        }

        // 2) REWARDS
        int rewardCount = 5;
        requiredRewards = rewardCount;
        for (int i = 0; i < rewardCount; i++) {
            Cell cell = board.getRandomEmptyCell();
            if (cell != null) {
                cell.setReward(new RegularReward(cell.getX(), cell.getY(), GameConfig.SCORE_COLLECT_REWARD));
            }
        }

        // 3) BONUS REWARDS
        int bonusRewardCount = 2;
        for (int i = 0; i < bonusRewardCount; i++) {
            Cell cell = board.getRandomEmptyCell();
            if (cell != null) {
                BonusReward bonus = new BonusReward(
                    cell.getX(), cell.getY(),
                    GameConfig.SCORE_COLLECT_REWARD * 2,
                    15
                );
                cell.setReward(bonus);
            }
        }

        // 4) TRAPS
        for (int i = 0; i < 3; i++) {
            Cell cell = board.getRandomEmptyCell();
            if (cell != null) {
                Punishment trap = new Punishment(cell.getX(), cell.getY(), GameConfig.TRAP_DAMAGE);
                cell.setPunishment(trap);
                traps.add(trap);
            }
        }
    }

    /**
     * Starts the main game loop, updating the game state at fixed intervals.
     */
    public void startGameLoop() {
        if (gameTimer != null) gameTimer.cancel();
        gameState = GameState.RUNNING;
        gameTimer = new Timer("GameLoop", true);
        gameTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (gameState == GameState.RUNNING) tick();
            }
        }, 0, TICK_INTERVAL);
    }

    /**
     * Performs a single game tick, updating all game entities and checking win/loss conditions.
     */
    public void tick() {
        if (gameState != GameState.RUNNING) return;

        updateBonusRewards();

        Iterator<Enemy> enemyIt = enemies.iterator();
        while (enemyIt.hasNext()) {
            Enemy enemy = enemyIt.next();
            if (enemy.isAlive()) {
                enemy.tick(board, player);
            } else {
                board.getCell(enemy.getX(), enemy.getY()).setEnemy(null);
                enemyIt.remove();
                addScore(GameConfig.SCORE_KILL_ENEMY);
            }
        }

        for (Punishment trap : traps) {
            trap.tick();
        }

        spawnManager.tick();

        if (!player.isAlive()) {
             gameState = GameState.LOST;
             stopGameLoop();
             notifyGameEnd(false, score);
        } else if (checkWinCondition()) {
             gameState = GameState.WON;
             stopGameLoop();
             addScore(GameConfig.SCORE_WIN_BONUS);
             notifyGameEnd(true, score);
        }
        notifyObservers();
    }

    /**
     * Updates all active bonus rewards.
     */
    private void updateBonusRewards() {
        Iterator<BonusReward> iterator = activeBonusRewards.iterator();
        while (iterator.hasNext()) {
            BonusReward bonus = iterator.next();
            boolean expired = bonus.tick(); // Update the timer

            if (expired) {
                // Remove expired bonus from the board
                Cell cell = board.getCell(bonus.getX(), bonus.getY());
                if (cell != null && cell.getReward() == bonus) {
                    cell.setReward(null);
                    System.out.println("Bonus reward expired at (" + bonus.getX() + ", " + bonus.getY() + ")");
                }
                iterator.remove();

                // Notify UI about expired bonus
                for (GameObserver observer : observers) {
                    observer.onBonusTimerUpdate(0);
                }
            } else {
                // Update bonus timer display for active bonuses
                for (GameObserver observer : observers) {
                    observer.onBonusTimerUpdate(bonus.getTicksRemaining());
                }
            }
        }
    }

    /**
     * Handles collection of a bonus reward by the player.
     * @param bonus
     */
    public void bonusRewardCollected(BonusReward bonus) {
        if (activeBonusRewards.remove(bonus)) {
            addScore(bonus.getScoreValue());

            // Notify UI about bonus collection
            for (GameObserver observer : observers) {
                observer.onBonusTimerUpdate(0);
            }
            System.out.println("Bonus reward collected at (" + bonus.getX() + ", " + bonus.getY() + ")");
        }
    }

    /**
     * Removes an enemy from the game and clears it from its current cell
     * @param e the Enemy to remove
     */
    public void removeEnemy(Enemy e) {
        if (e == null) return;
        Cell c = board.getCell(e.getX(), e.getY());
        if (c != null && c.getEnemy() == e) {
            c.setEnemy(null);
        }
        enemies.remove(e);
    }

    /**
     * Stops the main game loop timer
     */
    private void stopGameLoop() {
        if (gameTimer != null){
            gameTimer.cancel();
        }
    }

    /**
     * Checks whether the win condition for the game has been met.
     * @return
     */
    public boolean checkWinCondition() {
        Cell cell = board.getCell(player.getX(), player.getY());
        return cell != null && cell.isExit() && rewardsCollected >= requiredRewards;
    }

    public List<Enemy> getEnemies() { return enemies; }
    public void regularRewardCollected() { rewardsCollected++; }
    public void addScore(int points) { score += points; notifyScoreChanged(score); }
    public void addObserver(GameObserver o) { observers.add(o); }
    public void notifyObservers() { for(GameObserver o : observers) o.update(); }
    public void notifyScoreChanged(int s) { for(GameObserver o : observers) o.onScoreChanged(s); }
    public void notifyGameEnd(boolean w, int s) { for(GameObserver o : observers) o.onGameEnd(w, s); }
    public Board getBoard() { return board; }
    public Player getPlayer() { return player; }
    public void setPlayer(Player p) { this.player = p; }
    public int getCollectedRewards() { return rewardsCollected; }
    public int getTotalRewards() { return requiredRewards; }
    public boolean isGameOver() { return gameState == GameState.WON || gameState == GameState.LOST; }
    public int getScore() { return score; }
}
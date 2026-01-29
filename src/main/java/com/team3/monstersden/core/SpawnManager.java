package com.team3.monstersden.core;

import com.team3.monstersden.characters.Enemy;
import com.team3.monstersden.characters.HumanEnemy;
import com.team3.monstersden.characters.Monster;
import com.team3.monstersden.patterns.strategy.SmartEnemyStrategy;

import java.util.List;
import java.util.Random;

/**
 * Manages the spawning of enemies in the game at periodic intervals
 */
public class SpawnManager {
    private Game game;
    private int spawnTimer;
    private Random random;

    /**
     * Constructor for SpawnManager for the Game
     * @param game Game instance to spawn
     */
    public SpawnManager(Game game) {
        this.game = game;
        this.spawnTimer = 0;
        this.random = new Random();
    }

    /**
     * Advances the spawn timer by one tick and attempts to spawn a new enemy
     * if the spawn interval has been reached.
     */
    public void tick() {
        spawnTimer++;
        if (spawnTimer >= GameConfig.SPAWN_INTERVAL_TICKS) {
            spawnTimer = 0;
            attemptSpawn();
        }
    }

    /**
     * Attempts to spawn a new enemy on a random walkable and empty cell.
     */
    private void attemptSpawn() {
        Board board = game.getBoard();
        List<Enemy> enemies = game.getEnemies();

        if (enemies.size() >= GameConfig.MAX_ACTIVE_ENEMIES) return;

        int attempts = 0;
        while (attempts < 10) {
            int x = random.nextInt(board.getWidth());
            int y = random.nextInt(board.getHeight());
            Cell cell = board.getCell(x, y);

            if (cell != null && cell.isWalkable() && cell.isEmpty() && !cell.isStart() && !cell.isExit()) {
                Enemy newEnemy;
                // PASSING 4 ARGUMENTS HERE:
                if (random.nextBoolean()) {
                    newEnemy = new Monster(x, y, GameConfig.MONSTER_DAMAGE, GameConfig.MONSTER_MAX_HP);
                } else {
                    newEnemy = new HumanEnemy(x, y, GameConfig.HUMAN_DAMAGE, GameConfig.HUMAN_MAX_HP);
                }
                
                newEnemy.setMovementStrategy(new SmartEnemyStrategy());
                cell.setEnemy(newEnemy);
                enemies.add(newEnemy);
                System.out.println("Spawned " + newEnemy.getClass().getSimpleName() + " at (" + x + "," + y + ")");
                return;
            }
            attempts++;
        }
    }
}
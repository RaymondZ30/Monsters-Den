package com.team3.monstersden.patterns.strategy;

import com.team3.monstersden.characters.AIState;
import com.team3.monstersden.characters.Character;
import com.team3.monstersden.characters.Enemy;
import com.team3.monstersden.characters.Player;
import com.team3.monstersden.core.Board;
import com.team3.monstersden.core.GameConfig;
import com.team3.monstersden.util.AStarPathFinder;
import com.team3.monstersden.util.Direction;

import java.util.Random;

/**
 * Advanced movement strategy that implements a state machine for enemy AI.
 * Uses A* pathfinding for chasing and random movement for patrolling.
 */
public class SmartEnemyStrategy implements MovementStrategy {

    private final Random random = new Random();

    @Override
    public Direction chooseMove(Board board, Character self, Player player) {
        if (!(self instanceof Enemy)) return Direction.NONE;
        Enemy enemy = (Enemy) self;

        // Update AI State based on environment
        updateState(enemy, player);

        // Decide move based on current state
        switch (enemy.getCurrentState()) {
            case CHASE:
                return AStarPathFinder.findNextMove(board, enemy.getX(), enemy.getY(), player.getX(), player.getY());
            case PATROL:
                return getPatrolMove(board, enemy);
            case IDLE:
            case ATTACK:
            default:
                return Direction.NONE;
        }
    }

    private void updateState(Enemy enemy, Player player) {
        if (player == null || !player.isAlive()) {
            enemy.setCurrentState(AIState.PATROL);
            return;
        }

        int dist = Math.abs(enemy.getX() - player.getX()) + Math.abs(enemy.getY() - player.getY());

        if (dist <= GameConfig.ENEMY_ATTACK_RANGE) {
            enemy.setCurrentState(AIState.ATTACK);
        } else if (dist <= GameConfig.ENEMY_CHASE_RANGE) {
            enemy.setCurrentState(AIState.CHASE);
        } else {
             // If lost player, go back to patrol
            enemy.setCurrentState(AIState.PATROL);
        }
    }

    private Direction getPatrolMove(Board board, Enemy enemy) {
        // Simple random wander for patrol, but avoids walls and keeps moving if possible
        Direction[] dirs = Direction.values();
        // Try a few random directions to find a valid one
        for (int i = 0; i < 4; i++) {
             Direction dir = dirs[random.nextInt(4)]; // Only UP, DOWN, LEFT, RIGHT
             if (dir == Direction.NONE) continue;
             
             int nx = enemy.getX() + dir.getDx();
             int ny = enemy.getY() + dir.getDy();
             if (board.isValidMove(nx, ny) && !board.getCell(nx, ny).hasEnemy()) {
                 return dir;
             }
        }
        return Direction.NONE; // Stay still if trapped or unlucky
    }
}
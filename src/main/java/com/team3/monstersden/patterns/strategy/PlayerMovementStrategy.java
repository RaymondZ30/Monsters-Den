package com.team3.monstersden.patterns.strategy;

import com.team3.monstersden.characters.Character;
import com.team3.monstersden.characters.Enemy;
import com.team3.monstersden.characters.Player;
import com.team3.monstersden.core.Board;
import com.team3.monstersden.core.Cell;
import com.team3.monstersden.core.Game;
import com.team3.monstersden.core.GameConfig;
import com.team3.monstersden.items.BonusReward;
import com.team3.monstersden.items.Punishment;
import com.team3.monstersden.items.Reward;
import com.team3.monstersden.items.RegularReward;
import com.team3.monstersden.util.Direction;

/**
 * Player movement logic using Strategy pattern.
 * Handles directional input, collision checks, interactions, and notifies observers.
 */
public class PlayerMovementStrategy implements MovementStrategy {

    @Override
    public Direction chooseMove(Board board, Character self, Player player) {
        // Not used for manual player control
        return Direction.NONE;
    }

    /**
     * Moves the player in the specified direction if valid, handling all interactions.
     */
    public void addMove(Game game, Direction dir) {
    if (game == null || game.getBoard() == null || game.getPlayer() == null || dir == null) return;

    Board board = game.getBoard();
    Player player = game.getPlayer();

    int newX = player.getX();
int newY = player.getY();

switch (dir) {
    case UP:
        newY -= 1;
        break;
    case DOWN:
        newY += 1;
        break;
    case LEFT:
        newX -= 1;
        break;
    case RIGHT:
        newX += 1;
        break;
    default:
        return;
}


    // 1) Validate destination
    if (!board.isValidMove(newX, newY)) {
        game.notifyObservers();
        return;
    }

    Cell nextCell = board.getCell(newX, newY);

    // 2) Combat first: if enemy present, fight and only move if enemy dies
    if (nextCell.hasEnemy()) {
    Enemy enemy = nextCell.getEnemy();

    // Player attacks first
    player.attack(enemy);

    if (!enemy.isAlive()) {
        System.out.println("Enemy defeated!");
        nextCell.setEnemy(null);
        game.addScore(50);
        game.removeEnemy(enemy); // remove from enemy list too

        // Move into the now-empty cell
        player.setX(newX);
        player.setY(newY);
        game.notifyObservers();
        return;
    } else {
        // Enemy survives → counterattacks ONCE
        int damage = enemy.getDamage();
        player.takeDamage(damage);
        System.out.println(enemy.getClass().getSimpleName() + " hit back for " + damage + " damage!");
        game.notifyObservers();
        return; // Player does NOT move into the cell
    }
}


    // 3) No enemy: move freely
    player.setX(newX);
    player.setY(newY);

    // 4) Interactions after moving
    handleCellInteractions(game, nextCell);

    game.notifyObservers();
}

// Small helper to keep addMove clean
private void handleCellInteractions(Game game, Cell cell) {
    if (cell == null) return;

    // Rewards
    if (cell.hasReward()) {
        Reward reward = cell.getReward();
        if (!reward.isCollected()) {
            int points = reward.collect();
            game.addScore(points);

            if (reward instanceof RegularReward) {
                game.regularRewardCollected();
            } else if (reward instanceof BonusReward) {
                // Let Game handle active/expiry UI
                game.bonusRewardCollected((BonusReward) reward);
            }
            cell.setReward(null);
        }
    }

    // Traps
    if (cell.hasPunishment()) {
        Punishment trap = cell.getPunishment();
        if (trap.isActive()) {
            int penalty = trap.trigger();
            game.addScore(-penalty);
            game.getPlayer().takeDamage(GameConfig.TRAP_HP_DAMAGE); // HP loss too

            if (!trap.isRespawnable()) {
                cell.setPunishment(null);
            }
        }
    }
}


}
package com.team3.monstersden.patterns.command;

import com.team3.monstersden.core.Game;
import com.team3.monstersden.patterns.strategy.PlayerMovementStrategy;
import com.team3.monstersden.util.Direction;

/**
 * Command that moves the player in a given direction using the PlayerMovementStrategy.
 */
public class MoveCommand implements Command {

    private final PlayerMovementStrategy movementStrategy;
    private final Direction direction;

    public MoveCommand(PlayerMovementStrategy strategy, Direction direction) {
        this.movementStrategy = strategy;
        this.direction = direction;
    }

    @Override
    public void execute(Game game) {
        if (movementStrategy != null && direction != null) {
            movementStrategy.addMove(game, direction);
        }
    }
}

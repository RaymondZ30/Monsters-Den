package com.team3.monstersden.patterns.strategy;

import com.team3.monstersden.characters.Character;
import com.team3.monstersden.characters.Player;
import com.team3.monstersden.core.Board ;
import com.team3.monstersden.util.Direction;

/**
 * MovementStrategy interface defines a strategy for character movement on the board.
 * Different implementations can provide various movement behaviors.
 * @author ProjectTeam3
 * @version 1.0
 */
public interface MovementStrategy {
    Direction chooseMove(Board board, Character self, Player player);
}

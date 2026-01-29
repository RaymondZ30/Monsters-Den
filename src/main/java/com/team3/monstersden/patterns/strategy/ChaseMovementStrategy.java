package com.team3.monstersden.patterns.strategy;
/**
 * ChaseMovementStrategy class that implements MovementStrategy to move towards the player.
 * This strategy calculates the direction to move based on the player's position.
 * @author Team 3
 * @version 1.0
 */
import com.team3.monstersden.characters.Character;
import com.team3.monstersden.characters.Player;
import com.team3.monstersden.core.Board;
import com.team3.monstersden.util.Direction;
public class ChaseMovementStrategy implements MovementStrategy {
    @Override
    public Direction chooseMove(Board board, Character self, Player player){
        int dx = Integer.compare(player.getX(), self.getX());
        int dy = Integer.compare(player.getY(), self.getY());
        if (Math.abs(dx) > Math.abs(dy)) {
            return dx > 0 ? Direction.RIGHT : Direction.LEFT; 
        } else {
            return dy > 0 ? Direction.DOWN : Direction.UP; 
        }
    }
}

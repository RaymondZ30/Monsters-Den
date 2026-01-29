package com.team3.monstersden.patterns.command;
import com.team3.monstersden.core.Game;
/**
 * Command interface for the Command design pattern.
 * @author Team 3
 * @version 1.0
 */
public interface Command {
    // encapsulates a user's action as an obj
    void execute(Game game);
}

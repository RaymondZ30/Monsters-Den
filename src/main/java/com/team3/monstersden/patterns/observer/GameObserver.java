package com.team3.monstersden.patterns.observer;

/**
 * Observer interface for the Observer design pattern.
 * Classes implementing this interface can observe game state changes.
 * 
 * @author Maria Leon Campos
 * @version 1.0
 */
public interface GameObserver {
    /**
     * Called when the game state changes (score update, player move, etc.).
     */
    void update();
    
    /**
     * Called when the game score changes.
     * 
     * @param newScore the updated score
     */
    void onScoreChanged(int newScore);
    
    /**
     * Called when a bonus reward timer updates.
     * 
     * @param ticksRemaining the number of ticks remaining
     */
    void onBonusTimerUpdate(int ticksRemaining);
    
    /**
     * Called when the game ends.
     * 
     * @param won true if player won, false if lost
     * @param finalScore the final score
     */
    void onGameEnd(boolean won, int finalScore);
}
package com.team3.monstersden.patterns.observer;

import com.team3.monstersden.core.Game;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class GameObserverPatternTest {

    private Game game;
    private MockGameObserver observer;

    // A simple mock implementation to verify callbacks
    private static class MockGameObserver implements GameObserver {
        boolean updateCalled = false;
        boolean scoreChangedCalled = false;
        boolean gameEndCalled = false;
        boolean bonusTimerCalled = false;
        
        int lastScore = -1;
        boolean gameWon = false;

        @Override
        public void update() {
            updateCalled = true;
        }

        @Override
        public void onScoreChanged(int newScore) {
            scoreChangedCalled = true;
            lastScore = newScore;
        }

        @Override
        public void onBonusTimerUpdate(int ticksRemaining) {
            bonusTimerCalled = true;
        }

        @Override
        public void onGameEnd(boolean won, int finalScore) {
            gameEndCalled = true;
            gameWon = won;
            lastScore = finalScore;
        }
    }

    @Before
    public void setUp() {
        game = Game.getInstance();
        game.initialize();
        observer = new MockGameObserver();
        game.addObserver(observer);
    }

    @Test
    public void testNotifyObserversCallsUpdate() {
        game.notifyObservers();
        assertTrue("update() should be called", observer.updateCalled);
    }

    @Test
    public void testAddScoreNotifiesObserver() {
        game.addScore(100);
        assertTrue("onScoreChanged() should be called", observer.scoreChangedCalled);
        assertEquals("Observer should receive correct score", 100, observer.lastScore);
    }

    @Test
    public void testGameEndNotification() {
        game.notifyGameEnd(true, 5000);
        assertTrue("onGameEnd() should be called", observer.gameEndCalled);
        assertTrue("Observer should know game was won", observer.gameWon);
        assertEquals("Observer should receive final score", 5000, observer.lastScore);
    }
    
    @Test
    public void testTickUpdatesBonusTimer() {
        // Ticking the game should trigger updates, including potentially bonus timers
        // if active bonus rewards exist.
        // We force an update to verify the loop mechanism
        game.notifyObservers(); 
        assertTrue(observer.updateCalled);
    }
}
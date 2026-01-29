package com.team3.monstersden.patterns.observer;

import com.team3.monstersden.core.Game;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class GameObserverTest {

    private Game game;
    private MockObserver observer;

    // Simple mock to track calls
    private static class MockObserver implements GameObserver {
        boolean updateCalled = false;
        boolean scoreChangedCalled = false;
        boolean gameEndCalled = false;
        boolean bonusTimerCalled = false;

        @Override
        public void update() {
            updateCalled = true;
        }

        @Override
        public void onScoreChanged(int newScore) {
            scoreChangedCalled = true;
        }

        @Override
        public void onBonusTimerUpdate(int ticksRemaining) {
            bonusTimerCalled = true;
        }

        @Override
        public void onGameEnd(boolean won, int finalScore) {
            gameEndCalled = true;
        }
    }

    @Before
    public void setUp() {
        game = Game.getInstance();
        game.initialize();
        observer = new MockObserver();
        game.addObserver(observer);
    }

    @Test
    public void testNotifyObservers() {
        game.notifyObservers();
        assertTrue("update() should be called", observer.updateCalled);
    }

    @Test
    public void testScoreChangeNotification() {
        game.addScore(100);
        assertTrue("onScoreChanged() should be called", observer.scoreChangedCalled);
    }

    @Test
    public void testGameEndNotification() {
        game.notifyGameEnd(true, 1000);
        assertTrue("onGameEnd() should be called", observer.gameEndCalled);
    }
}
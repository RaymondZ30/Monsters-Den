package com.team3.monstersden.characters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import com.team3.monstersden.core.GameConfig;
import com.team3.monstersden.util.Direction;

/**
 * Unit tests for {@link HumanEnemy}.
 */
public class HumanEnemyTest {

    @Test
    public void testConstructorSetsFields() {
        HumanEnemy h = new HumanEnemy(4, 5, GameConfig.HUMAN_DAMAGE, GameConfig.HUMAN_MAX_HP);

        assertEquals(4, h.getX());
        assertEquals(5, h.getY());
        assertEquals(GameConfig.HUMAN_DAMAGE, h.getDamage());
        assertEquals(GameConfig.HUMAN_MAX_HP, h.getHealth());
        assertEquals(AIState.PATROL, h.getCurrentState());
        assertTrue(h.isAlive());
    }

    @Test
    public void testTakeDamageAndIsAlive() {
        HumanEnemy h = new HumanEnemy(0, 0, GameConfig.HUMAN_DAMAGE, GameConfig.HUMAN_MAX_HP);

        int startHp = h.getHealth();
        h.takeDamage(5);
        assertEquals(startHp - 5, h.getHealth());
        assertTrue(h.isAlive());

        h.takeDamage(1000);
        assertEquals(0, h.getHealth());
        assertFalse(h.isAlive());
    }

    @Test
    public void testMoveReturnsNoneWhenNoStrategy() {
        HumanEnemy h = new HumanEnemy(0, 0, GameConfig.HUMAN_DAMAGE, GameConfig.HUMAN_MAX_HP);

        Direction d = h.move(null, null);

        assertEquals(Direction.NONE, d);
    }
}

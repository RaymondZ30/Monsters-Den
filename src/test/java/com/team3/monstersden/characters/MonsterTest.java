package com.team3.monstersden.characters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import com.team3.monstersden.core.GameConfig;
import com.team3.monstersden.util.Direction;

/**
 * Unit tests for {@link Monster}.
 */
public class MonsterTest {

    @Test
    public void testConstructorSetsFields() {
        Monster m = new Monster(2, 3, GameConfig.MONSTER_DAMAGE, GameConfig.MONSTER_MAX_HP);

        assertEquals(2, m.getX());
        assertEquals(3, m.getY());
        assertEquals(GameConfig.MONSTER_DAMAGE, m.getDamage());
        assertEquals(GameConfig.MONSTER_MAX_HP, m.getHealth());
        assertEquals(AIState.PATROL, m.getCurrentState());
        assertTrue(m.isAlive());
    }

    @Test
    public void testTakeDamageAndIsAlive() {
        Monster m = new Monster(0, 0, GameConfig.MONSTER_DAMAGE, GameConfig.MONSTER_MAX_HP);

        int startHp = m.getHealth();
        m.takeDamage(10);
        assertEquals(startHp - 10, m.getHealth());
        assertTrue(m.isAlive());

        m.takeDamage(1000);
        assertEquals(0, m.getHealth());
        assertFalse(m.isAlive());
    }

    @Test
    public void testMoveReturnsNoneWhenNoStrategy() {
        Monster m = new Monster(0, 0, GameConfig.MONSTER_DAMAGE, GameConfig.MONSTER_MAX_HP);

        Direction d = m.move(null, null);

        assertEquals(Direction.NONE, d);
    }
}

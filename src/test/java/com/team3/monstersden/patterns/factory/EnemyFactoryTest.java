package com.team3.monstersden.patterns.factory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import com.team3.monstersden.characters.Enemy;
import com.team3.monstersden.characters.HumanEnemy;
import com.team3.monstersden.characters.Monster;
import com.team3.monstersden.core.GameConfig;
/**
 * Unit tests for {@link EnemyFactory}
 * @see EnemyFactory
 * <p> Tests include:
 * <ul>
 * <li> Correct enemy subclass creation for each type ("monster", "human")</li>
 * <li> Case insensitivity of type parameter</li>
 * <li> Defaulting to Monster for null or unknown types</li>
 * <li> Proper navigation of coords (x, y)</li>
 * <li> Correct initialization of enemy dmg values using {@link GameConfig}</li>
 * </ul>
 * 
 * <p> These tests provide full coverage of the factory method.
 */
public class EnemyFactoryTest {

    /**
     * Tests that explicitly requesting a "monster" return a {@link Monster}
     * with proper coords + dmg
     */
    @Test
    public void testCreateMonsterExplicitType() {
        Enemy e = EnemyFactory.create("monster", 2, 3);
        assertTrue(e instanceof Monster);

        // casting to Monster to use getX/getY methods
        assertEquals(2, e.getX());
        assertEquals(3, e.getY());

        // enemy holds dmg, no HP
        assertEquals(GameConfig.MONSTER_DAMAGE, e.getDamage());
    }

    /**
     * Tests that explicitly requesting a "human" return a {@link HumanEnemy}
     * with proper coords + dmg
     */
    @Test
    public void testCreateHumanEnemy() {
        Enemy e = EnemyFactory.create("human", 4, 1);
        assertTrue(e instanceof HumanEnemy);

        HumanEnemy h = (HumanEnemy)e;
        assertEquals(4, e.getX());
        assertEquals(1, e.getY());
        assertEquals(GameConfig.HUMAN_DAMAGE, e.getDamage());
    }

    /**
     * Ensures that type matching is case insensitive
     * by mixing upper + lowercase char
     */
    @Test
    public void testTypeCaseInsensitive() {
        Enemy e = EnemyFactory.create("HuMaN", 0, 0);
        assertTrue(e instanceof HumanEnemy);
    }

    /**
     * Verifies null type defaults to creating a {@link Monster}
     * + coordinates dmg are set properly
     */
    @Test
    public void testNullTypeDefaultsToMonster() {
        Enemy e = EnemyFactory.create(null, 5, 5);
        assertTrue(e instanceof Monster);

        Monster m = (Monster)e;
        assertEquals(5, m.getX());
        assertEquals(5, m.getY());
        assertEquals(GameConfig.MONSTER_DAMAGE, m.getDamage());
    }

    /**
     * Verifies unknown type defaults to creating a {@link Monster}
     * + verifies behaviour is consistent w/ factory design
     */
    @Test
    public void testUnknownTypeDefaultsToMonster() {
        Enemy e = EnemyFactory.create("alien", 9, 9);
        assertTrue(e instanceof Monster);

        Monster m = (Monster)e;
        assertEquals(9, m.getX());
        assertEquals(9, m.getY());
        assertEquals(GameConfig.MONSTER_DAMAGE, m.getDamage());
    }

}

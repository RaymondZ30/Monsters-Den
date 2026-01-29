package com.team3.monstersden.characters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import com.team3.monstersden.items.SilverSword;
import com.team3.monstersden.items.SteelSword;
import com.team3.monstersden.items.Sword;
import com.team3.monstersden.util.Direction;

/**
 * Unit tests for {@link Player}.
 */
public class PlayerTest {

    /**
     * Basic constructor sanity check.
     */
    @Test
    public void testConstructorAndDefaults() {
        Player p = new Player("Mason", 2, 3);

        assertEquals("Mason", p.getName());
        assertEquals(2, p.getX());
        assertEquals(3, p.getY());
        assertNotNull(p.getCurrentSword());
        assertTrue(p.getCurrentSword() instanceof SilverSword); // default sword
        assertTrue(p.isAlive());
        assertEquals(100, p.getHealth());
    }

    /**
     * Movement should return NONE when no strategy is set.
     */
    @Test
    public void testMoveReturnsNoneWhenNoStrategy() {
        Player p = new Player(0, 0);

        Direction dir = p.move(null);

        assertEquals(Direction.NONE, dir);
    }

    /**
     * takeDamage should reduce health but never below zero.
     */
    @Test
    public void testTakeDamageReducesHealthAndClampsToZero() {
        Player p = new Player(0, 0);
        int startHp = p.getHealth();

        p.takeDamage(10);
        assertEquals(startHp - 10, p.getHealth());
        assertTrue(p.isAlive());

        // hit with more than remaining HP
        p.takeDamage(1000);
        assertEquals(0, p.getHealth());
        assertFalse(p.isAlive());
    }

    /**
     * Non-positive damage should be ignored.
     */
    @Test
    public void testTakeDamageNonPositiveIgnored() {
        Player p = new Player(0, 0);
        int startHp = p.getHealth();

        p.takeDamage(0);
        p.takeDamage(-5);

        assertEquals(startHp, p.getHealth());
    }

    /**
     * equipSword delegates to WeaponFactory and updates current sword.
     */
    @Test
    public void testEquipSwordUsesFactory() {
        Player p = new Player(0, 0);

        p.equipSword("steel");
        assertTrue(p.getCurrentSword() instanceof SteelSword);

        p.equipSword("silver");
        assertTrue(p.getCurrentSword() instanceof SilverSword);
    }

    /**
     * switchWeapon should update currentSword and ignore null.
     */
    @Test
    public void testSwitchWeapon() {
        Player p = new Player(0, 0);
        Sword original = p.getCurrentSword();
    
        // Use a real concrete sword as the "new" weapon
        Sword newWeapon = new SteelSword();
        p.switchWeapon(newWeapon);
    
        // Should now point to the new weapon
        assertSame(newWeapon, p.getCurrentSword());
        assertNotSame(original, p.getCurrentSword());
    
        // Switching to null should be ignored and keep current weapon
        p.switchWeapon(null);
        assertSame(newWeapon, p.getCurrentSword());
    }
    

    /**
     * attack should not throw and should not change anything
     * if either the enemy or weapon is null.
     */
    @Test
    public void testAttackSafeOnNullWeaponOrEnemy() {
        Player p = new Player(0, 0);
        RecordingMonster enemy = new RecordingMonster();

        int initialHp = enemy.getHealth();
        // null enemy
        p.attack(null);
        assertEquals(initialHp, enemy.getHealth());

        // null weapon
        p.setCurrentSword(null);
        p.attack(enemy);
        assertEquals(initialHp, enemy.getHealth());
    }

    /**
     * attack should use Sword.calculateDamage and apply bonus
     * when SilverSword vs Monster.
     */
    @Test
    public void testAttackAddsBonusForSilverAgainstMonster() {
        Player p = new Player(0, 0);
        RecordingMonster enemy = new RecordingMonster();

        TestSilverSword sword = new TestSilverSword();
        p.setCurrentSword(sword);

        int base = sword.calculateDamage(enemy); // deterministic value
        p.attack(enemy);

        assertEquals(base + 20, enemy.lastDamage);
    }

    /**
     * attack should use Sword.calculateDamage and apply bonus
     * when SteelSword vs HumanEnemy.
     */
    @Test
    public void testAttackAddsBonusForSteelAgainstHuman() {
        Player p = new Player(0, 0);
        RecordingHumanEnemy enemy = new RecordingHumanEnemy();

        TestSteelSword sword = new TestSteelSword();
        p.setCurrentSword(sword);

        int base = sword.calculateDamage(enemy); // deterministic value
        p.attack(enemy);

        assertEquals(base + 20, enemy.lastDamage);
    }

    // ---------- Test doubles ----------

    /**
     * Silver sword stub that returns a constant damage value.
     * Still a subclass of SilverSword so instanceof checks pass.
     */
    private static class TestSilverSword extends SilverSword {
        @Override
        public int calculateDamage(Enemy enemy) {
            return 10; // fixed for testing
        }
    }

    /**
     * Steel sword stub that returns a constant damage value.
     */
    private static class TestSteelSword extends SteelSword {
        @Override
        public int calculateDamage(Enemy enemy) {
            return 7; // fixed for testing
        }
    }

    /**
     * Monster subclass that records the last damage taken.
     */
    private static class RecordingMonster extends Monster {
        int lastDamage = 0;

        RecordingMonster() {
            super(0, 0, 5, 50);
        }

        @Override
        public void takeDamage(int amount) {
            super.takeDamage(amount);
            this.lastDamage = amount;
        }
    }

    /**
     * HumanEnemy subclass that records the last damage taken.
     */
    private static class RecordingHumanEnemy extends HumanEnemy {
        int lastDamage = 0;

        RecordingHumanEnemy() {
            super(0, 0, 5, 40);
        }

        @Override
        public void takeDamage(int amount) {
            super.takeDamage(amount);
            this.lastDamage = amount;
        }
    }
}

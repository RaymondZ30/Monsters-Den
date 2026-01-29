package com.team3.monstersden.patterns.factory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import com.team3.monstersden.items.SilverSword;
import com.team3.monstersden.items.SteelSword;
import com.team3.monstersden.items.Sword;

/**
 * Unit tests for {@link WeaponFactory}.
 *
 * <p>This test suite verifies:
 * <ul>
 *     <li>Correct weapon subclass creation for each type ("silver", "steel")</li>
 *     <li>Case-insensitive type matching</li>
 *     <li>Default behavior for null or unknown types</li>
 *     <li>Proper initialization of sword name and base damage values</li>
 * </ul>
 *
 * <p>These tests collectively provide full branch coverage of the WeaponFactory
 * {@link WeaponFactory#create(String)} method.</p>
 */
public class WeaponFactoryTest {

    /**
     * Tests that explicitly requesting a "silver" weapon returns a {@link SilverSword}
     * with the expected name and base damage.
     */
    @Test
    public void testCreateSilverSwordExplicitType() {
        Sword sword = WeaponFactory.create("silver");

        assertTrue(sword instanceof SilverSword);
        assertEquals("Silver Sword", sword.getName());
        assertEquals(30, sword.getBaseDamage());
    }

    /**
     * Tests that explicitly requesting a "steel" weapon returns a {@link SteelSword}
     * with the expected name and base damage.
     */
    @Test
    public void testCreateSteelSwordExplicitType() {
        Sword sword = WeaponFactory.create("steel");

        assertTrue(sword instanceof SteelSword);
        assertEquals("Steel Sword", sword.getName());
        assertEquals(30, sword.getBaseDamage());
    }

    /**
     * Ensures that type matching is case-insensitive by mixing upper- and lower-case
     * characters in the weapon type string.
     */
    @Test
    public void testTypeCaseInsensitive() {
        Sword sword = WeaponFactory.create("SiLvEr");

        assertTrue(sword instanceof SilverSword);
    }

    /**
     * Verifies that a null type defaults to creating a {@link SteelSword} and that
     * the created sword is initialized with the expected name and base damage.
     */
    @Test
    public void testNullTypeDefaultsToSteelSword() {
        Sword sword = WeaponFactory.create(null);

        assertTrue(sword instanceof SteelSword);
        assertEquals("Steel Sword", sword.getName());
        assertEquals(30, sword.getBaseDamage());
    }

    /**
     * Verifies that an unknown type defaults to creating a {@link SteelSword} and that
     * this behavior is consistent with the factory's design.
     */
    @Test
    public void testUnknownTypeDefaultsToSteelSword() {
        Sword sword = WeaponFactory.create("axe");

        assertTrue(sword instanceof SteelSword);
        assertEquals("Steel Sword", sword.getName());
        assertEquals(30, sword.getBaseDamage());
    }
}

package com.team3.monstersden.items;

import org.junit.*;
import static org.junit.Assert.*;

/**
 * Test suite for Punishment class (JUnit 4).
 * Tests trap triggering, cooldown system, and state management.
 *
 * @author Team 3 - CMPT 276
 * @version 1.0
 */
public class PunishmentTest {

    private Punishment punishment;

    @Before
    public void setUp() {
        punishment = new Punishment(5, 10, 20);
    }

    // ==================== Constructor Tests ====================

    @Test
    public void testPunishmentConstruction() {
        assertEquals("X coordinate should be 5", 5, punishment.getX());
        assertEquals("Y coordinate should be 10", 10, punishment.getY());
        assertEquals("Penalty value should be 20", 20, punishment.getPenaltyValue());
        assertTrue("Punishment should be active initially", punishment.isActive());
    }

    @Test
    public void testPunishmentZeroPenalty() {
        Punishment zeroPenalty = new Punishment(0, 0, 0);

        assertEquals("Penalty value should be 0", 0, zeroPenalty.getPenaltyValue());
    }

    @Test
    public void testPunishmentNegativeCoordinates() {
        Punishment trap = new Punishment(-5, -10, 15);

        assertEquals("Should handle negative X", -5, trap.getX());
        assertEquals("Should handle negative Y", -10, trap.getY());
    }

    // ==================== Trigger Tests ====================

    @Test
    public void testTriggerWhenActive() {
        int penalty = punishment.trigger();

        assertEquals("Should return penalty value", 20, penalty);
        assertFalse("Should not be active after trigger", punishment.isActive());
    }

    @Test
    public void testTriggerWhenInactive() {
        punishment.trigger(); // First trigger
        int secondTrigger = punishment.trigger(); // Second trigger while on cooldown

        assertEquals("Should return 0 when on cooldown", 0, secondTrigger);
    }

    @Test
    public void testMultipleTriggers() {
        int first = punishment.trigger();
        int second = punishment.trigger();
        int third = punishment.trigger();

        assertEquals("First trigger should return penalty", 20, first);
        assertEquals("Second trigger should return 0", 0, second);
        assertEquals("Third trigger should return 0", 0, third);
    }

    // ==================== Cooldown Tests ====================

    @Test
    public void testTickReducesCooldown() {
        punishment.trigger(); // Start cooldown
        assertFalse("Should be inactive", punishment.isActive());

        // Tick through cooldown period
        for (int i = 0; i < 20; i++) { // Assuming reasonable cooldown
            punishment.tick();
            if (punishment.isActive()) {
                break;
            }
        }

        assertTrue("Should eventually become active again", punishment.isActive());
    }

    @Test
    public void testTickWhenActive() {
        assertTrue("Should start active", punishment.isActive());

        punishment.tick();

        assertTrue("Should remain active if not triggered", punishment.isActive());
    }

    @Test
    public void testRearmAfterCooldown() {
        punishment.trigger(); // Trigger trap

        // Tick until re-armed (using reasonable upper limit)
        for (int i = 0; i < 100; i++) {
            punishment.tick();
            if (punishment.isActive()) {
                break;
            }
        }

        assertTrue("Should be active after cooldown", punishment.isActive());

        // Should be able to trigger again
        int penalty = punishment.trigger();
        assertEquals("Should return penalty value after re-arming", 20, penalty);
    }

    // ==================== State Tests ====================

    @Test
    public void testIsActiveInitialState() {
        assertTrue("New punishment should be active", punishment.isActive());
    }

    @Test
    public void testIsActiveAfterTrigger() {
        punishment.trigger();

        assertFalse("Punishment should not be active after trigger", punishment.isActive());
    }

    // ==================== Getter Tests ====================

    @Test
    public void testGetX() {
        assertEquals("getX should return correct coordinate", 5, punishment.getX());
    }

    @Test
    public void testGetY() {
        assertEquals("getY should return correct coordinate", 10, punishment.getY());
    }

    @Test
    public void testGetPenaltyValue() {
        assertEquals("getPenaltyValue should return correct value", 20, punishment.getPenaltyValue());
    }

    // ==================== Edge Cases ====================

    @Test
    public void testLargePenaltyValue() {
        Punishment largePenalty = new Punishment(0, 0, 1000);

        int penalty = largePenalty.trigger();

        assertEquals("Should handle large penalty values", 1000, penalty);
    }

    @Test
    public void testMultipleTicksWithoutTrigger() {
        for (int i = 0; i < 50; i++) {
            punishment.tick();
        }

        assertTrue("Should remain active without triggering", punishment.isActive());
    }

    @Test
    public void testTriggerRearmTriggerCycle() {
        // First trigger
        int penalty1 = punishment.trigger();
        assertEquals("First trigger should succeed", 20, penalty1);

        // Re-arm through ticking
        for (int i = 0; i < 100; i++) {
            punishment.tick();
            if (punishment.isActive()) break;
        }

        // Second trigger
        int penalty2 = punishment.trigger();
        assertEquals("Second trigger should succeed after re-arm", 20, penalty2);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testIsRespawnableThrowsException() {
        punishment.isRespawnable();
    }
}
package com.team3.monstersden.items;

import com.team3.monstersden.characters.Enemy;
import com.team3.monstersden.characters.Monster;
import com.team3.monstersden.characters.HumanEnemy;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for SteelSword class.
 * Tests weapon effectiveness against different enemy types.
 * 
 * @author Shahmeer Khan
 * @version 1.0
 */
public class SteelSwordTest {
    
    private SteelSword steelSword;
    private Monster mockMonster;
    private HumanEnemy mockHuman;
    
    @Before
    public void setUp() {
        steelSword = new SteelSword();
        
        // Create mock enemies (adjust constructor based on actual implementation)
        mockMonster = new Monster(0, 0, 50, 100);
        mockHuman = new HumanEnemy(0, 0, 0, 100);
    }
    
    /**
     * Test that SteelSword has correct default damage value.
     */
    @Test
    public void testDefaultDamage() {
        assertEquals("Steel sword should have default damage of 30", 
                     30, steelSword.getBaseDamage());
    }
    
    /**
     * Test that SteelSword has correct name.
     */
    @Test
    public void testSwordName() {
        assertEquals("Steel sword should have correct name", 
                     "Steel Sword", steelSword.getName());
    }
    
    /**
     * Test custom damage constructor.
     */
    @Test
    public void testCustomDamage() {
        SteelSword customSword = new SteelSword(50);
        assertEquals("Custom steel sword should have specified damage", 
                     50, customSword.getBaseDamage());
    }
    
    /**
     * Test that steel sword deals bonus damage to human enemies (1.5x).
     */
    @Test
    public void testFullDamageAgainstHuman() {
        int damage = steelSword.calculateDamage(mockHuman);
        assertEquals("Steel sword should deal bonus damage (45 = 30 * 1.5) to humans", 
                     45, damage);
    }
    
    /**
     * Test that steel sword deals reduced damage to monsters.
     */
    @Test
    public void testReducedDamageAgainstMonster() {
        int damage = steelSword.calculateDamage(mockMonster);
        assertEquals("Steel sword should deal 50% damage (15) to monsters", 
                     15, damage);
    }
    
    /**
     * Test isEffectiveAgainst returns true for human enemies.
     */
    @Test
    public void testIsEffectiveAgainstHuman() {
        assertTrue("Steel sword should be effective against humans", 
                   steelSword.isEffectiveAgainst(mockHuman));
    }
    
    /**
     * Test isEffectiveAgainst returns false for monsters.
     */
    @Test
    public void testIsNotEffectiveAgainstMonster() {
        assertFalse("Steel sword should not be effective against monsters", 
                    steelSword.isEffectiveAgainst(mockMonster));
    }
    
    /**
     * Test custom damage against human (with 1.5x bonus).
     */
    @Test
    public void testCustomDamageCalculation() {
        SteelSword customSword = new SteelSword(100);
        int damage = customSword.calculateDamage(mockHuman);
        assertEquals("Custom steel sword should deal bonus damage (150 = 100 * 1.5) to human", 
                     150, damage);
    }
    
    /**
     * Test custom damage penalty against monster.
     */
    @Test
    public void testCustomDamagePenalty() {
        SteelSword customSword = new SteelSword(100);
        int damage = customSword.calculateDamage(mockMonster);
        assertEquals("Custom steel sword should deal 50% damage to monster", 
                     50, damage);
    }
    
    /**
     * Test toString method.
     */
    @Test
    public void testToString() {
        String result = steelSword.toString();
        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain sword name", 
                   result.contains("Steel Sword"));
        assertTrue("toString should contain damage value", 
                   result.contains("30"));
    }
    
    /**
     * Test damage modification.
     */
    @Test
    public void testSetBaseDamage() {
        steelSword.setBaseDamage(40);
        assertEquals("Base damage should be updated", 40, steelSword.getBaseDamage());
        
        int damage = steelSword.calculateDamage(mockHuman);
        assertEquals("Damage calculation should use new base damage (60 = 40 * 1.5)", 
                     60, damage);
    }
    
    /**
     * Test comparing silver and steel swords.
     */
    @Test
    public void testSwordComparison() {
        SilverSword silver = new SilverSword();
        
        // Against monster
        assertTrue("Silver should do more damage to monster than steel",
                   silver.calculateDamage(mockMonster) > steelSword.calculateDamage(mockMonster));
        
        // Against human
        assertTrue("Steel should do more damage to human than silver",
                   steelSword.calculateDamage(mockHuman) > silver.calculateDamage(mockHuman));
    }
}
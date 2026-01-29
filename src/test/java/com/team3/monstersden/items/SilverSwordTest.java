package com.team3.monstersden.items;

import com.team3.monstersden.characters.Enemy;
import com.team3.monstersden.characters.Monster;
import com.team3.monstersden.characters.HumanEnemy;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for SilverSword class.
 * Tests weapon effectiveness against different enemy types.
 * 
 * @author Shahmeer Khan
 * @version 1.0
 */
public class SilverSwordTest {
    
    private SilverSword silverSword;
    private Monster mockMonster;
    private HumanEnemy mockHuman;
    
    @Before
    public void setUp() {
        silverSword = new SilverSword();
        
        // Create mock enemies (adjust constructor based on actual implementation)
        // You may need to adjust these based on your actual Enemy constructors
        mockMonster = new Monster(0, 0, 50, 100); // x, y, health
        mockHuman = new HumanEnemy(0, 0, 50, 100);
    }
    
    /**
     * Test that SilverSword has correct default damage value.
     */
    @Test
    public void testDefaultDamage() {
        assertEquals("Silver sword should have default damage of 30", 
                     30, silverSword.getBaseDamage());
    }
    
    /**
     * Test that SilverSword has correct name.
     */
    @Test
    public void testSwordName() {
        assertEquals("Silver sword should have correct name", 
                     "Silver Sword", silverSword.getName());
    }
    
    /**
     * Test custom damage constructor.
     */
    @Test
    public void testCustomDamage() {
        SilverSword customSword = new SilverSword(50);
        assertEquals("Custom silver sword should have specified damage", 
                     50, customSword.getBaseDamage());
    }
    
    /**
     * Test that silver sword deals bonus damage to monsters (1.5x).
     */
    @Test
    public void testFullDamageAgainstMonster() {
        int damage = silverSword.calculateDamage(mockMonster);
        assertEquals("Silver sword should deal bonus damage (45 = 30 * 1.5) to monsters", 
                     45, damage);
    }
    
    /**
     * Test that silver sword deals reduced damage to human enemies.
     */
    @Test
    public void testReducedDamageAgainstHuman() {
        int damage = silverSword.calculateDamage(mockHuman);
        assertEquals("Silver sword should deal 50% damage (15) to humans", 
                     15, damage);
    }
    
    /**
     * Test isEffectiveAgainst returns true for monsters.
     */
    @Test
    public void testIsEffectiveAgainstMonster() {
        assertTrue("Silver sword should be effective against monsters", 
                   silverSword.isEffectiveAgainst(mockMonster));
    }
    
    /**
     * Test isEffectiveAgainst returns false for humans.
     */
    @Test
    public void testIsNotEffectiveAgainstHuman() {
        assertFalse("Silver sword should not be effective against humans", 
                    silverSword.isEffectiveAgainst(mockHuman));
    }
    
    /**
     * Test custom damage against monster (with 1.5x bonus).
     */
    @Test
    public void testCustomDamageCalculation() {
        SilverSword customSword = new SilverSword(100);
        int damage = customSword.calculateDamage(mockMonster);
        assertEquals("Custom silver sword should deal bonus damage (150 = 100 * 1.5) to monster", 
                     150, damage);
    }
    
    /**
     * Test custom damage penalty against human.
     */
    @Test
    public void testCustomDamagePenalty() {
        SilverSword customSword = new SilverSword(100);
        int damage = customSword.calculateDamage(mockHuman);
        assertEquals("Custom silver sword should deal 50% damage to human", 
                     50, damage);
    }
    
    /**
     * Test toString method.
     */
    @Test
    public void testToString() {
        String result = silverSword.toString();
        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain sword name", 
                   result.contains("Silver Sword"));
        assertTrue("toString should contain damage value", 
                   result.contains("30"));
    }
    
    /**
     * Test damage modification.
     */
    @Test
    public void testSetBaseDamage() {
        silverSword.setBaseDamage(40);
        assertEquals("Base damage should be updated", 40, silverSword.getBaseDamage());
        
        int damage = silverSword.calculateDamage(mockMonster);
        assertEquals("Damage calculation should use new base damage (60 = 40 * 1.5)", 
                     60, damage);
    }
}
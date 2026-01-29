package com.team3.monstersden.integration;

import com.team3.monstersden.items.*;
import com.team3.monstersden.characters.*;
import com.team3.monstersden.characters.Character;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Integration tests for Items and Characters modules.
 * Tests the interaction between weapons and enemies.
 * Uses ONLY methods that exist in the actual implementation.
 * 
 * @author Team 3
 * @version 1.0
 */
public class CombatIntegrationTest {
    
    private Monster monster;
    private HumanEnemy humanEnemy;
    private SilverSword silverSword;
    private SteelSword steelSword;
    
    @Before
    public void setUp() {
        // Initialize enemies (note: 4 parameters - x, y, damage, maxHealth)
        monster = new Monster(1, 1, 15, 50);
        humanEnemy = new HumanEnemy(2, 2, 10, 50);
        
        // Initialize weapons
        silverSword = new SilverSword();
        steelSword = new SteelSword();
    }
    
    /**
     * Test silver sword deals bonus damage to monster.
     */
    @Test
    public void testSilverSwordEffectiveAgainstMonster() {
        int damage = silverSword.calculateDamage(monster);
        
        assertEquals("Silver sword should deal bonus damage (45 = 30 * 1.5) to monster", 
                     45, damage);
    }
    
    /**
     * Test silver sword deals reduced damage to human.
     */
    @Test
    public void testSilverSwordIneffectiveAgainstHuman() {
        int damage = silverSword.calculateDamage(humanEnemy);
        
        assertEquals("Silver sword should deal reduced damage (15 = 30 * 0.5) to human", 
                     15, damage);
    }
    
    /**
     * Test steel sword deals bonus damage to human.
     */
    @Test
    public void testSteelSwordEffectiveAgainstHuman() {
        int damage = steelSword.calculateDamage(humanEnemy);
        
        assertEquals("Steel sword should deal bonus damage (45 = 30 * 1.5) to human", 
                     45, damage);
    }
    
    /**
     * Test steel sword deals reduced damage to monster.
     */
    @Test
    public void testSteelSwordIneffectiveAgainstMonster() {
        int damage = steelSword.calculateDamage(monster);
        
        assertEquals("Steel sword should deal reduced damage (15 = 30 * 0.5) to monster", 
                     15, damage);
    }
    
    /**
     * Test correct weapon choice matters - silver vs monster vs steel vs monster.
     */
    @Test
    public void testWeaponChoiceMatters() {
        int silverDamage = silverSword.calculateDamage(monster);
        int steelDamage = steelSword.calculateDamage(monster);
        
        assertTrue("Silver sword should do more damage to monster than steel sword",
                   silverDamage > steelDamage);
        assertEquals("Silver should do 3x more damage than steel against monster",
                     silverDamage, steelDamage * 3);
    }
    
    /**
     * Test correct weapon choice matters - steel vs human vs silver vs human.
     */
    @Test
    public void testWeaponChoiceMattersMirror() {
        int steelDamage = steelSword.calculateDamage(humanEnemy);
        int silverDamage = silverSword.calculateDamage(humanEnemy);
        
        assertTrue("Steel sword should do more damage to human than silver sword",
                   steelDamage > silverDamage);
        assertEquals("Steel should do 3x more damage than silver against human",
                     steelDamage, silverDamage * 3);
    }
    
    /**
     * Test isEffectiveAgainst correctly identifies monster for silver sword.
     */
    @Test
    public void testSilverSwordIsEffectiveAgainstMonster() {
        assertTrue("Silver sword should be effective against monster",
                   silverSword.isEffectiveAgainst(monster));
        assertFalse("Silver sword should not be effective against human",
                    silverSword.isEffectiveAgainst(humanEnemy));
    }
    
    /**
     * Test isEffectiveAgainst correctly identifies human for steel sword.
     */
    @Test
    public void testSteelSwordIsEffectiveAgainstHuman() {
        assertTrue("Steel sword should be effective against human",
                   steelSword.isEffectiveAgainst(humanEnemy));
        assertFalse("Steel sword should not be effective against monster",
                    steelSword.isEffectiveAgainst(monster));
    }
    
    /**
     * Test weapon damage calculation is symmetric.
     */
    @Test
    public void testWeaponDamageSymmetry() {
        // Effective weapon does 1.5x damage
        int silverVsMonster = silverSword.calculateDamage(monster);
        int steelVsHuman = steelSword.calculateDamage(humanEnemy);
        
        assertEquals("Both effective weapons should do same damage",
                     silverVsMonster, steelVsHuman);
        
        // Ineffective weapon does 0.5x damage
        int silverVsHuman = silverSword.calculateDamage(humanEnemy);
        int steelVsMonster = steelSword.calculateDamage(monster);
        
        assertEquals("Both ineffective weapons should do same damage",
                     silverVsHuman, steelVsMonster);
    }
    
    /**
     * Test custom damage swords maintain effectiveness ratio.
     */
    @Test
    public void testCustomDamageSwordEffectiveness() {
        SilverSword customSilver = new SilverSword(100);
        
        int damageToMonster = customSilver.calculateDamage(monster);
        int damageToHuman = customSilver.calculateDamage(humanEnemy);
        
        assertEquals("Custom silver sword should deal 150 to monster", 150, damageToMonster);
        assertEquals("Custom silver sword should deal 50 to human", 50, damageToHuman);
        assertEquals("Ratio should be 3:1", damageToMonster, damageToHuman * 3);
    }
    
    /**
     * Test enemy types are correctly distinguished.
     */
    @Test
    public void testEnemyTypeDistinction() {
    // Test Monster type
    assertTrue("Monster should be instance of Monster", monster instanceof Monster);
    assertTrue("Monster should be instance of Enemy", monster instanceof Enemy);
    assertTrue("Monster should be instance of Character", monster instanceof Character);
    
    // Test HumanEnemy type
    assertTrue("HumanEnemy should be instance of HumanEnemy", humanEnemy instanceof HumanEnemy);
    assertTrue("HumanEnemy should be instance of Enemy", humanEnemy instanceof Enemy);
    assertTrue("HumanEnemy should be instance of Character", humanEnemy instanceof Character);
    
    // Test they are different classes
    assertNotEquals("Monster and HumanEnemy should be different classes",
                    monster.getClass(), humanEnemy.getClass());
}
    
    /**
     * Test weapon name properties.
     */
    @Test
    public void testWeaponNames() {
        assertEquals("Silver sword should have correct name", 
                     "Silver Sword", silverSword.getName());
        assertEquals("Steel sword should have correct name", 
                     "Steel Sword", steelSword.getName());
    }
    
    /**
     * Test weapon base damage values.
     */
    @Test
    public void testWeaponBaseDamage() {
        assertEquals("Silver sword should have base damage 30", 
                     30, silverSword.getBaseDamage());
        assertEquals("Steel sword should have base damage 30", 
                     30, steelSword.getBaseDamage());
    }
    
    /**
     * Test setBaseDamage affects damage calculation.
     */
    @Test
    public void testSetBaseDamageAffectsCalculation() {
        silverSword.setBaseDamage(50);
        
        int damageToMonster = silverSword.calculateDamage(monster);
        assertEquals("Modified silver sword should deal 75 to monster (50 * 1.5)", 
                     75, damageToMonster);
    }
}
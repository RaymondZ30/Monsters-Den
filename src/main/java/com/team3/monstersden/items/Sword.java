package com.team3.monstersden.items;

import com.team3.monstersden.characters.Enemy;

/**
 * Abstract base class representing a sword weapon in the game.
 * Swords have different effectiveness against different enemy types.
 * 
 * @author Team 3 - CMPT 276
 * @version 1.0
 */

 public abstract class Sword {
    
    /** Base damage dealt by the sword */
    protected int baseDamage;
    
    /** Name of the sword */
    protected String name;
    
    /** Damage reduction factor when using wrong weapon type (e.g., 0.5 = 50% damage) */
    protected static final double WRONG_WEAPON_PENALTY = 0.5;
    
    /**
     * Constructs a sword with specified name and damage.
     * 
     * @param name the name of the sword
     * @param baseDamage the base damage value
     */
    public Sword(String name, int baseDamage) {
        this.name = name;
        this.baseDamage = baseDamage;
    }
    
    /**
     * Calculates the effective damage against a specific enemy.
     * Subclasses override this to implement weapon effectiveness logic.
     * 
     * @param enemy the enemy being attacked
     * @return the calculated damage value
     */
    public abstract int calculateDamage(Enemy enemy);
    
    /**
     * Checks if this sword is effective against the given enemy type.
     * 
     * @param enemy the enemy to check against
     * @return true if effective, false otherwise
     */
    public abstract boolean isEffectiveAgainst(Enemy enemy);
    
    /**
     * Gets the name of the sword.
     * 
     * @return the sword's name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Gets the base damage of the sword.
     * 
     * @return the base damage value
     */
    public int getBaseDamage() {
        return baseDamage;
    }
    
    /**
     * Sets the base damage of the sword.
     * 
     * @param baseDamage the new base damage value
     */
    public void setBaseDamage(int baseDamage) {
        this.baseDamage = baseDamage;
    }
    
    @Override
    public String toString() {
        return name + " (Damage: " + baseDamage + ")";
    }
}

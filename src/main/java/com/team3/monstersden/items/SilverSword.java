package com.team3.monstersden.items;

import com.team3.monstersden.characters.Enemy;
import com.team3.monstersden.characters.Monster;

/**
 * Silver sword weapon that is highly effective against monsters.
 * Deals reduced damage to human enemies.
 * 
 * <p>Damage Rules:
 * <ul>
 *   <li>150% base damage against Monsters</li>
 *   <li>50% base damage against Human enemies</li>
 * </ul>
 * </p>
 * 
 * @author Team 3
 * @version 1.1
 */
public class SilverSword extends Sword {

    /** Multiplier for effectiveness against the correct enemy type. */
    private static final double EFFECTIVE_MULTIPLIER = 1.5;

    /** Penalty multiplier for using the wrong sword type. */
    private static final double WRONG_WEAPON_PENALTY = 0.5;

    /**
     * Constructs a silver sword with default damage.
     */
    public SilverSword() {
        super("Silver Sword", 30);
    }

    /**
     * Constructs a silver sword with custom damage.
     *
     * @param baseDamage the base damage value
     */
    public SilverSword(int baseDamage) {
        super("Silver Sword", baseDamage);
    }

    /**
     * Calculates damage dealt to an enemy.
     * 
     * <p>Silver Sword is highly effective against Monsters
     * and deals reduced damage to human enemies.</p>
     *
     * @param enemy the enemy being attacked
     * @return calculated damage value
     */
    @Override
    public int calculateDamage(Enemy enemy) {
        if (enemy == null) return baseDamage;

        if (enemy instanceof Monster) {
            // Strong vs Monsters
            return (int) Math.round(baseDamage * EFFECTIVE_MULTIPLIER);
        } else {
            // Weak vs Humans
            return (int) Math.round(baseDamage * WRONG_WEAPON_PENALTY);
        }
    }

    /**
     * Determines if this sword is effective against the given enemy.
     *
     * @param enemy the enemy to evaluate
     * @return true if the sword is effective, false otherwise
     */
    @Override
    public boolean isEffectiveAgainst(Enemy enemy) {
        return enemy instanceof Monster;
    }
}

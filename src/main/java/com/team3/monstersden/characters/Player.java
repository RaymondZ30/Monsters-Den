package com.team3.monstersden.characters;

import com.team3.monstersden.core.Board;
import com.team3.monstersden.core.Game;
import com.team3.monstersden.items.Sword;
import com.team3.monstersden.items.SilverSword;
import com.team3.monstersden.items.SteelSword;
import com.team3.monstersden.patterns.factory.WeaponFactory;
import com.team3.monstersden.patterns.command.MoveCommand;
import com.team3.monstersden.util.Direction;

/**
 * Class for the main character controlled by the user.
 * Handles player's specific attributes like name and equipped sword.
 */
public class Player extends Character {

    private String name;
    private Sword currentSword;

    /**
     * Construcot for the Player with name
     * @param name player name
     * @param x x-coordinate
     * @param y y-coordinate
     */
    public Player(String name, int x, int y) {
        super(x, y, 100); // Player starts with 100 health
        this.name = name;
        this.currentSword = new SilverSword(); // default sword
    }

    /**
     * Alternative constructor without Player name
     * @param x x-coordinate
     * @param y y-coordinate
     */
    public Player(int x, int y) {
        this("Player", x, y);
    }

    /**
     * Get the name of the Player
     * @return player name
     */
    public String getName() {
        return name;
    }

    /**
     * Get current sword for the Player
     * @return current sword
     */
    public Sword getCurrentSword() {
        return currentSword;
    }

    /**
     * Set current sword for the Player
     * @param sword new sword
     */
    public void setCurrentSword(Sword sword) {
        this.currentSword = sword;
    }

    /**
     * Equip sword based on swordType from WeaponFactory
     * @param swordType type of sword to equip
     */
    public void equipSword(String swordType) {
        Sword weapon = WeaponFactory.create(swordType);
        setCurrentSword(weapon);
    }

    /**
     * Switch current to another sword object
     * @param newWeapon new sword to switch to
     */
    public void switchWeapon(Sword newWeapon) {
        if (newWeapon == null) {
            System.out.println("Cannot switch to a null weapon!");
            return;
        }
        this.currentSword = newWeapon;
        System.out.println("Switched weapon to: " + newWeapon.getName());
    }

    /**
     * Move the player using its MoveCommand
     * @param command MoveCommand to execute
     * @param game game instance to reference
     */
    public void moveCommand(MoveCommand command, Game game) {
        if (command != null) {
            command.execute(game);
        }
    }

    /**
     * Calculate damage being dealt to an Enemy based on sword type
     * @param enemy Enemy target that a Player is attacking
     */
    public void attack(Enemy enemy) {
        if (currentSword == null || enemy == null) {
            System.out.println("No weapon equipped or invalid target!");
            return;
        }

        int baseDamage = currentSword.calculateDamage(enemy);
        int bonusDamage = 0;

        // Bonus logic based on weapon-enemy matchup
        if (currentSword instanceof SilverSword && enemy instanceof Monster) {
            bonusDamage = 20; // silver > monster
        } else if (currentSword instanceof SteelSword && enemy instanceof HumanEnemy) {
            bonusDamage = 20; // steel > human
        }

        int totalDamage = baseDamage + bonusDamage;
        enemy.takeDamage(totalDamage);

        System.out.println("Attacked " + enemy.getClass().getSimpleName() +
                " with " + currentSword.getName() +
                " for " + totalDamage + " damage. (Enemy HP: " + enemy.getHealth() + ")");

        // Kill feedback
        if (!enemy.isAlive()) {
            System.out.println(enemy.getClass().getSimpleName() + " has been slain!");
        }
    }

    /**
     * Calculate and update player's health based on damage inflicted by an Enemy or Trap
     * If health drops below 0, it’s capped at 0
     * @param amount amount of damage to reduce Character health by
     */
    public void takeDamage(int amount) {
        if (amount <= 0) {
            return;
        }

        this.health -= amount;
        if (this.health < 0) {
            this.health = 0;
        }

        System.out.println("Player took " + amount + " damage! (Health: " + this.health + ")");
    }

    /**
     * Determines next movement direction for a Player
     * @param board board game to reference from
     * @return the direction that Player should move
     */
    @Override
    public Direction move(Board board) {
        if (movementStrategy != null) {
            return movementStrategy.chooseMove(board, this, this);
        }
        return Direction.NONE;
    }
}
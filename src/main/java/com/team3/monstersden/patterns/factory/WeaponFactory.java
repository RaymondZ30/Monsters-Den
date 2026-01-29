package com.team3.monstersden.patterns.factory;
/**
 * Factory class for creating weapon instances.
 * @version 1.0
 * @author ProjectTeam3
 * 
 */
import com.team3.monstersden.items.SilverSword;
import com.team3.monstersden.items.SteelSword;
import com.team3.monstersden.items.Sword;

// create weapons by type string
public class WeaponFactory {
    private WeaponFactory(){}

    public static Sword create(String type){
        if (type == null){
            return new SteelSword();
        } 
        
        switch (type.toLowerCase()) {
            case "silver":
                return new SilverSword();
            case "steel":
                return new SteelSword();
            default:
                return new SteelSword();
        }
    }
}

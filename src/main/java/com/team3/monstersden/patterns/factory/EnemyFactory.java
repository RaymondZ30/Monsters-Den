package com.team3.monstersden.patterns.factory;

import com.team3.monstersden.characters.Enemy;
import com.team3.monstersden.characters.HumanEnemy;
import com.team3.monstersden.characters.Monster;
import com.team3.monstersden.core.GameConfig;

public class EnemyFactory {
    private EnemyFactory(){}

    public static Enemy create(String type, int x, int y) {
        if (type == null) {
             return new Monster(x, y, GameConfig.MONSTER_DAMAGE, GameConfig.MONSTER_MAX_HP);
        }
        switch (type.toLowerCase()) {
            case "monster":
                return new Monster(x, y, GameConfig.MONSTER_DAMAGE, GameConfig.MONSTER_MAX_HP);
            case "human":
                return new HumanEnemy(x, y, GameConfig.HUMAN_DAMAGE, GameConfig.HUMAN_MAX_HP);
            default:
                return new Monster(x, y, GameConfig.MONSTER_DAMAGE, GameConfig.MONSTER_MAX_HP);
        }
    }
}
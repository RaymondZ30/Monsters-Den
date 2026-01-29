package com.team3.monstersden.core;

/**
 * Central configuration class for game tuning parameters.
 * Allows for easy balancing of HP, damage, speeds, and spawn rates.
 */
public class GameConfig {
    // --- Player Settings ---
    public static final int PLAYER_START_HP = 100;
    public static final int PLAYER_DAMAGE_SILVER = 30; // Base damage, can be overridden by sword classes
    public static final int PLAYER_DAMAGE_STEEL = 30;

    // --- Enemy Settings ---
    public static final int MONSTER_MAX_HP = 60;
    public static final int MONSTER_DAMAGE = 15;
    public static final int HUMAN_MAX_HP = 50;
    public static final int HUMAN_DAMAGE = 10;
    
    public static final int ENEMY_CHASE_RANGE = 8;  // Tiles within which enemy will start chasing
    public static final int ENEMY_ATTACK_RANGE = 1; // Must be adjacent to attack

    // --- Spawning Settings ---
    public static final int MAX_ACTIVE_ENEMIES = 5;
    public static final int SPAWN_INTERVAL_TICKS = 15; // How many game ticks between spawn attempts

    // --- Trap (Punishment) Settings ---
    public static final int TRAP_DAMAGE = 20;
    public static final int TRAP_COOLDOWN_TICKS = 10; // Ticks before a trap re-arms

    // --- Scoring ---
    public static final int SCORE_KILL_ENEMY = 50;
    public static final int SCORE_COLLECT_REWARD = 100;
    public static final int SCORE_WIN_BONUS = 500;
    public static final int TRAP_HP_DAMAGE = 0;
}
package com.team3.monstersden.characters;

/**
 * Enum defining the possible states for Enemy AI.
 */
public enum AIState {
    IDLE,       // Standing still or very minor movement
    PATROL,     // Moving along a predefined or random path
    CHASE,      // Actively moving towards the player using pathfinding
    ATTACK      // Currently attacking the player
}
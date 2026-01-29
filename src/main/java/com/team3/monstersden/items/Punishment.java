package com.team3.monstersden.items;

import com.team3.monstersden.core.GameConfig;

/**
 * Punishment trap that reduces score/health when triggered.
 * Features a cooldown system for re-arming.
 */
public class Punishment {
    private int x, y;
    private int penaltyValue;
    private boolean isReady;
    private int cooldownTimer;

    public Punishment(int x, int y, int penaltyValue) {
        this.x = x;
        this.y = y;
        this.penaltyValue = penaltyValue;
        this.isReady = true;
        this.cooldownTimer = 0;
    }

    /**
     * Updates trap cooldown state. Called every game tick.
     */
    public void tick() {
        if (!isReady) {
            cooldownTimer--;
            if (cooldownTimer <= 0) {
                isReady = true;
                // System.out.println("Trap at (" + x + "," + y + ") re-armed.");
            }
        }
    }

    /**
     * Activates the trap if it is ready and applies its penalty
     * @return penalty value of the trap if triggered; 0 if trap is on cooldown
     */
    public int trigger() {
        if (isReady) {
            isReady = false;
            cooldownTimer = GameConfig.TRAP_COOLDOWN_TICKS;
            return penaltyValue;
        }
        return 0;
    }

    public boolean isActive() { return isReady; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getPenaltyValue() { return penaltyValue; }

    public boolean isRespawnable() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isRespawnable'");
    }
}
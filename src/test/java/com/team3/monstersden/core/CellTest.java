package com.team3.monstersden.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import com.team3.monstersden.characters.HumanEnemy;
import com.team3.monstersden.characters.Monster;
import com.team3.monstersden.items.BonusReward;
import com.team3.monstersden.items.Punishment;
import com.team3.monstersden.items.RegularReward;

/**
 * Test suite for Cell class.
 * Tests all cell properties and entity management.
 * 
 * @author Team 3 - CMPT 276
 * @version 1.0
 */
public class CellTest {

    private Cell cell;

    @Before
    public void setUp() {
        cell = new Cell(5, 10);
    }

    // ==================== Constructor Tests ====================

    @Test
    public void testCellConstruction() {
        assertEquals("X coordinate should be 5", 5, cell.getX());
        assertEquals("Y coordinate should be 10", 10, cell.getY());
        assertFalse("Cell should not be a wall by default", cell.isWall());
        assertFalse("Cell should not be start by default", cell.isStart());
        assertFalse("Cell should not be exit by default", cell.isExit());
        assertTrue("Cell should be empty by default", cell.isEmpty());
    }

    // ==================== Coordinate Tests ====================

    @Test
    public void testGetX() {
        Cell testCell = new Cell(15, 20);
        assertEquals("getX should return 15", 15, testCell.getX());
    }

    @Test
    public void testGetY() {
        Cell testCell = new Cell(15, 20);
        assertEquals("getY should return 20", 20, testCell.getY());
    }

    @Test
    public void testZeroCoordinates() {
        Cell origin = new Cell(0, 0);
        assertEquals("X should be 0", 0, origin.getX());
        assertEquals("Y should be 0", 0, origin.getY());
    }

    // ==================== Wall Tests ====================

    @Test
    public void testSetWall() {
        cell.setWall(true);
        assertTrue("Cell should be a wall", cell.isWall());
        assertFalse("Wall should not be walkable", cell.isWalkable());
    }

    @Test
    public void testSetWallFalse() {
        cell.setWall(true);
        cell.setWall(false);
        assertFalse("Cell should not be a wall", cell.isWall());
        assertTrue("Cell should be walkable", cell.isWalkable());
    }

    @Test
    public void testIsWalkable() {
        assertTrue("Non-wall cell should be walkable", cell.isWalkable());
    }

    @Test
    public void testWallNotEmpty() {
        cell.setWall(true);
        assertFalse("Wall cell should not be considered empty", cell.isEmpty());
    }

    // ==================== Start/Exit Tests ====================

    @Test
    public void testSetStart() {
        cell.setStart(true);
        assertTrue("Cell should be marked as start", cell.isStart());
    }

    @Test
    public void testSetExit() {
        cell.setExit(true);
        assertTrue("Cell should be marked as exit", cell.isExit());
    }

    @Test
    public void testUnsetStart() {
        cell.setStart(true);
        cell.setStart(false);
        assertFalse("Start flag should be unset", cell.isStart());
    }

    @Test
    public void testUnsetExit() {
        cell.setExit(true);
        cell.setExit(false);
        assertFalse("Exit flag should be unset", cell.isExit());
    }

    // ==================== Enemy Tests ====================

    @Test
    public void testSetEnemy() {
        Monster enemy = new Monster(5, 10, 20, 60);
        
        cell.setEnemy(enemy);
        
        assertEquals("Enemy should be set", enemy, cell.getEnemy());
        assertTrue("Cell should have enemy", cell.hasEnemy());
        assertFalse("Cell with enemy should not be empty", cell.isEmpty());
    }

    @Test
    public void testGetEnemyInitial() {
        assertNull("Enemy should be null initially", cell.getEnemy());
    }

    @Test
    public void testRemoveEnemy() {
        Monster enemy = new Monster(5, 10, 20, 60);
        cell.setEnemy(enemy);
        
        cell.setEnemy(null);
        
        assertNull("Enemy should be removed", cell.getEnemy());
        assertFalse("Cell should not have enemy", cell.hasEnemy());
    }

    @Test
    public void testHasEnemyInitial() {
        assertFalse("Cell should not have enemy initially", cell.hasEnemy());
    }

    @Test
    public void testReplaceEnemy() {
        Monster enemy1 = new Monster(5, 10, 20, 60);
        HumanEnemy enemy2 = new HumanEnemy(5, 10, 15, 50);
        
        cell.setEnemy(enemy1);
        cell.setEnemy(enemy2);
        
        assertEquals("Second enemy should replace first", enemy2, cell.getEnemy());
    }

    // ==================== Reward Tests ====================

    @Test
    public void testSetReward() {
        RegularReward reward = new RegularReward(5, 10, 100);
        
        cell.setReward(reward);
        
        assertEquals("Reward should be set", reward, cell.getReward());
        assertTrue("Cell should have reward", cell.hasReward());
        assertFalse("Cell with reward should not be empty", cell.isEmpty());
    }

    @Test
    public void testGetRewardInitial() {
        assertNull("Reward should be null initially", cell.getReward());
    }

    @Test
    public void testRemoveReward() {
        RegularReward reward = new RegularReward(5, 10, 100);
        cell.setReward(reward);
        
        cell.setReward(null);
        
        assertNull("Reward should be removed", cell.getReward());
        assertFalse("Cell should not have reward", cell.hasReward());
    }

    @Test
    public void testHasRewardInitial() {
        assertFalse("Cell should not have reward initially", cell.hasReward());
    }

    @Test
    public void testSetBonusReward() {
        BonusReward bonus = new BonusReward(5, 10, 200, 10);
        
        cell.setReward(bonus);
        
        assertEquals("Bonus reward should be set", bonus, cell.getReward());
        assertTrue("Cell should have reward", cell.hasReward());
    }

    // ==================== Punishment Tests ====================

    @Test
    public void testSetPunishment() {
        Punishment punishment = new Punishment(5, 10, 20);
        
        cell.setPunishment(punishment);
        
        assertEquals("Punishment should be set", punishment, cell.getPunishment());
        assertTrue("Cell should have punishment", cell.hasPunishment());
        assertFalse("Cell with punishment should not be empty", cell.isEmpty());
    }

    @Test
    public void testGetPunishmentInitial() {
        assertNull("Punishment should be null initially", cell.getPunishment());
    }

    @Test
    public void testRemovePunishment() {
        Punishment punishment = new Punishment(5, 10, 20);
        cell.setPunishment(punishment);
        
        cell.setPunishment(null);
        
        assertNull("Punishment should be removed", cell.getPunishment());
        assertFalse("Cell should not have punishment", cell.hasPunishment());
    }

    @Test
    public void testHasPunishmentInitial() {
        assertFalse("Cell should not have punishment initially", cell.hasPunishment());
    }

    // ==================== Legacy Content Tests ====================

    @Test
    public void testSetContent() {
        String testContent = "Test Object";
        
        cell.setContent(testContent);
        
        assertEquals("Content should be set", testContent, cell.getContent());
    }

    @Test
    public void testGetContentInitial() {
        assertNull("Content should be null initially", cell.getContent());
    }

    @Test
    public void testClearContent() {
        cell.setContent("Test");
        
        cell.clearContent();
        
        assertNull("Content should be cleared", cell.getContent());
    }

    @Test
    public void testHasContentOfType() {
        String testString = "Test";
        cell.setContent(testString);
        
        assertTrue("Should have String content", cell.hasContentOfType(String.class));
        assertFalse("Should not have Integer content", cell.hasContentOfType(Integer.class));
    }

    // ==================== Clear All Tests ====================

    @Test
    public void testClearAll() {
        Monster enemy = new Monster(5, 10, 20, 60);
        RegularReward reward = new RegularReward(5, 10, 100);
        Punishment punishment = new Punishment(5, 10, 20);
        
        cell.setEnemy(enemy);
        cell.setReward(reward);
        cell.setPunishment(punishment);
        cell.setContent("Test");
        
        cell.clearAll();
        
        assertNull("Enemy should be cleared", cell.getEnemy());
        assertNull("Reward should be cleared", cell.getReward());
        assertNull("Punishment should be cleared", cell.getPunishment());
        assertNull("Content should be cleared", cell.getContent());
        assertTrue("Cell should be empty after clearAll", cell.isEmpty());
    }

    // ==================== isEmpty Tests ====================

    @Test
    public void testIsEmptyDefault() {
        assertTrue("Default cell should be empty", cell.isEmpty());
    }

    @Test
    public void testIsEmptyWithEnemy() {
        cell.setEnemy(new Monster(5, 10, 20, 60));
        assertFalse("Cell with enemy should not be empty", cell.isEmpty());
    }

    @Test
    public void testIsEmptyWithReward() {
        cell.setReward(new RegularReward(5, 10, 100));
        assertFalse("Cell with reward should not be empty", cell.isEmpty());
    }

    @Test
    public void testIsEmptyWithPunishment() {
        cell.setPunishment(new Punishment(5, 10, 20));
        assertFalse("Cell with punishment should not be empty", cell.isEmpty());
    }

    @Test
    public void testIsEmptyWall() {
        cell.setWall(true);
        assertFalse("Wall should not be considered empty", cell.isEmpty());
    }

    // ==================== Complex Scenarios ====================

    @Test
    public void testCellWithMultipleEntities() {
        Monster enemy = new Monster(5, 10, 20, 60);
        RegularReward reward = new RegularReward(5, 10, 100);
        Punishment punishment = new Punishment(5, 10, 20);
        
        cell.setEnemy(enemy);
        cell.setReward(reward);
        cell.setPunishment(punishment);
        
        assertTrue("Should have enemy", cell.hasEnemy());
        assertTrue("Should have reward", cell.hasReward());
        assertTrue("Should have punishment", cell.hasPunishment());
        assertFalse("Cell should not be empty", cell.isEmpty());
    }

    @Test
    public void testStartCellWithEntities() {
        cell.setStart(true);
        cell.setReward(new RegularReward(5, 10, 100));
        
        assertTrue("Should be start cell", cell.isStart());
        assertTrue("Should have reward", cell.hasReward());
    }

    @Test
    public void testExitCellWithEntities() {
        cell.setExit(true);
        cell.setEnemy(new Monster(5, 10, 20, 60));
        
        assertTrue("Should be exit cell", cell.isExit());
        assertTrue("Should have enemy", cell.hasEnemy());
    }

    // ==================== toString Tests ====================

    @Test
    public void testToStringEmpty() {
        String result = cell.toString();
        
        assertNotNull("toString should not return null", result);
        assertTrue("Should contain X coordinate", result.contains("5"));
        assertTrue("Should contain Y coordinate", result.contains("10"));
        assertTrue("Should indicate empty", result.contains("empty"));
    }

    @Test
    public void testToStringWall() {
        cell.setWall(true);
        String result = cell.toString();
        
        assertTrue("Should indicate wall", result.contains("wall"));
    }

    @Test
    public void testToStringStart() {
        cell.setStart(true);
        String result = cell.toString();
        
        assertTrue("Should indicate start", result.contains("start"));
    }

    @Test
    public void testToStringExit() {
        cell.setExit(true);
        String result = cell.toString();
        
        assertTrue("Should indicate exit", result.contains("exit"));
    }

    @Test
    public void testToStringWithEnemy() {
        cell.setEnemy(new Monster(5, 10, 20, 60));
        String result = cell.toString();
        
        assertTrue("Should indicate enemy", result.contains("enemy"));
    }

    @Test
    public void testToStringWithReward() {
        cell.setReward(new RegularReward(5, 10, 100));
        String result = cell.toString();
        
        assertTrue("Should indicate reward", result.contains("reward"));
    }

    @Test
    public void testToStringWithPunishment() {
        cell.setPunishment(new Punishment(5, 10, 20));
        String result = cell.toString();
        
        assertTrue("Should indicate punishment", result.contains("punishment"));
    }

    @Test
    public void testToStringMultipleEntities() {
        cell.setEnemy(new Monster(5, 10, 20, 60));
        cell.setReward(new RegularReward(5, 10, 100));
        cell.setPunishment(new Punishment(5, 10, 20));
        
        String result = cell.toString();
        
        assertTrue("Should contain enemy", result.contains("enemy"));
        assertTrue("Should contain reward", result.contains("reward"));
        assertTrue("Should contain punishment", result.contains("punishment"));
    }
}
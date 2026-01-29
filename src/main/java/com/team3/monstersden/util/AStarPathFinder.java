package com.team3.monstersden.util;

import com.team3.monstersden.core.Board;
import com.team3.monstersden.core.Cell;

import java.util.*;

/**
 * Utility class implementing the A* pathfinding algorithm for grid-based movement.
 * Ensures entities can find the shortest path around obstacles.
 */
public class AStarPathFinder {

    /**
     * Finds the next step direction to take to reach the target.
     *
     * @param board The game board.
     * @param startX Starting X coordinate.
     * @param startY Starting Y coordinate.
     * @param targetX Target X coordinate.
     * @param targetY Target Y coordinate.
     * @return The Direction to move next, or Direction.NONE if no path found.
     */
    public static Direction findNextMove(Board board, int startX, int startY, int targetX, int targetY) {
        if (startX == targetX && startY == targetY) return Direction.NONE;

        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Set<String> closedSet = new HashSet<>();

        Node startNode = new Node(startX, startY, null, 0, heuristic(startX, startY, targetX, targetY));
        openSet.add(startNode);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            if (current.x == targetX && current.y == targetY) {
                return getFirstStep(current);
            }

            closedSet.add(current.x + "," + current.y);

            // Check all cardinal neighbors
            for (Direction dir : new Direction[]{Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT}) {
                int neighborX = current.x + dir.getDx();
                int neighborY = current.y + dir.getDy();

                // Check bounds and walkability
                if (!board.isValidMove(neighborX, neighborY)) continue;
                // Avoid other enemies for collision robustness (simplistic view, can be removed if they should path through each other)
                if (board.getCell(neighborX, neighborY).hasEnemy() && (neighborX != targetX || neighborY != targetY)) continue;

                if (closedSet.contains(neighborX + "," + neighborY)) continue;

                int newGCost = current.gCost + 1;
                Node neighborNode = new Node(neighborX, neighborY, current, newGCost, heuristic(neighborX, neighborY, targetX, targetY));

                // Check if a better path to this node already exists in openSet would go here,
                // but for simple uniform grid, just adding is often sufficient for basic AI.
                // A full implementation would use a map to track best gCosts.
                openSet.add(neighborNode);
            }
        }

        return Direction.NONE; // No path found
    }

    private static int heuristic(int x1, int y1, int x2, int y2) {
        // Manhattan distance for grid
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    private static Direction getFirstStep(Node endNode) {
        Node node = endNode;
        while (node.parent != null && node.parent.parent != null) {
            node = node.parent;
        }
        if (node.parent == null) return Direction.NONE;

        int dx = node.x - node.parent.x;
        int dy = node.y - node.parent.y;

        if (dx == 1) return Direction.RIGHT;
        if (dx == -1) return Direction.LEFT;
        if (dy == 1) return Direction.DOWN;
        if (dy == -1) return Direction.UP;

        return Direction.NONE;
    }

    private static class Node implements Comparable<Node> {
        int x, y;
        Node parent;
        int gCost; // Cost from start
        int hCost; // Heuristic cost to end

        Node(int x, int y, Node parent, int gCost, int hCost) {
            this.x = x;
            this.y = y;
            this.parent = parent;
            this.gCost = gCost;
            this.hCost = hCost;
        }

        int fCost() { return gCost + hCost; }

        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.fCost(), other.fCost());
        }
    }
}
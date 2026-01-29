package com.team3.monstersden.util;
/**
 * Direction enum represents possible movement directions.
 * @author ProjectTeam3
 * @version 2.0
 */
public enum Direction{
    // updated version with dx dy
     UP (-1,0),
     DOWN (1,0),
     LEFT (0,-1),
     RIGHT(0,1),
     NONE(0,0);

     private final int dx;
     private final int dy;
        Direction(int dx, int dy){
            this.dx = dx;
            this.dy = dy;
        }
        public int getDx(){
            return dx;
        }
        public int getDy(){
            return dy;
        }
}
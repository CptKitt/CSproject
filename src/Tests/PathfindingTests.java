package Tests;

import Model.Pathfinding;
import Model.Position;

import java.util.*;

public class PathfindingTests implements Pathfinding.Delegate {
    private Random rand;
    private boolean[][] walls;
    
    public double wallChance;
    public int moveRange;
    
    public PathfindingTests() {
        rand = new Random();
        wallChance = 0.3;
        moveRange = 5;
    }
    
    public void run() {
        int w = rand.nextInt(30) + 10;
        int h = rand.nextInt(10) + 10;
        walls = new boolean[w][h];
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                walls[x][y] = rand.nextDouble() < wallChance;
            }
        }
        
        Position pPos;
        do {
            pPos = new Position(
                    rand.nextInt(w),
                    rand.nextInt(h));
        } while (walls[pPos.x][pPos.y]);
        System.out.println("Player position " + pPos);
    
        Set<Position> moves = Pathfinding.movementForPosition(
                this, pPos, moveRange);
        System.out.println(moves.size() + " possible moves");
    
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Position p = new Position(x, y);
                if (p.equals(pPos)) {
                    System.out.print("\\o/");
                }
                else if (moves.contains(p))
                    System.out.print("[ ]");
                else if (walls[x][y])
                    System.out.print("███");
                else
                    System.out.print("   ");
            }
            System.out.println();
        }
    }
    
    public boolean validPosition(Position p) {
        return p.x >= 0 && p.x < walls.length
                && p.y >= 0 && p.y < walls[0].length
                && !walls[p.x][p.y];
    }
    
    public static void main(String[] args) {
        PathfindingTests t = new PathfindingTests();
        t.run();
    }
}

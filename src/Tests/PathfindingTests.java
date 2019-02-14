package Tests;

import Model.Pathfinding;
import Model.Position;

import java.util.*;

/**
 * Test class for Pathfinding.
 * Calling run() will generates a random 10-40x10-20 map with a
 * single entity and asks Pathfinding for its possible moves,
 * as well as the shortest path to some random destination.
 * <p></p>
 * Running the main() method in this class will loop tests
 * until "(q)uit" is entered, and allows for entering a number
 * as input to change the movement range of the entity.
 */
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
        // create walls
        int w = rand.nextInt(30) + 10;
        int h = rand.nextInt(10) + 10;
        walls = new boolean[w][h];
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                walls[x][y] = rand.nextDouble() < wallChance;
            }
        }
        
        // position player
        Position pPos;
        do {
            pPos = new Position(
                    rand.nextInt(w),
                    rand.nextInt(h));
        } while (walls[pPos.x][pPos.y]);
        System.out.println("Player position " + pPos);
        
        // movement pathfinding
        Set<Position> moves = Pathfinding.movementForPosition(
                this, pPos, moveRange);
        System.out.println(moves.size() + " possible moves");
        
        // display
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Position p = new Position(x, y);
                // player position
                if (p.equals(pPos)) {
                    System.out.print("\\o/");
                }
                // possible moves
                else if (moves.contains(p))
                    System.out.print("[ ]");
                // it's a wall
                else if (walls[x][y])
                    System.out.print("███");
                // nothing
                else
                    System.out.print("   ");
            }
            System.out.println();
        }
    }
    
    // necessary method for pathfinding
    @Override
    public boolean validPosition(Position p) {
        return p.x >= 0 && p.x < walls.length
                && p.y >= 0 && p.y < walls[0].length
                && !walls[p.x][p.y];
    }
    
    public static void main(String[] args) {
        // create testing object
        PathfindingTests t = new PathfindingTests();
        Scanner in = new Scanner(System.in);
        
        // loop tests
        while (true) {
            t.run();
            
            String input = in.nextLine();
            try {
                t.moveRange = Integer.parseInt(input);
            }
            catch (Exception ignored) {
                if (input.startsWith("q")) {
                    return;
                }
            }
        }
    }
}

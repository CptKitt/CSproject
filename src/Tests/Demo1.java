package Tests;

import Model.*;

/**
 * Short demonstration of the relationships between
 * Map, Pathfinding, and ControllerInput.
 *
 * Map contains the floor information, which Pathfinding
 * uses to determine the movement range from a Position
 * on the floor, and ControllerInput takes input from
 * the user to inform Map of changes to make.
 *
 * Run instructions:
 * - Navigate to /src, where Model and Tests are located.
 * - run "javac Tests/Demo1.java" from the /src directory.
 * - run "java Tests/Demo1" from the same directory.
 */
public class Demo1 {
    public static void main(String[] args) {
        Map m = new Map(20, 10);
        m.populateGrid();
        
        ControllerInput input = new ControllerInput();
        
        while (true) {
            m.pathfind();
            m.printGrid();
            
            int[] move = input.charMovementInput();
            
            m.setStart(new Position(move[0], move[1]));
        }
    }
}

package Tests;

import Model.*;

public class Demo1 {
    public static void main(String[] args) {
        Map m = new Map();
        m.populateGrid();
        
        ControllerInput input = new ControllerInput();
        
        while (true) {
            m.pathfind();
            m.printGrid();
            
            int[] move = input.charMovementInput();
            
            m.populateGrid();
            m.setStart(new Position(move[0], move[1]));
        }
    }
}

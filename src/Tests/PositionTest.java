package Tests;

import Model.Position;

public class PositionTest extends TestBase {
    public void test_distanceTo_same() {
        Position pos1 = new Position(5, 5);
        Position pos2 = new Position(5, 5);
        assertEquals("Distance between two equal positions", 0, pos1.distanceTo(pos2));
    }
    
    public void test_moved() {
    
    }
    
    public void test_adjacentPositions() {
    
    }
    
    public static void main(String[] args) {
    
    }
}
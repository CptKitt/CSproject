package Tests;

import Model.Position;
import org.junit.Test;

import static org.junit.Assert.*;

public class PositionTest {
    @Test
    public void test_distanceTo_same() {
        Position pos1 = new Position(5, 5);
        Position pos2 = new Position(5, 5);
        assertEquals("Distance between two equal positions", 0, pos1.distanceTo(pos2));
    }
    
    @Test
    public void test_distanceTo_15x() {
        Position pos1 = new Position(5, 5);
        Position pos2 = new Position(20, 5);
        assertEquals("Distance between two positions 15 x apart", 15, pos1.distanceTo(pos2));
    }
    
    @Test
    public void test_distanceTo_2y() {
        Position pos1 = new Position(7, 5);
        Position pos2 = new Position(5, 5);
        assertEquals("Distance between two positions 2 y apart", 2, pos1.distanceTo(pos2));
    }
    
    @Test
    public void test_distanceTo_negativeAndPositive() {
        Position pos1 = new Position(-3, -8);
        Position pos2 = new Position(4, 2);
        assertEquals("Distance between negative and positive positions", 17, pos1.distanceTo(pos2));
    }
    
    @Test
    public void test_moved_0_0() {
        Position pos1 = new Position(5, 5);
        Position pos2 = pos1.moved(0, 0);
        assertEquals("Moved (5,5) position by (0,0)", new Position(5, 5), pos2);
    }
    
    @Test
    public void test_moved_4x_negative4y() {
        Position pos1 = new Position(5, 5);
        Position pos2 = pos1.moved(4, -4);
        assertEquals("Moved (5,5) position by (4,-4)", new Position(9, 1), pos2);
    }
    
    @Test
    public void test_adjacentPositions_5_5() {
        Position pos1 = new Position(5, 5);
        Position[] adjacent = new Position[] {
                new Position(6, 5),
                new Position(4, 5),
                new Position(5, 6),
                new Position(5, 4)
        };
        assertArrayEquals("Adjacent positions of (5,5)", adjacent, pos1.adjacentPositions());
    }
    
    @Test
    public void test_adjacentPositions_3_neg2() {
        Position pos1 = new Position(3, -2);
        Position[] adjacent = new Position[] {
                new Position(4, -2),
                new Position(2, -2),
                new Position(3, -1),
                new Position(3, -3)
        };
        assertArrayEquals("Adjacent positions of (3,-2)", adjacent, pos1.adjacentPositions());
    }
    
    @Test
    public void test_toString_origin() {
        Position pos1 = Position.ORIGIN;
        assertEquals("toString of (0,0)", "P(0,0)", pos1.toString());
    }
    
    @Test
    public void test_toString_neg6_7() {
        Position pos1 = new Position(-6, 7);
        assertEquals("toString of (-6,7)", "P(-6,7)", pos1.toString());
    }
}
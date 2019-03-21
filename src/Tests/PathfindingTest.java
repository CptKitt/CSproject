package Tests;

import Model.Pathfinding;
import Model.Position;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class PathfindingTest {
    class MockMap implements Pathfinding.Delegate {
        private boolean[][] walls;
        Position start, destination;
        
        MockMap(String[] map) {
            walls = new boolean[map[0].length()][map.length];
            
            for (int x = 0; x < map[0].length(); x++) {
                for (int y = 0; y < map.length; y++) {
                    switch (map[y].charAt(x)) {
                        case 'o':
                            start = new Position(x, y);
                            break;
                        case 'x':
                            destination = new Position(x, y);
                            break;
                        case '#':
                            walls[x][y] = true;
                            break;
                    }
                }
            }
        }
        
        @Override
        public boolean validPosition(Position p) {
            if (p.x < 0 || p.x >= walls.length || p.y < 0 || p.y >= walls[0].length) {
                return false;
            }
            return !walls[p.x][p.y];
        }
    }
    
    @Test
    public void test_movementForPosition_zero() {
        String[] map = { // o = start, # = wall
                " o   #",
                "     #",
                "     #"
        };
        
        MockMap mock = new MockMap(map);
        
        Set<Position> moves = Pathfinding.movementForPosition(
                mock, mock.start, 0);
        Set<Position> expected = new HashSet<>(Arrays.asList(
                new Position(1, 0)
        ));
        
        String readable = String.join("\n", map);
        assertEquals("Movement 0 in map\n" + readable, expected, moves);
    }
    
    @Test
    public void test_movementForPosition_two() {
        String[] map = { // o = start, # = wall
                " o   #",
                "     #",
                "     #"
        };
        
        MockMap mock = new MockMap(map);
        
        Set<Position> moves = Pathfinding.movementForPosition(
                mock, mock.start, 2);
        Set<Position> expected = new HashSet<>(Arrays.asList(
                new Position(0, 0),
                new Position(1, 0),
                new Position(2, 0),
                new Position(3, 0),
                new Position(0, 1),
                new Position(1, 1),
                new Position(2, 1),
                new Position(1, 2)
        ));
        
        String readable = String.join("\n", map);
        assertEquals("Movement 2 in map\n" + readable, expected, moves);
    }
    
    @Test
    public void test_movementForPosition_cutOff() {
        String[] map = { // o = start, # = wall
                " o # ",
                "#### ",
                "     "
        };
        
        MockMap mock = new MockMap(map);
        
        Set<Position> moves = Pathfinding.movementForPosition(
                mock, mock.start, -1);
        Set<Position> expected = new HashSet<>(Arrays.asList(
                new Position(0, 0),
                new Position(1, 0),
                new Position(2, 0)
        ));
        
        String readable = String.join("\n", map);
        assertEquals("All movement in map\n" + readable, expected, moves);
    }
    
    @Test
    public void test_movementForPosition_maze() {
        String[] map = { // o = start, # = wall
                "o     #",
                "##  #  ",
                "   # # ",
                "# ##   ",
                " #  ## "
        };
        
        MockMap mock = new MockMap(map);
        
        Set<Position> moves = Pathfinding.movementForPosition(
                mock, mock.start, -1);
        Set<Position> expected = new HashSet<>(Arrays.asList(
                new Position(0, 0),
                new Position(1, 0),
                new Position(2, 0),
                new Position(3, 0),
                new Position(4, 0),
                new Position(5, 0),
                new Position(2, 1),
                new Position(3, 1),
                new Position(5, 1),
                new Position(6, 1),
                new Position(0, 2),
                new Position(1, 2),
                new Position(2, 2),
                new Position(4, 2),
                new Position(6, 2),
                new Position(1, 3),
                new Position(4, 3),
                new Position(5, 3),
                new Position(6, 3),
                new Position(6, 4)
        ));
        
        String readable = String.join("\n", map);
        assertEquals("All movement in map\n" + readable, expected, moves);
    }
    
    @Test
    public void test_shortestPath_maze() {
        String[] map = { // o = start, x = destination, # = wall
                "o      ",
                "##  ## ",
                "   # # ",
                "# # x# ",
                "#   ## ",
                "  #    "
        };
        
        MockMap mock = new MockMap(map);
        
        List<Position> path = Pathfinding.shortestPath(
                mock, mock.start, mock.destination);
        List<Position> expected = Arrays.asList(
                new Position(1, 0),
                new Position(2, 0),
                new Position(2, 1),
                new Position(2, 2),
                new Position(1, 2),
                new Position(1, 3),
                new Position(1, 4),
                new Position(2, 4),
                new Position(3, 4),
                new Position(3, 3),
                new Position(4, 3)
        );
        
        String readable = String.join("\n", map);
        assertEquals("Shortest path in map\n" + readable, expected, path);
    }
    
    @Test
    public void test_shortestPath_none() {
        String[] map = { // o = start, x = destination, # = wall
                "o  #",
                "## #",
                "  # "
        };
        
        MockMap mock = new MockMap(map);
        // manually set destination to start
        mock.destination = mock.start;
        
        List<Position> path = Pathfinding.shortestPath(
                mock, mock.start, mock.destination);
        List<Position> expected = new ArrayList<>();
        
        String readable = String.join("\n", map);
        assertEquals("Shortest path in map\n" + readable, expected, path);
    }
    
    @Test
    public void test_shortestPath_adjacent() {
        String[] map = { // o = start, x = destination, # = wall
                " x#",
                " o#"
        };
        
        MockMap mock = new MockMap(map);
        
        List<Position> path = Pathfinding.shortestPath(
                mock, mock.start, mock.destination);
        List<Position> expected = Arrays.asList(
                new Position(1, 0)
        );
        
        String readable = String.join("\n", map);
        assertEquals("Shortest path in map\n" + readable, expected, path);
    }
    
    @Test
    public void test_shortestPath_cutOff() {
        String[] map = { // o = start, x = destination, # = wall
                "          ",
                "       #  ",
                "  o    #  ",
                "       #  ",
                " #######  ",
                "        x ",
                "          "
        };
        
        MockMap mock = new MockMap(map);
        
        List<Position> path = Pathfinding.shortestPath(
                mock, mock.start, mock.destination);
        List<Position> expected = Arrays.asList(
                new Position(2, 3),
                new Position(1, 3),
                new Position(0, 3),
                new Position(0, 4),
                new Position(0, 5),
                new Position(1, 5),
                new Position(2, 5),
                new Position(3, 5),
                new Position(4, 5),
                new Position(5, 5),
                new Position(6, 5),
                new Position(7, 5),
                new Position(8, 5)
        );
        
        String readable = String.join("\n", map);
        assertEquals("Shortest path in map\n" + readable, expected, path);
    }
    
    @Test
    public void test_shortestPath_unreachable() {
        String[] map = { // o = start, x = destination, # = wall
                " o   #  ",
                "     # x",
                "     #  "
        };
        
        MockMap mock = new MockMap(map);
        
        List<Position> path = Pathfinding.shortestPath(
                mock, mock.start, mock.destination);
        List<Position> expected = Arrays.asList(
                new Position(1, 1),
                new Position(2, 1),
                new Position(3, 1),
                new Position(4, 1)
        );
        
        String readable = String.join("\n", map);
        assertEquals("Shortest path in map\n" + readable, expected, path);
    }
    
    @Test
    public void test_lineOfSight_clear() {
        String[] map = { // o = start, x = destination, # = wall
                " o  ",
                "   x",
                "    "
        };
        
        MockMap mock = new MockMap(map);
        
        boolean los = Pathfinding.lineOfSight(
                mock, mock.start, mock.destination);
        boolean expected = true;
        
        String readable = String.join("\n", map);
        assertEquals("Line of sight clear\n" + readable, expected, los);
    }
    
    @Test
    public void test_lineOfSight_wall() {
        String[] map = { // o = start, x = destination, # = wall
                " o #  ",
                "   # x",
                "   #  "
        };
        
        MockMap mock = new MockMap(map);
        
        boolean los = Pathfinding.lineOfSight(
                mock, mock.start, mock.destination);
        boolean expected = false;
        
        String readable = String.join("\n", map);
        assertEquals("Line of sight through wall\n" + readable, expected, los);
    }
    
    @Test
    public void test_lineOfSight_diagonal() {
        String[] map = { // o = start, x = destination, # = wall
                "o  # ",
                "  #  ",
                " #x  "
        };
        
        MockMap mock = new MockMap(map);
        
        boolean los = Pathfinding.lineOfSight(
                mock, mock.start, mock.destination);
        boolean expected = true;
        
        String readable = String.join("\n", map);
        assertEquals("Line of sight across diagonal\n" + readable, expected, los);
    }
    
    @Test
    public void test_lineOfSight_cornerFalse() {
        String[] map = { // o = start, x = destination, # = wall
                "   x  ",
                " o####",
                "  ####"
        };
        
        MockMap mock = new MockMap(map);
        
        boolean los = Pathfinding.lineOfSight(
                mock, mock.start, mock.destination);
        boolean expected = false;
        
        String readable = String.join("\n", map);
        assertEquals("Line of sight around tight corner\n" + readable, expected, los);
    }
    
    @Test
    public void test_lineOfSight_cornerTrue() {
        String[] map = { // o = start, x = destination, # = wall
                "   x  ",
                "o ####",
                "  ####"
        };
        
        MockMap mock = new MockMap(map);
        
        boolean los = Pathfinding.lineOfSight(
                mock, mock.start, mock.destination);
        boolean expected = true;
        
        String readable = String.join("\n", map);
        assertEquals("Line of sight around wide corner\n" + readable, expected, los);
    }
    
    @Test
    public void test_lineOfSight_pillar() {
        String[] map = { // o = start, x = destination, # = wall
                "          ",
                " o  ##    ",
                "    ##  x ",
                "          ",
                "          "
        };
        
        MockMap mock = new MockMap(map);
        
        boolean los = Pathfinding.lineOfSight(
                mock, mock.start, mock.destination);
        boolean expected = false;
        
        String readable = String.join("\n", map);
        assertEquals("Line of sight through pillar\n" + readable, expected, los);
    }
    
    // Note that the Pathfinding.lineOfSight tests
    //    double as Pathfinding.visibility tests.
}

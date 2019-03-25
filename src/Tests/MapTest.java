package Tests;

import Model.*;
import org.junit.Test;

import java.util.List;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class MapTest {
    @Test
    public void test_logMessage_overLimit() {
        for (int i = 0; i < 40; i++) {
            Map.logMessage("message " + i);
        }
        
        List<String> expected = new ArrayList<>();
        for (int i = 20; i < 40; i++) {
            expected.add(0, "message " + i);
        }
        
        assertEquals("Added 40 messages to Map.log", expected, Map.getLog());
    }
    
    @Test
    public void test_getGrid_privacy() {
        Map map = new Map();
        map.nextFloor();
        Entity[][] grid1 = map.getGrid();
        Entity[][] grid2 = map.getGrid();
        
        for (int x = 0; x < grid1.length; x++) {
            for (int y = 0; y < grid1[0].length; y++) {
                if (grid1[x][y] != null) {
                    assertNotSame("Grid returned by getGrid should not reference same entities", grid1[x][y], grid2[x][y]);
                }
            }
        }
    }
    
    @Test
    public void test_getVisibility_privacy() {
        Map map = new Map();
        map.nextFloor();
        double oldValue = map.getVisibility()[0][0];
        map.getVisibility()[0][0] += 1;
        double newValue = map.getVisibility()[0][0];
        assertEquals("Visibility returned by getVisibility should be a copy", oldValue, newValue, 0.0001);
    }
    
    @Test
    public void test_getPlayers_privacy() {
        Map map = new Map();
        map.nextFloor();
        List<Player> players1 = map.getPlayers();
        List<Player> players2 = map.getPlayers();
        assertNotEquals("Players returned by getPlayers should not be equal", players1, players2);
    }
    
    @Test
    public void test_positionOnMap_highXY_false() {
        Map map = new Map();
        Position pos = new Position(map.getWidth(), map.getHeight());
        boolean onMap = map.positionOnMap(pos);
        boolean expected = false;
        assertEquals("Test position on map " + pos, expected, onMap);
    }
    
    @Test
    public void test_positionOnMap_highXY_true() {
        Map map = new Map();
        Position pos = new Position(map.getWidth() - 1, map.getHeight() - 1);
        boolean onMap = map.positionOnMap(pos);
        boolean expected = true;
        assertEquals("Test position on map " + pos, expected, onMap);
    }
    
    @Test
    public void test_positionOnMap_smallXY_false() {
        Map map = new Map();
        Position pos = new Position(-1, -1);
        boolean onMap = map.positionOnMap(pos);
        boolean expected = false;
        assertEquals("Test position on map " + pos, expected, onMap);
    }
    
    @Test
    public void test_positionOnMap_smallXY_true() {
        Map map = new Map();
        Position pos = new Position(0, 0);
        boolean onMap = map.positionOnMap(pos);
        boolean expected = true;
        assertEquals("Test position on map " + pos, expected, onMap);
    }
    
    @Test
    public void test_nextFloorIncrement() {
        Map map = new Map();
        map.nextFloor();
        int expected = map.getFloor() + 1;
        map.nextFloor();
        assertEquals("nextFloor should increment floor ", expected, map.getFloor());
    }
}

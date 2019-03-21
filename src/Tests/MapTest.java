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
}

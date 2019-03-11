package Tests;

import Model.Map;
import Text.Display;

public class MapTests {
    public static void main(String[] args) {
        Display d = new Display();
        
        Map m = new Map(40, 40);
        m.generateDungeon();
        d.printMap(m);
        System.out.println();
    
        m = new Map(11, 11);
        m.generateCircle();
        d.printMap(m);
        System.out.println();
    
        m = new Map(20, 10);
        m.generateCircle();
        d.printMap(m);
        System.out.println();
    
        m = new Map(10, 10);
        m.generateDungeon();
        d.printMap(m);
        System.out.println();
    
        m = new Map(20, 20);
        m.generateCave();
        d.printMap(m);
        System.out.println();
    
        m = new Map(40, 10);
        m.generateCircle();
        d.printMap(m);
        System.out.println();
    
        m = new Map(40, 20);
        m.generateCave();
        d.printMap(m);
        System.out.println();
    
        m = new Map(40, 20);
        m.generateCave();
        d.printMap(m);
        System.out.println();
    }
}

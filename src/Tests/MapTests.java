package Tests;

import Model.Map;

public class MapTests {
    public static void main(String[] args) {
        Map m = new Map(10, 10);
        m.generateDungeon();
        m.printGrid();
        System.out.println();
    
        m = new Map(11, 11);
        m.generateDungeon();
        m.printGrid();
        System.out.println();
    
        m = new Map(20, 10);
        m.generateDungeon();
        m.printGrid();
        System.out.println();
    
        m = new Map(10, 20);
        m.generateDungeon();
        m.printGrid();
        System.out.println();
    
        m = new Map(20, 20);
        m.generateDungeon();
        m.printGrid();
        System.out.println();
    
        m = new Map(40, 10);
        m.generateDungeon();
        m.printGrid();
        System.out.println();
    
        m = new Map(40, 20);
        m.generateDungeon();
        m.printGrid();
        System.out.println();
    
        m = new Map(40, 20);
        m.generateDungeon();
        m.printGrid();
        System.out.println();
    }
}

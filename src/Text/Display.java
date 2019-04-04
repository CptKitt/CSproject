package Text;

import Model.*;

import java.util.Set;

/**
  * Display class is used to display the text-based version of Group 9's project.
  * Is implemented within the TextMain class.
  * Contains methods: printMap(map:Map, highlight:Set<Position>):void.
  */
public class Display {

    /**
     * printMap() method takes a Map, and Set<Position> objects that are used to print
     * the map/floor level layout, characters, and enemies to the terminal when called.
     * Parameters: map:Map, highlight:Set<Position>.
     * Returns:nothing.
     */
    public void printMap(Map map, Set<Position> highlight) {

        Entity[][] grid = map.getGrid();
        Entity entities;

	System.out.println("");
        System.out.print(" ");
        for (int i = 0; i < grid[0].length; i++) {
            if (i >= 0 && i < 11) {
                System.out.print("  " + i + " ");
            } else {
                System.out.print(" " + i + " ");
            }
        }


        for (int i = 0; i < grid.length; i++) {
            System.out.println();
            if (i >= 0 && i < 10) {
                System.out.print("0" + i + " ");
            } else {
                System.out.print(i + " ");
            }


            for (int j = 0; j < grid[i].length; j++) {


                Position position = new Position(i, j);
                entities = grid[i][j];

                if (highlight.contains(position)) {


                    if (entities == null) {
                        System.out.print("[ ] ");
                    } else if (entities instanceof Player) {
                        System.out.print("[x] ");

                    } else if (entities instanceof Enemy) {
                        System.out.print("[O] ");
                    } else if (entities instanceof Stairs) {
                        System.out.print("[<] ");
                    } else {
                        System.out.print("#   ");

                    }


                } else {
                    if (entities == null) {
                        System.out.print("    ");
                    } else if (entities instanceof Player) {
                        System.out.print("x   ");

                    } else if (entities instanceof Enemy) {
                        System.out.print("O   ");
                    } else if (entities instanceof Stairs) {
                        System.out.print("<   ");
                    } else {
                        System.out.print("#   ");

                    }

                }

            }
        }
		//Starts a new row to seperate the Display from the Input
		System.out.println();
    }
}

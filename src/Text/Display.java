package Text;

import Model.*;

/**
 *
 */
public class Display {

    //A suggestion by Tam: maybe have printGrid take a Map object as parameter so that TextMain can pass through the Map variable that it contains. Then the printGrid method should store a 2d array through the getGrid() method that you could permutate.
    public void printMap(Map map) {

        Entity[][] grid = map.getGrid();
        Entity entities;


        System.out.print(" ");
        for (int i = 0; i < grid[0].length; i++) {
            System.out.print("  " + i + " ");
        }


        for (int i = 0; i < grid.length; i++) {
            System.out.println();
            if (i >= 0 && i < 10) {
                System.out.print("0" + i + " ");
            } else {
                System.out.print(i + " ");}


                for (int j = 0; j < grid[i].length; j++) {


                    entities = grid[i][j];

                    if (entities == null) {
                        System.out.print("    ");
                    } else if (entities instanceof Player) {
                        System.out.print("x   ");

                    } else {
                        System.out.print("#   ");

                    }


                }

            System.out.println();
        }
    }
}






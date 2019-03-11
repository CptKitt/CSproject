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

            for (int i = 0; i < grid.length; i++) {
                System.out.print(i + "");
                System.out.print("  ");
                for (int j = 0; j < grid[i].length; j++) {
                    System.out.print(j + "");

                    entities = grid[i][j];

                if (entities == null) {
                    System.out.println(" ");

                } else if (entities instanceof Player) {
                    System.out.println("x");

                } else  {
                    System.out.println("#");

                }


            }


        }
    }
    public static void main(String[] args){


    }
}


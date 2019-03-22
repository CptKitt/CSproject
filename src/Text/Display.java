package Text;

import Model.*;
import javafx.geometry.Pos;

import java.util.Set;

/**
 *
 */
public class Display {


    public void printMap(Map map, Set<Position> highlight) {

        Entity[][] grid = map.getGrid();
        Entity entities;


        System.out.print(" ");
        for (int i = 0; i < grid[0].length; i++) {
            if (i >= 0 && i < 10) {
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
        }System.out.println();


    }


}







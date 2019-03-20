package Text;

import Model.*;

/**
 *
 */
public class Display {


    public void printMap(Map map) {

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
                System.out.print(i + " ");}


                for (int j = 0; j < grid[i].length; j++) {


                    entities = grid[i][j];

                    if (entities == null) {
                        System.out.print("    ");
                    } else if (entities instanceof Player) {
                        System.out.print("x   ");
		//Implement a symbol for the available movement spaces, i.e '*'
		//Implement a symbol for the stairs, i.e '>'
                    } else {
                        System.out.print("#   ");

                    }


                }

            System.out.println();
        }
    }
}






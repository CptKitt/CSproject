package Text; 

/**
 *
 */
public class Display {

	//A suggestion by Tam: maybe have printGrid take a Map object as parameter so that TextMain can pass through the Map variable that it contains. Then the printGrid method should store a 2d array through the getGrid() method that you could permutate.
    public void printGrid() {
	for (int i = 0; i < grid.length; i++) {
		System.out.print((row % 10) + " ");
		for (int j = 0; j < grid[i].length; j++) {
			System.out.print(grid[i][j]);
		}
		System.out.print("\n");
	}
	System.out.print(" ");
	for (int j = 0; j < 10; j++) {
		System.out.print((j % 10));
	}
	System.out.println();
    }

    public static void main (String[] args) {
        boolean exit = false;
        

}
    }
}

package Text; 

/**
 *
 */
public class Display {

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
        Scanner in = new Scanner(System.in);
        Map dis = new Map();
        dis.populateGrid();
        dis.printGrid();
        while(!exit) {
            String todo = in.nextLine();
            if (todo == "exit") {
                exit = true;
            }
            else {
                dis.populateGrid();
                dis.printGrid();
            }
}
    }
}

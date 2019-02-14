import java.util.Random;
import java.util.Scanner;

public class Map {
	private String[][] grid = new String[10][10];
	Random rand = new Random();

	public void populateGrid() {
		for (int i=0;i<grid.length;i++) {
			for (int j=0;j<grid[i].length;j++) {
				int tileChance = rand.nextInt(3);
				String tile;
				if (tileChance == 0 || tileChance == 1) {
					tile = ".";
				}
				else {
					tile = "#";
				}
				grid[i][j] = tile;
			}
		}
	}

	public void printGrid() {
		for(int i=0;i<grid.length;i++) {
			for(int j=0;j<grid[i].length;j++) {
				System.out.print(grid[i][j]);
			}
			System.out.print("\n");
		}
	}

	public static void main (String[] args) {
		boolean exit = false;
		Scanner in = new Scanner(System.in);
		Map asdf = new Map();
		asdf.populateGrid();
		asdf.printGrid();
		while(!exit) {
			String todo = in.nextLine();
			if (todo == "exit") {
				exit = true;
			}
			else {
				asdf.populateGrid();
				asdf.printGrid();
			}
		}
	}
}
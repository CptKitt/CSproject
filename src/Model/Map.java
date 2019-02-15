package Model;
import java.util.*;

public class Map implements Pathfinding.Delegate {
	private String[][] grid = new String[10][10];
	Random rand = new Random();

	private Position start = new Position(rand.nextInt(10),rand.nextInt(10));

	public Position getStart() {
		return start;
	}

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

	public boolean validPosition(Position p) {
		if (p.x < 0 || p.x > 10) {
			return false;
		} else if (p.y < 0 || p.y > 10) {
			return false;
		}
		if (grid[p.x][p.y] == "#") {
			return false;
		}
		return true;
	}

	public static void main (String[] args) {
		boolean exit = false;
		Scanner in = new Scanner(System.in);
		Map asdf = new Map();
		asdf.populateGrid();
		asdf.printGrid();

		Set<Position> moves = Pathfinding.movementForPosition(asdf,asdf.getStart(),asdf.rand.nextInt(5));

		System.out.println(moves);

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
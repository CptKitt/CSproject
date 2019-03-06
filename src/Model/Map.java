package Model;
import java.util.*;

public class Map implements Pathfinding.Delegate {
	private String[][] grid;
	Random rand = new Random();

	private Position start = new Position(rand.nextInt(10),rand.nextInt(10));

	public Map() {
		grid = new String[10][10];
	}
	public Map(int x,int y) {
		grid = new String[x][y];
	}

	public Position getStart() {
		return start;
	}

	public void setStart(Position start) {
		for (int i=0;i<grid.length;i++)
			for (int j=0;j<grid[i].length;j++)
				grid[i][j] = grid[i][j] == "#" ? "#" : ".";
		this.start = start;
	}

	public String[][] getGrid() {
		String[][] toReturn = new String[grid.length][grid[0].length];
		for(int i=0;i<toReturn.length;i++) {
			toReturn[i] = grid[i];
		}
		return toReturn;
	}

	public void newStart() {
		start = new Position(
				rand.nextInt(10),
				rand.nextInt(10));
	}

	public void populateGrid() {
		for (int i=0;i<grid.length;i++) {
			for (int j=0;j<grid[i].length;j++) {
				int tileChance = rand.nextInt(3);
				String tile;
				if (i == start.x && j == start.y) {
					tile = "x";
				}
				else if (tileChance == 0 || tileChance == 1) {
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
		for(int i=0;i<grid[0].length;i++) {
			for(int j=0;j<grid.length;j++) {
				System.out.print(grid[j][i]);
			}
			System.out.print("\n");
		}
	}

	public void pathfind() {
		Set<Position> moves = Pathfinding.movementForPosition(this,start,rand.nextInt(4)+2);

		for(Position p : moves) {
			if(p.x == start.x && p.y == start.y) {
				grid[p.x][p.y] = "x";
			}
			else {
				grid[p.x][p.y] = "*";
			}
		}
	}

	public boolean validPosition(Position p) {
		if (p.x < 0 || p.x >= 10) {
			return false;
		} else if (p.y < 0 || p.y >= 10) {
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
		asdf.pathfind();
		asdf.printGrid();

		while(!exit) {
			String todo = in.nextLine();
			if (todo == "exit") {
				exit = true;
			}
			else {
				asdf.newStart();
				asdf.populateGrid();
				asdf.pathfind();
				asdf.printGrid();
			}
		}
	}
}

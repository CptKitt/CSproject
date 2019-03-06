package Model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Map implements Pathfinding.Delegate {
	private String[][] grid;
	Random rand = new Random();
	private Entity[][] entities;
	private Position start;

	public Map() {
		this(10, 10);
	}
	
	public Map(int x, int y) {
		grid = new String[x][y];
		entities = new Entity[x][y];
		newStart();
		populateGrid();
	}

	// TODO: remove start methods
	public Position getStart() {
		return start;
	}
	
	public void setStart(Position start) {
		for (int i = 0; i < getWidth(); i++)
			for (int j = 0; j < getHeight(); j++)
				grid[i][j] = grid[i][j] == "#" ? "#" : ".";
		this.start = start;
	}
	
	public void newStart() {
		start = new Position(
				rand.nextInt(getWidth()),
				rand.nextInt(getHeight()));
	}
	
	public Entity[][] getGrid() {
		Entity[][] copy = new Entity[getWidth()][getHeight()];
		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				// TODO: create a new entity rather than copy reference
				copy[x][y] = entities[x][y];
			}
		}
		return entities;
	}

	public void populateGrid() {
		for (int i = 0; i < getWidth(); i++) {
			for (int j = 0; j < getHeight(); j++) {
				int tileChance = rand.nextInt(3);
				String tile;
				if (i == start.x && j == start.y) {
					tile = "x";
					entities[i][j] = new Player(
					        0, 0, 0, 0, 0,
                            new Position(i, j), 0, 0);
				}
				else if (tileChance == 0 || tileChance == 1) {
					tile = ".";
					entities[i][j] = new Entity(
					        0, 0, 0, 0, 0,
                            new Position(i, j), 0);
				}
				else {
					tile = "#";
					entities[i][j] = null;
				}
				grid[i][j] = tile;
			}
		}
	}

	// TODO: Delete in favor of Display.
	public void printGrid() {
		for(int i = 0; i < getHeight(); i++) {
			for(int j = 0; j < getWidth(); j++) {
				System.out.print(grid[j][i]);
			}
			System.out.print("\n");
		}
	}
	
	/**
	 * Calculates the possible moves for an Entity at a Position.
	 * @param p The Position of the Entity.
	 * @return A Set of Positions that the Entity can move to.
	 */
	public Set<Position> possibleMovesForCharacter(Position p) {
		// no character at position, return empty set
		if (entities[p.x][p.y] == null) {
			return new HashSet<>();
		}
		
		// TODO: use entity movement rather than number
		return Pathfinding.movementForPosition(
				this, p, 5);
	}
	
	/**
	 * Attempts to process an action.
	 * @param p1 The Entity performing the action.
	 * @param p2 The destination Position for the action.
	 * @return Whether the action was valid or not.
	 */
	public boolean processAction(Position p1, Position p2) {
		// no character selected, ignore
		if (entities[p1.x][p1.y] == null) {
			return false;
		}
		// destination is another character
		else if (entities[p2.x][p2.y] != null) {
			// not in melee range, return
			if (p1.distanceTo(p2) != 1) {
				return false;
			}
			else {
				// TODO: ask entities to fight each other
			}
		}
		// destination is an empty space
		else {
			// out of movement range, return
			if (!possibleMovesForCharacter(p1).contains(p2)) {
				return false;
			}
			else {
				// move character
				entities[p2.x][p2.y] = entities[p1.x][p1.y];
				entities[p1.x][p2.y] = null;
			}
		}
		
		// action successfully completed
		return true;
	}
	
	/**
	 * @param path The path of the file to read from.
	 */
	public void readMapFromFile(String path) {
		try {
			Scanner in = new Scanner(new File(path));
			List<String> lines = new ArrayList<>();
			while (in.hasNextLine()) {
				lines.add(in.nextLine());
			}
			in.close();
			
			if (lines.isEmpty()) {
				return;
			}
			
			int width = lines.get(0).length();
			int height = lines.size();
			
			grid = new String[width][height];
			entities = new Entity[width][height];
			
			for (int y = 0; y < height; y++) {
				String line = lines.get(y);
				for (int x = 0; x < width; x++) {
					switch (line.charAt(x)) {
						case ' ': // empty space
							break;
						case 'W': // wall
							entities[x][y] = new Entity(0, 0, 0, 0, 0, new Position(x, y), 0);
							break;
						case 'C': // character?
							break;
					}
				}
			}
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
    
    /**
     * @return The width, x-length of the map.
     */
	public int getWidth() {
	    return entities.length;
    }
    
    /**
     * @return The height, y-length of the map.
     */
    public int getHeight() {
	    return entities.length == 0 ? 0 : entities[0].length;
    }

	public void pathfind() {
		Set<Position> moves = Pathfinding.movementForPosition(
		        this, start,rand.nextInt(4)+2);

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
		// TODO: add entity wall checks
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
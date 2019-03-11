package Model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Map implements Pathfinding.Delegate {
	private String[][] grid;
	Random rand = new Random();
	private Entity[][] entities;
	private double[][] visibility;
	private Position start;

	public Map() {
		this(10, 10);
	}
	
	public Map(int x, int y) {
		grid = new String[x][y];
		entities = new Entity[x][y];
		visibility = new double[x][y];
		
		for (x = 0; x < getWidth(); x++) {
			for (y = 0; y < getHeight(); y++) {
				grid[x][y] = ".";
				entities[x][y] = null;
				visibility[x][y] = 0;
			}
		}
		
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
	
	/**
	 * @return A copy of the entities represented by this Map.
	 */
	public Entity[][] getGrid() {
		Entity[][] copy = new Entity[getWidth()][getHeight()];
		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				// TODO: create a new entity rather than copy reference
				copy[x][y] = entities[x][y];
			}
		}
		return copy;
	}
	
	/**
	 * @return A copy of the visibility of this Map.
	 */
	public double[][] getVisibility() {
		double[][] copy = new double[getWidth()][getHeight()];
		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				copy[x][y] = visibility[x][y];
			}
		}
		return copy;
	}
	
	/** Updates visibility for the whole Map. */
	public void updateVisibility() {
		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				Entity e = entities[x][y];
				if (e == null || !(e instanceof Player)) {
					continue;
				}
				Position pos = new Position(x, y);
				for (Position p : Pathfinding.visibility(this, pos, 5)) {
					if (p.x >= 0 && p.x < getWidth() && p.y >= 0 && p.y < getHeight()) {
						double opacity = 1.0 / Math.max(pos.distanceTo(p), 1);
						visibility[p.x][p.y] = Math.max(opacity, visibility[p.x][p.y]);
					}
				}
			}
		}
	}
    
    /** Creates a random map. */
	public void populateGrid() {
	    switch (rand.nextInt(4)) {
            case 0: generateCave(); break;
            case 1: generateCircle(); break;
            case 2: generateDungeon(); break;
            default: generateRandom(); break;
        }
	}
    
    /** Generates walls randomly. */
	public void generateRandom() {
        for (int i = 0; i < getWidth(); i++) {
            for (int j = 0; j < getHeight(); j++) {
                int tileChance = rand.nextInt(3);
                String tile;
                if (i == start.x && j == start.y) {
                    tile = "x";
                    entities[i][j] = newPlayer(new Position(i, j));
                }
                else if (tileChance == 0 || tileChance == 1) {
                    tile = ".";
                    entities[i][j] = null;
                }
                else {
                    tile = "#";
                    entities[i][j] = newWall(new Position(i, j));
                }
                grid[i][j] = tile;
            }
        }
    }
    
    /**
     * Generates a large elliptical room.
     * Uses the ellipse equation [(x/w)^2 + (y/h)^2 < 1]
     * to check whether a tile is a wall or not.
     */
    public void generateCircle() {
    	// calculate room dimensions
    	double cx = (double) getWidth() / 2;
    	double cy = (double) getHeight() / 2;
		double width = cx - 1;
	    double height = cy - 1;
		
	    // run ellipse calculations
		for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                double dx = x - cx + 0.5;
                double dy = y - cy + 0.5;
                
                // inside ellipse, empty space
                if (Math.pow(dx / width, 2) + Math.pow(dy / height, 2) < 1) {
                    grid[x][y] = ".";
                    entities[x][y] = null;
                }
                else { // wall
                    grid[x][y] = "#";
                    entities[x][y] = newWall(new Position(x, y));
                }
            }
		}
	
	    placePlayer();
    }
    
    /**
     * Generates a dungeon-like map.
     * Makes a number of rooms, then connects them with hallways.
     */
    public void generateDungeon() {
		/**
		 * Convenience class for room representation.
		 */
		class Room {
			final Position origin;
			final int width, height;
	
			/**
			 * @param origin The top left Position of the Room.
			 * @param width The width of the Room.
			 * @param height The height of the Room.
			 */
			Room(Position origin, int width, int height) {
				this.origin = origin;
				this.width = width;
				this.height = height;
			}
	
			/**
			 * @param other The Room to compare against.
			 * @return True if the rooms are touching, false otherwise.
			 */
			boolean touches(Room other) {
				// calculate expanded room to allow intersection check
				Room box = new Room(
						other.origin.moved(-1, -1),
						other.width + 2, other.height + 2);
				return !(origin.x > box.origin.x + box.width ||
						origin.x + width < box.origin.x ||
						origin.y > box.origin.y + box.height ||
						origin.y + height < box.origin.y);
			}
	
			/**
			 * @return A random Position inside the room.
			 */
			Position randomPosition() {
				return origin.moved(
						rand.nextInt(width),
						rand.nextInt(height));
			}
		}
		
		// fill with walls
		fillWalls();
		
		List<Room> rooms = new ArrayList<>();
		int failed = 0;
		
		// fill with as many rooms as possible
		while (failed < 100) {
			// generate dimensions
			Position pos = new Position(
					rand.nextInt(getWidth() - 4) + 1,
					rand.nextInt(getHeight() - 4) + 1);
			int dw = Math.min(6, getWidth() - pos.x - 2);
			int width = rand.nextInt(dw) + 2;
			int dh = Math.min(6, getHeight() - pos.y - 2);
			int height = rand.nextInt(dh) + 2;
			
			// create room (rectangle)
			Room room = new Room(pos, width, height);
			
			// check for intersect with other rooms
			if (rooms.stream().anyMatch(r -> r.touches(room))) {
				failed++;
				continue;
			}
			
			// add, clear tiles
			rooms.add(room);
			for (int x = pos.x; x < pos.x + width; x++) {
				for (int y = pos.y; y < pos.y + height; y++) {
					if (x >= 0 && x < getWidth()
							&& y >= 0 && y < getHeight()) {
						grid[x][y] = ".";
						entities[x][y] = null;
					}
				}
			}
		}
		
		// generate hallways
		List<Room> connected = new ArrayList<>();
		connected.add(rooms.get(0));
		
		for (Room r : rooms) {
			if (connected.isEmpty()) {
				connected.add(r);
				continue;
			}
			
			// pick random connected room
			Room r2 = connected.get(rand.nextInt(connected.size()));
			connected.add(r);
			
			Position start = r.randomPosition();
			Position end = r2.randomPosition();
			System.out.println(start + " " + end);
			
			// find path and clear
			for (Position p : Pathfinding
					.shortestPath(p -> true, start, end)) {
				grid[p.x][p.y] = ".";
				entities[p.x][p.y] = null;
			}
		}
		
		placePlayer();
    }
    
    /**
     * Generates a cave-like map.
     * Creates a number of connected lines and
     * empties out the tiles near them.
     */
    public void generateCave() {
    	// all walls to begin
	    fillWalls();
	
	    // loop generation
	    int probability = 130;
	    Position start = new Position(
	    		rand.nextInt(getWidth()),
			    rand.nextInt(getHeight()));
	    do {
	    	// random end position
	        Position end = new Position(
			        rand.nextInt(getWidth()),
			        rand.nextInt(getHeight()));
	        
	        // find random line to end position
		    // (Pathfinding A* is too linear)
		    List<Position> line = new ArrayList<>();
		    Position move = start;
		    while (!move.equals(end)) {
		    	line.add(move);
				
		    	if (move.x != end.x) {
		    		if (move.y != end.y && rand.nextBoolean()) {
					    move = move.moved(0, move.y > end.y ? -1 : 1);
				    }
		    		else {
					    move = move.moved(move.x > end.x ? -1 : 1, 0);
				    }
			    }
		    	else {
				    move = move.moved(0, move.y > end.y ? -1 : 1);
			    }
		    }
	        
	        // clear around line
		    for (Position p : line) {
		    	double radius = Math.sqrt(rand.nextInt(20));
			    
			    // inefficiently check all tiles
			    for (int x = 0; x < getWidth(); x++) {
			    	for (int y = 0; y < getHeight(); y++) {
			    		double dx = Math.pow(x - p.x, 2);
			    		double dy = Math.pow(y - p.y, 2);
			    		
			    		// inside radius, clear wall
			    		if (dx + dy < radius) {
			    			grid[x][y] = ".";
			    			entities[x][y] = null;
					    }
				    }
			    }
		    }
		    
		    // prep for new line
	        start = end;
	        
	        // chance to continue cave
		    probability -= 30;
        } while (rand.nextInt(100) < probability);
	    
	    placePlayer();
    }
	
	/** Convenience function to fill the map with walls. */
    private void fillWalls() {
	    for (int x = 0; x < getWidth(); x++) {
		    for (int y = 0; y < getHeight(); y++) {
			    grid[x][y] = "#";
			    entities[x][y] = newWall(new Position(x, y));
		    }
	    }
    }
	
	/** Temporary convenience function to place the character. */
    private void placePlayer() {
    	Position p;
    	do {
    		p = new Position(
    				rand.nextInt(getWidth()),
				    rand.nextInt(getHeight()));
	    } while (entities[p.x][p.y] != null);
    	entities[p.x][p.y] = newPlayer(p);
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
		if (!(entities[p.x][p.y] instanceof Player)) {
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
		// destination is the entity, ignore
		if (p1.equals(p2)) {
			return false;
		}
		// no character selected, ignore
		else if (!(entities[p1.x][p1.y] instanceof Player)) {
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
				entities[p1.x][p1.y] = null;
				entities[p2.x][p2.y].setPOS(p2);
			}
		}
		
		// action successfully completed, finish
		updateVisibility();
		return true;
	}
	
	/**
	 * @param path The path of the file to read from.
	 */
	public void readMapFromFile(String path) {
		try {
		    // get all lines
			Scanner in = new Scanner(new File(path));
			List<String> lines = new ArrayList<>();
			while (in.hasNextLine()) {
				lines.add(in.nextLine());
			}
			in.close();
			
			if (lines.isEmpty()) {
				return;
			}
			
			// form grid
			int width = lines.get(0).length();
			int height = lines.size();
			
			grid = new String[width][height];
			entities = new Entity[width][height];
			
			// read information
			for (int y = 0; y < height; y++) {
				String line = lines.get(y);
				for (int x = 0; x < width; x++) {
					switch (line.charAt(x)) {
						case ' ': // empty space
							entities[x][y] = null;
							break;
						case 'W': // wall
							entities[x][y] = newWall(new Position(x, y));
							break;
						case 'C': // some character
                            entities[x][y] = newPlayer(new Position(x, y));
							break;
					}
				}
			}
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
    
    /** @return The width, x-length of the map. */
	public int getWidth() {
	    return entities.length;
    }
    
    /** @return The height, y-length of the map. */
    public int getHeight() {
	    return entities.length == 0 ? 0 : entities[0].length;
    }
	
	/** Temporary function to create a wall. */
    private Entity newWall(Position position) {
    	return new Entity(
    			0, 0, 0, 0, 0,
			    position, 0);
    }
	
	/** Temporary function to return a player. */
    private Entity newPlayer(Position position) {
    	return new Player(
    			0, 0, 0, 0, 0,
			    position, 0, 0);
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
		if (p.x < 0 || p.x >= getWidth()) {
			return false;
		} else if (p.y < 0 || p.y >= getHeight()) {
			return false;
		}
		
		Entity e = entities[p.x][p.y];
		if (e != null && !(e instanceof Player)) {
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

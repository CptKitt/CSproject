package src.Model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Model class containing all information necessary
 * to play and interact with the game.
 * <p></p>
 * As Map is purely a logic class, it cannot display
 * its information itself, but other classes can use
 * its {@code getGrid()} and {@code processAction()}
 * methods to display and interact with it.
 */
public class Map implements Pathfinding.Delegate {
	private Random rand = new Random();
	
	/** The characters and walls held by the map. */
	private Entity[][] entities;
	
	/**
	 * A grid of the visibility of positions on the Map.
	 * Each tile contains a double from 0.0 to 1.0, where
	 * 0.0 represents a non-visible tile, and
	 * 1.0 represents a fully-visible tile.
	 */
	private double[][] visibility;
	
	/** Creates a 10x10 Map. */
	public Map() {
		this(10, 10);
	}
	
	/**
	 * Creates a Map with the specified dimensions.
	 * @param x The width of the Map.
	 * @param y The height of the Map.
	 */
	public Map(int x, int y) {
		entities = new Entity[x][y];
		visibility = new double[x][y];
		
		populateGrid();
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
	private void updateVisibility() {
		List<Position> toUpdate = new ArrayList<>();
		
		// look for players
		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				Entity e = entities[x][y];
				if (e instanceof Player) {
					toUpdate.add(new Position(x, y));
				}
			}
		}
		
		System.out.println(toUpdate);
		
		// go through each player position
		for (Position pos: toUpdate) {
			for (Position p : Pathfinding.visibility(this, pos, 7)) {
				// check OOB
				if (p.x < 0 || p.x >= getWidth() || p.y < 0 || p.y >= getHeight()) {
					continue;
				}
				
				// parabolic opacity curve
				double dist = pos.distanceTo(p);
				double opacity = Math.min(1, -Math.pow(dist / 7, 2) + 1.1);
				visibility[p.x][p.y] = Math.max(opacity, visibility[p.x][p.y]);
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
        
        visibility = new double[getWidth()][getHeight()];
        updateVisibility();
	}
    
    /** Generates walls randomly. */
	public void generateRandom() {
        for (int i = 0; i < getWidth(); i++) {
            for (int j = 0; j < getHeight(); j++) {
                // 1/3 chance for a wall
                int tileChance = rand.nextInt(3);
                
                if (tileChance <= 1) {
                    entities[i][j] = null;
                }
                else {
                    entities[i][j] = newWall(new Position(i, j));
                }
            }
        }
        
        wallBorder();
        placePlayer();
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
                    entities[x][y] = null;
                }
                else { // wall
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
			for (Position p : Pathfinding.shortestPath(p -> true, start, end)) {
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
		    Position end;
		    do {
		    	end = new Position(
					    rand.nextInt(getWidth()),
					    rand.nextInt(getHeight()));
		    } while (start.distanceTo(end) <= 8);
	        
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
	    
	    wallBorder();
	    placePlayer();
    }
	
	/** Convenience function to fill the map with walls. */
    private void fillWalls() {
	    for (int x = 0; x < getWidth(); x++) {
		    for (int y = 0; y < getHeight(); y++) {
			    entities[x][y] = newWall(new Position(x, y));
		    }
	    }
    }
    
    /** Convenience function to fill the border of the map with walls. */
    private void wallBorder() {
        for (int x = 0; x < getWidth(); x++) {
            entities[x][0] = newWall(new Position(x, 0));
            entities[x][getHeight()-1] = newWall(new Position(x, getHeight()-1));
        }
        
        for (int y = 0; y < getHeight(); y++) {
            entities[0][y] = newWall(new Position(0, y));
            entities[getWidth()-1][y] = newWall(new Position(getWidth()-1, y));
        }
    }
	
	/** Convenience function to place the character on the Map. */
    private void placePlayer() {
    	Position p;
    	do {
    		p = new Position(
    				rand.nextInt(getWidth()),
				    rand.nextInt(getHeight()));
	    } while (entities[p.x][p.y] != null);
    	entities[p.x][p.y] = newPlayer(p);
    }
    
    /** Function to be deprecated. */
    public void setStart(Position p) {
    	Position playerPos = Position.NONE;
    	for (int x = 0; x < getWidth(); x++) {
    		for (int y = 0; y < getHeight(); y++) {
    			if (entities[x][y] instanceof Player) {
    				playerPos = new Position(x, y);
			    }
		    }
	    }
    	
    	processAction(playerPos, p);
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
			    position, 0, 0,0);
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
}

package Model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

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
	
	/**
	 * A list of the players on the map.
	 */
	private List<Player> players;
	
	/**
	 * A list of the enemies on the map.
	 * Subject to change in size as enemies are defeated.
	 */
	private List<Enemy> enemies;
	
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
		players = new ArrayList<>();
		enemies = new ArrayList<>();
		
		populateGrid();
	}
	
	/**
	 * @return A copy of the entities represented by this Map.
	 */
	public Entity[][] getGrid() {
		Entity[][] copy = new Entity[getWidth()][getHeight()];
		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				if (entities[x][y] != null) {
					copy[x][y] = entities[x][y].copy();
				}
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
	
	/**
	 * @return A copy of the Players on the Map.
	 */
	public List<Player> getPlayers() {
		List<Player> copy = new ArrayList<>();
		for (Player p : players) {
			copy.add((Player) p.copy());
		}
		return copy;
	}
	
	/**
	 * @return A copy of the Enemies on the Map.
	 */
	public List<Enemy> getEnemies() {
		List<Enemy> copy = new ArrayList<>();
		for (Enemy e : enemies) {
			copy.add((Enemy) e.copy());
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
		players.clear();
		enemies.clear();
		visibility = new double[getWidth()][getHeight()];
		
	    switch (rand.nextInt(4)) {
            case 0: generateCave(); break;
            case 1: generateCircle(); break;
            case 2: generateDungeon(); break;
            default: generateRandom(); break;
        }
        
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
        placeEnemies();
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
		placeEnemies();
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
			
			// find path and clear
			for (Position p : Pathfinding.shortestPath(p -> true, start, end)) {
				entities[p.x][p.y] = null;
			}
		}
		
		placePlayer();
		placeEnemies();
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
	
	    Player player = newPlayer(p);
	    players.add(player);
	    entities[p.x][p.y] = player;
    }
	
	/** Convenience function to place enemies on the Map. */
	private void placeEnemies() {
		int num = rand.nextInt(5) + 3;
		
		for (int i = 0; i < num; i++) {
			Position p = new Position(
					rand.nextInt(getWidth()),
					rand.nextInt(getHeight()));
			
			if (entities[p.x][p.y] != null ||
					players.stream().anyMatch(pl -> pl.POS.distanceTo(p) < 6)) {
				i--;
			}
			else {
				Enemy e = newEnemy(p);
				enemies.add(e);
				entities[p.x][p.y] = e;
			}
		}
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
	 * Calculates the possible moves for an Player at a Position.
	 * @param p The Position of the Player.
	 * @return A Set of Positions that the Player can move to.
	 */
	public Set<Position> possibleMovesForCharacter(Position p) {
		// no character at position, return empty set
		if (!(entities[p.x][p.y] instanceof Player)) {
			return new HashSet<>();
		}
		
		// get moves and range
		Set<Position> moves = possibleMovesForEntity(p);
		int range = entities[p.x][p.y].SPD;
		
		// add enemy attacks
		moves.addAll(enemies.stream().map(e -> e.POS).filter(e ->
				// enemy in range of attack and open square next to the position
				Pathfinding.shortestPath(this, p, e).size() < range
						&& moves.stream().anyMatch(p2 -> p2.distanceTo(e) == 1)
		).collect(Collectors.toList()));
		
		return moves;
	}
	
	/**
	 * Calculates the possible moves for an Enemy at a Position.
	 * This method should not need to be called
	 * outside of Enemy.makeMove().
	 * @param p The Position of the Enemy.
	 * @return A Set of Positions that the Enemy can move to.
	 */
	public Set<Position> possibleMovesForEnemy(Position p) {
		if (!(entities[p.x][p.y] instanceof Enemy)) {
			return new HashSet<>();
		}
		
		// get moves and range
		Set<Position> moves = possibleMovesForEntity(p);
		int range = entities[p.x][p.y].SPD;
		
		// add player attacks
		for (Player e : players) {
			// enemy in range of attack and open square next to the position
			if (Pathfinding.shortestPath(this, p, e.POS).size() <= range
					&& moves.stream().anyMatch(p2 -> p2.distanceTo(e.POS) == 1)) {
				moves.add(e.POS);
			}
		}
		
		return moves;
	}
	
	/**
	 * @param p The Position of the Entity.
	 * @return A Set of Positions that the Entity can move to.
	 */
	private Set<Position> possibleMovesForEntity(Position p) {
		if (entities[p.x][p.y] == null) {
			return new HashSet<>();
		}
		
		return Pathfinding.movementForPosition(
				this, p, entities[p.x][p.y].SPD);
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
		// not a possible move, ignore
		else if (!possibleMovesForCharacter(p1).contains(p2)) {
			return false;
		}
		// destination is an enemy
		else if (entities[p2.x][p2.y] instanceof Enemy) {
			// not close enough
			if (p1.distanceTo(p2) != 1) {
				// move character next to other entity
				Position toMove = new Position(1000, 1000);
				for (Position p : possibleMovesForCharacter(p1)) {
					if (p.distanceTo(p2) == 1 && entities[p.x][p.y] == null
							&& p.distanceTo(p1) < toMove.distanceTo(p1)) {
						toMove = p;
					}
				}
				entities[toMove.x][toMove.y] = entities[p1.x][p1.y];
				entities[p1.x][p1.y] = null;
				entities[toMove.x][toMove.y].setPOS(toMove);
				p1 = toMove;
			}
			
			// ask player to attack enemy
			Enemy e = (Enemy) entities[p2.x][p2.y];
			Player player = (Player) entities[p1.x][p1.y];
			player.attack(e);
			
			// killed enemy, remove
			if (e.HP <= 0) {
				enemies.remove(e);
				entities[p2.x][p2.y] = null;
				System.out.println("You defeated the enemy!");
			}
		}
		// destination is an empty space
		else {
			// move character
			entities[p2.x][p2.y] = entities[p1.x][p1.y];
			entities[p1.x][p1.y] = null;
			entities[p2.x][p2.y].setPOS(p2);
		}
		
		// action successfully completed, finish
		updateVisibility();
		return true;
	}
	
	/**
	 * Processes moves for all enemies on the Map.
	 * @return Whether any actions were completed or not.
	 */
	public boolean processEnemyMoves() {
		if (enemies.isEmpty()) {
			return false;
		}
		
		for (int i = 0; i < enemies.size(); i++) {
			Enemy e = enemies.get(i);
			Position p1 = e.POS;
			
			// ask enemy for move
			Position p2 = e.makeMove(this);
			
			// invalid move
			if (p2 == null || p1.equals(p2)
					|| !possibleMovesForEnemy(p1).contains(p2)) {
				continue;
			}
			
			// empty space
			if (entities[p2.x][p2.y] == null) {
				entities[p2.x][p2.y] = e;
				entities[p1.x][p1.y] = null;
				e.setPOS(p2);
			}
			else if (entities[p2.x][p2.y] instanceof Player) {
				if (p1.distanceTo(p2) != 1) {
					// move enemy next to Player
					Position toMove = Position.NONE;
					for (Position p : possibleMovesForEnemy(p1)) {
						if (p.distanceTo(p2) == 1 && entities[p.x][p.y] == null
								&& p.distanceTo(p1) < toMove.distanceTo(p1)) {
							toMove = p;
						}
					}
					if (!toMove.equals(Position.NONE)) {
						entities[toMove.x][toMove.y] = entities[p1.x][p1.y];
						entities[p1.x][p1.y] = null;
						entities[toMove.x][toMove.y].setPOS(toMove);
					}
				}
				
				// attack player
				Player player = (Player) entities[p2.x][p2.y];
				e.attack(player);
				
				// game over (?)
				if (player.HP <= 0) {
					entities[p2.x][p2.y] = null;
					System.out.println("You died.\nGame over!");
					// TODO: do something after game over
				}
			}
		}
		
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
	private Obstacle newWall(Position position) {
		return new Obstacle(
				0, 0, 0, 0, 0,
				position, 0);
	}
	
	/** Temporary function to return a player. */
	private Player newPlayer(Position position) {
		Player p = Player.randomPlayer();
		p.setPOS(position);
		return p;
	}
	
	/**
	 * Temporary function to return an enemy.
	 */
	private Enemy newEnemy(Position position) {
		Enemy e = Enemy.randomEnemy(players.get(0));
		e.setPOS(position);
		return e;
	}
	
	@Override
	public boolean validPosition(Position p) {
		if (p.x < 0 || p.x >= getWidth() || p.y < 0 || p.y >= getHeight()) {
			return false;
		}
		
		return entities[p.x][p.y] == null || entities[p.x][p.y] instanceof Player;
	}
	
	@Override
	public boolean transparentPosition(Position p) {
		if (p.x < 0 || p.x >= getWidth() || p.y < 0 || p.y >= getHeight()) {
			return false;
		}
		
		return !(entities[p.x][p.y] instanceof Obstacle);
	}
}

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
public final class Map implements Pathfinding.Delegate {
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
	
	/** Held reference to Stairs. */
	private Stairs stairs;
    
    /** The type of the Map. */
	private Type type;
    
    /** @return The type of the Map. */
    public Type getType() {
        return type;
    }
    
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
		for (Player player : players) {
			copy.add((Player) player.copy());
		}
		return copy;
	}
	
	/**
	 * @return A copy of the Enemies on the Map.
	 */
	public List<Enemy> getEnemies() {
		List<Enemy> copy = new ArrayList<>();
		for (Enemy enemy : enemies) {
			copy.add((Enemy) enemy.copy());
		}
		return copy;
	}
	
	/** Updates visibility for the whole Map. */
	private void updateVisibility() {
		List<Position> toUpdate = new ArrayList<>();
		
		// look for players
		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				Entity entity = entities[x][y];
				if (entity instanceof Player) {
					toUpdate.add(new Position(x, y));
				}
			}
		}
		
		// go through each player position
		for (Position pos: toUpdate) {
			for (Position pos2 : Pathfinding.visibility(this, pos, 7)) {
				// check OOB
				if (!positionOnMap(pos2)) {
					continue;
				}
				
				// parabolic opacity curve
				double dist = pos.distanceTo(pos2);
				double opacity = Math.min(1, -Math.pow(dist / 7, 2) + 1.1);
				visibility[pos2.x][pos2.y] = Math.max(opacity, visibility[pos2.x][pos2.y]);
			}
		}
	}
    
    /** Creates a random map. */
	public void populateGrid() {
		enemies.clear();
		visibility = new double[getWidth()][getHeight()];
		
		int num = rand.nextInt(3);
		if (num == 0) {
			entities = MapGenerator.generateCave(getWidth(), getHeight());
		}
		else if (num == 1) {
			entities = MapGenerator.generateCircle(getWidth(), getHeight());
		}
		else if (num == 2) {
			entities = MapGenerator.generateDungeon(getWidth(), getHeight());
		}
		else {
			entities = MapGenerator.generateRandom(getWidth(), getHeight());
		}
  
		placePlayer();
		placeStairs();
	    placeEnemies();
        
        updateVisibility();
	}
	
	/** Convenience function to place the character on the Map. */
    private void placePlayer() {
    	// random starting point
	    Position pos;
	    do {
		    pos = new Position(
				    rand.nextInt(getWidth()),
				    rand.nextInt(getHeight()));
	    } while (entities[pos.x][pos.y] != null);
	    
	    // place new player
    	if (players.isEmpty()) {
		    entities[pos.x][pos.y] = newPlayer(pos);
		
		    Player player = newPlayer(pos);
		    players.add(player);
		    entities[pos.x][pos.y] = player;
	    }
    	// place existing players nearby
    	else {
    		// try to place as close as possible
    		Deque<Position> tiles = new ArrayDeque<>();
    		tiles.addLast(pos);
    		
    		for (Player player : players) {
    			while (true) {
    				// poll and add adjacent to queue
    				Position newPos = tiles.pollFirst();
    				for (Position adjPos : newPos.adjacentPositions()) {
    					tiles.addLast(adjPos);
				    }
    				
    				// empty space, place player
    				if (entities[newPos.x][newPos.y] == null) {
    					entities[newPos.x][newPos.y] = player;
    					player.setPOS(newPos);
    					break;
				    }
			    }
		    }
	    }
    }
	
	/** Convenience function to place enemies on the Map. */
	private void placeEnemies() {
		int num = rand.nextInt(5) + 3;
		
		for (int i = 0; i < num; i++) {
			Position pos = new Position(
					rand.nextInt(getWidth()),
					rand.nextInt(getHeight()));
			
			if (entities[pos.x][pos.y] != null ||
					players.stream().anyMatch(
							player -> player.POS.distanceTo(pos) < 6)) {
				i--;
			}
			else {
				Enemy enemy = newEnemy(pos);
				enemies.add(enemy);
				entities[pos.x][pos.y] = enemy;
			}
		}
	}
	
	/** Convenience function to place stairs on the Map. */
	private void placeStairs() {
		// random starting point
		while (true) {
			Position pos = new Position(
					rand.nextInt(getWidth()),
					rand.nextInt(getHeight()));
			
			// not covered and far enough away from players
			if (entities[pos.x][pos.y] == null && players.stream()
					.allMatch(player -> player.POS.distanceTo(pos) > 8)) {
				stairs = newStairs(pos);
				entities[pos.x][pos.y] = stairs;
				break;
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
		moves.addAll(enemies.stream().map(enemy -> enemy.POS).filter(pos ->
				// enemy in range of attack and open square next to the position
				Pathfinding.shortestPath(this, p, pos).size() < range
						&& moves.stream().anyMatch(
								pos2 -> pos2.distanceTo(pos) == 1)
		).collect(Collectors.toList()));
		
		// add stairs
		if (Pathfinding.shortestPath(this, p, stairs.POS).size() < range
				&& moves.stream().anyMatch(
						pos -> pos.distanceTo(stairs.POS) == 1)) {
			moves.add(stairs.POS);
		}
		
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
		for (Player player : players) {
			// enemy in range of attack and open square next to the position
			if (Pathfinding.shortestPath(this, p, player.POS).size() <= range
					&& moves.stream().anyMatch(p2 -> p2.distanceTo(player.POS) == 1)) {
				moves.add(player.POS);
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
	 * @return A Turn representing the action taken,
	 * or null if no changes occurred.
	 */
	public Turn processAction(Position p1, Position p2) {
		// invalid
		if (p1 == null || p2 == null) {
			return null;
		}
		// destination is the entity, ignore
		else if (p1.equals(p2)) {
			return null;
		}
		
		Entity entity1 = entities[p1.x][p1.y];
		Entity entity2 = entities[p2.x][p2.y];
		
		// no character selected, ignore
		if (!(entity1 instanceof Player)) {
			return null;
		}
		
		Player player = (Player) entity1;
		Turn turn = new Turn();
		turn.start = p1;
		
		// not a possible move, ignore
		if (!possibleMovesForCharacter(p1).contains(p2)) {
			return null;
		}
		// move to stairs
		else if (entity2 instanceof Stairs) {
			// refresh map
			populateGrid();
			turn.end = p2;
		}
		// destination is an enemy
		else if (entity2 instanceof Enemy) {
			// not close enough
			if (p1.distanceTo(p2) != 1) {
				// move character next to other entity
				Position toMove = new Position(1000, 1000);
				for (Position pos : possibleMovesForCharacter(p1)) {
					if (pos.distanceTo(p2) == 1 && entities[pos.x][pos.y] == null
							&& pos.distanceTo(p1) < toMove.distanceTo(p1)) {
						toMove = pos;
					}
				}
				entities[toMove.x][toMove.y] = player;
				entities[p1.x][p1.y] = null;
				player.setPOS(toMove);
			}
			
			// ask player to attack enemy
			Enemy enemy = (Enemy) entity2;
			turn.attackPos = enemy.POS;
			player.attack(enemy);
			
			// killed enemy, remove
			if (enemy.HP <= 0) {
				enemies.remove(enemy);
				entities[p2.x][p2.y] = null;
				System.out.println("You defeated the enemy!");
			}
		}
		// destination is an empty space
		else {
			// move character
			entities[p2.x][p2.y] = player;
			entities[p1.x][p1.y] = null;
			player.setPOS(p2);
		}
		
		// action successfully completed, finish
		updateVisibility();
		
		turn.end = player.POS;
		turn.pathfind(this);
		return turn;
	}
	
	/**
	 * Ends Player actions and processes Enemy Turns.
	 * @return A List of actions that were taken by enemies.
	 */
	public List<Turn> endTurn() {
		return processEnemyMoves();
	}
	
	/**
	 * Processes moves for all enemies on the Map.
	 * @return A List of actions that were taken by enemies.
	 */
	private List<Turn> processEnemyMoves() {
		if (enemies.isEmpty()) {
			return new ArrayList<>();
		}
		
		List<Turn> turns = new ArrayList<>();
		
		for (Enemy enemy : enemies) {
			Turn turn = new Turn();
			
			Position p1 = enemy.POS;
			turn.start = p1;
			
			// ask enemy for move
			Position p2 = enemy.makeMove(this);
			
			// invalid move
			if (p2 == null || p1.equals(p2)
					|| !possibleMovesForEnemy(p1).contains(p2)) {
				continue;
			}
			
			// empty space
			if (entities[p2.x][p2.y] == null) {
				entities[p2.x][p2.y] = enemy;
				entities[p1.x][p1.y] = null;
				enemy.setPOS(p2);
			}
			else if (entities[p2.x][p2.y] instanceof Player) {
				if (p1.distanceTo(p2) != 1) {
					// move enemy next to Player
					Position toMove = Position.NONE;
					for (Position pos : possibleMovesForEnemy(p1)) {
						if (pos.distanceTo(p2) == 1 && entities[pos.x][pos.y] == null
								&& pos.distanceTo(p1) < toMove.distanceTo(p1)) {
							toMove = pos;
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
				turn.attackPos = player.POS;
				enemy.attack(player);
				
				// game over (?)
				if (player.HP <= 0) {
					entities[p2.x][p2.y] = null;
					System.out.println("You died.\nGame over!");
					// TODO: do something after game over
				}
			}
			
			turn.end = enemy.POS;
			turn.pathfind(this);
			turns.add(turn);
		}
		
		return turns;
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
					if (line.charAt(x) == ' ') { // empty space
						entities[x][y] = null;
					}
					else if (line.charAt(x) == 'W') { // wall
						entities[x][y] = newWall(new Position(x, y));
					}
					else if (line.charAt(x) == 'C') { // some character
						entities[x][y] = newPlayer(new Position(x, y));
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
	
	/** Function to create stairs. */
    private Stairs newStairs(Position position) {
    	Stairs stairs = new Stairs();
    	stairs.setPOS(position);
    	return stairs;
    }
	
	/** Function to create a wall. */
	private Obstacle newWall(Position position) {
		Obstacle obstacle = new Obstacle();
		obstacle.setPOS(position);
		return obstacle;
	}
	
	/** Function to return a player. */
	private Player newPlayer(Position position) {
		Player player = Player.randomPlayer();
		player.setPOS(position);
		return player;
	}
	
	/** Function to return an enemy. */
	private Enemy newEnemy(Position position) {
		Enemy enemy = Enemy.randomEnemy(players.get(0));
		enemy.setPOS(position);
		return enemy;
	}
	
	/**
	 * @param p The Position to check.
	 * @return True if the Position is on the Map, false otherwise.
	 */
	public boolean positionOnMap(Position p) {
		return p.x >= 0 && p.x < getWidth()
				&& p.y >= 0 && p.y < getHeight();
	}
	
	@Override
	public boolean validPosition(Position p) {
		if (!positionOnMap(p)) {
			return false;
		}
		
		Entity entity = entities[p.x][p.y];
		return entity == null || entity instanceof Player;
	}
	
	@Override
	public boolean transparentPosition(Position p) {
		if (!positionOnMap(p)) {
			return false;
		}
		
		return !(entities[p.x][p.y] instanceof Obstacle);
	}
    
    /** Types of Maps. */
	enum Type {
	    CAVE, DUNGEON, TOWER
    }
}

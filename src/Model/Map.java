package Model;

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
	// Private variables
	
	/** The characters and walls held by the map. */
	private Entity[][] entities;
	
	/**
	 * A grid of the visibility of positions on the Map.
	 * Each tile contains a double from 0.0 to 1.0, where
	 * 0.0 represents a non-visible tile, and
	 * 1.0 represents a fully-visible tile.
	 */
	private double[][] visibility;
	
	/** A list of the players on the map. */
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
	
	/** The current floor number. Affects map generation. */
	private int floor;
	
	// TODO: remove temporary set in favor of Entity.stamina
	private Set<Position> moved = new HashSet<>();
	
	// Static variables
	
	/**
	 * A log of the twenty most recent events.
	 * Stored in a Deque for better insertion/removal times,
	 * but is returned as a chronological List in {@code getLog()}.
	 */
	private static Deque<String> log = new ArrayDeque<>();
    
    // Constructors
    
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
		// create lists
		entities = new Entity[x][y];
		visibility = new double[x][y];
		players = new ArrayList<>();
		enemies = new ArrayList<>();
		
		// set var defaults
		floor = 0;
		type = Type.TOWER;
		
		// create players
		players.add(Player.randomPlayer());
		players.add(Player.randomPlayer());
		players.add(Player.randomPlayer());
	}
	
	// Static public API
	
	/** @return The List of recent messages. */
	static public List<String> getLog() {
		return new ArrayList<>(log);
	}
	
	/**
	 * Adds a message to the log list.
	 * Although this method is not privacy-secure,
	 * the Map does not use the log for anything
	 * other than sending messages to display.
	 * @param message The message to add.
	 */
	static public void logMessage(String message) {
		// limit to 20 messages
		while (log.size() >= 20) {
			log.removeLast();
		}
		
		log.addFirst(message);
		
		// TODO: remove debug
		System.out.println(message);
	}
	
	// Public API
	
	/** Types of Maps. */
	public enum Type {
		CAVE, DUNGEON, TOWER
	}
	
	/** @return The type of the Map. */
	public Type getType() {
		return type;
	}
	
	/** @return A copy of the entities represented by this Map. */
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
	
	/** @return A copy of the visibility of the Map. */
	public double[][] getVisibility() {
		double[][] copy = new double[getWidth()][getHeight()];
		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				copy[x][y] = visibility[x][y];
			}
		}
		return copy;
	}
	
	/** @return A copy of the Players on the Map. */
	public List<Player> getPlayers() {
		List<Player> copy = new ArrayList<>();
		for (Player player : players) {
			copy.add((Player) player.copy());
		}
		return copy;
	}
	
	/** @return A copy of the Enemies on the Map. */
	public List<Enemy> getEnemies() {
		List<Enemy> copy = new ArrayList<>();
		for (Enemy enemy : enemies) {
			copy.add((Enemy) enemy.copy());
		}
		return copy;
	}
	
	/** @return The floor number. */
	public int getFloor() {
		return floor;
	}
	
	/** @return The width, x-length of the map. */
	public int getWidth() {
		return entities.length;
	}
	
	/** @return The height, y-length of the map. */
	public int getHeight() {
		return entities.length == 0 ? 0 : entities[0].length;
	}
	
	/**
	 * @param p The Position to check.
	 * @return True if the Position is on the Map, false otherwise.
	 */
	public boolean positionOnMap(Position p) {
		return p.x >= 0 && p.x < getWidth()
				&& p.y >= 0 && p.y < getHeight();
	}
	
	// Public functions
	
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
				double opacity = -Math.pow(pos.distanceTo(pos2) / 7.0, 2) + 1.1;
				if (opacity > visibility[pos2.x][pos2.y]) {
					visibility[pos2.x][pos2.y] = Math.min(1, opacity);
				}
			}
		}
	}
	
	/** Increments the floor number and recreates the Map. */
	public void nextFloor() {
		floor += 1;
		visibility = new double[getWidth()][getHeight()];
		moved.clear();
		
		// different types based on floor
		if (floor <= 10) {
			type = Type.TOWER;
			entities = MapGenerator.generateCircle(getWidth(), getHeight());
		}
		else if (floor <= 20) {
			type = Type.CAVE;
			entities = MapGenerator.generateCave(getWidth(), getHeight());
		}
		else if (floor <= 30) {
			type = Type.DUNGEON;
			entities = MapGenerator.generateDungeon(getWidth(), getHeight());
		}
		else {
			entities = new Entity[getWidth()][getHeight()];
		}
		
		// TODO: remove
		entities = MapGenerator.randomMap(getWidth(), getHeight());
		
		MapGenerator.placePlayers(entities, players);
		stairs = MapGenerator.placeStairs(entities, players);
		enemies = MapGenerator.placeEnemies(entities, players);
		
		updateVisibility();
	}
    
    /** Deprecated. */
	public void populateGrid() {
		nextFloor();
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
		
		// TODO: replace with stamina checks
		if (moved.contains(p)) { // already moved
			return new HashSet<>();
		}
		
		// get moves and range
		Set<Position> moves = possibleMovesForEntity(p);
		int range = entities[p.x][p.y].SPD;
		
		// add enemy attacks
		moves.addAll(enemies.stream().map(enemy -> enemy.POS).filter(pos ->
				// enemy in range of attack and open square next to the position
				Pathfinding.shortestPath(this, p, pos).size() < range
						&& moves.stream().anyMatch(pos2 -> pos2.distanceTo(pos) == 1)
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
		moves.addAll(players.stream().map(player -> player.POS).filter(pos ->
				Pathfinding.shortestPath(this, p, pos).size() < range
						&& moves.stream().anyMatch(pos2 -> pos2.distanceTo(pos) == 1)
		).collect(Collectors.toList()));
		
		return moves;
	}
	
	// Interaction functions
	
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
		// not a possible move, ignore
		else if (!possibleMovesForCharacter(p1).contains(p2)) {
			return null;
		}
		
		Player player = (Player) entity1;
		Turn turn = new Turn();
		turn.start = p1;
		
		// destination is an empty space
		if (entity2 == null) {
			// move character
			entities[p2.x][p2.y] = player;
			entities[p1.x][p1.y] = null;
			player.setPOS(p2);
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
				logMessage("You defeated the enemy!");
			}
		}
		else {
			return null;
		}
		
		// action successfully completed, finish
		updateVisibility();
		
		moved.add(player.POS);
		turn.end = player.POS;
		turn.pathfind(this);
		return turn;
	}
	
	/**
	 * Ends Player actions and processes Enemy Turns.
	 * @return A List of actions that were taken by enemies.
	 */
	public List<Turn> endTurn() {
		moved.clear();
		
		return processEnemyMoves();
	}
	
	// Private functions
	
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
					players.remove(player);
					entities[p2.x][p2.y] = null;
					logMessage("A character has died.");
					// TODO: do something after game over
				}
			}
			else {
				// some other object: ignore
				continue;
			}
			
			turn.end = enemy.POS;
			turn.pathfind(this);
			turns.add(turn);
		}
		
		return turns;
	}
	
	// Interface implementation methods
	
	@Override
	public boolean validPosition(Position p) {
		if (!positionOnMap(p)) {
			return false;
		}
		
		return entities[p.x][p.y] == null;
	}
	
	@Override
	public boolean transparentPosition(Position p) {
		if (!positionOnMap(p)) {
			return false;
		}
		
		return !(entities[p.x][p.y] instanceof Obstacle);
	}
}

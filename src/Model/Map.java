package Model;

import java.util.*;
import java.util.function.Consumer;
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
	
	/** The method to call when messages are logged by Map. */
	static public Consumer<String> logHandler;
	
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
		
		// inform handler
		if (logHandler != null) {
			logHandler.accept(message);
		}
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
		return Arrays.stream(entities).map(arr ->
				Arrays.stream(arr).map(entity -> entity == null ? null : entity.copy())
						.toArray(Entity[]::new))
				.toArray(Entity[][]::new);
	}
	
	/** @return A copy of the visibility of the Map. */
	public double[][] getVisibility() {
		return Arrays.stream(visibility)
				.map(arr -> Arrays.copyOf(arr, getHeight()))
				.toArray(double[][]::new);
	}
	
	/** @return A copy of the Players on the Map. */
	public List<Player> getPlayers() {
		return players.stream().map(player -> (Player) player.copy())
				.collect(Collectors.toList());
	}
	
	/** @return A copy of the Enemies on the Map. */
	public List<Enemy> getEnemies() {
		return enemies.stream().map(enemy -> (Enemy) enemy.copy())
				.collect(Collectors.toList());
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
		// go through each player position
		players.stream().map(Player::getPOS).forEach(pos -> {
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
		});
	}
	
	/** Increments the floor number and recreates the Map. */
	public void nextFloor() {
		floor += 1;
		
		// reset variables
		visibility = new double[getWidth()][getHeight()];
		players.forEach(player -> player.setSTM(player.getSPD()));
		
		// different types based on floor
		if (floor <= 3) {
			type = Type.TOWER;
			entities = MapGenerator.generateCircle(getWidth(), getHeight());
		}
		else if (floor <= 6) {
			type = Type.CAVE;
			entities = MapGenerator.generateCave(getWidth(), getHeight());
		}
		else if (floor <= 9) {
			type = Type.DUNGEON;
			entities = MapGenerator.generateDungeon(getWidth(), getHeight());
		}
		else {
			type = Type.DUNGEON;
			entities = MapGenerator.generateBossRoom(getWidth(), getHeight());
		}
		
		MapGenerator.placePlayers(entities, players);
		stairs = MapGenerator.placeStairs(entities, players);
		enemies = MapGenerator.placeEnemies(entities, players, floor);
		
		updateVisibility();
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
		int range = ((Player) entities[p.x][p.y]).getSTM();
		if (range == 0) {
			return new HashSet<>();
		}
		Set<Position> moves = possibleMovesForEntity(p, range);
		
		// add enemy attacks
		moves.addAll(enemies.stream().map(Enemy::getPOS).filter(pos ->
				// enemy in range of attack and open square next to the position
				Pathfinding.shortestPath(this, p, pos).size() < range
						&& moves.stream().anyMatch(pos2 -> pos2.distanceTo(pos) == 1)
		).collect(Collectors.toList()));
		
		// add stairs
		if (Pathfinding.shortestPath(this, p, stairs.getPOS()).size() < range
				&& moves.stream().anyMatch(
						pos -> pos.distanceTo(stairs.getPOS()) == 1)) {
			moves.add(stairs.getPOS());
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
		int range = entities[p.x][p.y].getSPD();
		Set<Position> moves = possibleMovesForEntity(p, range);
		
		// add player attacks
		moves.addAll(players.stream().map(Player::getPOS).filter(pos ->
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
			turn.end = p2;
			turn.pathfind(this);
			logMessage("Advanced to floor " + (floor + 1) + ".");
			nextFloor();
			return turn;
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
			turn.attackPos = enemy.getPOS();
			double startHP = enemy.getHP();
			player.attack(enemy);
			turn.damage = (int) (startHP - enemy.getHP());
			
			// killed enemy, remove
			if (enemy.getHP() <= 0) {
				enemies.remove(enemy);
				entities[p2.x][p2.y] = null;
			}
		}
		else {
			return null;
		}
		
		// action successfully completed, finish
		updateVisibility();
		
		turn.end = player.getPOS();
		turn.pathfind(this);
		
		// update player stamina
		if (turn.attackPos != null) {
			player.setSTM(0);
		}
		else {
			player.setSTM(player.getSTM() - turn.path.size() - 1);
		}
		
		return turn;
	}
	
	/**
	 * Ends Player actions and processes Enemy Turns.
	 * @return A List of actions that were taken by enemies.
	 */
	public List<Turn> endTurn() {
		// reset player stamina
		players.forEach(player -> player.setSTM(player.getSPD()));
		
		return processEnemyMoves();
	}
	
	// Private functions
	
	/**
	 * @param p The Position of the Entity.
	 * @return A Set of Positions that the Entity can move to.
	 */
	private Set<Position> possibleMovesForEntity(Position p, int range) {
		if (entities[p.x][p.y] == null || range <= 0) {
			return new HashSet<>();
		}
		
		return Pathfinding.movementForPosition(this, p, range);
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
			
			Position p1 = enemy.getPOS();
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
				turn.attackPos = player.getPOS();
				double startHP = player.getHP();
				enemy.attack(player);
				turn.damage = (int) (startHP - player.getHP());
				
				// rip player
				if (player.getHP() <= 0) {
					players.remove(player);
					entities[p2.x][p2.y] = null;
					logMessage("A character has died.");
				}
				
				if (players.isEmpty()) {
					logMessage("Game over!");
				}
			}
			else {
				// some other object: ignore
				continue;
			}
			
			turn.end = enemy.getPOS();
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

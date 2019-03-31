package Model;

import java.util.*;

public class Enemy extends Entity {
	private String type;
	public Enemy(double HP, double ATK, double DEF, int SPD, Position POS, int LVL, String type) {
		super(HP, ATK, DEF, SPD, POS, LVL);
		this.type = type;
	}
	
	/** Takes the floor level of the dungeon and generates a random enemy based on it **/
	public static Enemy randomEnemy(int Floor) {
		int multiplier = Floor;
		Random rand = new Random();
		double HP = (rand.nextInt(9) + 3) * multiplier;
		double ATK = (rand.nextInt(9) + 1) * multiplier;
		int SPD = rand.nextInt(3) + 1;
		double DEF = (rand.nextInt(9) + 1) * multiplier;
		Enemy enemy = new Enemy(HP, ATK, DEF, SPD, null, 1, "Placeholder");
		return enemy;
	}
	
	/** Takes a map, and returns a position for the enemy to move to. **/
	public Position makeMove(Map map) {
		// retrieve own moves from map
		Set<Position> moves = map.possibleMovesForEnemy(getPOS());
		if (moves.isEmpty()) {
			return getPOS();
		}
		
		// find closest visible player in map
		Position toAttack = map.getPlayers().stream().map(Entity::getPOS)
				.filter(pos -> Pathfinding.lineOfSight(map, pos, getPOS()) && pos.distanceTo(getPOS()) < 7)
				.min(Comparator.comparingInt(getPOS()::distanceTo))
				.orElse(null);
		
		// no player found: return random move in range
		if (toAttack == null) {
			return new ArrayList<>(moves).get(new Random().nextInt(moves.size()));
		}
		// attack player in range
		else if (moves.contains(toAttack)) {
			return toAttack;
		}
		// path towards closest position to player
		else {
			return Pathfinding.shortestPath(map, getPOS(), toAttack).stream()
					.reduce((pos1, pos2) -> moves.contains(pos2) ? pos2 : pos1).orElse(getPOS());
		}
	}
	
	/** Attacks a player, and subtracts HP from them based on the Enemy's attack and the Player's defense. **/
	public void attack(Player Player) {
		double ATK = this.getATK();
		double damage = (ATK * 10)/(Player.getDEF() + 5);
		Player.setHP(Player.getHP() - damage);
		
		Map.logMessage("Took " + (int) damage + " damage!");
   }
	
	@Override
	public Entity copy() {
		Enemy Enemy = new Enemy(getmaxHP(), getATK(), getDEF(), getSPD(), getPOS(), getLVL(), "Placeholder");
		Enemy.setHP(getHP());
		return Enemy;
	}
}

package Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Enemy extends Entity {
	private String type;
	public Enemy(double HP, double ATK, double DEF, int SPD, Position POS, int LVL, double EXP, String type) {
		super(HP, ATK, DEF, SPD, POS, LVL);
	}
	/** Takes the floor level of the dungeon and generates a random enemy based on it **/
	public static Enemy randomEnemy(int Floor) {
		int multiplier = Floor;
		Random rand = new Random();
		double HP = (rand.nextInt(9) + 3) * multiplier;
		double ATK = (rand.nextInt(9) + 1) * multiplier;
		int SPD = rand.nextInt(3) + 1;
		double DEF = (rand.nextInt(9) + 1) * multiplier;
		Enemy enemy = new Enemy(HP, ATK, DEF, SPD, null, 1, 0, "Placeholder");
		return enemy;
	}
	
	/** Takes a map, and returns a position for the enemy to move to. **/
	public Position makeMove(Map map) {
		Set<Position> moves = map.possibleMovesForEnemy(getPOS());
		if (moves.isEmpty()) {
			return getPOS();
		}
		
		// check through players in map
		for (Player player : map.getPlayers()) {
			// player in range and in line of sight
			if (Pathfinding.shortestPath(map, getPOS(), player.getPOS()).size() < 6
					&& Pathfinding.lineOfSight(map, getPOS(), player.getPOS())) {
				// attack if in range
				if (moves.contains(player.getPOS())) {
					return player.getPOS();
				}
				// path towards player
				List<Position> path = Pathfinding.shortestPath(map, getPOS(), player.getPOS());
				Collections.reverse(path);
				for (Position pos : path) {
					if (moves.contains(pos)) {
						return pos;
					}
				}
			}
		}
		
		// no player found: return random move in range
		return new ArrayList<>(moves).get(new Random().nextInt(moves.size()));
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
		Enemy Enemy = new Enemy(getmaxHP(), getATK(), getDEF(), getSPD(), getPOS(), getLVL(), 0, "Placeholder");
		Enemy.setHP(getHP());
		return Enemy;
	}
}

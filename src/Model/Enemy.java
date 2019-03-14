package Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Enemy extends Entity {
	public Enemy(double HP, double EVS, double ATK, double DEF, int SPD, Position POS, int LVL, double EXP) {
		super(HP, EVS, ATK, DEF, SPD, POS, LVL);
	}
	/** Takes a players level and generates a random enemy based on it **/
	public static Enemy randomEnemy(Player p) {
		int multiplier = p.getLVL();
		Random rand = new Random();
		double HP = (rand.nextInt(9) + 3) * multiplier;
		double EVS = (rand.nextInt(9) + 1) * multiplier;
		double ATK = (rand.nextInt(9) + 1) * multiplier;
		int SPD = rand.nextInt(3) + 1;
		double DEF = (rand.nextInt(9) + 1) * multiplier;
		Enemy enemy = new Enemy(HP, EVS, ATK, DEF, SPD, null, 1, 0);
		return enemy;
	}
	
	/** Takes a map, and returns a position for the enemy to move to. **/
	public Position makeMove(Map map) {
		Set<Position> moves = map.possibleMovesForEnemy(POS);
		if (moves.isEmpty()) {
			return POS;
		}
		
		// check through players in map
		for (Player player : map.getPlayers()) {
			// player in range and in line of sight
			if (Pathfinding.shortestPath(map, POS, player.POS).size() < 6
					&& Pathfinding.lineOfSight(map, POS, player.POS)) {
				// attack if in range
				if (moves.contains(player.POS)) {
					return player.POS;
				}
				// path towards player
				List<Position> path = Pathfinding.shortestPath(map, POS, player.POS);
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
	public void attack(Player p) {
		double ATK = this.ATK;
		double damage = (ATK * 10)/(p.DEF + 5);
		p.setHP(p.HP - damage);
		
		System.out.println("Took " + damage + " damage!");
   }
	
	@Override
	public Entity copy() {
		Enemy e = new Enemy(maxHP, EVS, ATK, DEF, SPD, POS, LVL, 0);
		e.HP = HP;
		return e;
	}
}

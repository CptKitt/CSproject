package Model;

import java.util.Random;

public class Enemy extends Entity {
	public Enemy(double HP, double EVS, double ATK, double DEF, int SPD, Position POS, int LVL, double EXP) {
		super(HP, EVS, ATK, DEF, SPD, POS, LVL);
	}
	/** Takes a players level and generates a random enemy based on it **/
	public static Enemy randomEnemy(Player p) {
		int multiplier = p.getLVL();
		Random rand = new Random();
		double HP = rand.nextInt(10) * multiplier;
		double EVS = rand.nextInt(10) * multiplier;
		double ATK = rand.nextInt(10) * multiplier;
		int SPD = rand.nextInt(3);
		double DEF = rand.nextInt(10) * multiplier;
		Enemy enemy = new Enemy(HP, EVS, ATK, DEF, SPD, null, 0, 0);
		return enemy;
	}
	/** Takes a map, and returns a position for the enemy to move to. **/
	public Position makeMove(Map map) {
		return null;
	}
	/** Attacks a player, and subtracts HP from them based on the Enemy's attack and the Player's defense. **/
	public void attack(Player p) {
		double ATK = this.ATK;
		double Multiplier = this.LVL / 2;
		double damage = ATK * Multiplier - (p.DEF / 2);
		p.setHP(p.HP - damage);
   }
}

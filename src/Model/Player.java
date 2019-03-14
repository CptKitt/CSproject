package Model;
import java.util.Random;

public class Player extends Entity {
	protected double EXP;
	private double LVLlimit = this.LVL * 150; 
	public Player(double HP, double EVS, double ATK, double DEF, int SPD, Position POS, int LVL, double EXP, double LVLlimit) {
		super(HP, EVS, ATK, DEF, SPD, POS, LVL);
		this.EXP = EXP;
		this.LVLlimit = LVLlimit;
	}
	/** Generate a new player with random stats ranging between 1-10 **/
	public static Player randomPlayer() {
		Random rand = new Random();
		double HP = rand.nextInt(10);
		double EVS = rand.nextInt(10);
		double ATK = rand.nextInt(10);
		int SPD = rand.nextInt(2) + 4;
		double DEF = rand.nextInt(10);
		Player player = new Player(HP, EVS, ATK, DEF, SPD, null, 1, 0, 150);
		return player;
	}
	/** Return the player's level as an integer. **/
	public int getLVL() {
		return this.LVL;
	}
	/** Set EXP to an amount, given as a double. **/
	public void setEXP(double EXP) {
		this.EXP = EXP;
	}
	/** Add EXP to the EXP pool of the player. **/
	public void addEXP(double EXP) {
		this.EXP += EXP;
	}
	/** Level up character by 1
	 * and increase their stats based on level.
	 * @param EXP
	 **/
	public void LVLup(double EXP) {
			this.LVL += 1;
			this.HP = (this.HP/ (this.LVL-1)) * this.LVL;
			this.ATK = (this.ATK/ (this.LVL-1)) * this.LVL;
			this.DEF = (this.DEF/ (this.LVL-1)) * this.LVL;
			this.HP = (this.HP/ (this.LVL-1)) * this.LVL;
			this.EVS = (this.EVS/ (this.LVL-1)) * this.LVL;
		}
	/** Attack an enemy, dealing damage to HP depending on the player's 
	 * attack stat and the enemy's defense stat, and add EXP if the enemy is killed.
	 * Also determine if the player will level up based on their EXP. **/
	public void attack(Enemy e) {
		double ATK = this.ATK;
		double Multiplier = this.LVL / 2;
		double damage = ATK * Multiplier - (e.DEF / 2);
		e.setHP(e.HP - damage);
		if((e.HP - damage) < 0) {
			this.addEXP(e.LVL * 50);
		}
		if(this.EXP > this.LVLlimit) {
			this.LVLup(this.EXP);
		}
		
		System.out.println("Dealt " + damage + " damage!");
	}
	
	@Override
	public Entity copy() {
		Player p = new Player(maxHP, EVS, ATK, DEF, SPD, POS, LVL, EXP, LVLlimit);
		p.HP = HP;
		return p;
	}
}
	


package Model;
import java.util.Random;

/**
 * Player class inherits from entity class.
 * Defines an entity that the user can control on a map.
 */
public class Player extends Entity {
	//Defines extra stats for a player entity
	private double EXP;
	private double LVLlimit = 150; 
	private int STM;
	private String name;

	/**
	 * Player contructor for setting all stat values
	 */
	public Player(double HP, double ATK, double DEF, int SPD, Position POS, int LVL, double EXP, double LVLlimit, int STM, String name) {
		super(HP, ATK, DEF, SPD, POS, LVL);
		this.EXP = EXP;
		this.LVLlimit = LVLlimit;
		this.STM = SPD;
	}
	/** 
	 * Generate a new player with random stats ranging between 1-10 
	 */
	public static Player randomPlayer() {
		Random rand = new Random();
		double HP = (rand.nextInt(10) + 10);
		double ATK = (rand.nextInt(5) + 5);
		int SPD = rand.nextInt(2) + 4;
		double DEF = (rand.nextInt(9) + 1);

		Player player = new Player(HP, ATK, DEF, SPD, null, 1, 0, 150, SPD, "Placeholder");
		return player;
	}
	/**
	 * Set stamina to an amount, given as an integer. 
	 */
	public void setSTM(int STM) {
		this.STM = STM;
	}
	/**
	 * Return stamina of player as an integer.
	 */
	public int getSTM() {
		return STM;
	}
	/** 
	 * Add EXP to the EXP pool of the player, and levels them up
	 * if their EXP surpasses their EXP limit for their current level.
	 */
	public void addEXP(double EXP) {
		this.EXP += EXP;
		if(this.EXP >= this.LVLlimit) {
			this.LVLup(this.EXP);
		}
	}
	/** 
	 * Level up character by 1
	 * and increase their stats based on level.
	 * @param EXP
	 */
	public void LVLup(double EXP) {
			this.setLVL(this.getLVL() + 1);
			this.setHP((this.getmaxHP()/ (this.getLVL()-1)) * this.getLVL());
			this.setATK((this.getATK()/ (this.getLVL() -1)) * this.getLVL());
			this.setDEF((this.getDEF()/ (this.getLVL() - 1)) * this.getLVL());
			this.setmaxHP((this.getHP()/ (this.getLVL()-1)) * this.getLVL());
			this.LVLlimit = this.getLVL() * 150;
			System.out.println("Level up! You are now level " + this.getLVL() + ".");
		}
	/**
	 * Attack an enemy, dealing damage to HP depending on the player's 
	 * attack stat and the enemy's defense stat, and add EXP if the enemy is killed.
	 */
	public void attack(Enemy e) {
		Random rand = new Random();
		double ATK = this.getATK();
		double damage = (ATK * 10)/(e.getDEF() + 5);
		e.setHP(e.getHP() - damage);
		if((e.getHP() - damage) < 0) {
			int EXPgained = rand.nextInt(21) + 30;
			this.addEXP(EXPgained);
			Map.logMessage("Player gained " + EXPgained + " experience points!");
		}
		Map.logMessage("Dealt " + (int) damage + " damage!");
	
	}
	
	/**
	 * copy() method type casts this Player object into the parent Entity class
	 * Parameters: none
	 * Returns: An Entity object containing the same info as Player
	 */
	@Override
	public Entity copy() {
		Player Player = new Player(getHP(), getATK(), getDEF(), getSPD(), getPOS(), getLVL(), EXP, LVLlimit, STM, "Placeholder");
		Player.setmaxHP(getHP());
		Player.setSTM(getSTM());
		return Player;
	}
}
	


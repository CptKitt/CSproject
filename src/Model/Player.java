package Model;
import java.util.Random;

public class Player extends Entity {
	private double EXP;
	private double LVLlimit = 150; 
	private int STM;
	private String name;
	public Player(double HP, double ATK, double DEF, int SPD, Position POS, int LVL, double EXP, double LVLlimit, int STM, String name) {
		super(HP, ATK, DEF, SPD, POS, LVL);
		this.EXP = EXP;
		this.LVLlimit = LVLlimit;
		this.STM = SPD;
	}
	/** Generate a new player with random stats ranging between 1-10 **/
	public static Player randomPlayer() {
		Random rand = new Random();
		double HP = (rand.nextInt(10) + 10);
		double ATK = (rand.nextInt(5) + 5);
		int SPD = rand.nextInt(2) + 4;
		double DEF = (rand.nextInt(9) + 1);

		Player player = new Player(HP, ATK, DEF, SPD, null, 1, 0, 150, SPD, "Placeholder");
		return player;
	}
	/** Set stamina to an amount, given as an integer. **/
	public void setSTM(int STM) {
		this.STM = STM;
	}
	/** Return stamina of player as an integer. **/
	public int getSETM() {
		return STM;
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
			this.setLVL(this.getLVL() + 1);
			this.setHP((this.getHP()/ (this.getLVL()-1)) * this.getLVL());
			this.setATK((this.getATK()/ (this.getLVL() -1)) * this.getLVL());
			this.setDEF((this.getDEF()/ (this.getLVL() - 1)) * this.getLVL());
			this.setmaxHP((this.getHP()/ (this.getLVL()-1)) * this.getLVL());
			this.LVLlimit = this.getLVL() * 150;
			System.out.println("Level up! You are now level " + this.getLVL() + ".");
		}
	/** Attack an enemy, dealing damage to HP depending on the player's 
	 * attack stat and the enemy's defense stat, and add EXP if the enemy is killed.
	 * Also determine if the player will level up based on their EXP. **/
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
		if(this.EXP >= this.LVLlimit) {
			this.LVLup(this.EXP);
		}
		Map.logMessage("Dealt " + (int) damage + " damage!");
	
	}
	
	@Override
	public Entity copy() {
		Player Player = new Player(getHP(), getATK(), getDEF(), getSPD(), getPOS(), getLVL(), EXP, LVLlimit, STM, "Placeholder");
		Player.setmaxHP(getHP());
		return Player;
	}
}
	


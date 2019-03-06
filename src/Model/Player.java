package Model;

public class Player extends Entity {
	public double EXP;
	public Player(double HP, double EVS, double ATK, double DEF, double SPD, Position POS, int LVL, double EXP) {
		super(HP, EVS, ATK, DEF, SPD, POS, LVL);
		this.EXP = EXP;
	}
	public void setEXP(double EXP) {
		this.EXP = EXP;
	}
	

	
	
	}
	


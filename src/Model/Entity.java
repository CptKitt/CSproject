package Model;

public abstract class Entity {	
	private double maxHP;
	private double HP;
	private double ATK;
	private double DEF;
	private int SPD;
	private int LVL;
	private Position POS;
	
	
	public Entity(double HP, double ATK, double DEF, int SPD, Position POS, int LVL) {
		this.maxHP = HP;
		this.HP = HP;
		this.ATK = ATK;
		this.DEF = DEF;
		this.SPD = SPD;
		this.POS = POS;
		this.LVL = LVL;
	}
	public void setHP(double HP) {
		this.HP = HP;
	}
	public void setATK(double ATK) {
		this.ATK = ATK;
	}
	public void setDEF(double DEF) {
		this.DEF = DEF;
	}
	public void setSPD(int SPD) {
		this.SPD = SPD;
	}
	public void setPOS(Position POS) {
		this.POS = POS;
	}
	public void setLVL(int LVL) {
		this.LVL = LVL;
	}
	public void setmaxHP(double maxHP) {
		this.maxHP = maxHP;
	}
	public Position getPOS() {
		return POS;
	}
	public double getATK() {
		return ATK;
	}
	public double getHP() {
		return HP;
	}
	public double getDEF() {
		return DEF;
	}
	public int getSPD() {
		return SPD;
	}
	public int getLVL() {
		return LVL;
	}
	public double getmaxHP() {
		return maxHP;
	}
	/**
	 * A polymorphic copy method for subclasses to override.
	 * This method must be preferred over a copy constructor,
	 * as the Map does not know which subclass an Entity is.
	 * @return A copy of this Entity.
	 */
	public abstract Entity copy();
}

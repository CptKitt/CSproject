package Model;

public class Entity {	
	public double maxHP;
	public double HP;
	public double ATK;
	public double DEF;
	public int SPD;
	public double EVS;
	public int LVL;
	public Position POS;
	
	public Entity(double HP, double EVS, double ATK, double DEF, int SPD, Position POS, int LVL) {
		this.maxHP = HP;
		this.HP = HP;
		this.EVS = EVS;
		this.ATK = ATK;
		this.DEF = DEF;
		this.SPD = SPD;
		this.POS = POS;
		this.LVL = LVL;
	}
	public void setHP(double HP) {
		this.HP = HP;
	}
	public void setEVS(double EVS) {
		this.EVS = EVS;
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
	
	/**
	 * A polymorphic copy method for subclasses to override.
	 * This method must be preferred over a copy constructor,
	 * as the Map does not know which subclass an Entity is.
	 * @return A copy of this Entity.
	 */
	public Entity copy() {
		Entity e = new Entity(maxHP, EVS, ATK, DEF, SPD, POS, LVL);
		e.HP = HP;
		return e;
	}
}

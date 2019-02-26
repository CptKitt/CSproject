package Model;

public class Entity {
	public double HP;
	public double ATK;
	public double DEF;
	public double SPD;
	public double EVS;
	public int LVL;
	public Position POS;
	
	public Entity(double HP, double EVS, double ATK, double DEF, double SPD, Position POS, int LVL) {
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
	public void setSPD(double SPD) {
		this.SPD = SPD;
	}
	public void setPOS(Position POS) {
		this.POS = POS;
	}
	public void setLVL(int LVL) {
		this.LVL = LVL;
	}
}

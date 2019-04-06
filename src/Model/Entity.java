package Model;

/**
 * Entity class describes a general (abstract) object that will exist within a playable map
 * Contains getter and setter methods for instance variables that act as characteristics and attributes that will apply to actions for each entity created.
 * Contains one abstract method that type casts child-classes into parent.
 */
public abstract class Entity {	
	private double maxHP;
	private double HP;
	private double ATK;
	private double DEF;
	private int SPD;
	private int LVL;
	private Position POS;
	
	/**
	 * Basic Constructor for manual setting of each stat in creation of an Entity object. 
	 */
	public Entity(double HP, double ATK, double DEF, int SPD, Position POS, int LVL) {
		this.maxHP = HP;
		this.HP = HP;
		this.ATK = ATK;
		this.DEF = DEF;
		this.SPD = SPD;
		this.POS = POS;
		this.LVL = LVL;
	}

	/**
	 * Setter method for setting Entity health
	 * @param HP:double
	 */
	public void setHP(double HP) {
		this.HP = HP;
	}

	/**
	 * Setter method for setting Entity attack
	 * @param ATK:double
	 */
	public void setATK(double ATK) {
		this.ATK = ATK;
	}

	/**
	 * Setter method for setting Entity defense
	 * @param DEF:double
	 */
	public void setDEF(double DEF) {
		this.DEF = DEF;
	}

	/**
	 * Setter method for setting Entity movement allowance
	 * @param SPD:int
	 */
	public void setSPD(int SPD) {
		this.SPD = SPD;
	}

	/**
	 * Setter method for setting Entity Position
	 * @param POS:Position
	 */
	public void setPOS(Position POS) {
		this.POS = POS;
	}

	/**
	 * Setter method for setting Entity level
	 * @param LVL:int
	 */
	public void setLVL(int LVL) {
		this.LVL = LVL;
	}

	/**
	 * Setter method for setting Entity maximum health points
	 * @param maxHP:double
	 */
	public void setmaxHP(double maxHP) {
		this.maxHP = maxHP;
	}

	/**
	 * Getter method for obtaining Entity Position info
	 * @return Position object
	 */
	public Position getPOS() {
		return POS;
	}

	/**
	 * Getter method for obtaining Entity attack value
	 * @return double
	 */
	public double getATK() {
		return ATK;
	}

	/**
	 * Getter method for obtaining Entity current health value
	 * @return double
	 */
	public double getHP() {
		return HP;
	}

	/**
	 * Getter method for obtaining Entity defense value
	 * @return double
	 */
	public double getDEF() {
		return DEF;
	}

	/**
	 * Getter method for obtaining Entity movement value
	 * @return int
	 */
	public int getSPD() {
		return SPD;
	}

	/**
	 * Getter method for obtaining Entity current level
	 * @return int
	 */
	public int getLVL() {
		return LVL;
	}

	/**
	 * Getter method for obtaining Entity maximum health points
	 * @return double
	 */
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

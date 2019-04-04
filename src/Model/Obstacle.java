package Model;

/**
 * Obstacle class inherits from the Entity class.
 * Is used for defining an entity that takes up its own space and may not be destructable.
 */
public class Obstacle extends Entity {

	/**
	 * An Obstacle contructor that allows the object to be initiated as desired
	 */
	public Obstacle(double HP, double ATK, double DEF, int SPD, Position POS, int LVL) {
		super(HP, ATK, DEF, SPD, POS, LVL);
	}
	
	/**
	 * Default constructor that sets an indestructable entity
	 */
	public Obstacle() {
		super(1000, 0, 1000, 0, null, 1);
	}
	
	/**
	 * copy() method allows an Obstacle object be class typed to the parent class Entity
	 * Parameters: none
	 * Returns: An Entity object containing the same info as the Obstacle
	 */
	@Override
	public Entity copy() {
		Obstacle Obstacle = new Obstacle();
		Obstacle.setHP(getHP());
		return Obstacle;
	}
}

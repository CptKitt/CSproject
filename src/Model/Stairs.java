package Model;

/**
 * Stairs class inherits from Entity.
 * Used to represent passable locales that will 
 * allow the User to progress to new or old floors.
 */
public class Stairs extends Entity{

  /**
   * Default settings when creating a stairway.
   */
  public Stairs() {
		super(1000, 0, 1000, 0, null, 1);
	}

  /**
   * copy() method that will type cast Stair object into parent Entity
   * no parameters
   * Returns an Entity class containing the same stats as current Stairs
   */
  @Override
  public Entity copy() {
		Stairs Stairs = new Stairs();
		Stairs.setHP(getHP());
		return Stairs;
	}
}

package Model;

public class Stairs extends Entity{
  public Stairs() {
		super(1000, 0, 1000, 0, null, 1);
	}
  @Override
  public Entity copy() {
		Stairs Stairs = new Stairs();
		Stairs.setHP(getHP());
		return Stairs;
	}
}

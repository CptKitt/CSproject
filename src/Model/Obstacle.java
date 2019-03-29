package Model;

public class Obstacle extends Entity {

	public Obstacle(double HP, double ATK, double DEF, int SPD, Position POS, int LVL) {
		super(HP, ATK, DEF, SPD, POS, LVL);
	}
	
	public Obstacle() {
		super(1000, 0, 1000, 0, null, 1);
	}
	
	@Override
	public Entity copy() {
		Obstacle Obstacle = new Obstacle();
		Obstacle.setHP(getHP());
		return Obstacle;
	}
}

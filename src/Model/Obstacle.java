package Model;

public class Obstacle extends Entity {

	public Obstacle(double HP, double EVS, double ATK, double DEF, int SPD, Position POS, int LVL) {
		super(HP, EVS, ATK, DEF, SPD, POS, LVL);
	}
	
	public Obstacle() {
		super(1000, 0, 0, 1000, 0, null, 1);
	}
	
	@Override
	public Entity copy() {
		Obstacle o = new Obstacle();
		o.HP = HP;
		return o;
	}
}

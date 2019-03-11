package Text;

import Model.*;

/**
*Executes the Interactive Text-Based Version of Group 9's game.
*Contain instance variable: map:Map.
*Contains methods: validInput(input:String):boolean, main(args:String[]):void.
*
*/
public class TextMain {
	static private Map map;

	public boolean validInput(String input) {
		return false;
	}

	public static void main(String[] args) {
		map = new Map(20, 10);
		map.populateGrid();

		Display display = new Display();

		UserInput input = new UserInput();

		while (true) {
			map.pathfind();
			display.printMap(map);
			Position move = input.movementInput();
			map.setStart(new Position(move));
		}
	}
}
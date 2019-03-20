package Text;

import Model.*;
import java.util.ArrayList;

/**
*Executes the Interactive Text-Based Version of Group 9's game.
*Contain instance variable: map:Map.
*Contains methods: validInput(input:String):boolean, main(args:String[]):void.
*
*/
public class TextMain {
	static private Map map;
	static 

	public static void main(String[] args) {

		for 
		map = new Map(10, 20);
		map.populateGrid();

		Display display = new Display();

		UserInput input = new UserInput();

		while (true) {
			display.printMap(map);
			Position move = input.movementInput();
			map.setStart(new Position(move));
		}
	}

	/**
	*Used to disguish user/player turn order, and methods that user may use
	*/
	public static void playerTurn() {

	}

	/**
	*Used to distinguis enemy turn and methods that enemyAI may use
	*/
	public static void computerTurn() {

	}
}
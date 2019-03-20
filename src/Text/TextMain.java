package Text;

import Model.*;
import java.util.ArrayList;

/**
*Executes the Interactive Text-Based Version of Group 9's game.
*Contain instance variable: map:Map.
*Contains methods: main(args:String[]):void.
*
*/
public class TextMain {
	private static Map map;
	private static String floor;

	public static void main(String[] args) {

		for (int fLevel = 0; fLevel < 4; fLevel++) {
			fLevel = 3;
			if (floorType(fLevel)) {
				for (int i = 0; i < 10; i++) {
					map = new Map(10, 20);
					map.populateGrid();

					Display display = new Display();

					UserInput input = new UserInput();

					while (true) {
						display.printMap(map);
						System.out.println("Floor Level: " + fLevel + i + " " + floor);
						Position move = input.movementInput();
						map.setStart(new Position(move));
					}
				}
			} else {
				System.out.println("Congratulations! You have traversed all floors, and Conquered the Map");
				break;
			}
		}
	}

	/**
	* Currently Looks for the type of Floor for each threshold
	*/
	public static boolean floorType(int level) {
		if (level == 0) {
			floor = "Dungeon";
			return true;
		} else if (level == 1) {
			floor = "Cave";
			return true;
		} else if (level == 2) {
			floor = "Tower";
			return true;
		} else {
			return false;
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
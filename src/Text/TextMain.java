package Text;

import Model.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
*Executes the Interactive Text-Based Version of Group 9's game.
*Contain instance variable: map:Map.
*Contains methods: main(args:String[]):void.
*
*/
public class TextMain {
	private static Map map;

	public static void main(String[] args) {
		map = new Map(10, 15);
		map.nextFloor();
		List<Player> playable = map.getPlayers();
		Display display = new Display();
		UserInput input = new UserInput();

		while (true) {
			display.printMap(map, new HashSet<>());
			for (Player userChar: playable) {
				Position move = input.moveInput();
				map.processAction(userChar.POS, move);
			}
			//map.endTurn()
		}
	}
}
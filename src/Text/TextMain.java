package Text;

import Model.*;
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
		
		map = new Map(10,15);
		Display display = new Display();
		List<Player> playable = map.getPlayers();
		UserInput input = new UserInput();

		while (true) {
			for (Player userChar: playable) {
				Entity player = userChar.copy();
				display.printMap(map);
				Position move = input.moveInput();
				map.processAction(player.POS, move);
			}
			map.endTurn();
		}
	}
}
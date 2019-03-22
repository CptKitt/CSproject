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
	private static Map map = new Map(10,15);
	private static Display display = new Display();
	private static UserInput input = new UserInput();
	private static List<Player> playable = map.getPlayers();

	public static void main(String[] args) {
		map.nextFloor();
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
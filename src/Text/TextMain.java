package Text;

import Model.*;
import java.util.HashSet;
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

				//Setting a player's position from map
				Entity[][] grid = map.getGrid();
				for (int i = 0; i < grid.length; i++) {
					for (int j = 0; j < grid[i].length; j++) {
						Entity entity = grid[i][j];
						if (entity instanceof Player) {
							if (entity.maxHP == userChar.maxHP && entity.HP == userChar.HP) {
								userChar.POS = new Position(i,j);
								i = grid.length;
								break;
							}
						}
					}
				}
				//Displaying the map indicating one character and prompting for input
				display.printMap(map, map.possibleMovesForCharacter(userChar.POS));
				System.out.println("Floor: " + map.getFloor() + " " + map.getType());
				Position move = input.moveInput();
				map.processAction(userChar.POS, move);
				if (move != null) {
					map.logMessage("Character moved to: " + move);
					
				} else {
					map.logMessage("Character has passed their move");
				}
				
			}
			playable = map.getPlayers();
			if (playable.isEmpty()) {
				System.out.println("");
				System.out.println("All playable characters have died. GAME OVER!");
				System.exit(0);
			}
			map.endTurn();
			System.out.println("Your turn has ended!");
			System.out.println("");
		}
	}
}
package Text;

import Model.*;
import java.util.List;

/**
* TextMain class executes the Interactive Text-Based Version of Group 9's game.
* Contain instance variable: map:Map, display:Display, input:UserInput, playable:List<Player>.
* Contains methods: main(args:String[]):void.
*/
public class TextMain {
	private static Map map = new Map(10,15);
	private static Display display = new Display();
	private static UserInput input = new UserInput();
	private static List<Player> playable = map.getPlayers();

	/**
	 * main() method is used to execute the implementation of UserInput, Display, and some logic of the project
	 */
	public static void main(String[] args) {
		//Instantiates the floor level for the game
		map.nextFloor();

		//Loop is used to maintain the game flow as long as the User wants or if the game is completed
		while (true) {
			//Setting a player's position from the map
			for (Player userChar: playable) {
				Entity[][] grid = map.getGrid();
				for (int i = 0; i < grid.length; i++) {
					for (int j = 0; j < grid[i].length; j++) {
						Entity entity = grid[i][j];
						if (entity instanceof Player) {
							if (entity.getmaxHP() == userChar.getmaxHP() && entity.getHP() == userChar.getHP()) {
								userChar.setPOS(new Position(i,j));
								i = grid.length;
								break;
							}
						}
					}
				}

				//Displaying the map indicating one character and prompting for input
				display.printMap(map, map.possibleMovesForCharacter(userChar.getPOS()));
				System.out.println("Floor: " + map.getFloor() + " " + map.getType());
				Position move = input.moveInput();
				map.processAction(userChar.getPOS(), move);

				//Saving messages into the game's log
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
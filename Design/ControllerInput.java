/**
*
*/

import java.util.Scanner;

public class ControllerInput {

	/**
	 *
	 */
	public void activeCharMovement() {
		int[] movement = new int[2]
		Scanner userInput = new Scanner(System.in);
		System.out.println("Please enter a position for the character to move to as an ordered pair (e.g. x,y):");
		String input = userInput.nextLine();
		if (input.length() != 3) {
			System.out.println("Please include the comma \",\" inbetween your x and y values");
			String input = userInput.nextLine();
		}
		int xCoord = Integer.parseInt(input.charAt(0));
		int yCoord = Integer.parseInt(input.charAt(2));
		movement[0] = xCoord;
		movement[1] = yCoord;
		return movement;
	}

	/**
	 *
	 */
	public boolean wrongInputType() {
		
	}

	/*
	 *
	 */
	public boolean wrongCharacterName(String name) {
		for (Character charName : charTeam) {
			if (name.equals(charName.getName()) {
				return true;
			}
		}
		return false;
	}
}
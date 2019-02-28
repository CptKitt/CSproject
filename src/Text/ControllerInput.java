package Text; /**
*
*/

import java.util.Scanner;

public class ControllerInput {
	

	/**
	 *
	 */
	public int[] charMovementInput() {
		int[] movementInput = new int[2];
		Scanner userInput = new Scanner(System.in);
		System.out.println("Please enter a position to move a character as an ordered pair, with a comma inbetween (e.g. x,y):");
		String input = userInput.nextLine();
		while (input.length() != 3) {
			System.out.println("Please include the comma \",\" inbetween your x and y values");
			input = userInput.nextLine();
		}
		int xCoord = Integer.parseInt(String.valueOf(input.charAt(0)));
		int yCoord = Integer.parseInt(String.valueOf(input.charAt(2)));
		movementInput[0] = xCoord;
		movementInput[1] = yCoord;
		return movementInput;
	}

	/**
	 *
	 */
	public boolean wrongInput(String input) {
		return false;
	}

}
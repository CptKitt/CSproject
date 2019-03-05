package Text; 

import Model.Position;
import java.util.Scanner;

/**
*
*/
public class UserInput {
	
	/**
	 *This method asks the User for a position on the map displayed in the
	 *Interactive Text-Based version in the form: x,y.
	 *Has no parameters, Returns a Position Object.
	 */
	public Position movementInput() {
		Scanner userInput = new Scanner(System.in);
		System.out.println("Please enter a position to move a character as an ordered pair, with a comma inbetween (e.g. x,y):");

		String input = userInput.nextLine();
		while (input.length() != 3) {
			System.out.println("Please include the comma \",\" inbetween your x and y values");
			input = userInput.nextLine();
		}

		int xCoord = Integer.parseInt(String.valueOf(input.charAt(0)));
		int yCoord = Integer.parseInt(String.valueOf(input.charAt(2)));
		Position inputPos = new Position(xCoord, yCoord);
		return inputPos;
	}

	/**
	*This main method is used for testing the compilation and functionality of methods within this class
	*/
	public static void main(String[] args) {
		UserInput input = new UserInput();
		Position test = new Position(input.movementInput());
		System.out.println(test);
	}
}
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
	 *@return a Position object containing coordinate information on where the user wants a character to move
	 */
	public Position moveInput() {
		Scanner userInput = new Scanner(System.in);
		System.out.println("Please enter a position to move a character as an ordered pair, with a comma inbetween (e.g. x,y):");


		while (true) {
			try {
				String input = userInput.nextLine();

				if (input.equals("")) {
					System.exit(0);
				}

				String[] parts = input.split(",");
				
				int xCoord = Integer.parseInt(parts[0]);
				int yCoord = Integer.parseInt(parts[1]);
				Position inputPos = new Position(yCoord, xCoord);
				return inputPos;
			}
			catch (Exception ignored) {
				System.out.println("Please include the comma \",\" inbetween your x and y values");
			}
		}
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
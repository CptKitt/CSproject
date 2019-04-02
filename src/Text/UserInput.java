package Text; 

import Model.Position;
import java.util.Scanner;

/**
 * UserInput class asks and takes input from the user through the system terminal.
 * Is implemented within TextMain to take input for the text-based version of Group 9's project.
 * Contains methods: moveInput():Position.
 */
public class UserInput {
	
	/**
	 * The method moveInput() asks the User for a position on the map displayed in the Interactive Text-Based version in the form: x,y.
	 * Parameters: none.
	 * Returns: Position - contains coordinate information on where the user wants a character to move
	 */
	public Position moveInput() {
		Scanner userInput = new Scanner(System.in);
		System.out.println("");
		System.out.println("Please enter a position as <x,y> to highlighted \"x\", or <> to pass, or <exit> to close game:");

		while (true) {
			try {
				String input = userInput.nextLine();

				if (input.equals("")) {
					return null;
				}
				if (input.equals("exit")) {
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
	 * This main method is used for testing the compilation and functionality of methods within this class
	 */
	public static void main(String[] args) {
		UserInput input = new UserInput();
		Position test = new Position(input.moveInput());
		System.out.println(test);
	}
}
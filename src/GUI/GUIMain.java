package GUI;

import Model.*;
import java.util.Set;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * JavaFX application representing the GUI
 * interface for the game.
 */
public class GUIMain extends Application {
	private Map map;
	private Display display;
	private Input input;
	private Scene scene;
	private Group root;

	private Position selectedPosition = null;
	private Set<Position> possibleActions = null;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		// create i/o objects
		display = new Display();
		input = new Input();

		// create map
		map = new Map();
		map.populateGrid();

		// javafx setup
		root = new Group();
		scene = new Scene(root, 720, 480);

		// set up event handlers
		scene.addEventFilter(MouseEvent.MOUSE_PRESSED, this::sceneClicked);

		// display once
		display.drawMapOnScene(map, root);

		// show application
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.setTitle("\"Sample Text Here\" Game");
		primaryStage.show();
	}

	/**
	 * Starting point to launch the application.
	 */
	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Event handling function for clicks in the scene.
	 * @param e The MouseEvent to process.
	 */
	private void sceneClicked(MouseEvent e) {
		// delegate input handling
		Position p = input.handleClick(e);

		// first click
		if (selectedPosition == null) {
			Set<Position> moves = map.possibleMovesForCharacter(p);
			
			// select character if possible moves exist
			if (!moves.isEmpty()) {
				selectedPosition = p;
				possibleActions = moves;
			}
		}
		// second click: try performing action
		else {
			map.processAction(selectedPosition, p);
			selectedPosition = null;
			possibleActions = null;
		}
		
		// TODO: remove debug line, send possibleActions to Display
		System.out.println("clicked x:" + e.getSceneX()
				+ ", y:" + e.getSceneY());

		// clear and update display
		root.getChildren().clear();
		display.drawMapOnScene(map, root);
	}
}

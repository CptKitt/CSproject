package GUI;

import Model.*;
import java.util.Set;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.canvas.Canvas;

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
<<<<<<< HEAD
	private Canvas canvas = new Canvas(400,400);
	private GraphicsContext gc = canvas.getGraphicsContext2D();
=======
	private Canvas canvas;
>>>>>>> master

	private Position selectedPosition = null;
	private Set<Position> possibleActions = null;
	
	public final int WIDTH = 720;
	public final int HEIGHT = 480;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		// create i/o objects
		display = new Display();
		input = new Input();

		// create map
		map = new Map();
		map.populateGrid();

		// javafx setup
<<<<<<< HEAD
		root = new Group();

		// display once
		display.drawMapOnScene(map, gc);

		root.getChildren().add(canvas);
		scene = new Scene(root, 720, 480);
=======
		canvas = new Canvas(WIDTH, HEIGHT);
		root = new Group(canvas);
		scene = new Scene(root, WIDTH, HEIGHT);
>>>>>>> master

		// set up event handlers
		scene.addEventFilter(MouseEvent.MOUSE_PRESSED, this::sceneClicked);

<<<<<<< HEAD

=======
		// display once
		display.drawMapOnScene(map, canvas.getGraphicsContext2D());
>>>>>>> master

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
<<<<<<< HEAD
		display.drawMapOnScene(map,gc);
	}

	public boolean validInput(String s) {
		return false;
=======
		display.drawMapOnScene(map, canvas.getGraphicsContext2D());
>>>>>>> master
	}
}

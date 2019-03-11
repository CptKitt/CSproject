package GUI;

import Model.*;
import java.util.Set;
import java.util.HashSet;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * JavaFX application representing the GUI
 * interface for the game.
 */
public class GUIMain extends Application {
	private Map map;
	private Display display;
	private Scene scene;
	private Group root;
	private Canvas canvas;

	private Position selectedPosition;
	private Set<Position> possibleMoves;

	public static final int WIDTH = 704;
	public static final int HEIGHT = 480;

	@Override
	public void start(Stage primaryStage) throws Exception {
		// create i/o
		display = new Display();

		// javafx setup
		canvas = new Canvas(WIDTH, HEIGHT);
		root = new Group(canvas);
		scene = new Scene(root, WIDTH, HEIGHT);

		// set up event handlers
		scene.addEventFilter(MouseEvent.MOUSE_PRESSED, this::sceneClicked);
		
		// temporary reset button
		scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
			if (e.getCode() == KeyCode.SPACE) {
				reset();
			}
		});
		
		// create map and vars
		reset();

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
	 * Generates a new map and resets variables.
	 */
	private void reset() {
		map = new Map(15, 22);
		map.populateGrid();
		selectedPosition = null;
		possibleMoves = new HashSet<>();
		
		// display once
		display.drawMapOnScene(map, canvas.getGraphicsContext2D(), possibleMoves);
	}

	/**
	 * Event handling function for clicks in the scene.
	 * @param e The MouseEvent to process.
	 */
	private void sceneClicked(MouseEvent e) {
		// convert coordinates to account for drawing
		int x = (int)(e.getSceneX() / 32);
		int y = (int)(e.getSceneY() / 32);
		Position p = new Position(y, x);
		
		// make sure position exists on map
		if (p.x < 0 || p.x >= map.getWidth() || p.y < 0 || p.y >= map.getHeight()) {
			return;
		}

		// first click
		if (selectedPosition == null) {
			Set<Position> moves = map.possibleMovesForCharacter(p);

			// select character if possible moves exist
			if (!moves.isEmpty()) {
				selectedPosition = p;
				possibleMoves = moves;
			}
		}
		// second click: try performing action
		else {
			map.processAction(selectedPosition, p);
			selectedPosition = null;
			possibleMoves.clear();
		}

		// TODO: remove debug line, send possibleActions to Display
		System.out.println("clicked x:" + e.getSceneX()
				+ ", y:" + e.getSceneY());

		// update display
		display.drawMapOnScene(map, canvas.getGraphicsContext2D(), possibleMoves);
	}
}

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
 * JavaFX application representing the GUI interface for the game.
 */
public class GUIMain extends Application {
	private Map map;
	private Display display;
	private Scene scene;
	private Group root;
	private Canvas canvas;
	private Group infoGroup;

	private Position hoverPosition;
	private Position selectedPosition;
	private Set<Position> possibleMoves;

	/** The width of the application. */
	public static final int WIDTH = 30 * 32 + 300;
	/** The height of the application. */
	public static final int HEIGHT = 20 * 32;

	@Override
	public void start(Stage primaryStage) throws Exception {
		// create i/o
		display = new Display();

		// javafx setup
		canvas = new Canvas(WIDTH, HEIGHT);
		infoGroup = new Group();
		infoGroup.setTranslateX(30 * 32);
		root = new Group(canvas, infoGroup);
		scene = new Scene(root, WIDTH, HEIGHT);

		// set up event handlers
		scene.addEventFilter(MouseEvent.MOUSE_PRESSED, this::sceneClicked);
		scene.addEventFilter(MouseEvent.MOUSE_MOVED, this::sceneHovered);

		// temporary reset button
		scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (event.getCode() == KeyCode.SPACE) {
				System.out.println("enemy turns: " + map.endTurn());
				display.drawMapOnScene(
						map, canvas.getGraphicsContext2D(), possibleMoves);
			}
			else if (event.getCode() == KeyCode.BACK_SPACE) {
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

	/** Starting point to launch the application. */
	public static void main(String[] args) {
		launch(args);
	}

	/** Generates a new map and resets variables. */
	private void reset() {
		map = new Map(20, 30);
		map.populateGrid();
		selectedPosition = null;
		possibleMoves = new HashSet<>();

		// display once
		display.drawMapOnScene(map, canvas.getGraphicsContext2D(), possibleMoves);
	}

	/**
	 * Event handling function for clicks in the scene.
	 * @param event The MouseEvent to process.
	 */
	private void sceneClicked(MouseEvent event) {
		// convert coordinates to account for drawing
		int x = (int) (event.getSceneX() / Display.size);
		int y = (int) (event.getSceneY() / Display.size);

		// click in sidebar
		if (x > 30) {
			System.out.println("enemy turns: " + map.endTurn());
		}
		// map click
		else {
			Position pos = new Position(y, x);

			// make sure position exists on map
			if (!map.positionOnMap(pos)) {
				return;
			}

			// first click
			if (selectedPosition == null) {
				Set<Position> moves = map.possibleMovesForCharacter(pos);

				// select character if possible moves exist
				if (!moves.isEmpty()) {
					selectedPosition = pos;
					possibleMoves = moves;
				}
			}
			// second click: try performing action
			else {
				// TODO: implement end turn event checks
				Turn playerTurn = map.processAction(selectedPosition, pos);
				System.out.println("player turn: " + playerTurn);

				selectedPosition = null;
				possibleMoves.clear();
			}
		}

		// update display
		display.drawMapOnScene(map, canvas.getGraphicsContext2D(), possibleMoves);
	}

	/**
	 * Event handler for mouse movement on the screen.
	 * @param event The MouseEvent to process.
	 */
	private void sceneHovered(MouseEvent event) {
		// convert coordinates to map
		int x = (int) (event.getSceneX() / Display.size);
		int y = (int) (event.getSceneY() / Display.size);
		Position pos = new Position(y, x);

		// check bounds
		if (!map.positionOnMap(pos)) {
			pos = null;
		}

		// no significant movement
		if ((pos == null && hoverPosition == null)
				|| (pos != null && pos.equals(hoverPosition))) {
			return;
		}
		hoverPosition = pos;

		Entity entity;

		// check if hovered position is visible
		if (pos == null || map.getVisibility()[pos.x][pos.y] < 0.1) {
			entity = null;
		}
		else {
			entity = map.getGrid()[pos.x][pos.y];
		}

		// TODO: send to Display
		if (entity != null) {
			System.out.println("hover pos " + pos + ": " + entity);
		}
		display.drawInfoOnScene(map, infoGroup, entity);
	}
}

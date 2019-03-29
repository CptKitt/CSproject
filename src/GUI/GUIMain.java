package GUI;

import Model.*;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
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
	private int selectHint = 0;
	private Set<Position> possibleMoves;

	/** The width of the application. */
	public static final double WIDTH = 30 * Display.size + 300;
	/** The height of the application. */
	public static final double HEIGHT = 20 * Display.size;

	@Override
	public void start(Stage primaryStage) throws Exception {
		// javafx setup
		canvas = new Canvas(WIDTH, HEIGHT);
		infoGroup = new Group();
		infoGroup.setTranslateX(30 * 32);
		root = new Group(canvas, infoGroup);
		scene = new Scene(root, WIDTH, HEIGHT);
		
		// create i/o
		display = new Display(root, 30, 20);

		// set up event handlers
		scene.addEventFilter(MouseEvent.MOUSE_PRESSED, this::sceneClicked);
		scene.addEventFilter(MouseEvent.MOUSE_MOVED, this::sceneHovered);

		// temporary reset button
		scene.addEventFilter(KeyEvent.KEY_PRESSED, this::keyboardPress);

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
		map.nextFloor();
		selectedPosition = null;
		possibleMoves = new HashSet<>();

		// display once
		redrawMap();
	}
	
	/**
	 * Event handling function for key presses in the scene.
	 * @param event The KeyEvent to process.
	 */
	private void keyboardPress(KeyEvent event) {
		Position toMove = null;
		switch (event.getCode()) {
			case SPACE: // end enemy turn
				selectedPosition = null;
				System.out.println("enemy turns: " + map.endTurn());
				redrawMap();
				break;
				
			case BACK_SPACE: // debug
				reset();
				break;
				
			case TAB: // select next player
				List<Player> players = map.getPlayers();
				for (int last = ++selectHint + players.size(); selectHint < last; selectHint++) {
					Player player = players.get(selectHint % players.size());
					if (player.getSTM() > 0) {
						selectedPosition = player.getPOS();
						redrawMap();
						break;
					}
				}
				break;
				
			case A: case LEFT: // movement
				if (selectedPosition != null) {
					toMove = selectedPosition.moved(0, -1);
				}
				break;
			case S: case DOWN:
				if (selectedPosition != null) {
					toMove = selectedPosition.moved(1, 0);
				}
				break;
			case D: case RIGHT:
				if (selectedPosition != null) {
					toMove = selectedPosition.moved(0, 1);
				}
				break;
			case W: case UP:
				if (selectedPosition != null) {
					toMove = selectedPosition.moved(-1, 0);
				}
				break;
		}
		
		// movement key pressed
		if (toMove != null) {
			Turn playerTurn = map.processAction(selectedPosition, toMove);
			if (playerTurn != null) {
				selectedPosition = toMove;
				redrawMap();
			}
		}
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
				selectedPosition = pos;
			}
			// second click: try performing action
			else {
				// TODO: implement end turn event checks
				Turn playerTurn = map.processAction(selectedPosition, pos);
				System.out.println("player turn: " + playerTurn);

				selectedPosition = null;
			}
		}

		// update display
		redrawMap();
	}
	
	/** Runs possible move calculations and asks Display to redraw the map. */
	private void redrawMap() {
		// position selected
		if (selectedPosition != null) {
			possibleMoves = map.possibleMovesForCharacter(selectedPosition);
			
			// no moves available, deselect position
			if (possibleMoves.isEmpty()) {
				selectedPosition = null;
			}
		}
		else {
			possibleMoves.clear();
		}
		
		display.drawMapOnScene(map, possibleMoves);
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
		
		display.drawInfoOnScene(map, infoGroup, entity);
	}
}

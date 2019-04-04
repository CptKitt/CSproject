package GUI;

import Model.*;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * JavaFX application representing the GUI interface for the game.
 */
public class GUIMain extends Application {
	/** Map representing the Model of the application. */
	private Map map;
	/** I/O object responsible for displaying the map. */
	private Display display;
	/** Group in which the Display draws the sidebar. */
	private Group infoGroup;

	/** The Map-coordinate Position over which the user is hovering. */
	private Position hoverPosition = Position.ORIGIN;
	/** The Position that the user has selected. May be null. */
	private Position selected;
	/** Positions that the selected Player can move to. May be null. */
	private Set<Position> possibleMoves;
	
	/** A private hint to inform swapping between Players. */
	private int selectHint = 0;
	/** Flag representing whether the Display is animating or not. */
	private boolean animating = false;
	
	/** The grid width of the Map. */
	private static final int MAP_WIDTH = 30;
	/** The grid height of the Map. */
	private static final int MAP_HEIGHT = 20;

	/** The width of the application. */
	public static final double WIDTH = MAP_WIDTH * Display.size + 300;
	/** The height of the application. */
	public static final double HEIGHT = MAP_HEIGHT * Display.size;

	@Override
	public void start(Stage primaryStage) throws Exception {
	    double yScale = Screen.getPrimary().getVisualBounds()
                .getHeight() / (MAP_HEIGHT * 16);
	    Display.size = 16 * (int) yScale;
	    
		// javafx setup
		infoGroup = new Group();
        infoGroup.setTranslateX(MAP_WIDTH * Display.size);
		Group root = new Group(infoGroup);
		Scene scene = new Scene(root, WIDTH, HEIGHT);
		
		// create i/o
        Display.loadImages();
		display = new Display(root, MAP_WIDTH, MAP_HEIGHT);

		// set up event handlers
		scene.addEventFilter(MouseEvent.MOUSE_PRESSED, this::sceneClicked);
		scene.addEventFilter(MouseEvent.MOUSE_MOVED, this::sceneHovered);
		scene.addEventFilter(KeyEvent.KEY_PRESSED, this::keyboardPress);

		// create map and vars
		reset();

		// show application
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.setTitle("");
		primaryStage.show();
	}

	/** Starting point to launch the application. */
	public static void main(String[] args) {
		launch(args);
	}

	/** Generates a new map and resets variables. */
	private void reset() {
        map = new Map(MAP_HEIGHT, MAP_WIDTH);
		map.nextFloor();
		selected = null;
		possibleMoves = new HashSet<>();

		// display once
		redrawMap();
	}
	
	/**
	 * Event handling function for key presses in the scene.
	 * @param event The KeyEvent to process.
	 */
	private void keyboardPress(KeyEvent event) {
		if (animating) return;
		
		// optional movement
		int dx = 0, dy = 0;
		
		switch (event.getCode()) {
			case SPACE: // end player turn
				endTurn();
				break;
				
			case BACK_SPACE: // reset button
				animating = true;
				display.fadeToBlack(event2 -> {
					reset();
					animating = false;
				});
				break;
				
			case TAB: // select next player
                List<Player> players = map.getPlayers();
                for (int i = 0; i < players.size(); i++) {
                    int index = ++selectHint % players.size();
                    Player player = players.get(index);
                    if (player.getSTM() > 0) {
                        selected = player.getPOS();
                        redrawMap();
                        break;
                    }
                }
				break;
			
			// movement
			case A: case LEFT: dy = -1; break;
			case S: case DOWN: dx = 1; break;
			case D: case RIGHT: dy = 1; break;
			case W: case UP: dx = -1; break;
		}
		
		// movement key pressed
		if (selected != null && (dx != 0 || dy != 0)) {
			Position start = selected;
			selected = selected.moved(dx, dy);
			if (!playerTurn(start, selected)) {
				selected = start;
			}
		}
	}
	
	/**
	 * Event handling function for clicks in the scene.
	 * @param event The MouseEvent to process.
	 */
	private void sceneClicked(MouseEvent event) {
		if (animating) return;
		
		// right click ends turn
		if (event.getButton() == MouseButton.SECONDARY) {
			endTurn();
			return;
		}
		
		// convert coordinates to account for drawing
		int x = (int) (event.getSceneX() / Display.size);
		int y = (int) (event.getSceneY() / Display.size);

		// click in sidebar
		if (x >= 30) {
			endTurn();
		}
		// map click
		else {
			Position pos = new Position(y, x);

			// make sure position exists on map
			if (!map.positionOnMap(pos)) {
				return;
			}

			// first click
			if (selected == null) {
				selected = pos;
				redrawMap();
			}
			// second click: try performing action
			else {
				if (map.getPlayers().stream().map(Player::getPOS)
						.anyMatch(pos::equals)) {
					selected = pos.equals(selected) ? null : pos;
					redrawMap();
				}
				else {
					Position start = selected;
					selected = pos;
					if (!playerTurn(start, pos)) {
						selected = null;
						redrawMap();
					}
				}
			}
		}
	}
	
	/** Returns true if the player turn is completed. */
	private boolean playerTurn(Position start, Position end) {
		if (start == null || end == null) {
			return false;
		}
		
		boolean nextFloor = map.getGrid()[end.x][end.y] instanceof Stairs;
		
		// ask map to process
		Turn turn = map.processAction(start, end);
		if (turn != null) {
			animating = true;
			
			display.animateTurn(turn, event -> {
				// special case: fade to black
				if (nextFloor) {
					display.fadeToBlack(event2 -> {
						redrawMap();
						animating = false;
					});
				}
				else {
					redrawMap();
					animating = false;
				}
			});
		}
		
		return turn != null;
	}
	
	/** Ends the player turn and animates enemy turns. */
	private void endTurn() {
		selected = null;
		redrawMap();
		
		animating = true;
		display.animateTurns(map.endTurn(), e -> {
			animating = false;
			redrawMap();
		});
	}
	
	/** Checks possible moves and asks Display to redraw the map. */
	private void redrawMap() {
		// position selected
		if (selected != null) {
			possibleMoves = map.possibleMovesForCharacter(selected);
			
			// no moves available, deselect position
			if (possibleMoves.isEmpty()) {
				selected = null;
				display.drawInfoOnScene(map, infoGroup, null);
			}
			else {
				Entity entity = map.getGrid()[selected.x][selected.y];
				display.drawInfoOnScene(map, infoGroup, entity);
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
		if (pos != null && pos.equals(hoverPosition)) {
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

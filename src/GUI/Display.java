package GUI;

import Model.*;
import Model.Map;

import javafx.animation.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.*;

/**
 * Class for displaying Maps as JavaFX.
 */
public class Display {
	/** The length of all displayed Images. */
	public static double size = 32d;

	// image assets
	private static Image
			//environment (walls, floors, et cetera)
			tower_floor, dungeon_floor, cave_floor,
			wall, cave_wall, space, highlight, shade,
			upstairs, downstairs_dungeon, downstairs_cave, downstairs_tower,

			//entities (players, enemies)
			red_slime, orange_slime, yellow_slime,
			green_slime, cyan_slime, blue_slime,
			purple_slime, rainbow_slime,
			white_slime, black_slime,
			hero;

	private static Font pixelFont, infoFont;

	// screen objects
	private Group root;
	private ImageView[][] floors, entities, shades;
	private Rectangle[][] highlights, covers;

	// info bar objects
	private VBox statusDisplay, logBox;
	private ImageView portrait;
	private Rectangle hpBar;
	private Text floorText, nameText, hpText, lvlText, atkText, defText, spdText;
	private Deque<String> backlog = new ArrayDeque<>();
	private boolean logAnimating = false;

	/**
	 * Initializes a new Display object.
	 * @param root The root Group of the Scene.
	 * @param width The width of the Map to display.
	 * @param height The height of the Map to display.
	 */
	Display(Group root, int width, int height) {
		this.root = root;

		Map.logHandler = this::handleLog;

		floors = new ImageView[width][height];
		entities = new ImageView[width][height];
		highlights = new Rectangle[width][height];
		shades = new ImageView[width][height];
		covers = new Rectangle[width][height];

		Color highlightColor = Color.valueOf("#02ff06")
				.deriveColor(0, 1, 1, 0.5);

		// initialize views
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				floors[x][y] = defaultImageView(dungeon_floor, x, y);
				entities[x][y] = defaultImageView(null, x, y);
				highlights[x][y] = defaultRectangle(highlightColor, x, y);
				shades[x][y] = defaultImageView(shade, x, y);
				covers[x][y] = defaultRectangle(Color.BLACK, x, y);
			}
		}

		// layer views
		for (Node[][] nodes : new Node[][][] {
				floors, highlights, entities, shades, covers
		}) {
			for (Node[] children : nodes) {
				root.getChildren().addAll(children);
			}
		}

		// info bar objects
		floorText = new Text();
		portrait = new ImageView();
		portrait.setFitWidth(size * 2);
		portrait.setFitHeight(size * 2);
		hpBar = new Rectangle(0, 8, Color.RED);
		hpBar.setArcWidth(8);
		hpBar.setArcHeight(8);
		nameText = new Text();
		hpText = new Text();
		lvlText = new Text();
		atkText = new Text();
		defText = new Text();
		spdText = new Text();

		// apply styles to labels
		for (Text label : new Text[] {
				floorText, nameText, hpText, lvlText, atkText, defText, spdText
		}) {
			label.setFont(infoFont);
			label.setFill(Color.WHITE);
			label.setWrappingWidth(260);
		}
		floorText.setFont(pixelFont);
		hpText.setWrappingWidth(0);

		// layout objects
		Rectangle hpBarBack = new Rectangle(260, 8, Color.DARKRED);
		hpBarBack.setArcWidth(8);
		hpBarBack.setArcHeight(8);
		StackPane hpStack = new StackPane(hpBarBack, hpBar, hpText);
		hpStack.setAlignment(Pos.CENTER);

		VBox labelBox = new VBox(8,
				nameText, hpStack, lvlText, atkText, defText, spdText);
		labelBox.setMaxWidth(260);
		labelBox.setTranslateX(10);

		statusDisplay = new VBox(12, portrait, labelBox);
		statusDisplay.setAlignment(Pos.CENTER);
		statusDisplay.setOpacity(0);

		logBox = new VBox(12);
		logBox.setFillWidth(true);
		logBox.setMaxWidth(280);
		logBox.setMinWidth(280);

		VBox barBox = new VBox(30, floorText, statusDisplay, logBox);
		barBox.setAlignment(Pos.CENTER);
		barBox.setTranslateX(10);
		barBox.setTranslateY(20);

		Group infoGroup = new Group(barBox);
		infoGroup.setTranslateX(width * size);
		root.getChildren().add(infoGroup);
	}

	/** Creates a new ImageView with the specified image. */
	private ImageView defaultImageView(Image image, int x, int y) {
		ImageView iv = new ImageView(image);
		iv.setTranslateX(x * size);
		iv.setTranslateY(y * size);
		iv.setFitWidth(size);
		iv.setFitHeight(size);
		return iv;
	}

	/** Creates a new Rectangle of the specified color. */
	private Rectangle defaultRectangle(Color color, int x, int y) {
		Rectangle rect = new Rectangle(size, size, color);
		rect.setTranslateX(x * size);
		rect.setTranslateY(y * size);
		return rect;
	}

	/**
	 * Draw the map on the screen.
	 * @param map is a Map that gets drawn.
	 * @param highlighted is a set of positions used to draw the move overlay.
	 */
	public void drawMapOnScene(Map map, Set<Position> highlighted) {
		Entity[][] grid = map.getGrid();
		double[][] visibility = map.getVisibility();

		for (int x = 0; x < map.getHeight(); x++) {
			for (int y = 0; y < map.getWidth(); y++) {
				Entity e = grid[y][x];
				Image newImage = null;

				// reset
				if (!(e instanceof Player)) {
					entities[x][y].setOpacity(1);
				}

				// update floors
				switch (map.getType()) {
					case TOWER: floors[x][y].setImage(tower_floor); break;
					case CAVE: floors[x][y].setImage(cave_floor); break;
					case DUNGEON: floors[x][y].setImage(dungeon_floor); break;
				}

				// process entity
				if (e instanceof Obstacle) {
					if (y < map.getWidth() - 1 && grid[y+1][x] instanceof Obstacle) {
						newImage = space;
					}
					else {
						switch (map.getType()) {
							case TOWER: newImage = wall; break;
							case CAVE: newImage = cave_wall; break;
							case DUNGEON: newImage = wall; break;
						}
					}
				}
				else if (e instanceof Stairs) {
					switch (map.getType()) {
						case TOWER: floors[x][y].setImage(downstairs_tower); break;
						case CAVE: floors[x][y].setImage(downstairs_cave); break;
						case DUNGEON: floors[x][y].setImage(downstairs_dungeon); break;
					}
				}
				else if (e instanceof Enemy) {
					newImage = green_slime;
				}
				else if (e instanceof Player) {
					newImage = hero;
					// fade out players with no moves left
					double opacity = ((Player) e).getSTM() <= 0 ? 0.5 : 1;
					fadeNodeOpacity(entities[x][y], opacity, 0.2);
				}
				entities[x][y].setImage(newImage);

				// fade animations for highlights and shadows
				double newOpacity = highlighted.contains(new Position(y, x)) ? 1 : 0;
				fadeNodeOpacity(highlights[x][y], newOpacity, 0.2);

				double opacity = 1 - visibility[y][x];
				fadeNodeOpacity(shades[x][y], opacity, 0.2);
				fadeNodeOpacity(covers[x][y], opacity, 0.2);
			}
		}

		// update text
		floorText.setText("Floor " + map.getFloor() + ": " + map.getType());
	}

	/**
	 * Animates a node's opacity to a different value.
	 * @param node The node to fade.
	 * @param opacity The destination opacity.
	 */
	private void fadeNodeOpacity(Node node, double opacity, double duration) {
		// no fade if node is already near destination
		if (Math.abs(node.getOpacity() - opacity) > 0.01) {
			FadeTransition fade = new FadeTransition(Duration.seconds(duration), node);
			fade.setToValue(opacity);
			fade.play();
		}
	}

	/**
	 * Fades the screen to black.
	 * @param handler The handler to execute on completion.
	 */
	public void fadeToBlack(EventHandler<ActionEvent> handler) {
		// fade all covers and shades
		for (int x = 0; x < covers.length; x++) {
			for (int y = 0; y < covers[0].length; y++) {
				fadeNodeOpacity(covers[x][y], 1, 1);
				fadeNodeOpacity(shades[x][y], 1, 1);
			}
		}
		// placeholder to execute handler
		PauseTransition pause = new PauseTransition(Duration.seconds(1));
		pause.setOnFinished(handler);
		pause.play();
	}

	/**
	 * Creates an animation from a Turn.
	 * @param turn The Turn to read information from.
	 * @return A SequentialTransition containing the animation to play.
	 */
	public Animation animationForTurn(Turn turn) {
		// animation builder
		SequentialTransition st = new SequentialTransition();
		Position start = new Position(turn.start.y, turn.start.x);
		Position end = new Position(turn.end.y, turn.end.x);

		if (!turn.start.equals(turn.end)) {
			List<Position> positions = turn.path;
			positions.add(turn.end);

			// start position

			Path path = new Path(new MoveTo(
					start.x * size + size / 2,
					start.y * size + size / 2));
			// create path
			for (Position p : positions) {
				path.getElements().add(new LineTo(
						p.y * size + size / 2, p.x * size + size / 2));
			}

			// get properties
			ImageView iv = entities[start.x][start.y];
			ImageView iv2 = entities[end.x][end.y];
			double duration = Math.sqrt(positions.size() / 40.0) - 0.1;

			PathTransition pt = new PathTransition(Duration.seconds(duration), path, iv);
			pt.setInterpolator(Interpolator.EASE_BOTH);
			// replace images on finish
			pt.setOnFinished(e -> {
				iv2.setImage(iv.getImage());
				iv.setImage(null);
				iv.setTranslateX(start.x * size);
				iv.setTranslateY(start.y * size);
			});

			st.getChildren().add(pt);
		}

		// additional attack animation using end position
		if (turn.attackPos != null) {
			// movement
			ImageView iv1 = entities[end.x][end.y];
			PathTransition path = new PathTransition(Duration.seconds(0.2), new Path(
					new MoveTo(end.x * size + size/2,
							end.y * size + size/2),
					new LineTo(turn.attackPos.y * size + size/2,
							turn.attackPos.x * size + size/2),
					new LineTo(end.x * size + size/2,
							end.y * size + size/2)
			), iv1);
			path.setInterpolator(Interpolator.EASE_OUT);

			// enemy shake
			ImageView iv2 = entities[turn.attackPos.y][turn.attackPos.x];
			PathTransition shake = new PathTransition(Duration.seconds(0.2), new Path(
					new MoveTo(turn.attackPos.y * size + size/2,
							turn.attackPos.x * size + size/2),
					new HLineTo(turn.attackPos.y * size + size*3/8),
					new HLineTo(turn.attackPos.y * size + size*5/8),
					new HLineTo(turn.attackPos.y * size + size/2)
			), iv2);
			shake.setInterpolator(Interpolator.EASE_OUT);

			ParallelTransition parallel = new ParallelTransition(path, shake);

			// floating text on finish
			parallel.setOnFinished(event -> {
				Text damage = new Text("" + turn.damage);
				damage.setFont(pixelFont);
				damage.setFill(Color.WHITE);
				double halfWidth = damage.getBoundsInParent().getWidth() / 2;
				damage.setTranslateX(turn.attackPos.y * size + size/2 - halfWidth);
				damage.setTranslateY(turn.attackPos.x * size + size/2);
				root.getChildren().add(damage);

				FadeTransition fade = new FadeTransition(Duration.seconds(2), damage);
				fade.setToValue(0);
				fade.setOnFinished(event2 -> {
					root.getChildren().remove(damage);
				});
				fade.setInterpolator(Interpolator.EASE_OUT);

				TranslateTransition move = new TranslateTransition(Duration.seconds(2), damage);
				move.setToY(turn.attackPos.x * size - size);
				move.setInterpolator(Interpolator.EASE_OUT);

				ParallelTransition parallel2 = new ParallelTransition(fade, move);
				parallel2.play();
			});

			st.getChildren().add(parallel);
		}

		return st;
	}

	/**
	 * Animates a Turn.
	 * @param turn The Turn to animate.
	 * @param handler The handler to run on completion.
	 */
	public void animateTurn(Turn turn, EventHandler<ActionEvent> handler) {
		Animation anim = animationForTurn(turn);
		anim.setOnFinished(handler);
		anim.play();
	}

	/**
	 * Animates a number of turns in parallel.
	 * @param turns The Turns to animate.
	 * @param handler The handler to run on completion.
	 */
	public void animateTurns(List<Turn> turns, EventHandler<ActionEvent> handler) {
		if (turns.isEmpty()) {
			handler.handle(null);
		}

		// delay turns by a bit
		Animation[] anims = turns.stream()
				.map(this::animationForTurn).toArray(Animation[]::new);
		for (int i = 0; i < anims.length; i++) {
			SequentialTransition st = new SequentialTransition(
					new PauseTransition(Duration.seconds(i * 0.1)),
					anims[i],
					new PauseTransition(Duration.seconds(0.1))
			);
			// add handler to final animation
			if (i == anims.length - 1) {
				st.setOnFinished(handler);
			}
			st.play();
		}
	}

	/** Asynchronous animation handler for Map logs. */
	private void handleLog(String log) {
		if (log != null) {
			backlog.addFirst(log);
		}

		if (!logAnimating && !backlog.isEmpty()) {
			logAnimating = true;

			// create new label
			Text text = new Text(backlog.removeLast());
			text.setWrappingWidth(280);
			text.setLineSpacing(4);
			text.setFont(infoFont);
			text.setFill(Color.WHITE);
			text.setOpacity(0);
			ObservableList<Node> nodes = logBox.getChildren();
			nodes.add(0, text);
			if (nodes.size() > 20) {
				nodes.remove(20, nodes.size() - 1);
			}

			// animate down
			double height = text.getBoundsInParent().getHeight() + 12;
			logBox.setTranslateY(-height);
			TranslateTransition fall = new TranslateTransition();
			fall.setDuration(Duration.seconds(0.2));
			fall.setNode(logBox);
			fall.setToY(0);

			// build animations
			ParallelTransition parallel = new ParallelTransition(fall);
			PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
			pause.setOnFinished(event -> {
				logAnimating = false;
				handleLog(null);
			});

			// add fade animations
			for (int i = 0; i < nodes.size(); i++) {
				FadeTransition fade = new FadeTransition();
				fade.setDuration(Duration.seconds(0.2));
				fade.setNode(nodes.get(i));
				fade.setToValue(4.0 / (i + 2) - 0.2);
				parallel.getChildren().add(fade);
			}

			parallel.play();
			pause.play();
		}
	}

	/**
	 * Draws the info box on the screen.
	 * @param map The Map associated with the Display.
	 * @param ent The Entity to display information for.
	 */
	public void drawInfoOnScene(Map map, Entity ent) {
		// no entity to display info for
		if (ent == null) {
			fadeNodeOpacity(statusDisplay, 0, 0.3);
			return;
		}

		fadeNodeOpacity(statusDisplay, 1, 0.3);

		if (ent instanceof Obstacle) {
			portrait.setImage(wall);
			nameText.setText("Wall");
		}
		else if (ent instanceof Stairs) {
			portrait.setImage(upstairs);
			nameText.setText("Portal");
		}
		else if (ent instanceof Enemy) {
			portrait.setImage(green_slime);
			nameText.setText("Green Slime");
		}
		else if (ent instanceof Player) {
			portrait.setImage(hero);
			nameText.setText("Generic Shifty-eyed Hero");
		}

		int hp = (int) Math.ceil(ent.getHP());
		int maxHP = (int) Math.ceil(ent.getmaxHP());
		hpBar.setWidth(260.0 * hp / maxHP);
		hpText.setText("HP: " + hp + "/" + maxHP);
		lvlText.setText("LVL: " + ent.getLVL());
		atkText.setText("ATK: " + (int) Math.ceil(ent.getATK()));
		defText.setText("DEF: " + (int) Math.ceil(ent.getDEF()));
		spdText.setText("SPD: " + ent.getSPD());
	}

	/** Attempts to load GUI images. */
	public static void loadImages() {
		String env = "environment/";
		String over = "overlays/";
		String slime = "slimes/";

		tower_floor = asset(env + "tile3.png");
		dungeon_floor = asset(env + "tile1.png");
		cave_floor = asset(env + "tile2.png");
		wall = asset(env + "wall2.png");
		cave_wall = asset(env + "wall1.png");
		space = asset(env + "wall3.png");
		highlight = asset(over + "move_highlight.png");
		shade = asset(over + "night_overlay.png");
		upstairs = asset(env + "stairs_up.png");
		downstairs_dungeon = asset(env + "stairs_down_dungeon.png");
		downstairs_cave = asset(env + "stairs_down_cave.png");
		downstairs_tower = asset(env + "stairs_down_tower.png");

		red_slime = asset(slime + "red_slime.png");
		orange_slime = asset(slime + "orange_slime.png");
		yellow_slime = asset(slime + "yellow_slime.png");
		green_slime = asset(slime + "Animations/green_slime_idle.gif");
		cyan_slime = asset(slime + "cyan_slime.png");
		blue_slime = asset(slime + "blue_slime.png");
		purple_slime = asset(slime + "purple_slime.png");
		rainbow_slime = asset(slime + "rainbow_slime.png");
		white_slime = asset(slime + "white_slime.png");
		black_slime = asset(slime + "black_slime.png");

		hero = asset("player1.png");


		pixelFont = GUI.assets.fonts.Fonts.boldPixel(18);
		infoFont = GUI.assets.fonts.Fonts.pixel(14);
	}

	/**
	 * Image loading function that crashes if the asset is not found.
	 * @param name The name of the asset, with extension.
	 * @return A newly initialized Image.
	 */
	private static Image asset(String name) {
		String path = "GUI/assets/" + name;
		try {
			return new Image(path, size * 2, size * 2, true, false);
		}
		catch (IllegalArgumentException iae) {
			System.out.println("Missing image at URL: " + path);
			System.exit(100);
			return null; // should never run
		}
	}
}

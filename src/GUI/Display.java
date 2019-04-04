package GUI;

import Model.*;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.List;
import java.util.Set;

/**
 * Class for displaying Maps as JavaFX.
 */
public class Display {
	/** The length of all displayed Images. */
	public static double size = 32d;
	
	//environment (walls, floors, et cetera)
	private static Image tower_floor, dungeon_floor, cave_floor,
			wall, cave_wall,
			space, highlight, shade, upstairs,
			downstairs_dungeon, downstairs_cave;
	
	//entities (players, enemies)
	private static Image red_slime, orange_slime, yellow_slime;
	private static Image green_slime, cyan_slime, blue_slime;
	private static Image purple_slime, rainbow_slime;
	private static Image white_slime, black_slime;
	
	private static Image hero;
	
	private static Font pixelFont;
	
	// screen objects
	private Group root;
	private ImageView[][] floors, entities, shades;
	private Rectangle[][] highlights, covers;
	
	/**
	 * Initializes a new Display object.
	 * @param root The root Group of the Scene.
	 * @param width The width of the Map to display.
	 * @param height The height of the Map to display.
	 */
	Display(Group root, int width, int height) {
		this.root = root;
		
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
	 * @param m is a Map that gets drawn.
	 * @param highlighted is a set of positions used to draw the move overlay.
	 */
	public void drawMapOnScene(Map m, Set<Position> highlighted) {
		Entity[][] grid = m.getGrid();
		double[][] visibility = m.getVisibility();
		
		for (int x = 0; x < m.getHeight(); x++) {
			for (int y = 0; y < m.getWidth(); y++) {
				Entity e = grid[y][x];
				Image newImage = null;
				
				// reset
				if (!(e instanceof Player)) {
					entities[x][y].setOpacity(1);
				}
				
				// update floors
				if (m.getType() == Map.Type.CAVE) {
					floors[x][y].setImage(cave_floor);
				}
				else if (m.getType() == Map.Type.TOWER) {
					floors[x][y].setImage(tower_floor);
				}
				else {
					floors[x][y].setImage(dungeon_floor);
				}
				
				// process entity
				if (e instanceof Obstacle) {
					if (y < m.getWidth() - 1 && grid[y+1][x] instanceof Obstacle) {
						newImage = space;
					}
					else {
						if (m.getType() == Map.Type.CAVE) {
							newImage = cave_wall;
						}
						else {
							newImage = wall;
						}
					}
				}
				else if (e instanceof Stairs) {
					if (m.getType() == Map.Type.CAVE) {
						floors[x][y].setImage(downstairs_cave);
					}
					else {
						floors[x][y].setImage(downstairs_dungeon);
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
	
	/**
	 * Draws the info box on the screen.
	 * @param m The Map associated with the Display.
	 * @param info The Group to draw to.
	 * @param ent The Entity to display information for.
	 */
	public void drawInfoOnScene(Map m, Group info, Entity ent) {
		ImageView portrait = new ImageView();
		Image picture;
		Label name;
		Label hp;
		Label lvl;
		Label atk;
		Label def;
		Label spd;
		int children = info.getChildren().size();

		if (children > 0){
			info.getChildren().remove(0,1);
		}

		if (ent != null){
			hp = new Label("HP: " + ent.getHP());
			lvl = new Label("LVL: " + ent.getLVL());
			atk = new Label("ATK: " + ent.getATK());
			def = new Label("DEF: " + ent.getDEF());
			spd = new Label("SPD: " + ent.getSPD());
		}
		else {
			hp = new Label();
			lvl = new Label();
			atk = new Label();
			def = new Label();
			spd = new Label();
		}
		if (ent instanceof Obstacle) {
			picture = wall;
			name = new Label("Wall");
		}
		else if (ent instanceof Stairs) {
			picture = upstairs;
			name = new Label("Portal");
		}
		else if (ent instanceof Enemy) {
			picture = green_slime;
			name = new Label("Green Slime");
		}
		else if (ent instanceof Player) {
			picture = hero;
			name = new Label("Generic Shifty-eyed Hero");
		}
		else {
			picture = null;
			name = new Label();
		}

		portrait.setImage(picture);

		HBox topbox = new HBox();
		VBox overviewbox = new VBox();
		HBox statbox = new HBox();
		VBox atkdef = new VBox();
		VBox lvlspd = new VBox();

		atkdef.getChildren().addAll(atk,def);
		lvlspd.getChildren().addAll(lvl,spd);
		statbox.getChildren().addAll(lvlspd,atkdef);
		overviewbox.getChildren().addAll(name,hp,statbox);
		topbox.getChildren().addAll(portrait,overviewbox);

		info.getChildren().add(topbox);

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
		
		pixelFont = Font.loadFont("file:src/GUI/assets/fonts/pixelmix.ttf", 18);
		if (pixelFont == null) {
			System.out.println("Unable to load custom fonts. Defaulting to system font.");
			pixelFont = new Font(18);
		}
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

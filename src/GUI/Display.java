package GUI;

import Model.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.Set;
import javafx.scene.Group;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Class for displaying Maps as JavaFX.
 */
public class Display {
	public static final double size = 32d;
	
	//environment (walls, floors, et cetera)
	private static Image floor, wall, space, highlight;
	private static Image shade, upstairs, downstairs;

	//entities (players, enemies)
	private static Image red_slime, orange_slime, yellow_slime;
	private static Image green_slime, cyan_slime, blue_slime;
	private static Image purple_slime, rainbow_slime;
	private static Image white_slime, black_slime;

	private static Image hero;
	
	static {
		loadImages();
	}
	
	// screen objects
	ImageView[][] floors, highlights, entities, shades;
	Rectangle[][] covers;
	
	/**
	 * Initializes a new Display object.
	 * @param root The root Group of the Scene.
	 * @param width The width of the Map to display.
	 * @param height The height of the Map to display.
	 */
	Display(Group root, int width, int height) {
		floors = new ImageView[width][height];
		entities = new ImageView[width][height];
		highlights = new ImageView[width][height];
		shades = new ImageView[width][height];
		covers = new Rectangle[width][height];
		
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				floors[x][y] = new ImageView(floor);
				floors[x][y].setTranslateX(x * size);
				floors[x][y].setTranslateY(y * size);
				entities[x][y] = new ImageView();
				entities[x][y].setTranslateX(x * size);
				entities[x][y].setTranslateY(y * size);
				highlights[x][y] = new ImageView(highlight);
				highlights[x][y].setTranslateX(x * size);
				highlights[x][y].setTranslateY(y * size);
				shades[x][y] = new ImageView(shade);
				shades[x][y].setTranslateX(x * size);
				shades[x][y].setTranslateY(y * size);
				covers[x][y] = new Rectangle(size, size, Color.BLACK);
				covers[x][y].setTranslateX(x * size);
				covers[x][y].setTranslateY(y * size);
			}
		}
		
		for (int x = 0; x < width; x++)
			root.getChildren().addAll(floors[x]);
		for (int x = 0; x < width; x++)
			root.getChildren().addAll(highlights[x]);
		for (int x = 0; x < width; x++)
			root.getChildren().addAll(entities[x]);
		for (int x = 0; x < width; x++)
			root.getChildren().addAll(shades[x]);
		for (int x = 0; x < width; x++)
			root.getChildren().addAll(covers[x]);
	}
	
	/**
	 * Draw the map on the screen.
	 * @param m is a Map that gets drawn.
	 * @param highlighted is a set of positions used to draw the move overlay.
	 */
	public void drawMapOnScene(Map m, Set<Position> highlighted) {
		Entity[][] grid = m.getGrid();
		double[][] visgrid = m.getVisibility();
		
		for (int x = 0; x < m.getHeight(); x++) {
			for (int y = 0; y < m.getWidth(); y++) {
				Entity e = grid[y][x];
				Image newImage = null;
				
				if (e instanceof Obstacle) {
					if (y < m.getWidth() - 1 && grid[y+1][x] instanceof Obstacle) {
						newImage = space;
					}
					else {
						newImage = wall;
					}
				}
				else if (e instanceof Stairs) {
					floors[x][y].setImage(downstairs);
				}
				else if (e instanceof Enemy) {
					newImage = green_slime;
				}
				else if (e instanceof Player) {
					newImage = hero;
				}
				entities[x][y].setImage(newImage);
				
				highlights[x][y].setVisible(highlighted.contains(new Position(y,x)));
				
				double opacity = 1 - visgrid[y][x];
				shades[x][y].setOpacity(opacity);
				covers[x][y].setOpacity(opacity);
			}
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
			picture = new Image("GUI/assets/environment/wall2.png",size*2,size*2,true,false);
			name = new Label("Wall");
		}
		else if (ent instanceof Stairs) {
			picture = new Image("GUI/assets/environment/stairs_up.png",size*2,size*2,true,false);
			name = new Label("Portal");
		}
		else if (ent instanceof Enemy) {
			picture =  new Image("GUI/assets/slimes/green_slime.png",size*2,size*2,true,false);
			name = new Label("Green Slime");
		}
		else if (ent instanceof Player) {
			picture = new Image("GUI/assets/player1.png",size*2,size*2,true,false);
			name = new Label("Generic Shifty-eyed Hero");
		}
		else {
			picture = new Image("GUI/assets/environment/wall3.png",size*2,size*2,true,false);
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
	private static void loadImages() {
		String env = "environment/";
		String over = "overlays/";
		String slime = "slimes/";
		
		floor = asset(env + "tile1.png");
		wall = asset(env + "wall2.png");
		space = asset(env + "wall3.png");
		highlight = asset(over + "move_highlight.png");
		shade = asset(over + "night_overlay.png");
		upstairs = asset(env + "stairs_up.png");
		downstairs = asset(env + "stairs_down.png");
		
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
	}
	
	/**
	 * Image loading function that crashes if the asset is not found.
	 * @param name The name of the asset, with extension.
	 * @return A newly initialized Image.
	 */
	private static Image asset(String name) {
		String path = "GUI/assets/" + name;
		try {
			return new Image(path, size, size, true, false);
		}
		catch (IllegalArgumentException iae) {
			System.out.println("Missing image at URL: " + path);
			System.exit(100);
			return null; // should never run
		}
	}
}

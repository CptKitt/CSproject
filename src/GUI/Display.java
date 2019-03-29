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

	/**Draw the map on the screen.
	*@param m is a Map that gets drawn.
	*@param g is a GraphicsContext used like a canvas to draw the map onto .
	*@param highlighted is a set of positions used to draw the move overlay.
	*
	*/
	public void drawMapOnScene(Map m, GraphicsContext g, Set<Position> highlighted) {
		Entity[][] grid = m.getGrid();
		double[][] visgrid = m.getVisibility();

		for(int i=grid.length-1;i>=0;i--) {
			for(int j=grid[i].length-1;j>=0;j--) {
				g.drawImage(floor,j*size,i*size);

				Entity e = grid[i][j];

				if (e instanceof Obstacle) { //draw walls
            if (i<grid.length-1 && grid[i+1][j] instanceof Obstacle) {
                g.drawImage(space,j*size,i*size);
            }
            else {
                g.drawImage(wall,j*size,i*size);
            }
        }
				else if (e instanceof Stairs) { //draw stairs
					g.drawImage(downstairs,j*size,i*size);
				}
				else if (e instanceof Enemy) { //draw enemies
					//TODO: add other slime colours
					g.drawImage(green_slime,j*size,i*size);
				}
        else if (grid[i][j] instanceof Player) {//draw players
            g.drawImage(hero,j*size,i*size);
        }

				if (highlighted.contains(new Position(i,j))) { //draw move overlay
					g.drawImage(highlight,j*size,i*size);
				}
				else { //draw visibility overlay (the "shroud" or fog of war)
					g.setGlobalAlpha(1-visgrid[i][j]);
					g.drawImage(shade,j*size,i*size);
					g.setGlobalAlpha(1);
					g.setFill(new Color(0,0,0,1-visgrid[i][j]));
					g.fillRect(j*size,i*size,size,size);
				}

			}
		}
	}

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
		
		floor = asset(env + "tile1");
		wall = asset(env + "wall2");
		space = asset(env + "wall3");
		highlight = asset(over + "move_highlight");
		shade = asset(over + "night_overlay");
		upstairs = asset(env + "stairs_up");
		downstairs = asset(env + "stairs_down");
		
		red_slime = asset(slime + "red_slime");
		orange_slime = asset(slime + "orange_slime");
		yellow_slime = asset(slime + "yellow_slime");
		green_slime = asset(slime + "green_slime");
		cyan_slime = asset(slime + "cyan_slime");
		blue_slime = asset(slime + "blue_slime");
		purple_slime = asset(slime + "purple_slime");
		rainbow_slime = asset(slime + "rainbow_slime");
		white_slime = asset(slime + "white_slime");
		black_slime = asset(slime + "black_slime");
		
		hero = asset("player1");
	}
	
	/**
	 * Image loading function that crashes if the asset is not found.
	 * @param name The name of the PNG asset.
	 * @return A newly initialized Image.
	 */
	private static Image asset(String name) {
		String path = "GUI/assets/" + name + ".png";
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

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
import java.io.File;

//TODO: Add sidebar
public class Display {
	public static final double size = 32d;
	//environment (walls, floors, et cetera)
	private Image floor = new Image("GUI/assets/environment/tile1.png",size,size,true,false);
	private Image wall = new Image("GUI/assets/environment/wall2.png",size,size,true,false);
	private Image space = new Image("GUI/assets/environment/wall3.png",size,size,true,false);
	private Image highlight = new Image("GUI/assets/overlays/move_highlight.png",size,size,true,false);
	private Image shade = new Image("GUI/assets/overlays/night_overlay.png",size,size,true,false);
	private Image upstairs = new Image("GUI/assets/environment/stairs_up.png",size,size,true,false);
	private Image downstairs = new Image("GUI/assets/environment/stairs_down.png",size,size,true,false);

	//entities (players, enemies)
	private Image red_slime = new Image("GUI/assets/slimes/red_slime.png",size,size,true,false);
	private Image orange_slime = new Image("GUI/assets/slimes/orange_slime.png",size,size,true,false);
	private Image yellow_slime = new Image("GUI/assets/slimes/yellow_slime.png",size,size,true,false);
	private Image green_slime = new Image("GUI/assets/slimes/green_slime.png",size,size,true,false);
	private Image cyan_slime = new Image("GUI/assets/slimes/cyan_slime.png",size,size,true,false);
	private Image blue_slime = new Image("GUI/assets/slimes/blue_slime.png",size,size,true,false);
	private Image purple_slime = new Image("GUI/assets/slimes/purple_slime.png",size,size,true,false);
	private Image rainbow_slime = new Image("GUI/assets/slimes/rainbow_slime.png",size,size,true,false);
	private Image white_slime = new Image("GUI/assets/slimes/white_slime.png",size,size,true,false);
	private Image black_slime = new Image("GUI/assets/slimes/black_slime.png",size,size,true,false);

	private Image hero = new Image("GUI/assets/player1.png",size,size,true,false);

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
}

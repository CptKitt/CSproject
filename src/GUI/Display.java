package GUI;

import Model.*;
import java.util.Set;
import javafx.scene.Group;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import java.io.File;

//TODO: Add sidebar
public class Display {
	public static final double size = 32d;

	/**Draw the map on the screen.
	*@param m is a Map that gets drawn.
	*@param g is a GraphicsContext used like a canvas to draw the map onto .
	*@param highlighted is a set of positions used to draw the move overlay.
	*
	*/
	public void drawMapOnScene(Map m, GraphicsContext g, Set<Position> highlighted) {
		Entity[][] grid = m.getGrid();
		double[][] visgrid = m.getVisibility();
		//environment (walls, floors, et cetera)
		Image floor = new Image("GUI/assets/environment/tile1.png",size,size,true,false);
		Image wall = new Image("GUI/assets/environment/wall2.png",size,size,true,false);
		Image space = new Image("GUI/assets/environment/wall3.png",size,size,true,false);
		Image highlight = new Image("GUI/assets/overlays/move_highlight.png",size,size,true,false);
		Image shade = new Image("GUI/assets/overlays/night_overlay.png",size,size,true,false);
		Image upstairs = new Image("GUI/assets/environment/stairs_up.png",size,size,true,false);
		Image downstairs = new Image("GUI/assets/environment/stairs_down.png",size,size,true,false);

		//entities (players, enemies)
		Image red_slime = new Image("GUI/assets/slimes/red_slime.png",size,size,true,false);
		Image orange_slime = new Image("GUI/assets/slimes/orange_slime.png",size,size,true,false);
		Image yellow_slime = new Image("GUI/assets/slimes/yellow_slime.png",size,size,true,false);
		Image green_slime = new Image("GUI/assets/slimes/green_slime.png",size,size,true,false);
		Image cyan_slime = new Image("GUI/assets/slimes/cyan_slime.png",size,size,true,false);
		Image blue_slime = new Image("GUI/assets/slimes/blue_slime.png",size,size,true,false);
		Image purple_slime = new Image("GUI/assets/slimes/purple_slime.png",size,size,true,false);
		Image rainbow_slime = new Image("GUI/assets/slimes/rainbow_slime.png",size,size,true,false);
		Image white_slime = new Image("GUI/assets/slimes/white_slime.png",size,size,true,false);
		Image black_slime = new Image("GUI/assets/slimes/black_slime.png",size,size,true,false);

		Image hero = new Image("GUI/assets/player1.png",size,size,true,false);

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
}

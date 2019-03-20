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
		Image slime = new Image("GUI/assets/slimes/green_slime.png",size,size,true,false);
		Image hero = new Image("GUI/assets/player1.png",size,size,true,false);

		for(int i=grid.length-1;i>=0;i--) {
			for(int j=grid[i].length-1;j>=0;j--) {
				g.drawImage(floor,j*size,i*size);

				Entity e = grid[i][j];

				if (e instanceof Obstacle) {
                    //"space" is a black box (representing where the wall isn't visible because of the roof)
                    if (i<grid.length-1 && grid[i+1][j] instanceof Obstacle) {
                        g.drawImage(space,j*size,i*size);
                    }
                    else {
                        g.drawImage(wall,j*size,i*size);
                    }
                }
				else if (e instanceof Stairs) {
					g.drawImage(downstairs,j*size,i*size);
				}
				else if (e instanceof Enemy) {
					g.drawImage(slime,j*size,i*size);
				}
                else if (grid[i][j] instanceof Player) {
                    g.drawImage(hero,j*size,i*size);
                }

				if (highlighted.contains(new Position(i,j))) {
					g.drawImage(highlight,j*size,i*size);
				}
				else {
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

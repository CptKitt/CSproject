package GUI;

import Model.*;
import javafx.scene.Group;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.io.File;

public class Display {
	public void drawMapOnScene(Map m, GraphicsContext g) {
		Entity[][] grid = m.getGrid();
		Image floor = new Image("GUI\\assets\\tile1.png",32,32,true,false);
		Image wall = new Image("GUI\\assets\\wall2.png",32,32,true,false);
		Image space = new Image("GUI\\assets\\wall3.png",32,32,true,false);

		double size = 32d;

		for(int i=grid.length-1;i>=0;i--) {
			for(int j=grid[i].length-1;j>=0;j--) {
				g.drawImage(floor,j*size,i*size);

				//"space" is a black box (representing where the wall isn't visible because of the roof)
				if (i<grid.length-1) {
					if (!(grid[i+1][j] instanceof Player) && grid[i+1][j] != null) {
						g.drawImage(space,j*size,i*size);
					}
					else if (!(grid[i][j] instanceof Player) && grid[i][j] != null) {
						g.drawImage(wall,j*size,i*size);
					}
				}
				else if (!(grid[i][j] instanceof Player) && grid[i][j] != null) {
					g.drawImage(wall,j*size,i*size);
				}
			}
		}
	}
}

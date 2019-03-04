package GUI;

import Model.Map;
import javafx.scene.Group;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Display {
	public void drawMapOnScene(Map m, GraphicsContext g) {
		String[][] grid = m.getGrid();
		Image floor = new Image("file:///C:/Users/Kitt/Desktop/Programming/CPSC233/Project/assets/tile1.png",32,32,true,false);
		Image wall = new Image("file:///C:/Users/Kitt/Desktop/Programming/CPSC233/Project/assets/wall2.png",32,32,true,false);
		Image space = new Image("file:///C:/Users/Kitt/Desktop/Programming/CPSC233/Project/assets/wall3.png",32,32,true,false);

		double size = 32d;

		for(int i=grid.length-1;i>=0;i--) {
			for(int j=grid[i].length-1;j>=0;j--) {
				if (grid[i][j].equals(".") || grid[i][j].equals("x")) {
					g.drawImage(floor,j*size,i*size);
				}
				if (i<grid.length-1) {
					if (grid[i+1][j].equals("#")) {
						g.drawImage(space,j*size,i*size);
					}
					else if (grid[i][j].equals("#")) {
						g.drawImage(wall,j*size,i*size);
					}
				}
				else if (grid[i][j].equals("#")) {
					g.drawImage(wall,j*size,i*size);
				}
			}
		}
	}
}

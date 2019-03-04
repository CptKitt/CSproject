package GUI;

import Model.Map;
import javafx.scene.Group;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Display {
	public void drawMapOnScene(Map m, Group s) {
		String[][] grid = m.getGrid();
		//GraphicsContext gc = new canvas.getGraphicsContext2D();
		Image floor = new Image("..//..//assets//tile1.png");

		for(int i=0;i<grid.length;i++) {
			for(int j=0;j<grid.length;j++) {
				//gc.drawImage(floor,j*16.0,i*16.0);
			}
		}
	}
}

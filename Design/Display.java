public class Display extends Map {

    Map dis = new Map();
    dis.Mapmethod();

    private String[][] =new String [10][10];

    public void printGrid() {
        for (int i = 0; i < grid.length; i++) {
            System.out.print((row % 10) + " ");
            for (int j = 0; j < grid[i].length; j++) {
                System.out.print(grid[i][j]);
            }
            System.out.print("\n");

        }
    }
    System.out.print(" ");
    for (int j = 0; j < 10; j++){
        System.out.print((j % 10));
    }
    System.out.println();
}
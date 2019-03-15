package Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class MapGenerator {
    private MapGenerator() { }
    
    private static Random rand = new Random();
    
    /** Generates an entirely random Map. */
    public static Entity[][] generateRandom(int width, int height) {
        Entity[][] map = new Entity[width][height];
        
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // 1/3 chance for a wall
                int tileChance = rand.nextInt(3);
                
                if (tileChance <= 1) {
                    map[x][y] = null;
                }
                else {
                    map[x][y] = newWall(new Position(x, y));
                }
            }
        }
        
        wallBorder(map);
        
        return map;
    }
    
    /**
     * Generates a large elliptical room.
     * Uses the ellipse equation [(x/w)^2 + (y/h)^2 < 1]
     * to check whether a tile is a wall or not.
     */
    public static Entity[][] generateCircle(int width, int height) {
        Entity[][] map = new Entity[width][height];
        
        // calculate circle dimensions
        double centerX = (double) width / 2;
        double centerY = (double) height / 2;
        double w = centerX - 1;
        double h = centerY - 1;
    
        // run ellipse calculations
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double dx = x - centerX + 0.5;
                double dy = y - centerY + 0.5;
            
                // inside ellipse, empty space
                if (Math.pow(dx / w, 2) + Math.pow(dy / h, 2) < 1) {
                    map[x][y] = null;
                }
                else {
                    // wall
                    map[x][y] = newWall(new Position(x, y));
                }
            }
        }
        
        return map;
    }
    
    /**
     * Generates a dungeon-like map.
     * Makes a number of rooms, then connects them with hallways.
     */
    public static Entity[][] generateDungeon(int width, int height) {
        Entity[][] map = new Entity[width][height];
        
        /**
         * Convenience class for room representation.
         */
        class Room {
            final Position origin;
            final int width, height;
            
            /**
             * @param origin The top left Position of the Room.
             * @param width The width of the Room.
             * @param height The height of the Room.
             */
            Room(Position origin, int width, int height) {
                this.origin = origin;
                this.width = width;
                this.height = height;
            }
            
            /**
             * @param other The Room to compare against.
             * @return True if the rooms are touching, false otherwise.
             */
            boolean touches(Room other) {
                // calculate expanded room to allow intersection check
                Room box = new Room(
                        other.origin.moved(-1, -1),
                        other.width + 2, other.height + 2);
                return !(origin.x > box.origin.x + box.width ||
                        origin.x + width < box.origin.x ||
                        origin.y > box.origin.y + box.height ||
                        origin.y + height < box.origin.y);
            }
            
            /**
             * @return A random Position inside the room.
             */
            Position randomPosition() {
                return origin.moved(
                        rand.nextInt(width),
                        rand.nextInt(height));
            }
        }
        
        // fill with walls
        fillWalls(map);
        
        List<Room> rooms = new ArrayList<>();
        int failed = 0;
        
        // fill with as many rooms as possible
        while (failed < 100) {
            // generate dimensions
            Position pos = new Position(
                    rand.nextInt(width - 4) + 1,
                    rand.nextInt(height - 4) + 1);
            int dw = Math.min(6, width - pos.x - 2);
            int roomW = rand.nextInt(dw) + 2;
            int dh = Math.min(6, height - pos.y - 2);
            int roomH = rand.nextInt(dh) + 2;
            
            // create room (rectangle)
            Room room = new Room(pos, roomW, roomH);
            
            // check for intersect with other rooms
            if (rooms.stream().anyMatch(r -> r.touches(room))) {
                failed++;
                continue;
            }
            
            // add, clear tiles
            rooms.add(room);
            for (int x = pos.x; x < pos.x + roomW; x++) {
                for (int y = pos.y; y < pos.y + roomH; y++) {
                    if (x >= 0 && x < width && y >= 0 && y < height) {
                        map[x][y] = null;
                    }
                }
            }
        }
        
        // generate hallways
        List<Room> connected = new ArrayList<>();
        connected.add(rooms.get(0));
        
        for (Room room : rooms) {
            if (connected.isEmpty()) {
                connected.add(room);
                continue;
            }
            
            // pick random connected room
            Room room2 = connected.get(rand.nextInt(connected.size()));
            connected.add(room);
            
            Position start = room.randomPosition();
            Position end = room2.randomPosition();
            
            // find path and clear
            for (Position pos : Pathfinding.shortestPath(p -> true, start, end)) {
                map[pos.x][pos.y] = null;
            }
        }
        
        return map;
    }
    
    /**
     * Generates a cave-like map.
     * Creates a number of connected lines and
     * empties out the tiles near them.
     */
    public static Entity[][] generateCave(int width, int height) {
        Entity[][] map = new Entity[width][height];
        
        // all walls to begin
        fillWalls(map);
        
        // loop generation
        int probability = 130;
        Position start = new Position(
                rand.nextInt(width),
                rand.nextInt(height));
        do {
            // random end position
            Position end;
            do {
                end = new Position(
                        rand.nextInt(width),
                        rand.nextInt(height));
            } while (start.distanceTo(end) <= 8);
            
            // find random line to end position
            // (Pathfinding A* is too linear)
            List<Position> line = new ArrayList<>();
            Position move = start;
            while (!move.equals(end)) {
                line.add(move);
                
                if (move.x != end.x) {
                    if (move.y != end.y && rand.nextBoolean()) {
                        move = move.moved(0, move.y > end.y ? -1 : 1);
                    }
                    else {
                        move = move.moved(move.x > end.x ? -1 : 1, 0);
                    }
                }
                else {
                    move = move.moved(0, move.y > end.y ? -1 : 1);
                }
            }
            
            // clear around line
            for (Position pos : line) {
                double radius = Math.sqrt(rand.nextInt(20));
                
                // inefficiently check all tiles
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        double dx = Math.pow(x - pos.x, 2);
                        double dy = Math.pow(y - pos.y, 2);
                        
                        // inside radius, clear wall
                        if (dx + dy < radius) {
                            map[x][y] = null;
                        }
                    }
                }
            }
            
            // prep for new line
            start = end;
            
            // chance to continue cave
            probability -= 30;
        } while (rand.nextInt(100) < probability);
        
        wallBorder(map);
        
        return map;
    }
    
    /** Convenience function to fill the map with walls. */
    private static void fillWalls(Entity[][] map) {
        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[0].length; y++) {
                map[x][y] = newWall(new Position(x, y));
            }
        }
    }
    
    /** Convenience function to fill the border of the map with walls. */
    private static void wallBorder(Entity[][] map) {
        int maxX = map.length - 1, maxY = map[0].length - 1;
        
        for (int x = 0; x <= maxX; x++) {
            map[x][0] = newWall(new Position(x, 0));
            map[x][maxY] = newWall(new Position(x, maxY));
        }
        
        for (int y = 0; y <= maxY; y++) {
            map[0][y] = newWall(new Position(0, y));
            map[maxX][y] = newWall(new Position(maxX, y));
        }
    }
    
    /** Function to create stairs. */
    private static Stairs newStairs(Position position) {
        Stairs stairs = new Stairs();
        stairs.setPOS(position);
        return stairs;
    }
    
    /** Function to create a wall. */
    private static Obstacle newWall(Position position) {
        Obstacle obstacle = new Obstacle();
        obstacle.setPOS(position);
        return obstacle;
    }
    
    /** Function to return a player. */
    private static Player newPlayer(Position position) {
        Player player = Player.randomPlayer();
        player.setPOS(position);
        return player;
    }
    
    /** Function to return an enemy. */
    private static Enemy newEnemy(Position position) {
        Enemy enemy = Enemy.randomEnemy(newPlayer(position));
        enemy.setPOS(position);
        return enemy;
    }
}

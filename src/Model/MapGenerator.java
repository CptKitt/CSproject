package Model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * A class containing a collection of
 * static methods related to Map generation.
 */
public final class MapGenerator {
    private MapGenerator() { }
    
    private static Random rand = new Random();
    
    // helper methods to avoid infinite loops
    private static int count = 0;
    private static void resetCount() {
        count = 0;
    }
    private static int count() {
        count++;
        return count;
    }
    
    /** Generates one of the possible map types. */
    static Entity[][] randomMap(int width, int height) {
        switch (rand.nextInt(3)) {
            case 0: return generateCave(width, height);
            case 1: return generateCircle(width, height);
            case 2: return generateDungeonHallway(width, height);
            default: return new Entity[width][height];
        }
    }
    
    /** Generates an entirely random Map. */
    static Entity[][] generateRandom(int width, int height) {
        Entity[][] map = new Entity[width][height];
        
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // 1/3 chance for a wall
                int tileChance = rand.nextInt(3);
                
                if (tileChance <= 1) {
                    map[x][y] = null;
                }
                else {
                    newWall(map, x, y);
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
    static Entity[][] generateCircle(int width, int height) {
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
                    newWall(map, x, y);
                }
            }
        }
        
        // variations: 0 = no variation
        int variant = rand.nextInt(3);
        
        // quadrants
        if (variant == 1) {
            // generate walls
            int cx = (int) centerX;
            int cy = (int) centerY;
            for (int x = 0; x < width; x++) {
                newWall(map, x, cy);
            }
            for (int y = 0; y < height; y++) {
                newWall(map, cx, y);
            }
            
            // generate doors
            int missingDoor = rand.nextInt(4);
            if (missingDoor != 0) {
                int offset = rand.nextInt(cy - 2) + 1;
                map[cx][offset] = null;
            }
            if (missingDoor != 1) {
                int offset = rand.nextInt(cy - 2) + 1;
                map[cx][cy + offset] = null;
            }
            if (missingDoor != 2) {
                int offset = rand.nextInt(cx - 2) + 1;
                map[offset][cy] = null;
            }
            if (missingDoor != 3) {
                int offset = rand.nextInt(cx - 2) + 1;
                map[cx + offset][cy] = null;
            }
        }
        // center pillar (donut)
        else if (variant == 2) {
            w /= 2;
            h /= 2;
            
            // same, smaller ellipse calculations
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    double dx = x - centerX + 0.5;
                    double dy = y - centerY + 0.5;
                    
                    if (Math.pow(dx / w, 2) + Math.pow(dy / h, 2) < 1) {
                        newWall(map, x, y);
                    }
                }
            }
        }
        
        return map;
    }
    
    /** Generates a dungeon-like map. */
    static Entity[][] generateDungeon(int width, int height) {
        switch (rand.nextInt(2)) {
            case 0: return generateDungeonRooms(width, height);
            case 1: return generateDungeonHallway(width, height);
            default: return new Entity[0][0];
        }
    }
    
    /** Generates a hallway surrounded by rooms. */
    private static Entity[][] generateDungeonHallway(int width, int height) {
        Entity[][] map = new Entity[width][height];
        wallBorder(map);
        
        int topY = rand.nextInt(height/2 - 4) + 3;
        int botY = height/2 + rand.nextInt(height/2 - 4) + 1;
        
        for (int x = 0; x < width; x++) {
            newWall(map, x, topY);
            newWall(map, x, botY);
        }
        
        // top rooms
        boolean door = true;
        int x = rand.nextInt(5) + 2;
        do {
            if (door) {
                map[x][topY] = null;
            }
            else {
                for (int y = 0; y < topY; y++) {
                    newWall(map, x, y);
                }
            }
            door = !door;
            
            x += rand.nextInt(5) + 2;
        } while (x < width - 2);
        
        if (door) {
            map[width-2][topY] = null;
        }
        
        // bottom rooms
        door = true;
        x = rand.nextInt(5) + 2;
        do {
            if (door) {
                map[x][botY] = null;
            }
            else {
                for (int y = height-1; y > botY; y--) {
                    newWall(map, x, y);
                }
            }
        
            door = !door;
    
            x += rand.nextInt(5) + 2;
        } while (x < width - 2);
    
        if (door) {
            map[width-2][botY] = null;
        }
        
        return map;
    }
    
    /** Generates a number of rooms connected by hallways. */
    private static Entity[][] generateDungeonRooms(int width, int height) {
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
                        rand.nextInt(width-2)+1,
                        rand.nextInt(height-2)+1);
            }
        }
        
        // fill with walls
        fillWalls(map);
        
        List<Room> rooms = new ArrayList<>();
        resetCount();
        
        // fill with as many rooms as possible
        while (count() < 1000) {
            // generate dimensions
            Position pos = new Position(
                    rand.nextInt(width - 6) + 1,
                    rand.nextInt(height - 6) + 1);
            int dw = Math.min(10, width - pos.x - 3);
            int roomW = rand.nextInt(dw) + 3;
            int dh = Math.min(10, height - pos.y - 3);
            int roomH = rand.nextInt(dh) + 3;
            
            // create room (rectangle)
            Room room = new Room(pos, roomW, roomH);
            
            // check for intersect with other rooms
            if (rooms.stream().anyMatch(r -> r.touches(room))) {
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
    
    /** Generates a cave-like map. */
    static Entity[][] generateCave(int width, int height) {
        switch (rand.nextInt(2)) {
            case 0: return generateMessyCave(width, height);
            case 1: return generateMine(width, height);
            default: return new Entity[0][0];
        }
    }
    
    /** Generates a number of lines with random cleared out near them. */
    private static Entity[][] generateMessyCave(int width, int height) {
        Entity[][] map = new Entity[width][height];
        
        // all walls to begin
        fillWalls(map);
        
        // loop generation
        int probability = 140;
        Position start = new Position(
                rand.nextInt(width-2)+1,
                rand.nextInt(height-2)+1);
        do {
            // random end position
            resetCount();
            Position end;
            do {
                end = new Position(
                        rand.nextInt(width-2)+1,
                        rand.nextInt(height-2)+1);
            } while (start.distanceTo(end) <= 8 && count() < 1000);
            
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
                Deque<Position> toClear = new ArrayDeque<>();
                toClear.add(pos);
                
                // distance based probability clear
                while (!toClear.isEmpty()) {
                    Position newPos = toClear.removeFirst();
                    if (positionInRange(newPos, width, height)
                            && map[newPos.x][newPos.y] != null
                            && rand.nextInt(newPos.distanceTo(pos) + 1) == 0) {
                        map[newPos.x][newPos.y] = null;
                        toClear.addAll(Arrays.asList(newPos.adjacentPositions()));
                    }
                }
            }
            
            // prep for new line
            start = end;
            
            // chance to continue cave
            probability -= 20;
        } while (rand.nextInt(100) < probability);
        
        wallBorder(map);
        
        return map;
    }
    
    /** Generates long, connected, fixed size lines. */
    private static Entity[][] generateMine(int width, int height) {
        Entity[][] map = new Entity[width][height];
        
        // all walls to begin
        fillWalls(map);
        
        // generate points
        int minDist = (width + height) / 5;
        int nLines = rand.nextInt(3) + 4;
        List<Position> points = new ArrayList<>();
        resetCount();
        for (int i = 0; i < nLines; i++) {
            Position pos = new Position(
                    rand.nextInt(width-2)+1,
                    rand.nextInt(height-2)+1);
            
            if (count() > 1000) {
                points.add(pos);
            }
            else if (points.stream().anyMatch(p -> p.distanceTo(pos) < minDist)) {
                i--;
            }
            else {
                points.add(pos);
            }
        }
        
        // loop to create lines
        for (int i = 1; i < nLines; i++) {
            Position start = points.get(i-1);
            Position end = points.get(i);
            
            // find line to end position
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
                map[pos.x][pos.y] = null;
                for (Position pos2: pos.adjacentPositions()) {
                    if (positionInRange(pos2, width, height)) {
                        map[pos2.x][pos2.y] = null;
                    }
                }
            }
            
            // prep for new line
            start = end;
        }
        
        wallBorder(map);
        
        return map;
    }
    
    /** Generates a boss room. */
    static Entity[][] generateBossRoom(int width, int height) {
        Entity[][] map = new Entity[width][height];
        
        // double bordered walls
        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[0].length; y++) {
                if (x < 2 || x > map.length - 3 || y < 2 || y > map[0].length - 3) {
                    newWall(map, x, y);
                }
            }
        }
        
        // pillars
        int xDist = (width - 4) / 4;
        int yDist = (height - 4) / 4;
        for (int x = xDist-1; x < width - 2; x += xDist) {
            for (int y = yDist-1; y < height - 2; y += yDist) {
                newWall(map, x, y);
                newWall(map, x+1, y);
                newWall(map, x, y+1);
                newWall(map, x+1, y+1);
            }
        }
        
        // marker for placement functions
        map[0][0] = null;
        return map;
    }
    
    /** Places player(s) on the Map. */
    static void placePlayers(Entity[][] map, List<Player> players) {
        int width = map.length;
        int height = map[0].length;
        Position start;
        
        // special case: boss room
        if (!(map[0][0] instanceof Obstacle)) {
            start = new Position(width - 4, height/2);
        }
        else {
            // random starting point
            do {
                start = new Position(
                        rand.nextInt(width),
                        rand.nextInt(height));
            } while (map[start.x][start.y] != null);
        }
    
        // place new player
        if (players.isEmpty()) {
            players.add(newPlayer(map, start));
        }
        // place existing players nearby
        else {
            // try to place as close as possible
            double dist = 1;
            for (Player player : players) {
                Position pos;
                do {
                    int iDist = (int) dist;
                    pos = start.moved(
                            rand.nextInt(iDist*2+1)-iDist,
                            rand.nextInt(iDist*2+1)-iDist);
                    dist += 0.1;
                }
                while (!positionInRange(pos, width, height) || map[pos.x][pos.y] != null);
                
                // empty space, place player
                map[pos.x][pos.y] = player;
                player.setPOS(pos);
            }
        }
    }
    
    /** Places stairs on the Map. */
    static Stairs placeStairs(Entity[][] map, List<Player> players) {
        // special case: boss room
        if (map[0][0] == null) {
            return newStairs(map, Position.ORIGIN);
        }
    
        Stairs stairs;
        int minDist = (map.length + map[0].length) / 6;
        resetCount();
        
        // random starting point
        while (true) {
            Position pos = new Position(
                    rand.nextInt(map.length),
                    rand.nextInt(map[0].length));
            
            // too many tries, allow any position
            if (count() > 1000 && map[pos.x][pos.y] == null) {
                return newStairs(map, pos);
            }
            // not covered and far enough away from players
            if (map[pos.x][pos.y] == null && players.stream()
                    .allMatch(player -> player.getPOS().distanceTo(pos) > minDist)) {
                return newStairs(map, pos);
            }
        }
    }
    
    /** Places and returns enemies on the Map. */
    static List<Enemy> placeEnemies(Entity[][] map, List<Player> players, int floor) {
        List<Enemy> enemies = new ArrayList<>();
        int minDist = (map.length + map[0].length) / 8;
        resetCount();
    
        // special case: boss room
        if (!(map[0][0] instanceof Obstacle)) {
            // place STRONK enemy
            Enemy boss = Enemy.randomEnemy(floor + 4);
            boss.setSPD(5);
            boss.setPOS(new Position(4, map[0].length/2));
            map[4][map[0].length/2] = boss;
            enemies.add(boss);
        }
        
        // random number of enemies
        int num = rand.nextInt(5) + 3;
        for (int i = 0; i < num && count() < 1000; i++) {
            // find position away from player
            Position pos = new Position(
                    rand.nextInt(map.length),
                    rand.nextInt(map[0].length));
            
            if (map[pos.x][pos.y] != null ||
                    players.stream().anyMatch(
                            player -> player.getPOS().distanceTo(pos) < minDist)) {
                i--;
            }
            else {
                enemies.add(newEnemy(map, pos, floor));
            }
        }
        
        return enemies;
    }
    
    /** @param path The path of the file to read from. */
    static Entity[][] readMapFromFile(String path) {
        try {
            // get all lines
            Scanner in = new Scanner(new File(path));
            List<String> lines = new ArrayList<>();
            while (in.hasNextLine()) {
                lines.add(in.nextLine());
            }
            in.close();
            
            if (lines.isEmpty()) {
                return new Entity[0][0];
            }
            
            // form grid
            int width = lines.get(0).length();
            int height = lines.size();
            
            Entity[][] map = new Entity[width][height];
            
            // read information
            for (int y = 0; y < height; y++) {
                String line = lines.get(y);
                for (int x = 0; x < width; x++) {
                    if (line.charAt(x) == ' ') { // empty space
                        map[x][y] = null;
                    }
                    else if (line.charAt(x) == 'W') { // wall
                        newWall(map, x, y);
                    }
                    else if (line.charAt(x) == 'C') { // some character
                        newPlayer(map, new Position(x, y));
                    }
                }
            }
            
            return map;
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    
        return new Entity[0][0];
    }
    
    /** Convenience function to fill the map with walls. */
    private static void fillWalls(Entity[][] map) {
        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[0].length; y++) {
                newWall(map, x, y);
            }
        }
    }
    
    /** Convenience function to fill the border of the map with walls. */
    private static void wallBorder(Entity[][] map) {
        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[0].length; y++) {
                if (x == 0 || x == map.length-1 || y == 0 || y == map[0].length-1) {
                    newWall(map, x, y);
                }
            }
        }
    }
    
    /** Function to place stairs. */
    private static Stairs newStairs(Entity[][] map, Position position) {
        Stairs stairs = new Stairs();
        stairs.setPOS(position);
        map[position.x][position.y] = stairs;
        return stairs;
    }
    
    /** Function to place a wall. */
    private static Obstacle newWall(Entity[][] map, int x, int y) {
        Obstacle obstacle = new Obstacle();
        obstacle.setPOS(new Position(x, y));
        map[x][y] = obstacle;
        return obstacle;
    }
    
    /** Function to place a player. */
    private static Player newPlayer(Entity[][] map, Position position) {
        Player player = Player.randomPlayer();
        player.setPOS(position);
        map[position.x][position.y] = player;
        return player;
    }
    
    /** Function to place an enemy. */
    private static Enemy newEnemy(Entity[][] map, Position position, int floor) {
        Enemy enemy = Enemy.randomEnemy(floor);
        enemy.setPOS(position);
        map[position.x][position.y] = enemy;
        return enemy;
    }
    
    /** Returns true if the Position is in the range provided. */
    private static boolean positionInRange(Position pos, int width, int height) {
        return positionInRange(pos.x, pos.y, width, height);
    }
    
    /** Returns true if the coordinates are in the range provided. */
    private static boolean positionInRange(int x, int y, int width, int height) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
}

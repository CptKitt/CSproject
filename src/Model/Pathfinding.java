package Model;

import java.util.*;

/**
 * Class containing static methods for use in 2D grid pathfinding.
 * The class itself cannot be instantiated.
 * <p></p>
 * The pathfinding methods require a {@link Pathfinding.Delegate},
 * which provides information about the map.
 */
public final class Pathfinding {
    
    /**
     * Interface for classes that use Pathfinding methods.
     * An anonymous class or lambda may also be used
     * rather than directly implementing this interface.
     */
    public interface Delegate {
        /**
         * Returns true if the position is valid.
         * This should return false if the position is out of bounds
         * or there are objects blocking it.
         * @param p The position to check.
         * @return True if the position is passable, false otherwise.
         */
        boolean validPosition(Position p);
    
        /**
         * Returns true if the position is see-through.
         * This should return false if the position is out of bounds
         * or there are solid objects obstructing vision.
         * <p></p>
         * By default, this method returns {@code validPosition()}.
         * It may be overridden for cases where positions are
         * not valid, but may still be seen through.
         * @param p The position to check.
         * @return True if the position is see-through, false otherwise.
         */
        default boolean transparentPosition(Position p) {
            return validPosition(p);
        }
    }
    
    /**
     * Pathfinding-exclusive Position wrapper class containing
     * additional information used in A* pathfinding.
     * <p></p>
     * Implementation of Comparable allows for use in a
     * comparison-based priority queue, usually a TreeSet or TreeMap.
     */
    static private final class PathPosition
            implements Comparable<PathPosition> {
        final Position position;
        
        /**
         * The cost taken to arrive at this Position, g(n).
         */
        int cost;
        
        /**
         * The distance to the destination, h(n).
         */
        int distance;
        
        /**
         * @return The overall priority of the PathPosition, g(n) + h(n).
         */
        int priority() {
            return cost + distance;
        }
        
        PathPosition(Position pos, int cost, int distance) {
            position = pos;
            this.cost = cost;
            this.distance = distance;
        }
        
        @Override
        public String toString() {
            return position.toString() + "[" + cost +
                    ":" + distance + ":" + priority() + "]";
        }
        
        // Methods required for use in Sets and Maps
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof PathPosition)) {
                return false;
            }
            PathPosition other = (PathPosition) obj;
            return cost == other.cost && distance == other.distance
                    && position.equals(other.position);
        }
        
        @Override
        public int hashCode() {
            return (distance << 8) ^ (cost << 4) ^ position.hashCode();
        }
        
        @Override
        public int compareTo(PathPosition other) {
            if (priority() < other.priority()) return -1;
            if (priority() > other.priority()) return 1;
            return position.compareTo(other.position);
        }
    }
    
    /** Disallow instantiation. */
    private Pathfinding() { }
    
    /**
     * Given the starting position and the range of movement,
     * calculates all possible movement options.
     * <p></p>
     * There are no guarantees as to the order of the positions,
     * so the result is returned in a Set.
     * @param delegate The pathfinding delegate.
     * @param start The starting position.
     * @param range The range of movement.
     * @return A Set of Positions containing legal positions.
     */
    public static Set<Position> movementForPosition(
            Delegate delegate, Position start, int range) {
        // prep for search
        Set<Position> positions = new HashSet<>();
        Deque<Position> frontier = new ArrayDeque<>();
        Map<Position, Integer> distances = new HashMap<>();
        frontier.add(start);
        distances.put(start, 0);
        
        // loop until frontier exhausted
        while (!frontier.isEmpty()) {
            // pop position off queue
            Position pos = frontier.removeLast();
            
            // add to positions
            positions.add(pos);
            
            // hit max distance
            int dist = distances.get(pos);
            if (dist == range) {
                continue;
            }
            
            // loop through all adjacent positions
            for (Position newPos : pos.adjacentPositions()) {
                // ignore invalid and already visited positions
                // (BFS always finds the shortest path)
                if (delegate.validPosition(newPos)
                        && !distances.containsKey(newPos)) {
                    // update distance, add to frontier queue
                    distances.put(newPos, dist + 1);
                    frontier.addFirst(newPos);
                }
            }
        }
        
        return positions;
    }
    
    /**
     * Searches for the shortest path from one Position
     * to another using A* pathfinding.
     * @param delegate The pathfinding delegate.
     * @param start The starting position.
     * @param end The destination position.
     * @return An ArrayList of positions to traverse, in order,
     *         to get to the destination.
     */
    public static List<Position> shortestPath(
            Delegate delegate, Position start, Position end) {
        // setup
        TreeSet<PathPosition> frontier = new TreeSet<>();
        HashMap<Position, PathPosition> history = new HashMap<>();
        frontier.add(new PathPosition(start, 0, start.distanceTo(end)));
    
        // populate until goal reached
        while (!frontier.isEmpty()) {
            PathPosition pos = frontier.pollFirst();
        
            // reached goal, finish
            if (end.equals(pos)) {
                // backtrack to find path
                Position track = pos.position;
                List<Position> path = new ArrayList<>();
                while (!track.equals(start)) {
                    path.add(track);
                    track = history.get(track).position;
                }
            
                // faster to append then reverse: O(2n)
                Collections.reverse(path);
                return path;
            }
        
            // loop through adjacent positions
            for (Position newPos : pos.position.adjacentPositions()) {
                PathPosition newPathPos = new PathPosition(newPos,
                        pos.cost + 1, newPos.distanceTo(end));
                // ignore invalid positions and better paths
                if (delegate.validPosition(newPos)
                        && (!history.containsKey(newPos)
                        || history.get(newPos).priority()
                        > pos.priority())) {
                    // add to backtracking and frontier
                    history.put(newPos, pos);
                    frontier.add(newPathPos);
                }
            }
        }
    
        // frontier exhausted and didn't find path
        // find tile closest to destination
        Position closest = start;
        int lowestCost = 0;
        for (Map.Entry<Position, PathPosition> e : history.entrySet()) {
            if (e.getKey().distanceTo(end) < closest.distanceTo(end)
                    || e.getValue().cost < lowestCost) {
                closest = e.getKey();
                lowestCost = e.getValue().cost;
            }
        }
    
        // reroute: will only recurse once at maximum
        return shortestPath(delegate, start, closest);
    }
    
    /**
     * Checks whether there is line of sight between two positions.
     * <p></p>
     * This method does not take range into account, and will
     * return true if line of sight exists no matter the distance.
     * @param delegate The pathfinding delegate.
     * @param p1 The first position.
     * @param p2 The second position.
     * @return True if there is an unobstructed line between
     *         the two positions, or false otherwise.
     */
    public static boolean lineOfSight(
            Delegate delegate, Position p1, Position p2) {
        return visibility(delegate, p1, -1).contains(p2);
    }
    
    /**
     * Calculates all the positions visible to the position.
     * @param delegate The pathfinding delegate.
     * @param p The origin to check from.
     * @param range The maximum range to check.
     *              If <= 0, will allow unlimited range.
     * @return A Set of positions visible from the origin.
     */
    public static Set<Position> visibility(
            Delegate delegate, Position p, int range) {
        return new HashSet<>();
    }
}
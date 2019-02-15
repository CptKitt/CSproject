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
    }
    
    /**
     * Pathfinding-exclusive Position type containing additional
     * information used in A* pathfinding.
     * <p></p>
     * Implementation of Comparable allows for use in a
     * comparison-based priority queue, usually a TreeSet or TreeMap.
     */
    static private class PathPosition extends Position
            implements Comparable<PathPosition> {
        /**
         * The cost taken to arrive at this Position, g(n).
         */
        private int cost;
        
        /**
         * The distance to the destination, h(n).
         */
        private int distance;
        
        /**
         * @return The overall priority of the PathPosition, g(n) + h(n).
         */
        int priority() {
            return cost + distance;
        }
        
        PathPosition(Position pos, int cost, int distance) {
            super(pos);
            this.cost = cost;
            this.distance = distance;
        }
        
        @Override
        public String toString() {
            return super.toString() + "[" + cost +
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
                    && super.equals(obj);
        }
        
        @Override
        public int hashCode() {
            return (distance << 16) ^ cost ^ (super.hashCode() << 8);
        }
        
        @Override
        public int compareTo(PathPosition other) {
            if (priority() < other.priority()) return -1;
            if (priority() > other.priority()) return 1;
            if (x < other.x) return -1;
            if (x > other.x) return 1;
            return Integer.compare(y, other.y);
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
        frontier.add(new Position(start));
        distances.put(new Position(start), 0);
        
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
        Map<Position, PathPosition> history = new HashMap<>();
        frontier.add(new PathPosition(start, 0, start.distanceTo(end)));
        Position last = null;
    
        // populate until goal reached
        while (!frontier.isEmpty()) {
            PathPosition pos = frontier.pollFirst();
        
            // reached goal, finish
            if (end.equals(pos)) {
                last = new Position(pos);
                break;
            }
        
            // loop through adjacent positions
            for (Position newPos : pos.adjacentPositions()) {
                PathPosition newPathPos = new PathPosition(newPos,
                        pos.cost + 1, newPos.distanceTo(end));
                // ignore invalid positions and better paths
                if (delegate.validPosition(newPathPos)
                        && (!history.containsKey(newPos)
                        || history.get(newPos).priority()
                        > pos.priority())) {
                    // add to backtracking and frontier
                    history.put(newPos, pos);
                    frontier.add(newPathPos);
                }
            }
        }
    
        // didn't find path
        if (last == null) {
            // default to closest tile with lowest cost
            /* TODO: The algorithm doesn't always find the
                best route to the tile closest to the destination.
                Maybe return shortestPath() again with the
                closest tile as the destination, though
                performance might be affected.
             */
            last = history.entrySet().stream().min((e1, e2) -> {
                Position p1 = e1.getKey(), p2 = e2.getKey();
                if (p1.distanceTo(end) < p2.distanceTo(end)) return -1;
                if (p1.distanceTo(end) > p2.distanceTo(end)) return 1;
                return Integer.compare(e1.getValue().cost, e2.getValue().cost);
            }).map(e -> new Position(e.getKey())).orElse(start);
        }
    
        // backtrack to find path
        List<Position> path = new ArrayList<>();
        while (!start.equals(last)) {
            path.add(last);
            last = new Position(history.get(last));
        }
    
        // faster to append then reverse: O(2n)
        Collections.reverse(path);
        return path;
    }
    
    /**
     * Checks whether there is line of sight between two positions.
     * @param delegate The pathfinding delegate.
     * @param p1 The first position.
     * @param p2 The second position.
     * @return True if there is an unobstructed line between
     *         the two positions, or false otherwise.
     */
    public static boolean lineOfSight(
            Delegate delegate, Position p1, Position p2) {
        return false;
    }
}
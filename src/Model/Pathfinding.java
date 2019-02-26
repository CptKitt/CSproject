package Model;

import java.util.*;

/**
 * Abstract class containing static methods
 * for use in 2D grid pathfinding.
 * <p></p>
 * The pathfinding methods require a {@link Pathfinding.Delegate},
 * which provides information about the map.
 */
public abstract class Pathfinding {
    
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
        HashMap<Position, Integer> distances = new HashMap<>();
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
}
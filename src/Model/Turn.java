package Model;

import java.util.List;

/**
 * Class representing an action taken by an Enemy or Player.
 * This class is only used by Map to dispense information
 * to the controller and user, so changing the variables
 * of a Turn does not affect Map in any way.
 * Thus, the variables do not have to be encapsulated.
 */
public final class Turn {
    /** The path taken by the Entity to get from start to end. */
    public List<Position> path;
    
    /** The starting Position of the Entity. */
    public Position start;
    
    /** The end Position of the Entity after taking the Turn. */
    public Position end;
    
    /** A Position that the Entity attacked. May be null. */
    public Position attackPos;
    
    /** The damage dealt to the attacked Entity. */
    public int damage;
    
    /** Creates a Turn with all variables null. */
    public Turn() { }
    
    /**
     * Connects start and end using a delegate.
     * @param delegate The Pathfinding.Delegate to use for Pathfinding.
     */
    public void pathfind(Pathfinding.Delegate delegate) {
        if (start == null || end == null) {
            return;
        }
        path = Pathfinding.shortestPath(delegate, start, end);
    }
    
    @Override
    public String toString() {
        return start + " + " + path + " -> "
                + end + " X " + attackPos;
    }
}

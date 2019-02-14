package Model;

/**
 * Represents an integer point on a 2D grid.
 */
public class Position {
    public int x, y;
    
    /**
     * Creates a new Position.
     * @param x The x coordinate of the Position.
     * @param y The y coordinate of the Position.
     */
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Creates a new Position using information from another Position.
     * @param other The position to copy coordinates from.
     */
    public Position(Position other) {
        this(other.x, other.y);
    }
    
    /**
     * @param other The position to compare against.
     * @return The rectilinear distance to the other point.
     */
    public int distanceTo(Position other) {
        return Math.abs(x - other.x) + Math.abs(y - other.y);
    }
    
    /**
     * @return The four Positions adjacent to this Position.
     */
    public Position[] adjacentPositions() {
        return new Position[] {
                new Position(x + 1, y),
                new Position(x - 1, y),
                new Position(x, y + 1),
                new Position(x, y - 1)
        };
    }
    
    @Override
    public boolean equals(Object obj) {
        // can't be equal to non-position
        if (!(obj instanceof Position)) {
            return false;
        }
        // cast and compare coordinates
        Position other = (Position) obj;
        return x == other.x && y == other.y;
    }
    
    @Override
    public int hashCode() {
        return (x << 16) ^ y;
    }
    
    @Override
    public String toString() {
        return "P(" + x + "," + y + ")";
    }
}


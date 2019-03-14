package Model;

import java.util.List;

public class Turn {
    public List<Position> path;
    public Position start;
    public Position end;
    public Position attackPos;
    
    Turn() {
        path = null;
        start = Position.NONE;
        end = Position.NONE;
        attackPos = null;
    }
}

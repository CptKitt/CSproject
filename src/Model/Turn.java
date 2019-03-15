package Model;

import java.util.List;

public class Turn {
    public List<Position> path;
    public Position start;
    public Position end;
    public Position attackPos;
    
    Turn() {
        path = null;
        start = null;
        end = null;
        attackPos = null;
    }
    
    public void pathfind(Map map) {
        if (start == null || end == null) {
            return;
        }
        path = Pathfinding.shortestPath(map, start, end);
    }
    
    Turn(Map map, Position start, Position end, Position attackPos) {
        path = Pathfinding.shortestPath(map, start, end);
        this.start = start;
        this.end = end;
        this.attackPos = attackPos;
    }
}

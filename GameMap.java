import java.util.ArrayList;
import java.util.List;

public class GameMap {
    private final int width;
    private final int height;
    private final Tile[][] tiles;
    private final int safeZoneSize;

    public GameMap(int width, int height, int safeZoneSize) {
        this.width = width;
        this.height = height;
        this.safeZoneSize = safeZoneSize;
        this.tiles = new Tile[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = new Tile(x, y);
            }
        }
        markSafeZones();
    }

    private void markSafeZones() {
        markCorner(0, 0, FireNation.class);
        markCorner(width - safeZoneSize, 0, EarthKingdom.class);
        markCorner(0, height - safeZoneSize, WaterTribe.class);
        markCorner(width - safeZoneSize, height - safeZoneSize, AirNomad.class);
    }

    private void markCorner(int originX, int originY, Class<? extends Individual> faction) {
        for (int dx = 0; dx < safeZoneSize; dx++) {
            for (int dy = 0; dy < safeZoneSize; dy++) {
                tiles[originX + dx][originY + dy].markAsSafeZone(faction);
            }
        }
    }

    // Spec: obstacles generated randomly at the start of the simulation.
    // Only ever lands on empty, non-SafeZone tiles.
    public void placeRandomObstacles(int count) {
        int placed = 0;
        int attempts = 0;
        while (placed < count && attempts < count * 50) {
            attempts++;
            int rx = RandomGenerator.randomInt(0, width - 1);
            int ry = RandomGenerator.randomInt(0, height - 1);
            Tile tile = tiles[rx][ry];
            if (tile.isEmpty() && !tile.isSafeZone()) {
                tile.setOccupant(new Obstacle());
                placed++;
            }
        }
    }

    public Tile getTile(int x, int y) {
        return tiles[x][y];
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    // Spec: 3 tiles at a corner, 5 on an edge, 8 everywhere else.
    // No special-casing needed - bounds-checking the 3x3 block does it for free.
    public List<Tile> getNeighbors(int x, int y) {
        List<Tile> neighbors = new ArrayList<>();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                int nx = x + dx;
                int ny = y + dy;
                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    neighbors.add(tiles[nx][ny]);
                }
            }
        }
        return neighbors;
    }

    // Which of the 8 compass directions points from (x,y) toward the given
    // faction's SafeZone corner. Used when EP drops below 20% (spec rule).
    public Direction getDirectionToSafeZone(int x, int y, Class<? extends Individual> faction) {
        int targetX, targetY;
        if (faction == FireNation.class)      { targetX = 0;         targetY = 0; }
        else if (faction == EarthKingdom.class) { targetX = width - 1;  targetY = 0; }
        else if (faction == WaterTribe.class)   { targetX = 0;         targetY = height - 1; }
        else                                     { targetX = width - 1;  targetY = height - 1; } // AirNomad

        int dx = Integer.compare(targetX, x); // -1, 0, or 1
        int dy = Integer.compare(targetY, y);

        if (dx == 0 && dy == -1) return Direction.NORTH;
        if (dx == 1 && dy == -1) return Direction.NORTH_EAST;
        if (dx == 1 && dy == 0)  return Direction.EAST;
        if (dx == 1 && dy == 1)  return Direction.SOUTH_EAST;
        if (dx == 0 && dy == 1)  return Direction.SOUTH;
        if (dx == -1 && dy == 1) return Direction.SOUTH_WEST;
        if (dx == -1 && dy == 0) return Direction.WEST;
        if (dx == -1 && dy == -1) return Direction.NORTH_WEST;
        return null; // already standing on the SafeZone corner tile
    }
}

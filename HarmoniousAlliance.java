// Common alliance-level trait for Water Tribe and Air Nomads. Random
// exploration is bishop-like: diagonals only.
public abstract class HarmoniousAlliance extends Individual {

    protected static final Direction[] MOVEMENT_DIRECTIONS = {
        Direction.NORTH_EAST, Direction.SOUTH_EAST, Direction.SOUTH_WEST, Direction.NORTH_WEST
    };

    protected HarmoniousAlliance(int startEP, int x, int y) {
        super(startEP, x, y);
    }
}

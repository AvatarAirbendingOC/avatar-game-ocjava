// Common alliance-level trait for the "flow" benders: Water Tribe and Air
// Nomads. Their random-exploration movement is bishop-like: diagonals only.
public abstract class AllianceFlow extends Individual {

    protected static final Direction[] MOVEMENT_DIRECTIONS = {
        Direction.NORTH_EAST, Direction.SOUTH_EAST, Direction.SOUTH_WEST, Direction.NORTH_WEST
    };

    protected AllianceFlow(int startEP, int x, int y) {
        super(startEP, x, y);
    }
}

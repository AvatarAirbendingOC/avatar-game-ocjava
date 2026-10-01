// Common alliance-level trait for the "direct" benders: Fire Nation and
// Earth Kingdom. Their random-exploration movement is rook-like: straight
// cardinal lines only.
public abstract class AllianceDirect extends Individual {

    protected static final Direction[] MOVEMENT_DIRECTIONS = {
        Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST
    };

    protected AllianceDirect(int startEP, int x, int y) {
        super(startEP, x, y);
    }
}

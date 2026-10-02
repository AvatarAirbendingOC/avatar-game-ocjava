// Common alliance-level trait for Fire Nation and Earth Kingdom. Random
// exploration is rook-like: straight cardinal lines only.
public abstract class IndustrialAlliance extends Individual {

    protected static final Direction[] MOVEMENT_DIRECTIONS = {
        Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST
    };

    protected IndustrialAlliance(int startEP, int x, int y) {
        super(startEP, x, y);
    }
}

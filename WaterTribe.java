public class WaterTribe extends AllianceFlow {

    protected static int instanceCount = 0;

    public WaterTribe(int startEP, int x, int y) {
        super(startEP, x, y);
        instanceCount++;
    }

    public static int getInstanceCount() {
        return instanceCount;
    }

    @Override
    public void move(GameMap map) {
        attemptMove(map, MOVEMENT_DIRECTIONS);
        System.out.println(getClass().getSimpleName() + " at (" + x + "," + y + ") EP=" + ep + " redirects the flow of water.");
    }

    @Override
    public char getSymbol() {
        return 'w';
    }
}

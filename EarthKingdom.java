public class EarthKingdom extends IndustrialAlliance {

    protected static int instanceCount = 0;

    public EarthKingdom(int startEP, int x, int y) {
        super(startEP, x, y);
        instanceCount++;
    }

    public static int getInstanceCount() {
        return instanceCount;
    }

    @Override
    public void move(GameMap map) {
        attemptMove(map, MOVEMENT_DIRECTIONS);
        System.out.println(getClass().getSimpleName() + " at (" + x + "," + y + ") EP=" + ep + " raises a wall of stone.");
    }

    @Override
    public char getSymbol() {
        return 'k';
    }
}

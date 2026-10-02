public class FireNation extends IndustrialAlliance {

    protected static int instanceCount = 0;

    public FireNation(int startEP, int x, int y) {
        super(startEP, x, y);
        instanceCount++;
    }

    public static int getInstanceCount() {
        return instanceCount;
    }

    @Override
    public void move(GameMap map) {
        attemptMove(map, MOVEMENT_DIRECTIONS);
        System.out.println(getClass().getSimpleName() + " at (" + x + "," + y + ") EP=" + ep + " strikes forward with fire.");
    }

    @Override
    public char getSymbol() {
        return 'f';
    }
}

public class AirNomad extends AllianceFlow {

    protected static int instanceCount = 0;

    public AirNomad(int startEP, int x, int y) {
        super(startEP, x, y);
        instanceCount++;
    }

    public static int getInstanceCount() {
        return instanceCount;
    }

    @Override
    public void move(GameMap map) {
        attemptMove(map, MOVEMENT_DIRECTIONS);
        System.out.println(getClass().getSimpleName() + " at (" + x + "," + y + ") EP=" + ep + " glides on an air current.");
    }

    @Override
    public char getSymbol() {
        return 'a';
    }
}

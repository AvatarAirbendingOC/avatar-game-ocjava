public class MasterWaterTribe extends WaterTribe {

    private static MasterWaterTribe instance;

    private MasterWaterTribe(int startEP, int x, int y) {
        super(startEP, x, y);
    }

    public static synchronized MasterWaterTribe getInstance(int startEP, int x, int y) {
        if (instance == null) {
            instance = new MasterWaterTribe(startEP, x, y);
        }
        return instance;
    }

    @Override
    public void move(GameMap map) {
        // stationary
    }

    @Override
    public char getSymbol() {
        return 'W';
    }
}

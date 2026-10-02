public class MasterAirNomad extends AirNomad {

    private static MasterAirNomad instance;

    private MasterAirNomad(int startEP, int x, int y) {
        super(startEP, x, y);
    }

    public static synchronized MasterAirNomad getInstance(int startEP, int x, int y) {
        if (instance == null) {
            instance = new MasterAirNomad(startEP, x, y);
        }
        return instance;
    }

    @Override
    public void move(GameMap map) {
        // stationary
    }

    @Override
    public char getSymbol() {
        return 'A';
    }
}

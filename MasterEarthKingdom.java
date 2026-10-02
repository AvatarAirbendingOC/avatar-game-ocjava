public class MasterEarthKingdom extends EarthKingdom {

    private static MasterEarthKingdom instance;

    private MasterEarthKingdom(int startEP, int x, int y) {
        super(startEP, x, y);
    }

    public static synchronized MasterEarthKingdom getInstance(int startEP, int x, int y) {
        if (instance == null) {
            instance = new MasterEarthKingdom(startEP, x, y);
        }
        return instance;
    }

    @Override
    public void move(GameMap map) {
        // stationary
    }

    @Override
    public char getSymbol() {
        return 'K';
    }
}

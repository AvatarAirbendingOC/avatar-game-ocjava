public class MasterFireNation extends FireNation {

    private static MasterFireNation instance;

    private MasterFireNation(int startEP, int x, int y) {
        super(startEP, x, y);
    }

    public static MasterFireNation getInstance(int startEP, int x, int y) {
        if (instance == null) {
            instance = new MasterFireNation(startEP, x, y);
        }
        return instance;
    }

    @Override
    public void move(GameMap map) {
        // stationary
    }

    @Override
    public char getSymbol() {
        return 'F';
    }
}

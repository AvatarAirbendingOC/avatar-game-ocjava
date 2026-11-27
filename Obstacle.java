public class Obstacle implements Occupant {

    private final int type; // 0-3, purely visual variety

    public Obstacle() {
        this.type = RandomGenerator.randomInt(0, 3);
    }

    public int getType() {
        return type;
    }

    @Override
    public char getSymbol() {
        return '#';
    }
}

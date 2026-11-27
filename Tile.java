public class Tile {
    private final int x, y;
    private Occupant occupant;                          // null = empty
    private boolean safeZone = false;
    private Class<? extends Individual> owningFaction;   // which faction's SafeZone, if any

    public Tile(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    public boolean isEmpty() {
        return occupant == null;
    }

    public Occupant getOccupant() {
        return occupant;
    }

    public void setOccupant(Occupant occupant) {
        this.occupant = occupant;
    }

    public void clear() {
        this.occupant = null;
    }

    public boolean isSafeZone() {
        return safeZone;
    }

    void markAsSafeZone(Class<? extends Individual> owningFaction) {
        this.safeZone = true;
        this.owningFaction = owningFaction;
    }

    public Class<? extends Individual> getOwningFaction() {
        return owningFaction;
    }

    // '.' empty, '~' empty SafeZone tile, or the occupant's own symbol.
    public char getDisplaySymbol() {
        if (occupant != null) return occupant.getSymbol();
        if (safeZone) return '~';
        return '.';
    }
}

import java.util.ArrayList;
import java.util.List;

public abstract class Individual implements Occupant {

    protected List<String> messages = new ArrayList<>();
    protected Direction lastDirection;
    protected int ep;
    protected int maxEp;
    protected int x, y;
    protected boolean blockedLastTurn = false;
    protected char identityLetter = '?';

    protected static final int LOW_EP_THRESHOLD_PERCENT = 20;
    protected static final int MAX_STEP_DISTANCE = 3;
    protected static final int SAFEZONE_RECOVERY = 15;

    // Aggregate count across every faction (each faction class also keeps
    // its own count; this is the base-class total). Kept protected with a
    // public accessor rather than a raw public field, so nothing outside
    // this hierarchy can corrupt it directly.
    protected static int totalIndividualsCreated = 0;

    protected Individual(int startEP, int x, int y) {
        this.ep = startEP;
        this.maxEp = startEP;
        this.x = x;
        this.y = y;
        totalIndividualsCreated++;
    }

    public static int getTotalIndividualsCreated() {
        return totalIndividualsCreated;
    }

    public abstract void move(GameMap map);

    public abstract char getSymbol();

    public void placeOn(GameMap map) {
        map.getTile(x, y).setOccupant(this);
    }

    public void receiveMessage(String message) {
        if (!messages.contains(message)) {
            messages.add(message);
        }
    }

    // Needed for confrontation: the loser actually loses the stolen message,
    // unlike the union/trade cases which only ever add.
    public void removeMessage(String message) {
        messages.remove(message);
    }

    public List<String> getMessages() {
        return new ArrayList<>(messages);
    }

    public int getEP() {
        return ep;
    }

    public double getEpPercentage() {
        return maxEp == 0 ? 0 : (100.0 * ep / maxEp);
    }

    public void loseEP(int amount) {
        ep = Math.max(0, ep - amount);
        if (ep == 0) {
            becomeObstacle();
        }
    }

    public void gainEP(int amount) {
        ep = Math.min(maxEp, ep + amount);
    }

    protected void becomeObstacle() {
        messages.clear();
        System.out.println(getClass().getSimpleName() + " ran out of EP and became an obstacle.");
    }

    public int getX() { return x; }
    public int getY() { return y; }

    public void setIdentity(char letter) {
        this.identityLetter = letter;
    }

    public char getIdentity() {
        return identityLetter;
    }

    protected void attemptMove(GameMap map, Direction[] randomStylePool) {
        if (ep <= 0) {
            return;
        }

        int distance = RandomGenerator.randomInt(1, MAX_STEP_DISTANCE);
        Direction chosen;

        if (getEpPercentage() < LOW_EP_THRESHOLD_PERCENT) {
            chosen = map.getDirectionToSafeZone(x, y, getClass());
            if (chosen == null) {
                gainEP(SAFEZONE_RECOVERY);
                return;
            }
        } else {
            Direction avoid = blockedLastTurn ? lastDirection : null;
            chosen = RandomGenerator.randomDirection(randomStylePool, avoid);
        }

        TravelResult result = travel(map, chosen, distance);
        blockedLastTurn = (result.traveled() < distance);
        lastDirection = chosen;
        loseEP(distance);

        // Spec: a Meeting only happens when the path was stopped specifically
        // by another Individual (not a plain Obstacle, not a SafeZone edge).
        if (result.blocker() != null) {
            EncounterResolver.resolve(this, result.blocker());
        }
    }

    // Bundles the two things one travel attempt needs to report: how far it
    // actually got, and whether an Individual (not an Obstacle) is why it stopped.
    private record TravelResult(int traveled, Individual blocker) {}

    private TravelResult travel(GameMap map, Direction dir, int distance) {
        int dx = 0, dy = 0;
        switch (dir) {
            case NORTH:      dy = -1; break;
            case NORTH_EAST: dx = 1;  dy = -1; break;
            case EAST:       dx = 1;  break;
            case SOUTH_EAST: dx = 1;  dy = 1;  break;
            case SOUTH:      dy = 1;  break;
            case SOUTH_WEST: dx = -1; dy = 1;  break;
            case WEST:       dx = -1; break;
            case NORTH_WEST: dx = -1; dy = -1; break;
        }

        int curX = x, curY = y;
        int traveled = 0;
        Individual blocker = null;

        for (int step = 0; step < distance; step++) {
            int nx = curX + dx;
            int ny = curY + dy;

            if (nx < 0 || nx >= map.getWidth() || ny < 0 || ny >= map.getHeight()) {
                break; // map edge
            }

            Tile nextTile = map.getTile(nx, ny);

            if (nextTile.isSafeZone() && !nextTile.getOwningFaction().isInstance(this)) {
                break; // rival SafeZone boundary blocks like an obstacle, no Meeting
            }

            if (!nextTile.isEmpty()) {
                if (nextTile.getOccupant() instanceof Individual blockingIndividual) {
                    blocker = blockingIndividual; // Meeting: stopped by another Individual
                }
                break; // also stops for a plain Obstacle, just without a Meeting
            }

            curX = nx;
            curY = ny;
            traveled++;
        }

        if (traveled > 0) {
            map.getTile(x, y).clear();
            x = curX;
            y = curY;
            map.getTile(x, y).setOccupant(this);
        }

        return new TravelResult(traveled, blocker);
    }
}

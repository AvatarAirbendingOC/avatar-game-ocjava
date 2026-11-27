import java.util.Random;

// Spec requirement: a single dedicated class for every pseudo-random need
// in the project, so all randomness funnels through one swappable point.
public final class RandomGenerator {

    private static final Random RNG = new Random();

    private RandomGenerator() {
    }

    public static int randomInt(int minInclusive, int maxInclusive) {
        return minInclusive + RNG.nextInt(maxInclusive - minInclusive + 1);
    }

    // Picks randomly among the given directions, optionally excluding one
    // (used to avoid immediately repeating a direction that just got blocked).
    public static Direction randomDirection(Direction[] options, Direction exclude) {
        Direction choice;
        do {
            choice = options[RNG.nextInt(options.length)];
        } while (options.length > 1 && choice == exclude);
        return choice;
    }

    public static boolean randomBoolean() {
        return RNG.nextBoolean();
    }
}

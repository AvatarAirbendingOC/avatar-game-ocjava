import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class EncounterResolver {

    private EncounterResolver() {
    }

    public static void resolve(Individual mover, Individual other) {
        if (sameFaction(mover, other)) {
            mergeKnowledge(mover, other);
        } else if (sameAlliance(mover, other)) {
            exchangeSome(mover, other);
        } else {
            confront(mover, other);
        }
    }

    private static void mergeKnowledge(Individual a, Individual b) {
        List<String> gainedByA = new ArrayList<>(b.getMessages());
        gainedByA.removeAll(a.getMessages());
        List<String> gainedByB = new ArrayList<>(a.getMessages());
        gainedByB.removeAll(b.getMessages());

        for (String m : gainedByA) a.receiveMessage(m);
        for (String m : gainedByB) b.receiveMessage(m);

        SoundEngine.playMergeCue();
        EventLog.logEncounter("[Same population] " + label(a) + " & " + label(b) + " pool knowledge\n"
            + label(a) + " +" + gainedByA + "\n" + label(b) + " +" + gainedByB);
    }

    private static void exchangeSome(Individual a, Individual b) {
        List<String> toA = shareRandomSubset(b, a);
        List<String> toB = shareRandomSubset(a, b);

        SoundEngine.playTradeCue();
        EventLog.logEncounter("[Same alliance] " + label(a) + " & " + label(b) + " trade\n"
            + label(a) + " +" + toA + "\n" + label(b) + " +" + toB);
    }

    private static List<String> shareRandomSubset(Individual from, Individual to) {
        List<String> candidates = new ArrayList<>(from.getMessages());
        candidates.removeAll(to.getMessages());
        List<String> shared = new ArrayList<>();
        if (candidates.isEmpty()) return shared;

        Collections.shuffle(candidates);
        int count = RandomGenerator.randomInt(1, candidates.size());
        for (int i = 0; i < count; i++) {
            String m = candidates.get(i);
            to.receiveMessage(m);
            shared.add(m);
        }
        return shared;
    }

    private static void confront(Individual a, Individual b) {
        SoundEngine.playClashCue();

        Individual winner = RandomGenerator.randomBoolean() ? a : b;
        Individual loser  = (winner == a) ? b : a;

        List<String> stealable = new ArrayList<>(loser.getMessages());
        stealable.removeAll(winner.getMessages());

        List<String> stolen = new ArrayList<>();
        if (!stealable.isEmpty()) {
            Collections.shuffle(stealable);
            int count = RandomGenerator.randomInt(1, stealable.size());
            for (int i = 0; i < count; i++) {
                String m = stealable.get(i);
                winner.receiveMessage(m);
                loser.removeMessage(m);
                stolen.add(m);
            }
        }

        EventLog.logEncounter("[Confrontation] " + label(winner) + " defeats " + label(loser)
            + " (coin flip)\nStole: " + stolen);
    }

    private static boolean sameFaction(Individual a, Individual b) {
        return getFactionRoot(a) == getFactionRoot(b);
    }

    private static boolean sameAlliance(Individual a, Individual b) {
        return (a instanceof IndustrialAlliance) == (b instanceof IndustrialAlliance);
    }

    private static Class<? extends Individual> getFactionRoot(Individual ind) {
        if (ind instanceof FireNation) return FireNation.class;
        if (ind instanceof EarthKingdom) return EarthKingdom.class;
        if (ind instanceof WaterTribe) return WaterTribe.class;
        return AirNomad.class;
    }

    // Identity letter instead of a coordinate - "AirNomad B", not "AirNomad[3,3]".
    private static String label(Individual i) {
        return i.getClass().getSimpleName() + " " + i.getIdentity();
    }
}

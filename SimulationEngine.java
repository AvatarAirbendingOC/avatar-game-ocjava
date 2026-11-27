import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

// Must be started on its OWN background thread (never the EDT) - it blocks
// on Swing dialogs and, in interactive mode, on the advance gate.
public class SimulationEngine {

    private final GameMap map;
    private final List<Individual> roster;
    private final List<Individual> masters;
    private final Set<String> allMessages;
    private final int maxSteps;

    private BoardPanel boardPanel;
    private ScoreboardPanel scoreboardPanel;
    private boolean interactive = false;
    private final Semaphore advanceGate = new Semaphore(0);
    private volatile boolean stopRequested = false;

    private int currentStep = 0;

    public SimulationEngine(GameMap map, List<Individual> roster, List<Individual> masters,
                             Set<String> allMessages, int maxSteps) {
        this.map = map;
        this.roster = roster;
        this.masters = masters;
        this.allMessages = allMessages;
        this.maxSteps = maxSteps;
    }

    public void setBoardPanel(BoardPanel boardPanel) {
        this.boardPanel = boardPanel;
    }

    public void setScoreboardPanel(ScoreboardPanel scoreboardPanel) {
        this.scoreboardPanel = scoreboardPanel;
    }

    // One whole step (every individual's turn) waits for advance() before
    // it runs - not one individual at a time.
    public void setInteractive(boolean interactive) {
        this.interactive = interactive;
    }

    public void advance() {
        advanceGate.release();
    }

    public void requestStop() {
        stopRequested = true;
        advanceGate.release(); // wake it up if it's currently waiting on a step trigger
    }

    public void run() {
        printBoard();
        if (boardPanel != null) runOnEdtAndWait(boardPanel::repaint);

        while (currentStep < maxSteps) {
            if (interactive) {
                try {
                    advanceGate.acquire();
                } catch (InterruptedException ignored) {
                }
            }
            if (stopRequested) break;

            currentStep++;
            Collections.shuffle(roster);
            EventLog.log("--- Step " + currentStep + " ---");

            for (Individual ind : roster) {
                processIndividual(ind);
                if (stopRequested) break;
            }

            printBoard();
            refreshScoreboard();
            if (stopRequested) break;

            Individual earlyWinner = checkForCompletion();
            if (earlyWinner != null) {
                String msg = earlyWinner.getClass().getSimpleName()
                    + " Wins! Collected every distinct message at step " + currentStep + ".";
                EventLog.log(msg);
                SoundEngine.playVictoryFanfare();
                showModal(msg, "Game Over");
                announceResult();
                SoundEngine.stopBackgroundAmbience();
                return;
            }
        }

        if (stopRequested) {
            EventLog.log("Simulation stopped manually at step " + currentStep + ".");
        } else {
            EventLog.log("Step limit (" + maxSteps + ") reached. Comparing scores...");
        }
        SoundEngine.playVictoryFanfare();
        announceResult();
        SoundEngine.stopBackgroundAmbience();
    }

    private void refreshScoreboard() {
        if (scoreboardPanel == null) return;
        runOnEdtAndWait(() -> scoreboardPanel.update(masters));
    }

    private void processIndividual(Individual ind) {
        int fromX = ind.getX(), fromY = ind.getY();
        ind.move(map);
        int toX = ind.getX(), toY = ind.getY();

        if (boardPanel != null) {
            if (fromX != toX || fromY != toY) {
                runOnEdtAndWait(() -> boardPanel.animateMove(ind, fromX, fromY, toX, toY));
            } else {
                runOnEdtAndWait(boardPanel::repaint);
            }
        }

        String encounter = EventLog.consumeLastEncounter();
        if (encounter != null) {
            showModal(encounter, "Encounter");
        }

        if (!interactive) {
            try {
                Thread.sleep(80);
            } catch (InterruptedException ignored) {
            }
        }
    }

    private void showModal(String message, String title) {
        if (boardPanel == null) return;
        runOnEdtAndWait(() ->
            JOptionPane.showMessageDialog(boardPanel, message, title, JOptionPane.INFORMATION_MESSAGE));
    }

    private void runOnEdtAndWait(Runnable r) {
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
            return;
        }
        try {
            SwingUtilities.invokeAndWait(r);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Individual checkForCompletion() {
        for (Individual master : masters) {
            if (new HashSet<>(master.getMessages()).containsAll(allMessages)) {
                return master;
            }
        }
        return null;
    }

    private void announceResult() {
        EventLog.log("--- Master scores (distinct messages collected) ---");
        int best = -1;
        List<Individual> leaders = new ArrayList<>();

        for (Individual master : masters) {
            int score = master.getMessages().size();
            EventLog.log(master.getClass().getSimpleName() + ": " + score + " " + master.getMessages());
            if (score > best) {
                best = score;
                leaders.clear();
                leaders.add(master);
            } else if (score == best) {
                leaders.add(master);
            }
        }

        if (leaders.size() == 1) {
            EventLog.log("Winner: " + leaders.get(0).getClass().getSimpleName() + " with " + best + " distinct messages.");
        } else {
            StringBuilder sb = new StringBuilder("Draw between: ");
            for (Individual l : leaders) sb.append(l.getClass().getSimpleName()).append(" ");
            EventLog.log(sb.toString());
        }
    }

    private void printBoard() {
        for (int y = 0; y < map.getHeight(); y++) {
            StringBuilder row = new StringBuilder();
            for (int x = 0; x < map.getWidth(); x++) {
                row.append(map.getTile(x, y).getDisplaySymbol()).append(' ');
            }
            System.out.println(row);
        }
    }
}

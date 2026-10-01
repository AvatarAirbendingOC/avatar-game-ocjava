import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;

public class GameFrame extends JFrame {

    private final BoardPanel boardPanel;
    private final JTextArea logArea;
    private final ScoreboardPanel scoreboardPanel;
    private Runnable onAdvance = () -> {};
    private Runnable onStop = () -> {};

    public GameFrame(GameMap map) {
        super("Avatar MAS - Four Nations");
        setLayout(new BorderLayout());

        boardPanel = new BoardPanel(map);
        add(boardPanel, BorderLayout.CENTER);

        scoreboardPanel = new ScoreboardPanel();
        add(scoreboardPanel, BorderLayout.EAST);

        JPanel controls = new JPanel();
        JButton nextButton = new JButton("Next Step (Space)");
        nextButton.addActionListener(e -> onAdvance.run());
        controls.add(nextButton);

        JButton stopButton = new JButton("Stop & Show Results");
        stopButton.addActionListener(e -> onStop.run());
        controls.add(stopButton);

        add(controls, BorderLayout.NORTH);

        logArea = new JTextArea(10, 40);
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setBorder(BorderFactory.createTitledBorder("Encounter Log"));
        add(scroll, BorderLayout.SOUTH);

        getRootPane().registerKeyboardAction(
            e -> onAdvance.run(),
            KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public void setOnAdvance(Runnable onAdvance) {
        this.onAdvance = onAdvance;
    }

    public void setOnStop(Runnable onStop) {
        this.onStop = onStop;
    }

    public BoardPanel getBoardPanel() {
        return boardPanel;
    }

    public ScoreboardPanel getScoreboardPanel() {
        return scoreboardPanel;
    }

    public JTextArea getLogArea() {
        return logArea;
    }
}

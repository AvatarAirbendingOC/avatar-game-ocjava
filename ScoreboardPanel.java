import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;
import java.util.List;

// Expects masters in order: Fire, Earth, Water, Air.
public class ScoreboardPanel extends JPanel {

    private final JLabel fireLabel = new JLabel();
    private final JLabel earthLabel = new JLabel();
    private final JLabel waterLabel = new JLabel();
    private final JLabel airLabel = new JLabel();

    public ScoreboardPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Scoreboard"));

        style(fireLabel, new Color(200, 50, 40));
        style(earthLabel, new Color(90, 130, 60));
        style(waterLabel, new Color(60, 110, 190));
        style(airLabel, new Color(150, 130, 60));

        add(fireLabel);
        add(earthLabel);
        add(waterLabel);
        add(airLabel);

        update(List.of()); // initial blank state
    }

    private void style(JLabel label, Color color) {
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        label.setForeground(color);
        label.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
    }

    public void update(List<Individual> masters) {
        if (masters.isEmpty()) {
            fireLabel.setText("Fire Nation: 0");
            earthLabel.setText("Earth Kingdom: 0");
            waterLabel.setText("Water Tribe: 0");
            airLabel.setText("Air Nomads: 0");
            return;
        }

        int best = 0;
        for (Individual m : masters) best = Math.max(best, m.getMessages().size());

        setScore(fireLabel, "Fire Nation", masters.get(0).getMessages().size(), best);
        setScore(earthLabel, "Earth Kingdom", masters.get(1).getMessages().size(), best);
        setScore(waterLabel, "Water Tribe", masters.get(2).getMessages().size(), best);
        setScore(airLabel, "Air Nomads", masters.get(3).getMessages().size(), best);
    }

    private void setScore(JLabel label, String name, int score, int best) {
        String lead = (score == best && best > 0) ? " \u2191" : "";
        label.setText(name + ": " + score + lead);
    }
}

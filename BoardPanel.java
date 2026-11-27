import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;

public class BoardPanel extends JPanel {

    private final GameMap map;

    private Individual animatingIndividual;
    private int animFromCol, animFromRow, animToCol, animToRow;
    private double animProgress = 1.0;

    public BoardPanel(GameMap map) {
        this.map = map;
        setPreferredSize(new Dimension(600, 600));
        setBackground(Color.WHITE);
    }

    public void animateMove(Individual mover, int fromCol, int fromRow, int toCol, int toRow) {
        animatingIndividual = mover;
        animFromCol = fromCol;
        animFromRow = fromRow;
        animToCol = toCol;
        animToRow = toRow;

        int frames = 6;
        for (int f = 1; f <= frames; f++) {
            animProgress = (double) f / frames;
            paintImmediately(0, 0, getWidth(), getHeight());
            try {
                Thread.sleep(30);
            } catch (InterruptedException ignored) {
            }
        }

        animatingIndividual = null;
        animProgress = 1.0;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int cols = map.getWidth();
        int rows = map.getHeight();
        int cellSize = Math.min(getWidth() / cols, getHeight() / rows);

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                drawTile(g2, map.getTile(x, y), x * cellSize, y * cellSize, cellSize);
            }
        }

        if (animatingIndividual != null && animProgress < 1.0) {
            double fx = animFromCol + (animToCol - animFromCol) * animProgress;
            double fy = animFromRow + (animToRow - animFromRow) * animProgress;
            int px = (int) Math.round(fx * cellSize);
            int py = (int) Math.round(fy * cellSize);
            drawIndividual(g2, animatingIndividual, px, py, cellSize);
        }
    }

    private void drawTile(Graphics2D g2, Tile tile, int px, int py, int size) {
        if (tile.isSafeZone()) {
            g2.setColor(tint(factionColor(tile.getOwningFaction())));
        } else {
            g2.setColor(new Color(245, 245, 245));
        }
        g2.fillRect(px, py, size, size);
        g2.setColor(new Color(220, 220, 220));
        g2.drawRect(px, py, size, size);

        Occupant occ = tile.getOccupant();
        if (occ == animatingIndividual && animProgress < 1.0) {
            return; // drawn separately at its interpolated position
        }
        if (occ instanceof Obstacle obs) {
            drawObstacle(g2, obs, px, py, size);
        } else if (occ instanceof Individual ind) {
            drawIndividual(g2, ind, px, py, size);
        }
    }

    private void drawObstacle(Graphics2D g2, Obstacle obstacle, int px, int py, int size) {
        int margin = size / 6;
        int inner = size - 2 * margin;
        int ix = px + margin, iy = py + margin;

        Color base = blockColor(obstacle.getType());
        g2.setColor(base);
        g2.fillRoundRect(ix, iy, inner, inner, 6, 6);

        // Simple bevel so it reads as a solid block regardless of color.
        g2.setColor(base.brighter());
        g2.drawLine(ix, iy, ix + inner, iy);
        g2.drawLine(ix, iy, ix, iy + inner);
        g2.setColor(base.darker());
        g2.drawLine(ix + inner, iy, ix + inner, iy + inner);
        g2.drawLine(ix, iy + inner, ix + inner, iy + inner);
    }

    private Color blockColor(int type) {
        return switch (type) {
            case 0 -> new Color(120, 120, 120); // stone
            case 1 -> new Color(120, 80, 40);   // wood
            case 2 -> new Color(90, 90, 100);   // slate
            default -> new Color(100, 60, 60);  // brick
        };
    }

    private void drawIndividual(Graphics2D g2, Individual ind, int px, int py, int size) {
        Class<? extends Individual> faction = factionOf(ind);
        Color color = factionColor(faction);
        boolean isMaster = ind.getClass().getSimpleName().startsWith("Master");

        int margin = size / 8;
        int diameter = size - 2 * margin;
        int cx = px + margin;
        int cy = py + margin;

        g2.setColor(color);
        g2.fillOval(cx, cy, diameter, diameter);

        // EP health ring: green / yellow / red based on remaining EP.
        g2.setColor(healthColor(ind.getEpPercentage()));
        g2.setStroke(new BasicStroke(3f));
        g2.drawOval(cx, cy, diameter, diameter);

        // Master gets an extra outer gold ring.
        if (isMaster) {
            g2.setColor(new Color(255, 215, 0));
            g2.setStroke(new BasicStroke(3f));
            g2.drawOval(cx - 5, cy - 5, diameter + 10, diameter + 10);
        }

        g2.setColor(Color.WHITE);
        drawSilhouette(g2, faction, cx + diameter / 2, cy + diameter / 2, diameter);

        // Identity letter (A/B/C, or M) - replaces the old numeric EP label.
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, Math.max(11f, size / 4f)));
        String idText = String.valueOf(ind.getIdentity());
        FontMetrics fm = g2.getFontMetrics();
        int tw = fm.stringWidth(idText);
        int labelX = px + (size - tw) / 2;
        int labelBaseline = py + size - 4;
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRoundRect(labelX - 3, labelBaseline - fm.getAscent(), tw + 6, fm.getHeight(), 6, 6);
        g2.setColor(Color.WHITE);
        g2.drawString(idText, labelX, labelBaseline);
    }

    private Color healthColor(double epPercent) {
        if (epPercent >= 60) return new Color(60, 160, 60);
        if (epPercent >= 20) return new Color(220, 170, 40);
        return new Color(200, 50, 50);
    }

    private void drawSilhouette(Graphics2D g2, Class<? extends Individual> faction, int cxMid, int cyMid, int d) {
        int s = d / 3;

        if (faction == FireNation.class) {
            Path2D.Double flame = new Path2D.Double();
            flame.moveTo(cxMid, cyMid - s);
            flame.curveTo(cxMid + s * 0.6, cyMid - s * 0.2, cxMid + s * 0.3, cyMid + s * 0.3, cxMid, cyMid + s);
            flame.curveTo(cxMid - s * 0.3, cyMid + s * 0.3, cxMid - s * 0.6, cyMid - s * 0.2, cxMid, cyMid - s);
            flame.closePath();
            g2.fill(flame);
        } else if (faction == EarthKingdom.class) {
            int[] xs = { cxMid - s, cxMid - s / 2, cxMid, cxMid + s / 2, cxMid + s, cxMid + s / 2, cxMid - s / 2 };
            int[] ys = { cyMid, cyMid - s / 2, cyMid - s, cyMid - s / 2, cyMid, cyMid + s / 2, cyMid + s / 2 };
            g2.fillPolygon(xs, ys, xs.length);
        } else if (faction == WaterTribe.class) {
            Path2D.Double wave = new Path2D.Double();
            wave.moveTo(cxMid - s, cyMid);
            wave.curveTo(cxMid - s / 2, cyMid - s, cxMid, cyMid + s, cxMid + s, cyMid);
            g2.setStroke(new BasicStroke(Math.max(2f, d / 12f)));
            g2.draw(wave);
        } else {
            g2.setStroke(new BasicStroke(Math.max(2f, d / 12f)));
            g2.drawArc(cxMid - s, cyMid - s, 2 * s, 2 * s, 30, 300);
        }
    }

    private Class<? extends Individual> factionOf(Individual ind) {
        if (ind instanceof FireNation) return FireNation.class;
        if (ind instanceof EarthKingdom) return EarthKingdom.class;
        if (ind instanceof WaterTribe) return WaterTribe.class;
        return AirNomad.class;
    }

    private Color factionColor(Class<? extends Individual> faction) {
        if (faction == FireNation.class) return new Color(200, 50, 40);
        if (faction == EarthKingdom.class) return new Color(90, 130, 60);
        if (faction == WaterTribe.class) return new Color(60, 110, 190);
        return new Color(210, 200, 140);
    }

    private Color tint(Color c) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), 90);
    }
}

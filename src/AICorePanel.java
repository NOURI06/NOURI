import javax.swing.*;
import java.awt.*;

public class AICorePanel extends JPanel {

    public AICorePanel() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int cx = getWidth() / 2;
        int cy = getHeight() / 2;

        // Outer glow
        g2.setColor(new Color(0, 150, 255, 40));
        g2.fillOval(cx - 110, cy - 110, 220, 220);

        // Middle glow
        g2.setColor(new Color(0, 170, 255, 80));
        g2.fillOval(cx - 70, cy - 70, 140, 140);

        // Core
        g2.setColor(new Color(0, 220, 255));
        g2.fillOval(cx - 25, cy - 25, 50, 50);

        // Ring 1
        g2.setStroke(new BasicStroke(3));
        g2.drawOval(cx - 60, cy - 60, 120, 120);

        // Ring 2
        g2.drawOval(cx - 90, cy - 90, 180, 180);
    }
}
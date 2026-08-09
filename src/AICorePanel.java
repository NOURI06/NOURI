import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.AffineTransform;
import java.util.Random;

public class AICorePanel extends JPanel {

    private double rotation = 0;
    private double pulse = 0;
    private final Random random = new Random();

    private final Particle[] particles = new Particle[45];

    public AICorePanel() {

        setOpaque(false);

        for (int i = 0; i < particles.length; i++) {
            particles[i] = new Particle();
        }

        // Animation loop
        Timer timer = new Timer(30, e -> {

            rotation += 0.025;
            pulse += 0.08;

            if (rotation > Math.PI * 2) {
                rotation -= Math.PI * 2;
            }

            repaint();
        });

        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        g2.setRenderingHint(
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_SPEED
        );

        int cx = getWidth() / 2;
        int cy = getHeight() / 2;

        // ==========================================
        // PULSE
        // ==========================================

        double wave = (Math.sin(pulse) + 1) / 2;

        // ==========================================
        // OUTER GLOW
        // ==========================================

        for (int size = 260; size >= 100; size -= 20) {

            int alpha =
                    (int) (5 + wave * 15);

            g2.setColor(
                    new Color(0, 180, 255, alpha)
            );

            int x = cx - size / 2;
            int y = cy - size / 2;

            g2.fillOval(
                    x,
                    y,
                    size,
                    size
            );
        }

        // ==========================================
        // PARTICLES
        // ==========================================

        for (Particle particle : particles) {

            particle.update();

            double angle = particle.angle + rotation * 0.3;

            int x = cx +
                    (int) (
                            Math.cos(angle)
                                    * particle.distance
                    );

            int y = cy +
                    (int) (
                            Math.sin(angle)
                                    * particle.distance
                    );

            int alpha =
                    70 + random.nextInt(100);

            g2.setColor(
                    new Color(
                            0,
                            210,
                            255,
                            alpha
                    )
            );

            g2.fillOval(
                    x,
                    y,
                    particle.size,
                    particle.size
            );
        }

        // ==========================================
        // ROTATING RING 1
        // ==========================================

        AffineTransform old =
                g2.getTransform();

        g2.rotate(
                rotation,
                cx,
                cy
        );

        g2.setStroke(
                new BasicStroke(
                        2.5f
                )
        );

        g2.setColor(
                new Color(
                        0,
                        210,
                        255,
                        190
                )
        );

        g2.drawOval(
                cx - 105,
                cy - 105,
                210,
                210
        );

        // Ring details

        g2.drawLine(
                cx - 105,
                cy,
                cx - 80,
                cy
        );

        g2.drawLine(
                cx + 80,
                cy,
                cx + 105,
                cy
        );

        g2.drawLine(
                cx,
                cy - 105,
                cx,
                cy - 80
        );

        g2.drawLine(
                cx,
                cy + 80,
                cx,
                cy + 105
        );

        // ==========================================
        // ROTATING RING 2
        // ==========================================

        g2.rotate(
                -rotation * 2,
                cx,
                cy
        );

        g2.setStroke(
                new BasicStroke(
                        2f
                )
        );

        g2.setColor(
                new Color(
                        80,
                        230,
                        255,
                        160
                )
        );

        g2.drawOval(
                cx - 78,
                cy - 78,
                156,
                156
        );

        // Restore transformation

        g2.setTransform(old);

        // ==========================================
        // CORE GLOW
        // ==========================================

        int coreSize =
                55 + (int) (wave * 10);

        for (
                int size = coreSize + 35;
                size >= coreSize;
                size -= 5
        ) {

            int alpha =
                    20 + (coreSize + 35 - size) * 2;

            g2.setColor(
                    new Color(
                            0,
                            200,
                            255,
                            alpha
                    )
            );

            g2.fillOval(
                    cx - size / 2,
                    cy - size / 2,
                    size,
                    size
            );
        }

        // ==========================================
        // CORE
        // ==========================================

        g2.setColor(
                new Color(
                        0,
                        220,
                        255
                )
        );

        g2.fill(
                new Ellipse2D.Double(
                        cx - coreSize / 2.0,
                        cy - coreSize / 2.0,
                        coreSize,
                        coreSize
                )
        );

        // ==========================================
        // CORE HIGHLIGHT
        // ==========================================

        g2.setColor(
                new Color(
                        180,
                        250,
                        255
                )
        );

        int highlight =
                coreSize / 3;

        g2.fillOval(
                cx - highlight / 2,
                cy - highlight / 2,
                highlight,
                highlight
        );

        // ==========================================
        // LABEL
        // ==========================================

        g2.setFont(
                new Font(
                        "Consolas",
                        Font.BOLD,
                        14
                )
        );

        g2.setColor(
                new Color(
                        100,
                        220,
                        255,
                        180
                )
        );

        String text = "N O U R I";

        FontMetrics fm =
                g2.getFontMetrics();

        g2.drawString(
                text,
                cx - fm.stringWidth(text) / 2,
                cy + 135
        );

        g2.dispose();
    }

    // ==========================================
    // PARTICLE
    // ==========================================

    private class Particle {

        double angle;
        double distance;
        double speed;
        int size;

        Particle() {

            angle =
                    random.nextDouble()
                            * Math.PI * 2;

            distance =
                    70 + random.nextDouble() * 80;

            speed =
                    0.002 + random.nextDouble() * 0.008;

            size =
                    1 + random.nextInt(3);
        }

        void update() {

            angle += speed;

            if (angle > Math.PI * 2) {
                angle -= Math.PI * 2;
            }
        }
    }
}

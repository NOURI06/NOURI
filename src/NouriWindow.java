import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class NouriWindow {

    private JFrame frame;
    private JPanel chatPanel;
    private JScrollPane scrollPane;
    private JTextField inputField;
    private JButton sendButton;
    private JLabel statusLabel;
    private JLabel clockLabel;

    private Commands commands = new Commands();

    public NouriWindow() {

        frame = new JFrame("NOURI // AI SYSTEM");
        frame.setSize(1100, 720);
        frame.setMinimumSize(new Dimension(900, 600));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(5, 10, 18));

        // =========================
        // TOP HUD
        // =========================

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(7, 15, 25));
        top.setBorder(new EmptyBorder(15, 25, 15, 25));

        JLabel title = new JLabel("N O U R I");
        title.setForeground(new Color(0, 220, 255));
        title.setFont(new Font("Segoe UI", Font.BOLD, 25));

        JPanel systemPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        systemPanel.setOpaque(false);

        statusLabel = new JLabel("● SYSTEM ONLINE");
        statusLabel.setForeground(new Color(0, 255, 170));
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        clockLabel = new JLabel();
        clockLabel.setForeground(new Color(100, 180, 210));
        clockLabel.setFont(new Font("Consolas", Font.PLAIN, 13));

        systemPanel.add(statusLabel);
        systemPanel.add(clockLabel);

        top.add(title, BorderLayout.WEST);
        top.add(systemPanel, BorderLayout.EAST);

        // =========================
        // CENTER HUD
        // =========================

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(new Color(5, 10, 18));

        // AI CORE
        AICorePanel core = new AICorePanel();
        core.setPreferredSize(new Dimension(420, 350));

        JPanel coreContainer = new JPanel(new GridBagLayout());
        coreContainer.setBackground(new Color(5, 10, 18));
        coreContainer.add(core);

        center.add(coreContainer, BorderLayout.NORTH);

        // =========================
        // CHAT
        // =========================

        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(new Color(5, 10, 18));
        chatPanel.setBorder(new EmptyBorder(5, 30, 10, 30));

        scrollPane = new JScrollPane(chatPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(5, 10, 18));

        center.add(scrollPane, BorderLayout.CENTER);

        // =========================
        // BOTTOM COMMAND BAR
        // =========================

        JPanel bottom = new JPanel(new BorderLayout(12, 0));
        bottom.setBackground(new Color(7, 15, 25));
        bottom.setBorder(new EmptyBorder(15, 20, 15, 20));

        inputField = new JTextField();

        inputField.setBackground(new Color(10, 22, 34));
        inputField.setForeground(Color.WHITE);
        inputField.setCaretColor(new Color(0, 230, 255));
        inputField.setFont(new Font("Consolas", Font.PLAIN, 15));
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(0, 150, 210), 1),
                new EmptyBorder(12, 15, 12, 15)
        ));

        sendButton = new JButton("EXECUTE");

        sendButton.setBackground(new Color(0, 100, 150));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        sendButton.setFocusPainted(false);
        sendButton.setBorder(BorderFactory.createEmptyBorder(
                12, 22, 12, 22
        ));

        bottom.add(inputField, BorderLayout.CENTER);
        bottom.add(sendButton, BorderLayout.EAST);

        root.add(top, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);

        frame.setContentPane(root);

        // =========================
        // WELCOME
        // =========================

        addMessage(
                "NOURI",
                "SYSTEM INITIALIZED.<br>ALL SYSTEMS OPERATIONAL.",
                false
        );

        // =========================
        // EVENTS
        // =========================

        sendButton.addActionListener(e -> send());
        inputField.addActionListener(e -> send());

        // =========================
        // CLOCK
        // =========================

        Timer timer = new Timer(1000, e -> updateClock());
        timer.start();

        updateClock();

        frame.setVisible(true);
    }

    // =========================
    // SEND COMMAND
    // =========================

    private void send() {

        String text = inputField.getText().trim();

        if (text.isEmpty()) {
            return;
        }

        addMessage("YOU", text, true);

        inputField.setText("");
        sendButton.setEnabled(false);

        statusLabel.setText("● NOURI THINKING...");
        statusLabel.setForeground(new Color(0, 200, 255));

        new Thread(() -> {

            String reply = commands.execute(text);

            SwingUtilities.invokeLater(() -> {

                addMessage("NOURI", reply, false);

                sendButton.setEnabled(true);

                statusLabel.setText("● SYSTEM ONLINE");
                statusLabel.setForeground(new Color(0, 255, 170));

                inputField.requestFocus();
            });

        }).start();
    }

    // =========================
    // CHAT MESSAGE
    // =========================

    private void addMessage(
            String sender,
            String message,
            boolean user) {

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);

        JLabel label = new JLabel(
                "<html><b>"
                        + sender
                        + "</b><br>"
                        + message
                        + "</html>"
        );

        label.setOpaque(true);

        label.setBorder(new EmptyBorder(
                10, 15, 10, 15
        ));

        if (user) {

            label.setBackground(
                    new Color(0, 80, 120)
            );

            label.setForeground(Color.WHITE);

            wrapper.add(
                    label,
                    BorderLayout.EAST
            );

        } else {

            label.setBackground(
                    new Color(10, 25, 38)
            );

            label.setForeground(
                    new Color(180, 235, 255)
            );

            wrapper.add(
                    label,
                    BorderLayout.WEST
            );
        }

        wrapper.setBorder(
                new EmptyBorder(5, 0, 5, 0)
        );

        chatPanel.add(wrapper);

        chatPanel.revalidate();
        chatPanel.repaint();

        SwingUtilities.invokeLater(() -> {

            JScrollBar bar =
                    scrollPane.getVerticalScrollBar();

            bar.setValue(bar.getMaximum());
        });
    }

    // =========================
    // CLOCK
    // =========================

    private void updateClock() {

        clockLabel.setText(
                new java.text.SimpleDateFormat(
                        "HH:mm:ss"
                ).format(new java.util.Date())
        );
    }
}

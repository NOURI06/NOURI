```java
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class NouriWindow {

    private JFrame frame;
    private JPanel chatPanel;
    private JScrollPane scrollPane;
    private JTextField inputField;
    private JButton sendButton;

    private Commands commands = new Commands();

    public NouriWindow() {

        // =========================
        // MAIN WINDOW
        // =========================

        frame = new JFrame("NOURI Assistant");
        frame.setSize(950, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // =========================
        // COLORS
        // =========================

        Color bg = new Color(24, 26, 32);
        Color header = new Color(33, 37, 43);
        Color inputBg = new Color(40, 44, 52);
        Color blue = new Color(0, 120, 255);

        // =========================
        // ROOT
        // =========================

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(bg);

        // =========================
        // HEADER
        // =========================

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(header);
        top.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("NOURI");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JLabel status = new JLabel("● Online");
        status.setForeground(new Color(0, 220, 120));
        status.setFont(new Font("Segoe UI", Font.BOLD, 14));

        top.add(title, BorderLayout.WEST);
        top.add(status, BorderLayout.EAST);

        // =========================
        // AI CORE
        // =========================

        AICorePanel core = new AICorePanel();

        core.setPreferredSize(new Dimension(950, 230));
        core.setMinimumSize(new Dimension(950, 230));

        // =========================
        // CHAT PANEL
        // =========================

        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(bg);
        chatPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        scrollPane = new JScrollPane(chatPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(bg);

        // =========================
        // CENTER AREA
        // =========================

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(bg);

        center.add(core, BorderLayout.NORTH);
        center.add(scrollPane, BorderLayout.CENTER);

        // =========================
        // INPUT AREA
        // =========================

        JPanel bottom = new JPanel(new BorderLayout(10, 0));
        bottom.setBackground(header);
        bottom.setBorder(new EmptyBorder(10, 10, 10, 10));

        inputField = new JTextField();
        inputField.setBackground(inputBg);
        inputField.setForeground(Color.WHITE);
        inputField.setCaretColor(Color.WHITE);
        inputField.setBorder(new EmptyBorder(10, 10, 10, 10));
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        sendButton = new JButton("Send");
        sendButton.setBackground(blue);
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setFont(new Font("Segoe UI", Font.BOLD, 14));

        bottom.add(inputField, BorderLayout.CENTER);
        bottom.add(sendButton, BorderLayout.EAST);

        // =========================
        // ADD EVERYTHING
        // =========================

        root.add(top, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);

        frame.setContentPane(root);

        // =========================
        // WELCOME MESSAGE
        // =========================

        addMessage("NOURI", "Welcome to NOURI!", false);

        // =========================
        // BUTTON ACTIONS
        // =========================

        sendButton.addActionListener(e -> send());

        inputField.addActionListener(e -> send());

        // =========================
        // SHOW WINDOW
        // =========================

        frame.setVisible(true);
    }

    // =========================
    // SEND MESSAGE
    // =========================

    private void send() {

        String text = inputField.getText().trim();

        if (text.isEmpty()) {
            return;
        }

        addMessage("You", text, true);

        String reply = commands.execute(text);

        addMessage("NOURI", reply, false);

        inputField.setText("");
    }

    // =========================
    // ADD CHAT MESSAGE
    // =========================

    private void addMessage(
            String sender,
            String message,
            boolean user) {

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);

        JLabel label = new JLabel(
                "<html><b>" +
                sender +
                "</b><br>" +
                message +
                "</html>"
        );

        label.setOpaque(true);
        label.setBorder(
                new EmptyBorder(10, 15, 10, 15)
        );

        if (user) {
            label.setBackground(new Color(0, 120, 255));
        } else {
            label.setBackground(new Color(55, 60, 68));
        }

        label.setForeground(Color.WHITE);

        if (user) {
            wrapper.add(label, BorderLayout.EAST);
        } else {
            wrapper.add(label, BorderLayout.WEST);
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
}
```

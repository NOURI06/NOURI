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

        frame = new JFrame("NOURI Assistant");
        frame.setSize(950, 650);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        Color bg = new Color(24, 26, 32);
        Color header = new Color(33, 37, 43);
        Color inputBg = new Color(40, 44, 52);
        Color blue = new Color(0, 120, 255);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(bg);

        // HEADER
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(header);
        top.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("NOURI");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JLabel status = new JLabel("● Online");
        status.setForeground(new Color(0, 220, 120));

        top.add(title, BorderLayout.WEST);
        top.add(status, BorderLayout.EAST);

        // CHAT
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(bg);
        chatPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        scrollPane = new JScrollPane(chatPanel);
        scrollPane.setBorder(null);

        // INPUT
        JPanel bottom = new JPanel(new BorderLayout(10, 0));
        bottom.setBackground(header);
        bottom.setBorder(new EmptyBorder(10, 10, 10, 10));

        inputField = new JTextField();
        inputField.setBackground(inputBg);
        inputField.setForeground(Color.WHITE);
        inputField.setCaretColor(Color.WHITE);
        inputField.setBorder(new EmptyBorder(10, 10, 10, 10));

        sendButton = new JButton("Send");
        sendButton.setBackground(blue);
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);

        bottom.add(inputField, BorderLayout.CENTER);
        bottom.add(sendButton, BorderLayout.EAST);

        root.add(top, BorderLayout.NORTH);
        root.add(scrollPane, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);

        frame.setContentPane(root);

        addMessage("NOURI", "Hello! I'm ready.", false);

        sendButton.addActionListener(e -> send());
        inputField.addActionListener(e -> send());

        frame.setVisible(true);
    }

    private void send() {

        String text = inputField.getText().trim();

        if (text.isEmpty()) {
            return;
        }

        addMessage("You", text, true);

        inputField.setText("");
        sendButton.setEnabled(false);

        // Run Gemini without freezing the GUI
        new Thread(() -> {

            String reply = commands.execute(text);

            SwingUtilities.invokeLater(() -> {

                addMessage("NOURI", reply, false);

                sendButton.setEnabled(true);
                inputField.requestFocus();

            });

        }).start();
    }

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
        label.setBorder(new EmptyBorder(10, 15, 10, 15));

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

        wrapper.setBorder(new EmptyBorder(5, 0, 5, 0));

        chatPanel.add(wrapper);
        chatPanel.revalidate();
        chatPanel.repaint();

        SwingUtilities.invokeLater(() -> {

            JScrollBar bar = scrollPane.getVerticalScrollBar();

            bar.setValue(bar.getMaximum());

        });
    }
}

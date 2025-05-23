import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AdminLogin {
    private static final String ADMIN_USERNAME = "admin";
    private static final char[] ADMIN_PASSWORD = "password123".toCharArray();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminLogin::new);
    }

    public AdminLogin() {
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Admin Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 250);
        frame.setLocationRelativeTo(null);

        // Main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JButton loginButton = new JButton("Login");
        JLabel messageLabel = new JLabel(" ", SwingConstants.CENTER);

        // Label + field panel
        mainPanel.add(createLabeledPanel("Username:", usernameField));
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createLabeledPanel("Password:", passwordField));
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(loginButton);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(messageLabel);

        // Style
        Font font = new Font("SansSerif", Font.PLAIN, 14);
        usernameField.setFont(font);
        passwordField.setFont(font);
        loginButton.setFont(font);
        messageLabel.setFont(font);

        // Action: Login
        ActionListener loginAction = e -> {
            String username = usernameField.getText().trim();
            char[] password = passwordField.getPassword();

            if (username.isEmpty() || password.length == 0) {
                showMessage(messageLabel, "Please enter both fields.", Color.RED);
            } else if (validateCredentials(username, password)) {
                showMessage(messageLabel, "Login successful!", new Color(0, 128, 0));
                // Future: Launch dashboard or next window
            } else {
                showMessage(messageLabel, "Invalid credentials. Try again.", Color.RED);
            }

            // Wipe password for security
            java.util.Arrays.fill(password, '\0');
        };

        loginButton.addActionListener(loginAction);

        // Add Enter key as trigger
        passwordField.addActionListener(loginAction);

        frame.setContentPane(mainPanel);
        frame.setVisible(true);
    }

    private JPanel createLabeledPanel(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(80, 25));
        panel.add(label, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private boolean validateCredentials(String username, char[] password) {
        return ADMIN_USERNAME.equals(username) &&
               java.util.Arrays.equals(ADMIN_PASSWORD, password);
    }

    private void showMessage(JLabel label, String message, Color color) {
        label.setText(message);
        label.setForeground(color);
    }
}

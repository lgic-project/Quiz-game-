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
        frame.setSize(400, 280);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JTextField usernameField = new JTextField(20);
        usernameField.setToolTipText("Enter admin username");

        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setToolTipText("Enter admin password");

        JCheckBox showPassword = new JCheckBox("Show Password");
        showPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        showPassword.addActionListener(e ->
            passwordField.setEchoChar(showPassword.isSelected() ? '\0' : '\u2022'));

        JButton loginButton = new JButton("Login");
        JLabel messageLabel = new JLabel(" ", SwingConstants.CENTER);

        // Styling
        Font font = new Font("SansSerif", Font.PLAIN, 14);
        usernameField.setFont(font);
        passwordField.setFont(font);
        loginButton.setFont(font);
        messageLabel.setFont(font);

        // Layout components
        mainPanel.add(createLabeledPanel("Username:", usernameField));
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createLabeledPanel("Password:", passwordField));
        mainPanel.add(showPassword);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(loginButton);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(messageLabel);

        // Login logic
        ActionListener loginAction = e -> {
            String username = usernameField.getText().trim();
            char[] password = passwordField.getPassword();

            if (username.isEmpty() || password.length == 0) {
                showMessage(messageLabel, "Please enter both username and password.", Color.RED);
            } else if (validateCredentials(username, password)) {
                showMessage(messageLabel, "Login successful!", new Color(0, 128, 0));
                // TODO: Open admin dashboard window here
                // new AdminDashboard();  // <- Example stub
                frame.dispose(); // Close login window
            } else {
                showMessage(messageLabel, "Invalid credentials. Try again.", Color.RED);
            }

            // Clear password from memory
            java.util.Arrays.fill(password, '\0');
        };

        loginButton.addActionListener(loginAction);
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

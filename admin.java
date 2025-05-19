import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AdminLogin {
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "password123";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminLogin().createAndShowGUI());
    }

    private void createAndShowGUI() {
        // Create the frame
        JFrame frame = new JFrame("Admin Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 220);
        frame.setLocationRelativeTo(null); // Center on screen
        frame.setLayout(new GridBagLayout());

        // Components
        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(15);
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(15);
        JButton loginButton = new JButton("Login");
        JLabel messageLabel = new JLabel(" ", SwingConstants.CENTER);

        // Styling
        Font font = new Font("SansSerif", Font.PLAIN, 14);
        usernameLabel.setFont(font);
        passwordLabel.setFont(font);
        messageLabel.setFont(font);

        // Layout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        frame.add(usernameLabel, gbc);

        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        frame.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
        frame.add(passwordLabel, gbc);

        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        frame.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        frame.add(loginButton, gbc);

        gbc.gridy = 3;
        frame.add(messageLabel, gbc);

        // Login logic
        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            char[] password = passwordField.getPassword();

            if (username.isEmpty() || password.length == 0) {
                showMessage(messageLabel, "Please enter both username and password.", Color.RED);
                return;
            }

            if (validateCredentials(username, password)) {
                showMessage(messageLabel, "Login Successful!", new Color(0, 128, 0));
                // You can open a new window here
            } else {
                showMessage(messageLabel, "Invalid credentials, try again.", Color.RED);
            }

            // Clear password for security
            java.util.Arrays.fill(password, '0');
        });

        // Show frame
        frame.setVisible(true);
    }

    private boolean validateCredentials(String username, char[] password) {
        return ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(new String(password));
    }

    private void showMessage(JLabel label, String message, Color color) {
        label.setText(message);
        label.setForeground(color);
    }
}

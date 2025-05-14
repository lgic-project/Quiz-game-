import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AdminLogin {
    // Hardcoded credentials
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "password123";

    public static void main(String[] args) {
        // Create the frame
        JFrame frame = new JFrame("Admin Login");
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(3, 2, 10, 10));

        // Create the components
        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");
        JLabel messageLabel = new JLabel();

        // Add components to the frame
        frame.add(usernameLabel);
        frame.add(usernameField);
        frame.add(passwordLabel);
        frame.add(passwordField);
        frame.add(loginButton);
        frame.add(messageLabel);

        // Add login button action listener
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                char[] password = passwordField.getPassword();

                if (ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(new String(password))) {
                    messageLabel.setForeground(Color.GREEN);
                    messageLabel.setText("Login Successful!");
                } else {
                    messageLabel.setForeground(Color.RED);
                    messageLabel.setText("Invalid credentials, try again.");
                }
            }
        });

        // Set frame visibility
        frame.setVisible(true);
    }
}

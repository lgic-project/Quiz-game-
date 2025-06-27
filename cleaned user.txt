import java.io.*;
import java.util.*;

public class UserAut {
    static final String USERS_FILE = "users.txt";
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        new File(USERS_FILE).createNewFile(); // Ensure file exists
        while (true) {
            System.out.print("\n1. Register\n2. Login\n3. Exit\nEnter choice: ");
            switch (sc.nextLine()) {
                case "1" -> register();
                case "2" -> login();
                case "3" -> System.exit(0);
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    static void register() throws IOException {
        System.out.print("Username: ");
        String username = sc.nextLine().trim();
        System.out.print("Password: ");
        String password = sc.nextLine().trim();
        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("Username and password cannot be empty.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(username + ",")) {
                    System.out.println("Username already exists.");
                    return;
                }
            }
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(USERS_FILE, true))) {
            bw.write(username + "," + password + "\n");
        }
        System.out.println("Registered successfully.");
    }

    static void login() throws IOException {
        System.out.print("Username: ");
        String username = sc.nextLine().trim();
        System.out.print("Password: ");
        String password = sc.nextLine().trim();

        try (BufferedReader br = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().equals(username + "," + password)) {
                    System.out.println("Login successful. Welcome, " + username + "!");
                    return;
                }
            }
        }
        System.out.println("Invalid credentials.");
    }
}


import java.io.*;
import java.util.Scanner;

public class UserAut{
    static Scanner sc = new Scanner(System.in);
    static final String USERS_FILE = "users.txt";

    public static void main(String[] args) throws IOException {
        while (true) {
            System.out.println("\n1. Register\n2. Login\n3. Exit");
            System.out.print("Enter your choice: ");
            int choice = Integer.parseInt(sc.nextLine());
            switch (choice) {
                case 1: registerUser(); break;
                case 2: loginUser(); break;
                case 3: System.exit(0);
                default: System.out.println("Invalid choice.");
            }
        }
    }

    static void registerUser() throws IOException {
        System.out.print("Enter username: ");
        String username = sc.nextLine().trim();
        System.out.print("Enter password: ");
        String password = sc.nextLine().trim();

        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("Username and password cannot be empty.");
            return;
        }

        BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.trim().startsWith(username + ",")) {
                System.out.println("Username already exists.");
                reader.close();
                return;
            }
        }
        reader.close();

        BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE, true));
        writer.write(username + "," + password + "\n");
        writer.close();
        System.out.println("Registered successfully.");
    }

    static void loginUser() throws IOException {
        System.out.print("Enter username: ");
        String username = sc.nextLine().trim();
        System.out.print("Enter password: ");
        String password = sc.nextLine().trim();

        BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE));
        String line;
        boolean found = false;
        while ((line = reader.readLine()) != null) {
            if (line.trim().equals(username + "," + password)) {
                found = true;
                break;
            }
        }
        reader.close();

        if (found) {
            System.out.println("Login successful. Welcome, " + username + "!");
        } else {
            System.out.println("Invalid credentials.");
        }
    }
}

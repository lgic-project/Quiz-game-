import java.util.Scanner;

public class QuizGameAdminLogin {

    // Hardcoded admin credentials (in real systems, use a database and hashing)
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "quiz123";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("===== Quiz Game Admin Login =====");

        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        if (authenticateAdmin(username, password)) {
            System.out.println("Login successful! Welcome, Admin.");
            showAdminMenu(scanner);
        } else {
            System.out.println("Invalid username or password. Access denied.");
        }

        scanner.close();
    }

    private static boolean authenticateAdmin(String username, String password) {
        return username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD);
    }

    private static void showAdminMenu(Scanner scanner) {
        int choice;

        do {
            System.out.println("\n==== Admin Menu ====");
            System.out.println("1. Start Quiz");
            System.out.println("2. Manage Questions");
            System.out.println("3. View Scores");
            System.out.println("4. Logout");

            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    startQuiz();
                    break;
                case 2:
                    manageQuestions();
                    break;
                case 3:
                    viewScores();
                    break;
                case 4:
                    System.out.println("Logging out...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }

        } while (choice != 4);
    }

    // Placeholder methods
    private static void startQuiz() {
        System.out.println("Starting the quiz (not implemented yet).");
    }

    private static void manageQuestions() {
        System.out.println("Managing questions (not implemented yet).");
    }

    private static void viewScores() {
        System.out.println("Viewing scores (not implemented yet).");
    }
}

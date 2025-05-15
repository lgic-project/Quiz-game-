import java.io.*;
import java.util.*;

public class AdminMenu {
    private static final String QUESTIONS_FILE = "questions.txt";
    private static final String SCORES_FILE = "scores.txt";
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n=== Admin Menu ===");
            System.out.println("1. View High Scores");
            System.out.println("2. Add Question");
            System.out.println("3. Delete Question");
            System.out.println("4. Exit");

            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    viewHighScores();
                    break;
                case "2":
                    addQuestion();
                    break;
                case "3":
                    deleteQuestion();
                    break;
                case "4":
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void viewHighScores() {
        try (BufferedReader reader = new BufferedReader(new FileReader(SCORES_FILE))) {
            System.out.println("\n--- High Scores ---");
            String line;
            List<String> scores = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                scores.add(line);
            }
            scores.sort(Comparator.reverseOrder()); // Highest score first
            for (String score : scores) {
                System.out.println(score);
            }
        } catch (IOException e) {
            System.out.println("No scores found.");
        }
    }

    private static void addQuestion() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(QUESTIONS_FILE, true))) {
            System.out.print("Enter question: ");
            String question = scanner.nextLine();
            String[] options = new String[4];
            for (int i = 0; i < 4; i++) {
                System.out.print("Enter option " + (i + 1) + ": ");
                options[i] = scanner.nextLine();
            }
            System.out.print("Enter correct option number (1-4): ");
            String correct = scanner.nextLine();

            writer.write(question + "|" + String.join(",", options) + "|" + correct);
            writer.newLine();
            System.out.println("Question added.");
        } catch (IOException e) {
            System.out.println("Error writing question.");
        }
    }

    private static void deleteQuestion() {
        try {
            List<String> questions = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(QUESTIONS_FILE))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    questions.add(line);
                }
            }

            if (questions.isEmpty()) {
                System.out.println("No questions to delete.");
                return;
            }

            for (int i = 0; i < questions.size(); i++) {
                System.out.println((i + 1) + ": " + questions.get(i).split("\\|")[0]);
            }

            System.out.print("Enter question number to delete: ");
            int index = Integer.parseInt(scanner.nextLine()) - 1;

            if (index >= 0 && index < questions.size()) {
                questions.remove(index);
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(QUESTIONS_FILE))) {
                    for (String q : questions) {
                        writer.write(q);
                        writer.newLine();
                    }
                }
                System.out.println("Question deleted.");
            } else {
                System.out.println("Invalid question number.");
            }

        } catch (IOException | NumberFormatException e) {
            System.out.println("Error deleting question.");
        }
    }
}

import java.util.*;

class Question {
    String question;
    String[] options;
    int correctOption;

    public Question(String question, String[] options, int correctOption) {
        this.question = question;
        this.options = options;
        this.correctOption = correctOption;
    }

    public boolean ask(Scanner sc) {
        System.out.println("\n" + question);
        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ". " + options[i]);
        }
        System.out.print("Your answer (1-4): ");
        int answer = sc.nextInt();
        return answer == correctOption;
    }
}

public class QuizGame {
    static Scanner sc = new Scanner(System.in);
    static List<Question> challenger1Questions = new ArrayList<>();

    public static void main(String[] args) {
        preloadQuestions();

        while (true) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. Admin Menu");
            System.out.println("2. User Menu");
            System.out.println("3. Exit");
            System.out.print("Choose option: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1: adminMenu(); break;
                case 2: userMenu(); break;
                case 3: System.out.println("Goodbye!"); return;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    static void adminMenu() {
        while (true) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1. Add Question to Challenger 1");
            System.out.println("2. View Questions in Challenger 1");
            System.out.println("3. Back");
            System.out.print("Choose option: ");
            int choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (choice) {
                case 1 -> addQuestion();
                case 2 -> viewQuestions();
                case 3 -> { re


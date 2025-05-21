import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

class User {
    String username;
    String password;

    User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}

class Question {
    int id;
    String questionText;
    String[] options;
    int correctOption;

    Question(int id, String questionText, String[] options, int correctOption) {
        this.id = id;
        this.questionText = questionText;
        this.options = options;
        this.correctOption = correctOption;
    }

    public String toFileString() {
        return id + "," + questionText + "," + String.join("|", options) + "," + correctOption;
    }

    public static Question fromFileString(String line) {
        String[] parts = line.split(",");
        String[] options = parts[2].split("\\|");
        return new Question(Integer.parseInt(parts[0]), parts[1], options, Integer.parseInt(parts[3]));
    }
}

public class Quiz {
    static Scanner sc = new Scanner(System.in);
    static final String USERS_FILE = "users.txt";
    static final String QUESTIONS_FILE = "questions.txt";
    static final String SCORES_FILE = "scores.txt";

    static void mainMenu() throws IOException {
        while (true) {
            System.out.println("\n1. Login as User\n2. Register\n3. Login as Admin\n4. Exit");
            int choice = Integer.parseInt(sc.nextLine());
            switch (choice) {
                case 1: userLogin(); break;
                case 2: registerUser(); break;
                case 3: adminLogin(); break;
                case 4: System.exit(0);
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


    static void userLogin() throws IOException {
        System.out.print("Username: ");
        String username = sc.nextLine().trim();  // Trim input
        System.out.print("Password: ");
        String password = sc.nextLine().trim();  // Trim input

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

        if (found) userMenu(username);
        else System.out.println("Invalid credentials.");
    }

    static void userMenu(String username) throws IOException {
        while (true) {
            System.out.println("\n1. Play Quiz\n2. View Last Score\n3. View High Score\n4. Logout");
            int choice = Integer.parseInt(sc.nextLine());
            switch (choice) {
                case 1: playQuiz(username); break;
                case 2: viewLastScore(username); break;
                case 3: viewHighScore(username); break;
                case 4: return;
            }
        }
    }

    static void playQuiz(String username) throws IOException {
        List<Question> questions = loadQuestions();
        if (questions.isEmpty()) {
            System.out.println("No questions available.");
            return;
        }

        int score = 0;
        for (Question q : questions) {
            System.out.println(q.questionText);
            for (int i = 0; i < q.options.length; i++) {
                System.out.println((i + 1) + ". " + q.options[i]);
            }
            System.out.print("Answer (1-4): ");
            int ans = Integer.parseInt(sc.nextLine());
            if (ans == q.correctOption) score++;
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(SCORES_FILE, true));
        writer.write(username + "," + score + "," + LocalDateTime.now() + "\n");
        writer.close();
        System.out.println("Your score: " + score);
    }

    static void viewLastScore(String username) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(SCORES_FILE));
        String line;
        String lastScore = null;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith(username + ",")) {
                lastScore = line;
            }
        }
        reader.close();
        System.out.println(lastScore != null ? "Last Score: " + lastScore : "No score found.");
    }

    static void viewHighScore(String username) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(SCORES_FILE));
        String line;
        int max = -1;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith(username + ",")) {
                int score = Integer.parseInt(line.split(",")[1]);
                if (score > max) max = score;
            }
        }
        reader.close();
        System.out.println(max != -1 ? "High Score: " + max : "No score found.");
    }

    static void adminLogin() throws IOException {
        System.out.print("Admin Username: ");
        String username = sc.nextLine();
        System.out.print("Admin Password: ");
        String password = sc.nextLine();

        if (username.equals("admin") && password.equals("admin123")) {
            adminMenu();
        } else {
            System.out.println("Invalid admin credentials.");
        }
    }

    static void adminMenu() throws IOException {
        while (true) {
            System.out.println("\n1. Add Question\n2. Update Question\n3. Delete Question\n4. View All Questions\n5. View All Scores\n6. Logout");
            int choice = Integer.parseInt(sc.nextLine());
            switch (choice) {
                case 1: addQuestion(); break;
                case 2: updateQuestion(); break;
                case 3: deleteQuestion(); break;
                case 4: viewAllQuestions(); break;
                case 5: viewAllScores(); break;
                case 6: return;
            }
        }
    }

    static void addQuestion() throws IOException {
        System.out.print("Enter question: ");
        String text = sc.nextLine();
        String[] options = new String[4];
        for (int i = 0; i < 4; i++) {
            System.out.print("Option " + (i + 1) + ": ");
            options[i] = sc.nextLine();
        }
        System.out.print("Correct option (1-4): ");
        int correct = Integer.parseInt(sc.nextLine());

        List<Question> questions = loadQuestions();
        int id = questions.size() + 1;
        questions.add(new Question(id, text, options, correct));
        saveQuestions(questions);
        System.out.println("Question added.");
    }

    static void updateQuestion() throws IOException {
        List<Question> questions = loadQuestions();
        System.out.print("Enter question ID to update: ");
        int id = Integer.parseInt(sc.nextLine());

        for (Question q : questions) {
            if (q.id == id) {
                System.out.println("Current Question: " + q.questionText);
                System.out.print("New question text (press Enter to keep unchanged): ");
                String newText = sc.nextLine();
                if (!newText.trim().isEmpty()) {
                    q.questionText = newText;
                }

                for (int i = 0; i < 4; i++) {
                    System.out.println("Current Option " + (i + 1) + ": " + q.options[i]);
                    System.out.print("New Option " + (i + 1) + " (press Enter to keep unchanged): ");
                    String newOption = sc.nextLine();
                    if (!newOption.trim().isEmpty()) {
                        q.options[i] = newOption;
                    }
                }

                System.out.println("Current Correct Option: " + q.correctOption);
                System.out.print("New correct option (1-4, press Enter to keep unchanged): ");
                String newCorrectStr = sc.nextLine();
                if (!newCorrectStr.trim().isEmpty()) {
                    int newCorrect = Integer.parseInt(newCorrectStr);
                    if (newCorrect >= 1 && newCorrect <= 4) {
                        q.correctOption = newCorrect;
                    } else {
                        System.out.println("Invalid option. Keeping original correct answer.");
                    }
                }

                saveQuestions(questions);
                System.out.println("Question updated.");
                return;
            }
        }
        System.out.println("Question ID not found.");
    }

    static void deleteQuestion() throws IOException {
        List<Question> questions = loadQuestions();
        System.out.print("Enter question ID to delete: ");
        int id = Integer.parseInt(sc.nextLine());
        questions.removeIf(q -> q.id == id);
        saveQuestions(questions);
        System.out.println("Question deleted.");
    }

    static void viewAllQuestions() throws IOException {
        List<Question> questions = loadQuestions();
        if (questions.isEmpty()) {
            System.out.println("No questions available.");
            return;
        }
        for (Question q : questions) {
            System.out.println("ID: " + q.id);
            System.out.println("Question: " + q.questionText);
            for (int i = 0; i < q.options.length; i++) {
                System.out.println((i + 1) + ". " + q.options[i]);
            }
            System.out.println("Correct Option: " + q.correctOption);
            System.out.println();
        }
    }

    static void viewAllScores() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(SCORES_FILE));
        String line;
        System.out.println("Scores in chronological order:");
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        reader.close();
    }

    static List<Question> loadQuestions() throws IOException {
        List<Question> list = new ArrayList<>();
        File file = new File(QUESTIONS_FILE);
        if (!file.exists()) return list;
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = reader.readLine()) != null) {
            list.add(Question.fromFileString(line));
        }
        reader.close();
        return list;
    }

    static void saveQuestions(List<Question> questions) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(QUESTIONS_FILE));
        for (Question q : questions) {
            writer.write(q.toFileString() + "\n");
        }
        writer.close();
    }

    public static void main(String[] args) throws IOException {
        mainMenu();
    }
}

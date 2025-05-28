import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.security.MessageDigest;
import java.util.Base64;

class User {
    String username;
    String password;

    User(String username, String password) {
        this.username = username.toLowerCase();
        this.password = password;
    }
}

class Question {
    int id;
    String questionText;
    String[] options;
    int correctOption; // 1-based

    Question(int id, String questionText, String[] options, int correctOption) {
        this.id = id;
        this.questionText = questionText;
        this.options = options;
        this.correctOption = correctOption;
    }

    public String toFileString() {
        return id + "," + escape(questionText) + "," +
                String.join("|", Arrays.stream(options).map(Question::escape).toArray(String[]::new)) +
                "," + correctOption;
    }

    public static Question fromFileString(String line) {
        String[] parts = line.split(",", 4);
        String[] options = parts[2].split("\\|");
        return new Question(
                Integer.parseInt(parts[0]),
                unescape(parts[1]),
                Arrays.stream(options).map(Question::unescape).toArray(String[]::new),
                Integer.parseInt(parts[3])
        );
    }

    private static String escape(String s) {
        return s.replace(",", "\\,").replace("|", "\\|");
    }

    private static String unescape(String s) {
        return s.replace("\\,", ",").replace("\\|", "|");
    }
}

public class Quiz {
    static Scanner sc = new Scanner(System.in);
    static final String USERS_FILE = "users.txt";
    static final String QUESTIONS_FILE = "questions.txt";
    static final String SCORES_FILE = "scores.txt";
    static final String ADMIN_USERNAME = "admin";
    static final String ADMIN_PASSWORD = "admin123";

    public static void main(String[] args) throws IOException {
        ensureFileExists(USERS_FILE);
        ensureFileExists(QUESTIONS_FILE);
        ensureFileExists(SCORES_FILE);
        mainMenu();
    }

    static void ensureFileExists(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) file.createNewFile();
    }

    static void mainMenu() throws IOException {
        while (true) {
            System.out.println("\n1. Login as User\n2. Register\n3. Login as Admin\n4. Exit");
            int choice = getIntInput("Choose an option: ");
            switch (choice) {
                case 1 -> userLogin();
                case 2 -> registerUser();
                case 3 -> adminLogin();
                case 4 -> System.exit(0);
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    static void registerUser() throws IOException {
        System.out.print("Enter username: ");
        String username = sc.nextLine().trim().toLowerCase();
        System.out.print("Enter password: ");
        String password = sc.nextLine().trim();

        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("Username and password cannot be empty.");
            return;
        }

        BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith(username + ",")) {
                System.out.println("Username already exists.");
                reader.close();
                return;
            }
        }
        reader.close();

        BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE, true));
        writer.write(username + "," + hashPassword(password) + "\n");
        writer.close();
        System.out.println("Registered successfully.");
    }

    static void userLogin() throws IOException {
        System.out.print("Username: ");
        String username = sc.nextLine().trim().toLowerCase();
        System.out.print("Password: ");
        String password = sc.nextLine().trim();
        String hashed = hashPassword(password);

        BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE));
        String line;
        boolean found = false;
        while ((line = reader.readLine()) != null) {
            if (line.trim().equals(username + "," + hashed)) {
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
            int choice = getIntInput("Choose an option: ");
            switch (choice) {
                case 1 -> playQuiz(username);
                case 2 -> viewLastScore(username);
                case 3 -> viewHighScore(username);
                case 4 -> { return; }
                default -> System.out.println("Invalid choice.");
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
            int ans = getIntInput("Answer (1-4): ");
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
            if (line.startsWith(username + ",")) lastScore = line;
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
        String username = sc.nextLine().trim();
        System.out.print("Admin Password: ");
        String password = sc.nextLine().trim();

        if (username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
            adminMenu();
        } else {
            System.out.println("Invalid admin credentials.");
        }
    }

    static void adminMenu() throws IOException {
        while (true) {
            System.out.println("\n1. Add Question\n2. Update Question\n3. Delete Question\n4. View All Questions\n5. View All Scores\n6. Logout");
            int choice = getIntInput("Choose an option: ");
            switch (choice) {
                case 1 -> addQuestion();
                case 2 -> updateQuestion();
                case 3 -> deleteQuestion();
                case 4 -> viewAllQuestions();
                case 5 -> viewAllScores();
                case 6 -> { return; }
                default -> System.out.println("Invalid choice.");
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
        int correct = getIntInput("Correct option (1-4): ");

        List<Question> questions = loadQuestions();
        int maxId = questions.stream().mapToInt(q -> q.id).max().orElse(0) + 1;
        questions.add(new Question(maxId, text, options, correct));
        saveQuestions(questions);
        System.out.println("Question added.");
    }

    static void updateQuestion() throws IOException {
        List<Question> questions = loadQuestions();
        int id = getIntInput("Enter question ID to update: ");

        for (Question q : questions) {
            if (q.id == id) {
                System.out.println("Current Question: " + q.questionText);
                System.out.print("New question text (press Enter to keep unchanged): ");
                String newText = sc.nextLine();
                if (!newText.trim().isEmpty()) q.questionText = newText;

                for (int i = 0; i < 4; i++) {
                    System.out.println("Current Option " + (i + 1) + ": " + q.options[i]);
                    System.out.print("New Option " + (i + 1) + " (press Enter to keep unchanged): ");
                    String newOption = sc.nextLine();
                    if (!newOption.trim().isEmpty()) q.options[i] = newOption;
                }

                System.out.println("Current Correct Option: " + q.correctOption);
                System.out.print("New correct option (1-4, press Enter to keep unchanged): ");
                String newCorrectStr = sc.nextLine();
                if (!newCorrectStr.trim().isEmpty()) {
                    int newCorrect = Integer.parseInt(newCorrectStr);
                    if (newCorrect >= 1 && newCorrect <= 4) {
                        q.correctOption = newCorrect;
                    } else {
                        System.out.println("Invalid option. Keeping original.");
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
        int id = getIntInput("Enter question ID to delete: ");
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
        System.out.println("Scores:");
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        reader.close();
    }

    static List<Question> loadQuestions() throws IOException {
        List<Question> list = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(QUESTIONS_FILE));
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

    static int getIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            return null;
        }
    }
}

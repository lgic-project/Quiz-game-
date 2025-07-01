package User;

import db.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizScreenController {

    @FXML private Label questionLabel, scoreLabel;
    @FXML private RadioButton option1Radio, option2Radio, option3Radio, option4Radio;
    @FXML private Button nextButton, submitButton;
    @FXML private ToggleGroup answerGroup;

    private String loggedInUsername;
    private String category;
    private List<Question> questions = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int score = 0;
    private static final int POINTS_PER_QUESTION = 10;

    public void setLoggedInUsername(String username) {
        this.loggedInUsername = username;
    }

    public void setCategory(String category) {
        this.category = category;
        loadQuestions();
    }

    @FXML
    public void initialize() {
        answerGroup = new ToggleGroup();
        option1Radio.setToggleGroup(answerGroup);
        option2Radio.setToggleGroup(answerGroup);
        option3Radio.setToggleGroup(answerGroup);
        option4Radio.setToggleGroup(answerGroup);
    }

    private void loadQuestions() {
        String sql = "SELECT * FROM questions WHERE category = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Database connection is null.");
                return;
            }
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, category);
            ResultSet rs = stmt.executeQuery();
            questions.clear();
            while (rs.next()) {
                questions.add(new Question(
                        rs.getInt("id"),
                        rs.getString("question_text"),
                        rs.getString("option1"),
                        rs.getString("option2"),
                        rs.getString("option3"),
                        rs.getString("option4"),
                        rs.getString("correct_option"),
                        rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null,
                        rs.getString("category")
                ));
            }
            if (questions.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "No Questions", "No questions available for category: " + category);
                returnToDashboard();
                return;
            }
            Collections.shuffle(questions);
            currentQuestionIndex = 0;
            score = 0;
            scoreLabel.setText("Score: 0");
            displayQuestion();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load questions: " + e.getMessage());
        }
    }

    private void displayQuestion() {
        if (questions.isEmpty() || currentQuestionIndex >= questions.size()) {
            submitQuiz();
            return;
        }
        Question q = questions.get(currentQuestionIndex);
        questionLabel.setText(q.getQuestionText());
        option1Radio.setText(q.getOption1());
        option2Radio.setText(q.getOption2());
        option3Radio.setText(q.getOption3());
        option4Radio.setText(q.getOption4());
        answerGroup.selectToggle(null);
    }

    @FXML
    private void handleNextQuestion(ActionEvent event) {
        RadioButton selected = (RadioButton) answerGroup.getSelectedToggle();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Answer", "Please select an answer.");
            return;
        }
        Question q = questions.get(currentQuestionIndex);
        String selectedOption = selected.getText();
        String correctOption = q.getCorrectOption();
        if (("Option 1".equals(correctOption) && selectedOption.equals(q.getOption1())) ||
                ("Option 2".equals(correctOption) && selectedOption.equals(q.getOption2())) ||
                ("Option 3".equals(correctOption) && selectedOption.equals(q.getOption3())) ||
                ("Option 4".equals(correctOption) && selectedOption.equals(q.getOption4()))) {
            score += POINTS_PER_QUESTION;
            scoreLabel.setText("Score: " + score);
        }
        currentQuestionIndex++;
        displayQuestion();
    }

    @FXML
    private void handleSubmitQuiz(ActionEvent event) {
        submitQuiz();
    }

    private void submitQuiz() {
        if (loggedInUsername == null) {
            showAlert(Alert.AlertType.ERROR, "Authentication Error", "User not logged in.");
            returnToDashboard();
            return;
        }
        String sql = "INSERT INTO user_scores (user_id, score, category, timestamp) VALUES ((SELECT id FROM users WHERE username = ?), ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Database connection is null.");
                return;
            }
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, loggedInUsername);
            stmt.setInt(2, score);
            stmt.setString(3, category);
            stmt.setString(4, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            stmt.executeUpdate();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/loginUI/score_summary.fxml"));
            Parent root = loader.load();
            ScoreSummaryController controller = loader.getController();
            controller.setLoggedInUsername(loggedInUsername);
            controller.setScore(score);
            controller.setCategory(category);
            Stage stage = (Stage) submitButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Quiz Results");
        } catch (SQLException | IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save score: " + e.getMessage());
        }
    }

    private void returnToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/loginUI/UserDashboard.fxml"));
            Parent root = loader.load();
            UserDashboardController controller = loader.getController();
            controller.setLoggedInUsername(loggedInUsername);
            Stage stage = (Stage) submitButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("User Dashboard");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load dashboard: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class Question {
        private final int id;
        private final String questionText, option1, option2, option3, option4, correctOption, category;
        private final LocalDateTime createdAt;

        public Question(int id, String questionText, String option1, String option2, String option3,
                        String option4, String correctOption, LocalDateTime createdAt, String category) {
            this.id = id;
            this.questionText = questionText;
            this.option1 = option1;
            this.option2 = option2;
            this.option3 = option3;
            this.option4 = option4;
            this.correctOption = correctOption;
            this.createdAt = createdAt;
            this.category = category;
        }

        public String getQuestionText() { return questionText; }
        public String getOption1() { return option1; }
        public String getOption2() { return option2; }
        public String getOption3() { return option3; }
        public String getOption4() { return option4; }
        public String getCorrectOption() { return correctOption; }
        public String getCategory() { return category; }
    }
}
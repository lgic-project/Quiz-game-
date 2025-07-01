package User;

import db.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;

public class AdminDashboardController {

    @FXML private TextField questionField, option1Field, option2Field, option3Field, option4Field;
    @FXML private ComboBox<String> categoryCombo, correctOptionCombo;
    @FXML private TableView<Question> questionTable;
    @FXML private TableColumn<Question, String> questionColumn, categoryColumn, correctOptionColumn;
    @FXML private TableView<UserScore> scoreTable;
    @FXML private TableColumn<UserScore, String> userColumn, timestampColumn;
    @FXML private TableColumn<UserScore, Integer> scoreColumn;

    private final ObservableList<Question> questionList = FXCollections.observableArrayList();
    private final ObservableList<UserScore> scoreList = FXCollections.observableArrayList();
    private final ObservableList<String> categories = FXCollections.observableArrayList(
            "General Knowledge", "Science", "Math", "History"
    );

    @FXML
    public void initialize() {
        // Setup Table Columns
        questionColumn.setCellValueFactory(new PropertyValueFactory<>("questionText"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        correctOptionColumn.setCellValueFactory(new PropertyValueFactory<>("correctOption"));
        questionTable.setItems(questionList);

        userColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        timestampColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        scoreTable.setItems(scoreList);

        correctOptionCombo.setItems(FXCollections.observableArrayList("Option 1", "Option 2", "Option 3", "Option 4"));
        categoryCombo.setItems(categories);

        loadQuestions();
        loadUserScores();

        // When row is selected, populate fields
        questionTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                questionField.setText(newSel.getQuestionText());
                categoryCombo.setValue(newSel.getCategory());
                option1Field.setText(newSel.getOption1());
                option2Field.setText(newSel.getOption2());
                option3Field.setText(newSel.getOption3());
                option4Field.setText(newSel.getOption4());
                correctOptionCombo.setValue(newSel.getCorrectOption());
            } else {
                clearInputFields();
            }
        });
    }

    private void loadQuestions() {
        String sql = "SELECT * FROM questions ORDER BY created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            questionList.clear();
            while (rs.next()) {
                LocalDateTime createdAt = rs.getTimestamp("created_at") != null
                        ? rs.getTimestamp("created_at").toLocalDateTime()
                        : null;
                questionList.add(new Question(
                        rs.getInt("id"),
                        rs.getString("question_text"),
                        rs.getString("option1"),
                        rs.getString("option2"),
                        rs.getString("option3"),
                        rs.getString("option4"),
                        rs.getString("correct_option"),
                        createdAt,
                        rs.getString("category")
                ));
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load questions: " + e.getMessage());
        }
    }

    private void loadUserScores() {
        String sql = "SELECT u.username, us.score, us.timestamp FROM user_scores us JOIN users u ON us.user_id = u.id ORDER BY us.timestamp DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            scoreList.clear();
            while (rs.next()) {
                scoreList.add(new UserScore(
                        rs.getString("username"),
                        rs.getInt("score"),
                        rs.getString("timestamp")
                ));
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load scores: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddQuestion(ActionEvent event) {
        if (!areFieldsFilled()) return;

        String sql = "INSERT INTO questions (question_text, option1, option2, option3, option4, correct_option, category) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, questionField.getText());
            stmt.setString(2, option1Field.getText());
            stmt.setString(3, option2Field.getText());
            stmt.setString(4, option3Field.getText());
            stmt.setString(5, option4Field.getText());
            stmt.setString(6, correctOptionCombo.getValue());
            stmt.setString(7, categoryCombo.getValue());

            stmt.executeUpdate();
            loadQuestions();
            clearInputFields();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Question added.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add question: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteQuestion(ActionEvent event) {
        Question sel = questionTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a question to delete.");
            return;
        }

        String sql = "DELETE FROM questions WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sel.getId());
            stmt.executeUpdate();
            loadQuestions();
            clearInputFields();
            showAlert(Alert.AlertType.INFORMATION, "Deleted", "Question deleted.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to delete question: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdateQuestion(ActionEvent event) {
        Question sel = questionTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a question to update.");
            return;
        }

        if (!areFieldsFilled()) return;

        String sql = "UPDATE questions SET question_text = ?, option1 = ?, option2 = ?, option3 = ?, option4 = ?, correct_option = ?, category = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, questionField.getText());
            stmt.setString(2, option1Field.getText());
            stmt.setString(3, option2Field.getText());
            stmt.setString(4, option3Field.getText());
            stmt.setString(5, option4Field.getText());
            stmt.setString(6, correctOptionCombo.getValue());
            stmt.setString(7, categoryCombo.getValue());
            stmt.setInt(8, sel.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                loadQuestions();
                clearInputFields();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Question updated.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Update Error", "No question was updated.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to update question: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/loginUI/admin_login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Admin Login");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load login screen.");
        }
    }

    private boolean areFieldsFilled() {
        if (questionField.getText().isEmpty() ||
                option1Field.getText().isEmpty() ||
                option2Field.getText().isEmpty() ||
                option3Field.getText().isEmpty() ||
                option4Field.getText().isEmpty() ||
                correctOptionCombo.getValue() == null ||
                categoryCombo.getValue() == null) {

            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill in all fields and select the correct option and category.");
            return false;
        }
        return true;
    }

    private void clearInputFields() {
        questionField.clear();
        option1Field.clear();
        option2Field.clear();
        option3Field.clear();
        option4Field.clear();
        correctOptionCombo.setValue(null);
        categoryCombo.setValue(null);
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

        public int getId() { return id; }
        public String getQuestionText() { return questionText; }
        public String getOption1() { return option1; }
        public String getOption2() { return option2; }
        public String getOption3() { return option3; }
        public String getOption4() { return option4; }
        public String getCorrectOption() { return correctOption; }
        public String getCategory() { return category; }
        public LocalDateTime getCreatedAt() { return createdAt; }
    }

    public static class UserScore {
        private final String username;
        private final int score;
        private final String timestamp;

        public UserScore(String username, int score, String timestamp) {
            this.username = username;
            this.score = score;
            this.timestamp = timestamp;
        }

        public String getUsername() { return username; }
        public int getScore() { return score; }
        public String getTimestamp() { return timestamp; }
    }
}
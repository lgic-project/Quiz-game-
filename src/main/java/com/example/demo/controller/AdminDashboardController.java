package com.example.demo.controller;

import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import db.DatabaseConnection; // Assuming this is the correct package for DatabaseConnection
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;

public class AdminDashboardController {

    @FXML private TextField questionField;
    @FXML private TextField option1Field;
    @FXML private TextField option2Field;
    @FXML private TextField option3Field;
    @FXML private TextField option4Field;
    @FXML private ComboBox<String> correctOptionCombo;
    @FXML private TableView<Question> questionTable;
    @FXML private TableColumn<Question, String> questionColumn;

    @FXML private TableView<UserScore> scoreTable;
    @FXML private TableColumn<UserScore, String> userColumn;
    @FXML private TableColumn<UserScore, Integer> scoreColumn;
    @FXML private TableColumn<UserScore, String> timestampColumn;

    private ObservableList<Question> questionList = FXCollections.observableArrayList();
    private ObservableList<UserScore> scoreList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configure table columns
        questionColumn.setCellValueFactory(new PropertyValueFactory<>("questionText"));
        questionTable.setItems(questionList);

        userColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        timestampColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        scoreTable.setItems(scoreList);

        correctOptionCombo.setItems(FXCollections.observableArrayList("Option 1", "Option 2", "Option 3", "Option 4"));

        // Load data from database
        loadQuestions();
        loadUserScores();

        // Add listener to populate input fields when a question is selected
        questionTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                questionField.setText(newSelection.getQuestionText());
                option1Field.setText(newSelection.getOption1());
                option2Field.setText(newSelection.getOption2());
                option3Field.setText(newSelection.getOption3());
                option4Field.setText(newSelection.getOption4());
                correctOptionCombo.setValue(newSelection.getCorrectOption());
            } else {
                // Clear fields if no question is selected
                clearInputFields();
            }
        });
    }

    private void loadQuestions() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, question_text, option1, option2, option3, option4, correct_option, created_at FROM questions")) {
            questionList.clear();
            while (rs.next()) {
                questionList.add(new Question(
                        rs.getInt("id"),
                        rs.getString("question_text"),
                        rs.getString("option1"),
                        rs.getString("option2"),
                        rs.getString("option3"),
                        rs.getString("option4"),
                        rs.getString("correct_option"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                ));
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load questions: " + e.getMessage());
        }
    }

    private void loadUserScores() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT u.username, us.score, us.timestamp FROM user_scores us JOIN users u ON us.user_id = u.id")) {
            scoreList.clear();
            while (rs.next()) {
                scoreList.add(new UserScore(
                        rs.getString("username"),
                        rs.getInt("score"),
                        rs.getString("timestamp")
                ));
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load user scores: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddQuestion(ActionEvent event) {
        String question = questionField.getText();
        String opt1 = option1Field.getText();
        String opt2 = option2Field.getText();
        String opt3 = option3Field.getText();
        String opt4 = option4Field.getText();
        String correct = correctOptionCombo.getValue();

        if (!question.isEmpty() && !opt1.isEmpty() && !opt2.isEmpty() && !opt3.isEmpty() && !opt4.isEmpty() && correct != null) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "INSERT INTO questions (question_text, option1, option2, option3, option4, correct_option) VALUES (?, ?, ?, ?, ?, ?)")) {
                stmt.setString(1, question);
                stmt.setString(2, opt1);
                stmt.setString(3, opt2);
                stmt.setString(4, opt3);
                stmt.setString(5, opt4);
                stmt.setString(6, correct);
                stmt.executeUpdate();
                loadQuestions(); // Refresh question list
                clearInputFields();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Question added successfully.");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add question: " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Input Error", "All fields must be filled.");
        }
    }

    @FXML
    private void handleDeleteQuestion(ActionEvent event) {
        Question selected = questionTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM questions WHERE id = ?")) {
                stmt.setInt(1, selected.getId());
                stmt.executeUpdate();
                loadQuestions(); // Refresh question list
                clearInputFields();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Question deleted successfully.");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to delete question: " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a question to delete.");
        }
    }

    @FXML
    private void handleUpdateQuestion(ActionEvent event) {
        Question selected = questionTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            String question = questionField.getText();
            String opt1 = option1Field.getText();
            String opt2 = option2Field.getText();
            String opt3 = option3Field.getText();
            String opt4 = option4Field.getText();
            String correct = correctOptionCombo.getValue();

            if (!question.isEmpty() && !opt1.isEmpty() && !opt2.isEmpty() && !opt3.isEmpty() && !opt4.isEmpty() && correct != null) {
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(
                             "UPDATE questions SET question_text = ?, option1 = ?, option2 = ?, option3 = ?, option4 = ?, correct_option = ? WHERE id = ?")) {
                    stmt.setString(1, question);
                    stmt.setString(2, opt1);
                    stmt.setString(3, opt2);
                    stmt.setString(4, opt3);
                    stmt.setString(5, opt4);
                    stmt.setString(6, correct);
                    stmt.setInt(7, selected.getId());
                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected > 0) {
                        loadQuestions(); // Refresh question list
                        clearInputFields();
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Question updated successfully.");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Update Error", "No question was updated. Please try again.");
                    }
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to update question: " + e.getMessage());
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "Input Error", "All fields must be filled.");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a question to update.");
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/fxml/admin_login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Admin Login");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load login page: " + e.getMessage());
        }
    }

    private void clearInputFields() {
        questionField.clear();
        option1Field.clear();
        option2Field.clear();
        option3Field.clear();
        option4Field.clear();
        correctOptionCombo.setValue(null);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public class Question {
        private int id;
        private String questionText;
        private String option1;
        private String option2;
        private String option3;
        private String option4;
        private String correctOption;
        private LocalDateTime createdAt;

        public Question(int id, String questionText, String option1, String option2, String option3, String option4, String correctOption, LocalDateTime createdAt) {
            this.id = id;
            this.questionText = questionText;
            this.option1 = option1;
            this.option2 = option2;
            this.option3 = option3;
            this.option4 = option4;
            this.correctOption = correctOption;
            this.createdAt = createdAt;
        }

        public int getId() { return id; }
        public String getQuestionText() { return questionText; }
        public void setQuestionText(String questionText) { this.questionText = questionText; }
        public String getOption1() { return option1; }
        public String getOption2() { return option2; }
        public String getOption3() { return option3; }
        public String getOption4() { return option4; }
        public String getCorrectOption() { return correctOption; }
        public LocalDateTime getCreatedAt() { return createdAt; }
    }

    public class UserScore {
        private String username;
        private int score;
        private String timestamp;

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
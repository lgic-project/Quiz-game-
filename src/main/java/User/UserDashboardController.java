package User;

import db.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.Optional;

public class UserDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label statusLabel;
    @FXML private ComboBox<String> categoryCombo;
    @FXML private TableView<Score> scoreTable;
    @FXML private TableColumn<Score, String> categoryColumn;
    @FXML private TableColumn<Score, Integer> scoreColumn;
    @FXML private TableColumn<Score, String> timestampColumn;
    @FXML private Button playQuizButton, viewScoreButton, highScoreButton, logoutButton;

    private String loggedInUsername;

    private final ObservableList<Score> scoreList = FXCollections.observableArrayList();
    private final ObservableList<String> categories = FXCollections.observableArrayList(
            "General Knowledge", "Science", "Math", "History"
    );

    public void setLoggedInUsername(String username) {
        this.loggedInUsername = username;
        welcomeLabel.setText("Welcome, " + username + "!");
    }

    @FXML
    public void initialize() {
        initCategoryCombo();
        initTableColumns();
        scoreTable.setItems(scoreList);
        statusLabel.setText("Dashboard ready.");
    }

    private void initCategoryCombo() {
        categoryCombo.setItems(categories);
        categoryCombo.setValue(categories.get(0)); // Set default
    }

    private void initTableColumns() {
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        timestampColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
    }

    private void loadScoresFromDatabase() {
        String query = """
                SELECT category, score, timestamp 
                FROM user_scores 
                WHERE user_id = (SELECT id FROM users WHERE username = ?) 
                ORDER BY timestamp DESC
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            if (conn == null) {
                showAlert(Alert.AlertType.ERROR, "Connection Failed", "Database connection is unavailable.");
                return;
            }

            stmt.setString(1, loggedInUsername);
            ResultSet rs = stmt.executeQuery();

            scoreList.clear();
            while (rs.next()) {
                scoreList.add(new Score(
                        rs.getString("category"),
                        rs.getInt("score"),
                        rs.getString("timestamp")
                ));
            }

            statusLabel.setText("Scores refreshed.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error loading scores: " + e.getMessage());
        }
    }

    @FXML
    private void handlePlayQuiz(ActionEvent event) {
        String selectedCategory = categoryCombo.getValue();

        if (selectedCategory == null || selectedCategory.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Select Category", "Please select a category first.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/loginUI/quiz_screen.fxml"));
            Parent root = loader.load();
            QuizScreenController controller = loader.getController();
            controller.setLoggedInUsername(loggedInUsername);
            controller.setCategory(selectedCategory);

            Stage stage = (Stage) playQuizButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Quiz - " + selectedCategory);
            statusLabel.setText("Launching quiz...");

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Cannot load quiz screen: " + e.getMessage());
        }
    }

    @FXML
    private void handleViewScores(ActionEvent event) {
        loadScoresFromDatabase();
    }

    @FXML
    private void handleHighScores(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/loginUI/high_score.fxml"));
            Parent root = loader.load();
            HighScoreController controller = loader.getController();
            controller.setLoggedInUsername(loggedInUsername);

            Stage stage = (Stage) highScoreButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("High Scores");
            statusLabel.setText("Opened High Scores.");

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to open High Scores: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Logout Confirmation");
        confirm.setHeaderText("Are you sure you want to logout?");
        confirm.setContentText("You will be returned to the login screen.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/loginUI/Login.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) logoutButton.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Login");
                statusLabel.setText("Logged out successfully.");
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Logout Error", "Failed to return to login: " + e.getMessage());
            }
        } else {
            statusLabel.setText("Logout cancelled.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Inner class representing score records
    public static class Score {
        private final String category;
        private final int score;
        private final String timestamp;

        public Score(String category, int score, String timestamp) {
            this.category = category;
            this.score = score;
            this.timestamp = timestamp;
        }

        public String getCategory() { return category; }
        public int getScore() { return score; }
        public String getTimestamp() { return timestamp; }
    }
}

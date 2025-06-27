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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDashboardController {

    @FXML private Label welcomeLabel;
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
        categoryCombo.setItems(categories);
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        timestampColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        scoreTable.setItems(scoreList);
        loadScores();
    }

    private void loadScores() {
        String sql = "SELECT category, score, timestamp FROM user_scores WHERE user_id = (SELECT id FROM users WHERE username = ?) ORDER BY timestamp DESC";
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Database connection is null.");
                return;
            }
            PreparedStatement stmt = conn.prepareStatement(sql);
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
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load scores: " + e.getMessage());
        }
    }

    @FXML
    private void handlePlayQuiz(ActionEvent event) {
        String selectedCategory = categoryCombo.getValue();
        if (selectedCategory == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a category.");
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
            stage.setTitle("Quiz");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load quiz screen: " + e.getMessage());
        }
    }

    @FXML
    private void handleViewScores(ActionEvent event) {
        loadScores();
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
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load high scores screen: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/loginUI/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load login screen: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

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

package User;

import db.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HighScoreController {

    @FXML private TableView<Score> highScoreTable;
    @FXML private TableColumn<Score, String> usernameColumn;
    @FXML private TableColumn<Score, String> categoryColumn;
    @FXML private TableColumn<Score, Integer> scoreColumn;
    @FXML private TableColumn<Score, String> timestampColumn;
    @FXML private Button backButton;

    private String loggedInUsername;
    private final ObservableList<Score> highScoreList = FXCollections.observableArrayList();

    public void setLoggedInUsername(String username) {
        this.loggedInUsername = username;
    }

    @FXML
    public void initialize() {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        timestampColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        highScoreTable.setItems(highScoreList);
        loadHighScores();
    }

    private void loadHighScores() {
        String sql = "SELECT u.username, s.category, s.score, s.timestamp " +
                "FROM user_scores s JOIN users u ON s.user_id = u.id " +
                "ORDER BY s.score DESC LIMIT 10";
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Database connection is null.");
                return;
            }
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            highScoreList.clear();
            while (rs.next()) {
                highScoreList.add(new Score(
                        rs.getString("username"),
                        rs.getString("category"),
                        rs.getInt("score"),
                        rs.getString("timestamp")
                ));
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load high scores: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/loginUI/UserDashboard.fxml"));
            Parent root = loader.load();
            UserDashboardController controller = loader.getController();
            controller.setLoggedInUsername(loggedInUsername);
            Stage stage = (Stage) backButton.getScene().getWindow();
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

    public static class Score {
        private final String username;
        private final String category;
        private final int score;
        private final String timestamp;

        public Score(String username, String category, int score, String timestamp) {
            this.username = username;
            this.category = category;
            this.score = score;
            this.timestamp = timestamp;
        }

        public String getUsername() { return username; }
        public String getCategory() { return category; }
        public int getScore() { return score; }
        public String getTimestamp() { return timestamp; }
    }
}
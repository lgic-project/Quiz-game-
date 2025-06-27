package User;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.control.*;
import java.io.IOException;


public class ScoreSummaryController {

    @FXML private Label scoreLabel;
    @FXML private Button backButton;

    private String loggedInUsername;
    private int score;
    private String category;

    public void setLoggedInUsername(String username) {
        this.loggedInUsername = username;
    }

    public void setScore(int score) {
        this.score = score;
        scoreLabel.setText("Your Score: " + score + " in " + category);
    }

    public void setCategory(String category) {
        this.category = category;
        scoreLabel.setText("Your Score: " + score + " in " + category);
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
}
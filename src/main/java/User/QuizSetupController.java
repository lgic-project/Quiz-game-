package User;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;

public class QuizSetupController {

    @FXML private ComboBox<String> categoryCombo;
    @FXML private Button startQuizButton, backButton;

    private String loggedInUsername;

    public void setLoggedInUsername(String username) {
        this.loggedInUsername = username;
    }

    @FXML
    public void initialize() {
        categoryCombo.setItems(FXCollections.observableArrayList(
                "General Knowledge", "Science", "Math", "History"
        ));
    }

    @FXML
    private void handleStartQuiz(ActionEvent event) {
        String category = categoryCombo.getValue();
        if (category == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a category.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/loginUI/quiz_screen.fxml"));
            Parent root = loader.load();
            QuizScreenController controller = loader.getController();
            controller.setLoggedInUsername(loggedInUsername);
            controller.setCategory(category);
            Stage stage = (Stage) startQuizButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Quiz");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load quiz screen: " + e.getMessage());
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
}
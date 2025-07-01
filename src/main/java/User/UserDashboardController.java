package User;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class UserDashboardController {

    @FXML private Label welcomeLabel;

    public void setWelcomeMessage(String message) {
        welcomeLabel.setText(message);
    }

    @FXML
    private void playQuiz() {
        System.out.println("Quiz started!");
    }

    @FXML
    private void viewScore() {
        System.out.println("Showing score.");
    }

    @FXML
    private void logout() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

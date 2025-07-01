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
import java.util.Optional;

public class QuizSetupController {

    @FXML private ComboBox<String> categoryCombo;
    @FXML private ComboBox<String> difficultyCombo;
    @FXML private ComboBox<Integer> questionCountCombo;
    @FXML private Label timeEstimateLabel;
    @FXML private Label statusLabel;
    @FXML private Button startQuizButton, backButton;

    private String loggedInUsername;

    public void setLoggedInUsername(String username) {
        this.loggedInUsername = username;
    }

    @FXML
    public void initialize() {
        // category options
        categoryCombo.setItems(FXCollections.observableArrayList(
                "General Knowledge", "Science", "Math", "History"
        ));
        categoryCombo.setValue("General Knowledge");

        difficultyCombo.setItems(FXCollections.observableArrayList("Easy", "Medium", "Hard"));
        difficultyCombo.setValue("Medium");

        
        questionCountCombo.setItems(FXCollections.observableArrayList(5, 10, 15));
        questionCountCombo.setValue(10);

       
        categoryCombo.setOnAction(e -> updateTimeEstimate());
        difficultyCombo.setOnAction(e -> updateTimeEstimate());
        questionCountCombo.setOnAction(e -> updateTimeEstimate());

        updateTimeEstimate(); 
        updateStatus("Select options to begin.");
    }

    private void updateTimeEstimate() {
        String difficulty = difficultyCombo.getValue();
        Integer questionCount = questionCountCombo.getValue();
        int baseTime = switch (difficulty) {
            case "Easy" -> 10;
            case "Medium" -> 15;
            case "Hard" -> 20;
            default -> 15;
        };

        int totalTime = baseTime * questionCount;
        timeEstimateLabel.setText("Estimated Time: " + totalTime + " sec");
    }

    @FXML
    private void handleStartQuiz(ActionEvent event) {
        String category = categoryCombo.getValue();
        String difficulty = difficultyCombo.getValue();
        Integer questionCount = questionCountCombo.getValue();

        if (category == null || difficulty == null || questionCount == null) {
            showAlert(Alert.AlertType.WARNING, "Missing Selection", "Please select category, difficulty, and question count.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/loginUI/quiz_screen.fxml"));
            Parent root = loader.load();

            QuizScreenController controller = loader.getController();
            controller.setLoggedInUsername(loggedInUsername);
            controller.setCategory(category);
            controller.setDifficulty(difficulty);
            controller.setQuestionCount(questionCount);

            Stage stage = (Stage) startQuizButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Quiz - " + category);

            updateStatus("Quiz started for " + category + " (" + difficulty + ", " + questionCount + " Qs)");

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load quiz screen: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Exit");
        confirm.setHeaderText("Return to Dashboard?");
        confirm.setContentText("All selections will be discarded.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/loginUI/UserDashboard.fxml"));
                Parent root = loader.load();
                UserDashboardController controller = loader.getController();
                controller.setLoggedInUsername(loggedInUsername);

                Stage stage = (Stage) backButton.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("User Dashboard");

                updateStatus("Returned to dashboard.");
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load dashboard: " + e.getMessage());
            }
        } else {
            updateStatus("Stay on quiz setup.");
        }
    }

    private void updateStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

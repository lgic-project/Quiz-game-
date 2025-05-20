import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Student;
import Controller.studentMainScreenController;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class Main extends Application {

    @FXML private TextField studentEmail;
    @FXML private PasswordField studentPassword;
    @FXML private TextField adminEmail;
    @FXML private PasswordField adminPassword;
    @FXML private TextField teacherEmail;
    @FXML private PasswordField teacherPassword;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/FXML/LogIn.fxml"));
        primaryStage.setTitle("Seeker's Quiz");
        primaryStage.setScene(new Scene(root, 850, 500));
        primaryStage.show();
    }

    private boolean authenticateUser(String filePath, String email, String password) {
        try (Scanner scanner = new Scanner(new File(filePath))) {
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(",");
                if (parts.length >= 2 &&
                    parts[0].trim().equals(email) &&
                    parts[1].trim().equals(password)) {
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace(); // Ideally use a logger
        }
        return false;
    }

    private void showWarningAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void studentLoginButton(ActionEvent event) throws IOException {
        String email = studentEmail.getText().trim();
        String password = studentPassword.getText().trim();

        if (authenticateUser("student.txt", email, password)) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXML/studentMainScreen.fxml"));
            Parent root = fxmlLoader.load();
            studentMainScreenController controller = fxmlLoader.getController();
            controller.setStudent(new Student(email, password));

            Stage stage = (Stage) studentEmail.getScene().getWindow();
            stage.setScene(new Scene(root));
        } else {
            showWarningAlert("Email or Password do not match!");
        }
    }

    @FXML
    private void adminLoginButton(ActionEvent event) throws IOException {
        String email = adminEmail.getText();
        String password = adminPassword.getText();

        if (email.equals("admin") && password.equals("admin")) {
            Parent root = FXMLLoader.load(getClass().getResource("/FXML/adminHomeScreen.fxml"));
            Stage stage = (Stage) adminEmail.getScene().getWindow();
            stage.setScene(new Scene(root));
        } else {
            showWarningAlert("Email or Password do not match!");
        }
    }

    @FXML
    private void teacherLoginButton(ActionEvent event) throws IOException {
        String email = teacherEmail.getText().trim();
        String password = teacherPassword.getText().trim();

        if (authenticateUser("teacher.txt", email, password)) {
            Parent root = FXMLLoader.load(getClass().getResource("/FXML/addQuestion.fxml"));
            Stage stage = (Stage) teacherEmail.getScene().getWindow();
            stage.setScene(new Scene(root));
        } else {
            showWarningAlert("Email or Password do not match!");
        }
    }

    @FXML
    private void SignUpStudentButton(ActionEvent event) throws IOException {
        switchScene(studentEmail, "/FXML/signUpStudentInfo.fxml");
    }

    @FXML
    private void SignUpTeacherButton(ActionEvent event) throws IOException {
        switchScene(teacherEmail, "/FXML/signUpTeacherInfo.fxml");
    }

    private void switchScene(Control control, String fxmlPath) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        Stage stage = (Stage) control.getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}



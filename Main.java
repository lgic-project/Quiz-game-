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

    private static final String STUDENT_FILE = "student.txt";
    private static final String TEACHER_FILE = "teacher.txt";

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

    /** Handles user authentication against a simple text file. */
    private boolean authenticateUser(String filePath, String email, String password) {
        if (email.isEmpty() || password.isEmpty()) return false;

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
            System.err.println("Authentication file not found: " + filePath);
        }
        return false;
    }

    /** Displays a warning alert with a given message. */
    private void showWarningAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /** Reusable method to load FXML and set the scene. */
    private void loadScene(Control control, String fxmlPath) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        Stage stage = (Stage) control.getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    /** Handles student login */
    @FXML
    private void studentLoginButton(ActionEvent event) throws IOException {
        String email = studentEmail.getText().trim();
        String password = studentPassword.getText().trim();

        if (authenticateUser(STUDENT_FILE, email, password)) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/studentMainScreen.fxml"));
            Parent root = loader.load();

            studentMainScreenController controller = loader.getController();
            controller.setStudent(new Student(email, password));

            Stage stage = (Stage) studentEmail.getScene().getWindow();
            stage.setScene(new Scene(root));
        } else {
            showWarningAlert("Invalid student email or password.");
        }
    }

    /** Handles admin login */
    @FXML
    private void adminLoginButton(ActionEvent event) throws IOException {
        String email = adminEmail.getText().trim();
        String password = adminPassword.getText().trim();

        if ("admin".equals(email) && "admin".equals(password)) {
            loadScene(adminEmail, "/FXML/adminHomeScreen.fxml");
        } else {
            showWarningAlert("Invalid admin credentials.");
        }
    }

    /** Handles teacher login */
    @FXML
    private void teacherLoginButton(ActionEvent event) throws IOException {
        String email = teacherEmail.getText().trim();
        String password = teacherPassword.getText().trim();

        if (authenticateUser(TEACHER_FILE, email, password)) {
            loadScene(teacherEmail, "/FXML/addQuestion.fxml");
        } else {
            showWarningAlert("Invalid teacher email or password.");
        }
    }

    /** Handles navigation to student signup */
    @FXML
    private void SignUpStudentButton(ActionEvent event) throws IOException {
        loadScene(studentEmail, "/FXML/signUpStudentInfo.fxml");
    }

    /** Handles navigation to teacher signup */
    @FXML
    private void SignUpTeacherButton(ActionEvent event) throws IOException {
        loadScene(teacherEmail, "/FXML/signUpTeacherInfo.fxml");
    }
}

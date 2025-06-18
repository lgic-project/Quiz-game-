package Login;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

public class loginapplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Correct: Load the FXML file from the classpath
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/loginUI/Login.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("Login");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}


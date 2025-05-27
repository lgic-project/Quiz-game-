package com.example.demo.controller;

import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AdminDashboardController {

    @FXML private TextField questionField;
    @FXML private TextField option1Field;
    @FXML private TextField option2Field;
    @FXML private TextField option3Field;
    @FXML private TextField option4Field;
    @FXML private ComboBox<String> correctOptionCombo;
    @FXML private TableView<Question> questionTable;
    @FXML private TableColumn<Question, String> questionColumn;

    @FXML private TableView<UserScore> scoreTable;
    @FXML private TableColumn<UserScore, String> userColumn;
    @FXML private TableColumn<UserScore, Integer> scoreColumn;
    @FXML private TableColumn<UserScore, String> timestampColumn;

    private ObservableList<Question> questionList = FXCollections.observableArrayList();
    private ObservableList<UserScore> scoreList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        questionColumn.setCellValueFactory(new PropertyValueFactory<>("questionText"));
        questionTable.setItems(questionList);

        userColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        timestampColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        scoreTable.setItems(scoreList);

        correctOptionCombo.setItems(FXCollections.observableArrayList("Option 1", "Option 2", "Option 3", "Option 4"));

        // Sample score
        scoreList.add(new UserScore("User1", 90, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
    }

    @FXML
    private void handleAddQuestion(ActionEvent event) {
        String question = questionField.getText();
        String opt1 = option1Field.getText();
        String opt2 = option2Field.getText();
        String opt3 = option3Field.getText();
        String opt4 = option4Field.getText();
        String correct = correctOptionCombo.getValue();

        if (!question.isEmpty() && correct != null &&
                !opt1.isEmpty() && !opt2.isEmpty() && !opt3.isEmpty() && !opt4.isEmpty()) {

            questionList.add(new Question(question));
            questionField.clear();
            option1Field.clear();
            option2Field.clear();
            option3Field.clear();
            option4Field.clear();
            correctOptionCombo.setValue(null);
        }
    }

    @FXML
    private void handleDeleteQuestion(ActionEvent event) {
        Question selected = questionTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            questionList.remove(selected);
        }
    }

    @FXML
    private void handleUpdateQuestion(ActionEvent event) {
        Question selected = questionTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            String updated = questionField.getText();
            if (!updated.isEmpty()) {
                selected.setQuestionText(updated);
                questionTable.refresh();
                questionField.clear();
            }
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/fxml/admin_login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Admin Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

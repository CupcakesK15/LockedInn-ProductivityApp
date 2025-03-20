package com.example.lockedinn;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.util.HashMap;

public class LockedInnApp extends Application {

    HashMap<String, Color> subjects = new HashMap<>();
    Popup subjectCreation;
    TextField subjectNameField;
    ColorPicker colorPicker;
    Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        Button createAssignmentButton = new Button("Create Assignment");
        createAssignmentButton.setOnAction(event -> openAssignmentCreationDialog());

        Button createSubjectLabel = new Button("Create Subject Label");
        createSubjectLabel.setOnAction(event -> subjectLabelCreation());

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(createAssignmentButton, createSubjectLabel);

        subjectCreation = new Popup();
        HBox subjectPanel = new HBox(30);
        subjectPanel.setPadding(new Insets(5));
        subjectPanel.setBackground(new Background(new BackgroundFill(Color.SPRINGGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
        Label subjectPromptLabel = new Label("Subject Name:");
        subjectNameField = new TextField();
        Label colorPromptLabel = new Label("Pick a Color:");
        colorPicker = new ColorPicker();
        Button finishSubjectButton = new Button("Finish");
        finishSubjectButton.setOnAction(event -> closeSubjectCreation());
        subjectPanel.getChildren().addAll(subjectPromptLabel, subjectNameField, colorPromptLabel, colorPicker, finishSubjectButton);
        subjectCreation.getContent().add(subjectPanel);

        Scene scene = new Scene(layout, 400, 200);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Locked Inn - Assignment Tracker");
        primaryStage.show();
    }

    private void openAssignmentCreationDialog() {
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Create Assignment");

        Label headerLabel = new Label("Enter Assignment Details");
        headerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        Label nameLabel = new Label("Assignment Name:");
        TextField nameField = new TextField();
        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);

        Label startLabel = new Label("Start Point:");
        TextField startField = new TextField();
        grid.add(startLabel, 0, 1);
        grid.add(startField, 1, 1);

        Label finishLabel = new Label("Finish Point:");
        TextField finishField = new TextField();
        grid.add(finishLabel, 0, 2);
        grid.add(finishField, 1, 2);

        Label progressLabel = new Label("Progress:");
        TextField progressField = new TextField();
        grid.add(progressLabel, 0, 3);
        grid.add(progressField, 1, 3);

        Button saveButton = new Button("Save Assignment");
        saveButton.setOnAction(event -> {
            String assignmentName = nameField.getText();
            String startPoint = startField.getText();
            String finishPoint = finishField.getText();
            String progress = progressField.getText();

            System.out.println("Assignment Created:");
            System.out.println("Name: " + assignmentName);
            System.out.println("Start: " + startPoint);
            System.out.println("Finish: " + finishPoint);
            System.out.println("Progress: " + progress);

            dialogStage.close();
        });
        grid.add(saveButton, 0, 4, 2, 1);
        GridPane.setMargin(saveButton, new Insets(10, 0, 0, 0));
        saveButton.setMaxWidth(Double.MAX_VALUE);

        VBox dialogLayout = new VBox(10);
        dialogLayout.setPadding(new Insets(20));
        dialogLayout.getChildren().addAll(headerLabel, grid);

        Scene dialogScene = new Scene(dialogLayout, 350, 300);
        dialogStage.setScene(dialogScene);
        dialogStage.initOwner(primaryStage);
        dialogStage.show();
    }

    private void subjectLabelCreation() {
        subjectCreation.show(primaryStage);
    }

    private void closeSubjectCreation() {
        String temp = subjectNameField.getText();
        subjectNameField.setText("");
        Color color = colorPicker.getValue();
        colorPicker.setValue(Color.WHITE);
        subjects.put(temp, color);
        subjectCreation.hide();
        System.out.println("Subjects updated: " + subjects);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
package com.example.lockedinn;

import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
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

        HBox buttonPanel = new HBox(15);
        buttonPanel.setStyle("-fx-padding: 10;");
        buttonPanel.getChildren().add(createAssignmentButton);
        buttonPanel.getChildren().add(createSubjectLabel);

        GridPane assignmentPanel = new GridPane();

        subjectCreation = new Popup();
        HBox subjectPanel = new HBox(30);
        subjectPanel.setStyle("-fx-padding: 5;");
        subjectPanel.setBackground(new Background(new BackgroundFill(Color.SPRINGGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
        Label subjectPromptLabel = new Label("Subject Name: ");
        subjectNameField = new TextField();
        Label colorPromptLabel = new Label("Pick a Color: ");
        colorPicker = new ColorPicker();
        Button finishSubjectButton = new Button("Finish");
        finishSubjectButton.setOnAction(event -> closeSubjectCreation());
        subjectPanel.getChildren().addAll(subjectPromptLabel, subjectNameField);
        subjectPanel.getChildren().addAll(colorPromptLabel, colorPicker);
        subjectPanel.getChildren().add(finishSubjectButton);
        subjectCreation.getContent().add(subjectPanel);

        Scene scene = new Scene(buttonPanel, 400, 200);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Locked Inn - Assignment Tracker");
        primaryStage.show();
    }

    private void openAssignmentCreationDialog() {
        // Implementation for dialog...
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
        System.out.println(subjects);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
//module org.openjfx.triumvirateeoy {
//    requires javafx.controls;
//    requires javafx.fxml;
//    exports com.example.lockedinn;
//}

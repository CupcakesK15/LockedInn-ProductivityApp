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
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Consumer;

public class LockedInnApp extends Application {

    HashMap<String, Color> subjects = new HashMap<>();
    HashSet<Color> colors = new HashSet<>();
    Stage primaryStage;
    VBox assignmentPanel;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(@NotNull Stage primaryStage) {
        this.primaryStage = primaryStage;

        Button createAssignmentButton = new Button("Create Assignment");
        createAssignmentButton.setOnAction(event -> openAssignmentCreationDialog());
        Button createSubjectLabel = new Button("Create Subject Label");
        createSubjectLabel.setOnAction(event -> subjectLabelCreation());

        HBox buttonPanel = new HBox(15);
        buttonPanel.setPrefHeight(50);
        buttonPanel.setPadding(new Insets(10));
        buttonPanel.getChildren().addAll(createAssignmentButton, createSubjectLabel);

        Text music = new Text("Music");
        HBox musicPanel = new HBox(20);
        musicPanel.setPrefHeight(75);
        musicPanel.setPadding(new Insets(10));
        musicPanel.setStyle("-fx-background-color: #C2914F;");
        musicPanel.getChildren().add(music);

        assignmentPanel = new VBox(15);
        assignmentPanel.setPrefHeight(675);
        assignmentPanel.setPrefWidth(773);
        assignmentPanel.setStyle("-fx-background-color: #452103;");
        assignmentPanel.getChildren().add(new Text("Assignment Panel"));

        Text subjectHeader = new Text("Subject Panel");
        HBox subjectPanel = new HBox(15);
        subjectPanel.setPrefHeight(675);
        subjectPanel.setPrefWidth(773);
        subjectPanel.setStyle("-fx-background-color: #284520ff;");
        subjectPanel.getChildren().add(subjectHeader);

        GridPane root = new GridPane();
        root.setStyle("-fx-background-color: BLACK;");
        root.add(buttonPanel, 0, 0);
        root.add(assignmentPanel, 0, 1);
        root.add(subjectPanel, 1, 1);
        root.add(musicPanel, 0, 2, 2, 1);

        int screenWidth = (int) Screen.getPrimary().getBounds().getWidth();
        int screenHeight = (int) Screen.getPrimary().getBounds().getHeight() - 70;
        Scene scene = new Scene(root, screenWidth, screenHeight);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Locked Inn - Assignment Tracker");
        primaryStage.show();
    }

    private GridPane createAssignmentDetailsGrid(TextField nameField, TextField startField, TextField finishField, TextField progressField) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);
        grid.add(new Label("Assignment Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Start Point:"), 0, 1);
        grid.add(startField, 1, 1);
        grid.add(new Label("Finish Point:"), 0, 2);
        grid.add(finishField, 1, 2);
        grid.add(new Label("Progress:"), 0, 3);
        grid.add(progressField, 1, 3);
        return grid;
    }

    private void showAssignmentDialog(String title, String headerText,
                                      String initialName, String initialStart,
                                      String initialFinish, String initialProgress,
                                      Consumer<String> onSave) {
        Stage dialogStage = new Stage();
        dialogStage.setTitle(title);

        Label headerLabel = new Label(headerText);
        headerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TextField nameField = new TextField(initialName);
        TextField startField = new TextField(initialStart);
        TextField finishField = new TextField(initialFinish);
        TextField progressField = new TextField(initialProgress);

        GridPane grid = createAssignmentDetailsGrid(nameField, startField, finishField, progressField);

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            String newName = nameField.getText();
            String newStart = startField.getText();
            String newFinish = finishField.getText();
            String newProgress = progressField.getText();
            String updatedText = newName + " (Start: " + newStart + ", Finish: " + newFinish + ", Progress: " + newProgress + ")";
            onSave.accept(updatedText);
            System.out.println("Assignment Saved: " + updatedText);
            dialogStage.close();
        });
        grid.add(saveButton, 0, 4, 2, 1);
        GridPane.setMargin(saveButton, new Insets(10, 0, 0, 0));
        saveButton.setMaxWidth(Double.MAX_VALUE);

        VBox dialogLayout = new VBox(10, headerLabel, grid);
        dialogLayout.setPadding(new Insets(20));

        Scene dialogScene = new Scene(dialogLayout, 350, 300);
        dialogStage.setScene(dialogScene);
        dialogStage.initOwner(primaryStage);
        dialogStage.show();
    }

    private void openAssignmentCreationDialog() {
        showAssignmentDialog("Create Assignment", "Enter Assignment Details", "", "", "", "",
                updatedText -> {
                    Label assignmentLabel = new Label(updatedText);
                    assignmentLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
                    Button editButton = new Button("Edit");
                    editButton.setOnAction(ev -> openAssignmentEditingDialog(assignmentLabel));
                    HBox assignmentRow = new HBox(10, assignmentLabel, editButton);
                    assignmentRow.setAlignment(Pos.CENTER_LEFT);
                    assignmentPanel.getChildren().add(assignmentRow);
                });
    }

    private void openAssignmentEditingDialog(Label assignmentLabel) {
        String currentText = assignmentLabel.getText();
        String name = currentText.split(" \\(")[0];
        String details = currentText.substring(currentText.indexOf('(') + 1, currentText.indexOf(')'));
        String[] parts = details.split(", ");
        String start = parts[0].replace("Start: ", "");
        String finish = parts[1].replace("Finish: ", "");
        String progress = parts[2].replace("Progress: ", "");

        showAssignmentDialog("Edit Assignment", "Edit Assignment Details", name, start, finish, progress,
                assignmentLabel::setText);
    }

    private void subjectLabelCreation() {
        Popup subjectCreation = new Popup();
        HBox subjectPanel = new HBox(30);
        subjectPanel.setPadding(new Insets(5));
        subjectPanel.setBackground(new Background(new BackgroundFill(Color.SPRINGGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
        Label subjectPromptLabel = new Label("Subject Name:");
        TextField subjectNameField = new TextField();
        Label colorPromptLabel = new Label("Pick a Color:");
        ColorPicker colorPicker = new ColorPicker();
        Button finishSubjectButton = new Button("Finish");
        finishSubjectButton.setOnAction(event -> {
            String temp = subjectNameField.getText();
            subjectNameField.setText("");
            Color color = colorPicker.getValue();
            colorPicker.setValue(Color.WHITE);
            subjects.put(temp, color);
            colors.add(color);
            System.out.println("Subjects updated: " + subjects);
            subjectCreation.hide();
        });
        subjectPanel.getChildren().addAll(subjectPromptLabel, subjectNameField, colorPromptLabel, colorPicker, finishSubjectButton);
        subjectCreation.getContent().add(subjectPanel);
        subjectCreation.show(primaryStage);
    }
}
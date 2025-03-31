package com.example.lockedinn;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
    ScrollPane assignmentPanel;
    Accordion assignments;

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

        assignments = new Accordion();
        assignmentPanel = new ScrollPane(assignments);
        assignmentPanel.setPrefHeight(200);
        assignmentPanel.setPrefWidth(950);
        assignmentPanel.setStyle("-fx-background: #452103;");
        assignmentPanel.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        assignmentPanel.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        VBox temporary = new VBox();
        temporary.setPrefHeight(475);
        temporary.setPrefWidth(950);
        temporary.setStyle("-fx-background-color: #C1014E;");

        Text subjectHeader = new Text("Subject Panel");
        HBox subjectPanel = new HBox(15);
        subjectPanel.setPrefHeight(675);
        subjectPanel.setPrefWidth(596);
        subjectPanel.setStyle("-fx-background-color: #284520ff;");
        subjectPanel.getChildren().add(subjectHeader);

        GridPane root = new GridPane();
        root.setStyle("-fx-background-color: BLACK;");
        root.add(buttonPanel, 0, 0);
        root.add(assignmentPanel, 0, 1);
        root.add(temporary, 0, 2);
        root.add(subjectPanel, 1, 1, 1, 2);
        root.add(musicPanel, 0, 3, 2, 1);

        int screenWidth = (int) Screen.getPrimary().getBounds().getWidth();
        int screenHeight = (int) Screen.getPrimary().getBounds().getHeight() - 70;
        Scene scene = new Scene(root, screenWidth, screenHeight);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Locked Inn - Assignment Tracker");
        primaryStage.show();
    }

    private GridPane createAssignmentDetailsGrid(TextField nameField, TextField startField, TextField finishField, TextField progressField, ComboBox<String> localSubjectSelector) {
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
        grid.add(new Label("Subject:"), 0, 4);
        grid.add(localSubjectSelector, 1, 4);
        return grid;
    }

    private void showAssignmentDialog(String title, String headerText,
                                      String initialName, String initialStart,
                                      String initialFinish, String initialProgress,
                                      String subject,
                                      Consumer<Assignment> onSave) {
        Stage dialogStage = new Stage();
        dialogStage.setTitle(title);

        Label headerLabel = new Label(headerText);
        headerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TextField nameField = new TextField(initialName);
        TextField startField = new TextField(initialStart);
        TextField finishField = new TextField(initialFinish);
        TextField progressField = new TextField(initialProgress);

        ComboBox<String> localSubjectSelector = new ComboBox<>();
        localSubjectSelector.getItems().addAll(subjects.keySet());
        localSubjectSelector.setVisibleRowCount(5);
        if (subject != null && !subject.isEmpty()) {
            localSubjectSelector.setValue(subject);
        }

        GridPane grid = createAssignmentDetailsGrid(nameField, startField, finishField, progressField, localSubjectSelector);

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            String newName = nameField.getText();
            String newStart = startField.getText();
            String newFinish = finishField.getText();
            String newProgress = progressField.getText();
            String newSubject = localSubjectSelector.getValue() != null ? localSubjectSelector.getValue() : "";
            Assignment updatedAssignment = new Assignment(newName, newStart, newFinish, newProgress, newSubject);
            onSave.accept(updatedAssignment);
            System.out.println("Assignment Saved: " + updatedAssignment);
            dialogStage.close();
        });
        grid.add(saveButton, 0, 5, 2, 1);
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
        showAssignmentDialog("Create Assignment", "Enter Assignment Details", "", "", "", "", "",
                updatedAssignment -> {
                    TitledPane pane = new TitledPane();
                    pane.setText(updatedAssignment.name);
                    Label detailsLabel = new Label(updatedAssignment.toString());
                    Button editButton = new Button("Edit");
                    editButton.setOnAction(ev -> openAssignmentEditingDialog(pane, detailsLabel));
                    HBox assignmentRow = new HBox(10, detailsLabel, editButton);
                    assignmentRow.setAlignment(Pos.CENTER_LEFT);
                    pane.setContent(assignmentRow);

                    Color subjectColor = subjects.get(updatedAssignment.subject);
                    if (subjectColor != null) {
                        assignmentRow.setBackground(new Background(new BackgroundFill(subjectColor, CornerRadii.EMPTY, Insets.EMPTY)));
                    }
                    pane.setUserData(updatedAssignment);
                    assignments.getPanes().add(pane);
                });
    }

    private void openAssignmentEditingDialog(TitledPane pane, Label detailsLabel) {
        Assignment current = (Assignment) pane.getUserData();
        showAssignmentDialog("Edit Assignment", "Edit Assignment Details",
                current.name, current.start, current.finish, current.progress, current.subject,
                updatedAssignment -> {
                    pane.setUserData(updatedAssignment);
                    pane.setText(updatedAssignment.name);
                    detailsLabel.setText(updatedAssignment.toString());
                    Color subjectColor = subjects.get(updatedAssignment.subject);
                    if (subjectColor != null) {
                        ((HBox) pane.getContent()).setBackground(new Background(new BackgroundFill(subjectColor, CornerRadii.EMPTY, Insets.EMPTY)));
                    }
                });
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
            String subjectName = subjectNameField.getText();
            subjectNameField.setText("");
            Color color = colorPicker.getValue();
            colorPicker.setValue(Color.WHITE);

            subjects.put(subjectName, color);
            colors.add(color);
            System.out.println("Subjects updated: " + subjects);
            subjectCreation.hide();
        });

        subjectPanel.getChildren().addAll(subjectPromptLabel, subjectNameField, colorPromptLabel, colorPicker, finishSubjectButton);
        subjectCreation.getContent().add(subjectPanel);
        subjectCreation.show(primaryStage);
    }

    private static class Assignment {
        String name;
        String start;
        String finish;
        String progress;
        String subject;

        Assignment(String name, String start, String finish, String progress, String subject) {
            this.name = name;
            this.start = start;
            this.finish = finish;
            this.progress = progress;
            this.subject = subject;
        }

        @Override
        public String toString() {
            return "(Start: " + start + ", Finish: " + finish + ", Progress: " + progress + ")";
        }
    }
}

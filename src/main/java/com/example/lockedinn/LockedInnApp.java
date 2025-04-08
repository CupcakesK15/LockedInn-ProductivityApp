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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Consumer;

public class LockedInnApp extends Application {

    HashMap<String, Color> subjects = new HashMap<>();
    HashSet<Color> colors = new HashSet<>();

    Stage primaryStage;
    ScrollPane assignmentPanel;
    ScrollPane subjectPanel;
    Accordion assignments;
    VBox subjectBox;
    Accordion subjectWidgets;

    int buttonPanelSpacing = 15;
    int buttonPanelHeight = 50;
    int buttonPanelPadding = 10;
    int musicPanelSpacing = 20;
    int musicPanelHeight = 75;
    int musicPanelPadding = 10;
    int assignmentPanelHeight = 675;
    int assignmentPanelWidth = 950;
    int subjectPanelHeight = 675;
    int subjectPanelWidth = 596;
    int sceneFrameHeight = 70;

    LocalDate inputDate = LocalDate.now();
    DatePicker dateSelector = new DatePicker();

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

        HBox buttonPanel = new HBox(buttonPanelSpacing);
        buttonPanel.setPrefHeight(buttonPanelHeight);
        buttonPanel.setPadding(new Insets(buttonPanelPadding));
        buttonPanel.getChildren().addAll(createAssignmentButton, createSubjectLabel);

        Text music = new Text("Music");
        HBox musicPanel = new HBox(musicPanelSpacing);
        musicPanel.setPrefHeight(musicPanelHeight);
        musicPanel.setPadding(new Insets(musicPanelPadding));
        musicPanel.setStyle("-fx-background-color: #C2914F;");
        musicPanel.getChildren().add(music);

        assignments = new Accordion();
        assignments.setMinWidth(assignmentPanelWidth);
        assignmentPanel = new ScrollPane(assignments);
        assignmentPanel.setPrefHeight(assignmentPanelHeight);
        assignmentPanel.setPrefWidth(assignmentPanelWidth);
        assignmentPanel.setStyle("-fx-background: #452103;");
        assignmentPanel.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        assignmentPanel.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        subjectWidgets = new Accordion();
        subjectWidgets.setMinWidth(subjectPanelWidth);
        subjectPanel = new ScrollPane(subjectWidgets);
        subjectPanel.setPrefHeight(subjectPanelHeight);
        subjectPanel.setPrefWidth(subjectPanelWidth);
        subjectPanel.setStyle("-fx-background-color:#452103;");

        //not 100% sure what the heck this is :) - Sri K.
//        VBox temporary = new VBox();
//        temporary.setPrefHeight(475);
//        temporary.setPrefWidth(950);
//        temporary.setStyle("-fx-background-color: #C1014E;");

        //Subject panel on the right
//        Text subjectHeader = new Text("Subject Panel");
        subjectBox = new VBox(15);
        subjectPanel = new ScrollPane(subjectBox);
        subjectPanel.setPrefHeight(675);
        subjectPanel.setPrefWidth(596);
        subjectPanel.setStyle("-fx-background: #284520ff;");
        subjectPanel.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        subjectPanel.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        GridPane root = new GridPane();
        root.setStyle("-fx-background-color: BLACK;");
        root.add(buttonPanel, 0, 0);
        root.add(assignmentPanel, 0, 1);
        root.add(subjectPanel, 1, 1, 1, 2);
        root.add(musicPanel, 0, 3, 2, 1);

        int screenWidth = (int) Screen.getPrimary().getBounds().getWidth();
        int screenHeight = (int) Screen.getPrimary().getBounds().getHeight() - sceneFrameHeight;

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
        grid.add(new Label("Progress (%):"), 0, 3);
        grid.add(progressField, 1, 3);
        grid.add(new Label("Day Due:"), 0, 4);
        grid.add(dateSelector, 1, 4);
        grid.add(new Label("Subject:"), 0, 5);
        grid.add(localSubjectSelector, 1, 5);

        ColumnConstraints col0 = new ColumnConstraints();
        col0.setPercentWidth(40);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(60);
        grid.getColumnConstraints().addAll(col0, col1);

        return grid;
    }

    private void showAssignmentDialog(String title, String headerText, String initialName, String initialStart, LocalDate ignoredDate, String initialFinish, String initialProgress, String subject, Consumer<Assignment> onSave) {
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
            String progressText = progressField.getText();
            double progressValue;
            try {
                progressValue = Math.min(1.0, Math.max(0.0, Double.parseDouble(progressText) / 100.0));
            } catch (NumberFormatException ex) {
                progressValue = 0.0;
            }
            String newSubject = localSubjectSelector.getValue() != null ? localSubjectSelector.getValue() : "";
            Assignment updatedAssignment = new Assignment(newName, newStart, newFinish, progressValue, newSubject);
            onSave.accept(updatedAssignment);
            System.out.println("Assignment Saved: " + updatedAssignment);
            dialogStage.close();
        });
        grid.add(saveButton, 0, 6, 2, 1);
        GridPane.setMargin(saveButton, new Insets(10, 0, 0, 0));
        saveButton.setMaxWidth(Double.MAX_VALUE);
        VBox dialogLayout = new VBox(10, headerLabel, grid);
        dialogLayout.setPadding(new Insets(20));
        Scene dialogScene = new Scene(dialogLayout, 350, 350);
        dialogStage.setScene(dialogScene);
        dialogStage.initOwner(primaryStage);
        dialogStage.show();
    }

    private void openAssignmentCreationDialog() {
        showAssignmentDialog("Create Assignment", "Enter Assignment Details", "", "", inputDate, "", "", "", updatedAssignment -> {
            TitledPane pane = new TitledPane();
            pane.setText(updatedAssignment.name);
            Label detailsLabel = new Label(updatedAssignment.toString());
            Button editButton = new Button("Edit");
            editButton.setOnAction(ev -> openAssignmentEditingDialog(pane, detailsLabel));
            CheckBox completeCheckBox = new CheckBox("Complete");
            completeCheckBox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                Assignment assignment = (Assignment) pane.getUserData();
                assignment.completed = isNowSelected;
                if (isNowSelected) {
                    detailsLabel.setStyle("-fx-text-fill: gray; -fx-strikethrough: true;");
                } else {
                    detailsLabel.setStyle("");
                }
            });
            ProgressBar progressBar = new ProgressBar(updatedAssignment.progress);
            progressBar.setPrefWidth(150);
            HBox assignmentRow = new HBox(10, completeCheckBox, detailsLabel, progressBar, editButton);
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
        showAssignmentDialog("Edit Assignment", "Edit Assignment Details", current.name, current.start, inputDate, current.finish, String.valueOf((int) (current.progress * 100)), current.subject, updatedAssignment -> {
            updatedAssignment.completed = current.completed;
            pane.setUserData(updatedAssignment);
            pane.setText(updatedAssignment.name);
            detailsLabel.setText(updatedAssignment.toString());
            Color subjectColor = subjects.get(updatedAssignment.subject);
            if (subjectColor != null) {
                ((HBox) pane.getContent()).setBackground(new Background(new BackgroundFill(subjectColor, CornerRadii.EMPTY, Insets.EMPTY)));
            }
            HBox row = (HBox) pane.getContent();
            CheckBox completeCheckBox = (CheckBox) row.getChildren().get(0);
            ProgressBar progressBar = (ProgressBar) row.getChildren().get(2);
            progressBar.setProgress(updatedAssignment.progress);
            completeCheckBox.setSelected(updatedAssignment.completed);
            if (updatedAssignment.completed) {
                detailsLabel.setStyle("-fx-text-fill: gray; -fx-strikethrough: true;");
            } else {
                detailsLabel.setStyle("");
            }
        });
    }

    private void subjectLabelCreation() {
        Popup subjectCreation = new Popup();

        HBox subjectCreationPanel = new HBox(30);
        subjectCreationPanel.setPadding(new Insets(5));
        subjectCreationPanel.setBackground(new Background(new BackgroundFill(Color.SPRINGGREEN, CornerRadii.EMPTY, Insets.EMPTY)));

        Label subjectPromptLabel = new Label("Subject Name:");
        TextField subjectNameField = new TextField();
        Label colorPromptLabel = new Label("Pick a Color:");
        ColorPicker colorPicker = new ColorPicker();
        Button finishSubjectButton = getButton(subjectNameField, colorPicker, subjectCreation);

        subjectCreationPanel.getChildren().addAll(subjectPromptLabel, subjectNameField, colorPromptLabel, colorPicker, finishSubjectButton);
        subjectCreation.getContent().add(subjectCreationPanel);
        subjectCreation.show(primaryStage);
    }

    private @NotNull Button getButton(TextField subjectNameField, ColorPicker colorPicker, Popup subjectCreation) {
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

            HBox subject = new HBox(15);
            Label subjectNameLabel = new Label(subjectName);
            Button editButton = new Button("Edit");

            editButton.setOnAction(e -> {

            });
            subject.getChildren().add(subjectNameLabel);
            subject.getChildren().add(editButton);
            subject.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
            subjectBox.getChildren().add(subject);
        });
        return finishSubjectButton;
    }

    @Override
    public void stop() {
        try {
            File subjectFile = new File("SubjectsFile.txt");
            BufferedWriter sfw = new BufferedWriter(new FileWriter(subjectFile));
            for (Map.Entry<String, Color> entry : subjects.entrySet()) {
                sfw.write(entry.getKey() + " " + entry.getValue().getRed() + "," + entry.getValue().getGreen() + "," + entry.getValue().getBlue());
                sfw.newLine();
            }
            sfw.close();
            File assignmentFile = new File("AssignmentsFile.txt");
            BufferedWriter afw = new BufferedWriter(new FileWriter(assignmentFile));
            afw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class Assignment {
        String name;
        String start;
        String finish;
        double progress;
        String subject;
        boolean completed;

        Assignment(String name, String start, String finish, double progress, String subject) {
            this.name = name;
            this.start = start;
            this.finish = finish;
            this.progress = progress;
            this.subject = subject;
            this.completed = false;
        }

        @Override
        public String toString() {
            return "(Start: " + start + ", Finish: " + finish + ", Progress: " + progress + ")";
        }
    }
}
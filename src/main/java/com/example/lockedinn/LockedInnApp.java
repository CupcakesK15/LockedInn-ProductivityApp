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

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;

public class LockedInnApp extends Application {

    HashMap<String, Color> subjects = new HashMap<>();
    HashSet<Color> colors = new HashSet<>();

    Stage primaryStage;
    ScrollPane assignmentPanel;
    ScrollPane subjectPanel;
    Accordion assignments;
    VBox subjectBox;
//    Accordion subjectWidgets;

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

//        subjectWidgets = new Accordion();
//        subjectWidgets.setMinWidth(subjectPanelWidth);
//        subjectPanel = new ScrollPane(subjectWidgets);
//        subjectPanel.setPrefHeight(subjectPanelHeight);
//        subjectPanel.setPrefWidth(subjectPanelWidth);
//        subjectPanel.setStyle("-fx-background-color:#452103;");

        subjectBox = new VBox(15);
        subjectPanel = new ScrollPane(subjectBox);
        subjectPanel.setPrefHeight(675);
        subjectPanel.setPrefWidth(596);
        subjectPanel.setStyle("-fx-background: #284520ff;");
        subjectPanel.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        subjectPanel.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        //adding subjects and assignments from the file
        try {
            Scanner scan = new Scanner(new File("SubjectsFile.txt"));
            while (scan.hasNextLine()) {
                String[] parts = scan.nextLine().split(" ");
                String[] rgb = parts[1].split(",");
                Color color = Color.color(Double.parseDouble(rgb[0]), Double.parseDouble(rgb[1]), Double.parseDouble(rgb[2]));
                subjects.put(parts[0], color);

                HBox subject = new HBox(15);
                Label subjectNameLabel = new Label(parts[0]);
                Button editButton = new Button("Edit");
                editButton.setOnAction(e -> {
                });
                subject.getChildren().add(subjectNameLabel);
                subject.getChildren().add(editButton);
                subject.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
                subjectBox.getChildren().add(subject);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Couldn't pull from subject file, file was not found");
        }

        try {
            Scanner scan = new Scanner(new File("AssignmentsFile.txt"));
            while (scan.hasNextLine()) {
                String[] parts = scan.nextLine().split("=");

                if (parts[0].equals("true")) {
                    TitledPane pane = new TitledPane();
                    pane.setText(parts[2]);
                    Label detailsLabel = new Label(parts[3]);
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
                    ProgressBar progressBar = new ProgressBar(Double.parseDouble(parts[1]));
                    progressBar.setPrefWidth(150);
                    HBox assignmentRow = new HBox(10, completeCheckBox, detailsLabel, progressBar, editButton);
                    assignmentRow.setAlignment(Pos.CENTER_LEFT);
                    pane.setContent(assignmentRow);
                    Color subjectColor = subjects.get(parts[4]);
                    if (subjectColor != null) {
                        assignmentRow.setBackground(new Background(new BackgroundFill(subjectColor, CornerRadii.EMPTY, Insets.EMPTY)));
                    }
                    Assignment assignment = new Assignment(parts[2], Double.parseDouble(parts[1]), parts[4], parts[3]);
                    pane.setUserData(assignment);
                    assignments.getPanes().add(pane);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Couldn't pull from assignment file, file was not found");
        }

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
        grid.add(new Label("Progress (#):"), 0, 3);
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

    private void showAssignmentDialog(String title, String headerText, String initialName, String initialStart, String initialFinish, String initialProgress, String subject, Consumer<Assignment> onSave) {
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
            int progress = Integer.parseInt(progressField.getText());
            int total = Integer.parseInt(finishField.getText());
            double percent = ((double) progress / total);
            LocalDate inputDate = dateSelector.getValue();
            List<AssignmentPane> list = new ArrayList<AssignmentPane>();
            assignments.getPanes().removeAll();
            for (TitledPane x : assignments.getPanes()) {
                list.add((AssignmentPane) x);
            }
            //LocalDate dueDate = dueDatePicker.getValue();
            String newSubject = localSubjectSelector.getValue() != null ? localSubjectSelector.getValue() : "";
            Assignment updatedAssignment = new Assignment(newName + " " + inputDate.toString(), newStart, newFinish, percent, newSubject, inputDate);
            onSave.accept(updatedAssignment);
            System.out.println("Assignment Saved: " + updatedAssignment);
            dialogStage.close();
        });
        grid.add(saveButton, 0, 7, 2, 1);
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
        showAssignmentDialog("Create Assignment", "Enter Assignment Details", "", "", "", "", "", updatedAssignment -> {
            AssignmentPane pane = new AssignmentPane(updatedAssignment);
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
        showAssignmentDialog("Edit Assignment", "Edit Assignment Details", current.name, current.start, current.finish, String.valueOf((int) (current.progress * 100)), current.subject, updatedAssignment -> {
            updatedAssignment.completed = (updatedAssignment.progress == 1.0);
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
            for (TitledPane pane : assignments.getPanes()) {
                Assignment temp = (Assignment) pane.getUserData();
                afw.write(temp.compileInfo());
                afw.newLine();
            }
            afw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class Assignment implements Comparable<Assignment> {
        String name;
        String start;
        String finish;
        double progress;
        String subject;
        LocalDate dueDate;
        boolean completed;
        LocalDate inputDate;

        Assignment(String name, String start, String finish, double progress, String subject, LocalDate dueDate) {
            this.name = name;
            this.start = start;
            this.finish = finish;
            this.progress = progress;
            this.subject = subject;
            this.dueDate = dueDate;
            this.completed = false;
        }

        Assignment(String name, double progress, String subject, String data) {
            this.name = name;
            this.progress = progress;
            this.subject = subject;
            this.start = data.split(",")[0].split(" ")[1];
            this.finish = data.split(",")[1].split(" ")[1];
            this.dueDate = LocalDate.parse(data.split(",")[3].split(" ")[1]);
        }

        @Override
        public String toString() {
            int progressPercent = (int) (progress * 100);
            return "(Start: " + start + ", Finish: " + finish + ", Progress: " + progressPercent + "%, Due: " + dueDate + ")";
        }

        public String compileInfo() {
            int progressPercent = (int) (progress * 100);
//            return name + "-" + start + "-" + finish + "-" + progressPercent + "-" + subject + "-" + dueDate + "-" + completed;
            return completed + "=" + progressPercent + "=" + name + "=" + this + "=" + subject;
        }

        @Override
        public int compareTo(Assignment o) {
            return this.inputDate.compareTo(o.inputDate);
        }
    }

    private static class AssignmentPane extends TitledPane implements Comparable<AssignmentPane> {
        private final Assignment assignment;

        public AssignmentPane(Assignment assignment) {
            super();
            this.setText(assignment.name);
            this.assignment = assignment;
        }

        @Override
        public int compareTo(AssignmentPane other) {
            if (other == null) return -1;
            return this.assignment.compareTo(other.assignment);
        }

    }
}
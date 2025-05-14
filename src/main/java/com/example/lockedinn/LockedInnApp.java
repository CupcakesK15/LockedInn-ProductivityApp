package com.example.lockedinn;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;

public class LockedInnApp extends Application {

    TreeMap<String, Color> subjects = new TreeMap<>();
    HashSet<Color> colors = new HashSet<>();

    Stage primaryStage;
    AssignmentPanel assignmentPanel;
    ScrollPane subjectPanel;
    Accordion assignments;
    VBox subjectBox;

    int buttonPanelSpacing = 15;
    int buttonPanelHeight = 50;
    int buttonPanelPadding = 10;
    int assignmentPanelHeight = 750;
    int assignmentPanelWidth = 950;
    int subjectPanelHeight = 750;
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
        Button deleteSubjectLabel = new Button("Delete Subject");
        deleteSubjectLabel.setOnAction(event -> deleteSubject());

        HBox buttonPanel = new HBox(buttonPanelSpacing);
        buttonPanel.setPrefHeight(buttonPanelHeight);
        buttonPanel.setPadding(new Insets(buttonPanelPadding));
        buttonPanel.getChildren().addAll(createAssignmentButton, createSubjectLabel, deleteSubjectLabel);

        Class<? extends LockedInnApp> testClass = getClass();
        URL url = testClass.getClassLoader().getResource("gridPaneStyle.css");
        String css = null;
        if (url != null) {
            css = url.toExternalForm();
        }

        assignments = new Accordion();
        assignments.setMinWidth(assignmentPanelWidth);
        assignmentPanel = new AssignmentPanel();
        assignmentPanel.setContent(assignments);
        assignmentPanel.setPrefHeight(assignmentPanelHeight);
        assignmentPanel.setPrefWidth(assignmentPanelWidth);
        assignmentPanel.getStylesheets().addAll(css);
        assignmentPanel.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        assignmentPanel.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        double vBoxSpacing = 15;
        subjectBox = new VBox(vBoxSpacing);
        subjectBox.getStylesheets().addAll(css);
        subjectPanel = new ScrollPane(subjectBox);
        subjectPanel.setPrefHeight(subjectPanelHeight);
        subjectPanel.setPrefWidth(subjectPanelWidth);
        subjectPanel.getStylesheets().addAll(css);
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

                double subjectBoxSpacing = 50;
                HBox subject = new HBox(subjectBoxSpacing);
                subject.setMinWidth(subjectPanelWidth);
                Label subjectNameLabel = new Label(parts[0]);
                if (isDark(color)) {
                    subjectNameLabel.setTextFill(Color.WHITE);
                } else {
                    subjectNameLabel.setTextFill(Color.BLACK);
                }
                subject.getChildren().add(subjectNameLabel);
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

                if (!parts[0].equals("true")) {
                    Assignment assignment = new Assignment(parts[1], parts[2], parts[3], Double.parseDouble(parts[4]), parts[5], LocalDate.parse(parts[6]));
                    AssignmentPane pane = new AssignmentPane(assignment);
                    Label detailsLabel = new Label(assignment.toString());
                    Button editButton = new Button("Edit");
                    editButton.setOnAction(ev -> openAssignmentEditingDialog(pane, detailsLabel));
                    CheckBox completeCheckBox = new CheckBox("Complete");
                    completeCheckBox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                        Assignment assignment2 = (Assignment) pane.getUserData();
                        assignment2.completed = isNowSelected;
                        if (isNowSelected) {
                            detailsLabel.setStyle("-fx-text-fill: gray; -fx-strikethrough: true;");
                        } else {
                            detailsLabel.setStyle("");
                        }
                    });
                    double progressBarWidth = 150;
                    double assignmentRowSpacing = 10;
                    ProgressBar progressBar = new ProgressBar(assignment.progress);
                    progressBar.setPrefWidth(progressBarWidth);
                    HBox assignmentRow = new HBox(assignmentRowSpacing, completeCheckBox, detailsLabel, progressBar, editButton);
                    assignmentRow.setAlignment(Pos.CENTER_LEFT);
                    pane.setContent(assignmentRow);
                    Color subjectColor = subjects.get(assignment.subject);
                    if (subjectColor != null) {
                        assignmentRow.setBackground(new Background(new BackgroundFill(subjectColor, CornerRadii.EMPTY, Insets.EMPTY)));
                    }
                    pane.setUserData(assignment);
                    pane.setText(assignment.name + " - Due Date: " + assignment.dueDate);
                    assignments.getPanes().add(pane);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Couldn't pull from assignment file, file was not found");
        }
        GridPane root = new GridPane();
        root.setStyle("-fx-background-image: url('https://images3.alphacoders.com/132/thumb-1920-1329171.png')");
        root.add(buttonPanel, 0, 0);
        root.add(assignmentPanel, 0, 1);
        root.add(subjectPanel, 1, 1, 1, 2);

        int screenWidth = (int) Screen.getPrimary().getBounds().getWidth();
        int screenHeight = (int) Screen.getPrimary().getBounds().getHeight() - sceneFrameHeight;

        Scene scene = new Scene(root, screenWidth, screenHeight);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Locked Inn - Assignment Tracker");
        primaryStage.show();
    }

    private GridPane createAssignmentDetailsGrid(TextField nameField, TextField startField, TextField finishField, TextField progressField, ComboBox<String> localSubjectSelector) {
        double gridInsets = 20;
        double gridHGap = 10;
        double gridVGap = 10;
        double col0percentWidth = 40;
        double col1percentWidth = 60;
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(gridInsets));
        grid.setHgap(gridHGap);
        grid.setVgap(gridVGap);
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
        col0.setPercentWidth(col0percentWidth);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(col1percentWidth);
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
            String newSubject = localSubjectSelector.getValue() != null ? localSubjectSelector.getValue() : "";
            Assignment updatedAssignment = new Assignment(newName, newStart, newFinish, percent, newSubject, inputDate);
            onSave.accept(updatedAssignment);
            System.out.println("Assignment Saved: " + updatedAssignment);
            List<AssignmentPane> list = new ArrayList<>();
            for (TitledPane x : assignments.getPanes()) {
                list.add((AssignmentPane) x);
            }
            Collections.sort(list);
            assignments.getPanes().removeAll();
            assignments.getPanes().setAll(list);
            dialogStage.close();
        });
        double dialogLayoutSpacing = 10;
        double dialogLayoutPadding = 20;
        double dialogSceneWidth = 350;
        double dialogSceneHeight = 350;
        grid.add(saveButton, 0, 7, 2, 1);
        GridPane.setMargin(saveButton, new Insets(10, 0, 0, 0));
        saveButton.setMaxWidth(Double.MAX_VALUE);
        VBox dialogLayout = new VBox(dialogLayoutSpacing, headerLabel, grid);
        dialogLayout.setPadding(new Insets(dialogLayoutPadding));
        Scene dialogScene = new Scene(dialogLayout, dialogSceneWidth, dialogSceneHeight);
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
            double progressBarWidth = 150;
            double assignmentRowSpacing = 10;
            ProgressBar progressBar = new ProgressBar(updatedAssignment.progress);
            progressBar.setPrefWidth(progressBarWidth);
            HBox assignmentRow = new HBox(assignmentRowSpacing, completeCheckBox, detailsLabel, progressBar, editButton);
            assignmentRow.setAlignment(Pos.CENTER_LEFT);
            pane.setContent(assignmentRow);
            Color subjectColor = subjects.get(updatedAssignment.subject);
            if (subjectColor != null) {
                assignmentRow.setBackground(new Background(new BackgroundFill(subjectColor, CornerRadii.EMPTY, Insets.EMPTY)));
            }
            pane.setUserData(updatedAssignment);
            pane.setText(updatedAssignment.name + " - Due Date: " + updatedAssignment.dueDate);
            assignments.getPanes().add(pane);
        });
    }

    private void openAssignmentEditingDialog(TitledPane pane, Label detailsLabel) {
        Assignment current = (Assignment) pane.getUserData();
        showAssignmentDialog("Edit Assignment", "Edit Assignment Details", current.name, current.start, current.finish, String.valueOf((int) (current.progress * 10)), current.subject, updatedAssignment -> {
            AssignmentPane paneTwo = new AssignmentPane(updatedAssignment);
            updatedAssignment.completed = (updatedAssignment.progress == 1.0);
            paneTwo.setUserData(updatedAssignment);
            paneTwo.setText(updatedAssignment.name + " - Due Date: " + updatedAssignment.dueDate);
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
            paneTwo.setContent(row);

            assignments.getPanes().add(paneTwo);
            assignments.getPanes().remove(pane);
        });
    }

    private void subjectLabelCreation() {
        double subjectCreationPanelSpacing = 50;
        double subjectCreationPanelPadding = 5;
        Popup subjectCreation = new Popup();
        HBox subjectCreationPanel = new HBox(subjectCreationPanelSpacing);
        subjectCreationPanel.setPadding(new Insets(subjectCreationPanelPadding));
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

    private void deleteSubject() {
        double subjectDeletionPanelSpacing = 30;
        double subjectDeletionPanelPadding = 5;
        Popup subjectDeletion = new Popup();
        HBox subjectDeletionPanel = new HBox(subjectDeletionPanelSpacing);
        subjectDeletionPanel.setPadding(new Insets(subjectDeletionPanelPadding));
        subjectDeletionPanel.setBackground(new Background(new BackgroundFill(Color.SPRINGGREEN, CornerRadii.EMPTY, Insets.EMPTY)));

        Label subjectPromptLabel = new Label("Subject Name: ");
        TextField subjectNameField = new TextField();
        Button finishButton = new Button("Delete");
        finishButton.setOnAction(ev -> {
            String name = subjectNameField.getText();
            subjects.remove(name);
            deleteSubjectFromView();
            subjectDeletion.hide();
        });

        subjectDeletionPanel.getChildren().addAll(subjectPromptLabel, subjectNameField, finishButton);
        subjectDeletion.getContent().add(subjectDeletionPanel);
        subjectDeletion.show(primaryStage);
    }

    private void deleteSubjectFromView() {
        double subjectBoxSpacing = 10;
        double subjectSpacing = 50;
        subjectBox = new VBox(subjectBoxSpacing);
        for (Map.Entry<String, Color> entry : subjects.entrySet()) {
            HBox subject = new HBox(subjectSpacing);
            subject.setMinWidth(subjectPanelWidth);
            Label subjectNameLabel = new Label(entry.getKey());
            if (isDark(entry.getValue())) {
                subjectNameLabel.setTextFill(Color.WHITE);
            } else {
                subjectNameLabel.setTextFill(Color.BLACK);
            }
            subject.getChildren().add(subjectNameLabel);
            subject.setBackground(new Background(new BackgroundFill(entry.getValue(), CornerRadii.EMPTY, Insets.EMPTY)));
            subjectBox.getChildren().add(subject);
        }
        subjectPanel.setContent(subjectBox);
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

            double subjectSpacing = 50;
            HBox subject = new HBox(subjectSpacing);
            subject.setMinWidth(subjectPanelWidth);
            Label subjectNameLabel = new Label(subjectName);
            if (isDark(color)) {
                subjectNameLabel.setTextFill(Color.WHITE);
            } else {
                subjectNameLabel.setTextFill(Color.BLACK);
            }
            subject.getChildren().add(subjectNameLabel);

            subject.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
            subjectBox.getChildren().add(subject);
        });
        return finishSubjectButton;
    }

    public boolean isDark(Color color) {
        return (1 - (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue() / 255) >= 0.5);
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
            System.out.println("Error");
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

        Assignment(String name, String start, String finish, double progress, String subject, LocalDate dueDate) {
            this.name = name;
            this.start = start;
            this.finish = finish;
            this.progress = progress;
            this.subject = subject;
            this.dueDate = dueDate;
            this.completed = false;
        }

        @Override
        public String toString() {
            int progressPercent = (int) (progress * 100);
            return "(Start: " + start + ", Finish: " + finish + ", Progress: " + progressPercent + "%, Due: " + dueDate + ")";
        }

        public String compileInfo() {
            return completed + "=" + name + "=" + start + "=" + finish + "=" + progress + "=" + subject + "=" + dueDate;
        }

        @Override
        public int compareTo(Assignment o) {
            return this.dueDate.compareTo(o.dueDate);
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
        public int compareTo(@NotNull AssignmentPane other) {
            return this.assignment.compareTo(other.assignment);
        }

    }

    private static class AssignmentPanel extends ScrollPane {

        public AssignmentPanel() {
            super();
        }

        @Override
        public javafx.collections.ObservableList
                <javafx.scene.Node> getChildren() {
            return super.getChildren();
        }

    }
}
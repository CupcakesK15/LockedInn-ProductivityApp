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
    ComboBox subjectSelector = new ComboBox();
    HashSet<Color> colors = new HashSet<>();
    Stage primaryStage;
    //VBox assignmentPanel;
    ScrollPane assignmentPanel;
    Accordion assignments;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(@NotNull Stage primaryStage) {
        this.primaryStage = primaryStage;
        subjectSelector.setVisibleRowCount(5);

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

        //assignmentPanel = new VBox(15);
        assignmentPanel = new ScrollPane(assignments);
        //assignmentPanel.setPrefHeight(675);
        assignmentPanel.setPrefHeight(200);
        assignmentPanel.setPrefWidth(950);
        //assignmentPanel.setStyle("-fx-background-color: transparent;");
        // assignmentPanel.setId("mainAssignmentPanel");
        //assignmentPanel.getClassCssMetaData().get();
        //#mainaAsignmentPanel {
        //    -fx-background-color: transparent;
        // }
        //assignmentPanel.getStyleClass();
        assignmentPanel.setStyle("-fx-background: #452103;");
        assignmentPanel.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        assignmentPanel.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        //assignmentPanel.lookup(".viewport").setStyle("-fx-background-color: #452103;");
        //assignmentPanel.getChildren().add(new Text("Assignment Panel"));
        //assignmentPanel.getChildren().add(assignments);

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

        /*
        GridPane root = new GridPane();
        root.setStyle("-fx-background-color: BLACK;");
        root.add(buttonPanel, 0, 0);
        root.add(assignmentPanel, 0, 1);
        root.add(subjectPanel, 1, 1);
        root.add(musicPanel, 0, 2, 2, 1);
        */

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

    private GridPane createAssignmentDetailsGrid(TextField nameField, TextField startField, TextField finishField, TextField progressField, ComboBox subjectSelector) {
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
        grid.add(subjectSelector, 1, 4);
        return grid;
    }

    private void showAssignmentDialog(String title, String headerText,
                                      String initialName, String initialStart,
                                      String initialFinish, String initialProgress, String subject,
                                      Consumer<String> onSave) {
        Stage dialogStage = new Stage();
        dialogStage.setTitle(title);

        Label headerLabel = new Label(headerText);
        headerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TextField nameField = new TextField(initialName);
        TextField startField = new TextField(initialStart);
        TextField finishField = new TextField(initialFinish);
        TextField progressField = new TextField(initialProgress);
        subjectSelector.setValue(subject);

        GridPane grid = createAssignmentDetailsGrid(nameField, startField, finishField, progressField, subjectSelector);

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            String newName = nameField.getText();
            String newStart = startField.getText();
            String newFinish = finishField.getText();
            String newProgress = progressField.getText();
            String newSubject = subjectSelector.getValue().toString();
            String updatedText = newName + " (Start: " + newStart + ", Finish: " + newFinish + ", Progress: " + newProgress + ")" + newSubject;
            onSave.accept(updatedText);
            System.out.println("Assignment Saved: " + updatedText);
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
                updatedText -> {
                    TitledPane pane = new TitledPane();
                    pane.setText(updatedText.substring(0, updatedText.indexOf('(')));
                    Label assignmentLabel = new Label(updatedText.substring(updatedText.indexOf('('), updatedText.indexOf(')') + 1));
                    Button editButton = new Button("Edit");
                    editButton.setOnAction(ev -> openAssignmentEditingDialog(new Label(updatedText)));
                    HBox assignmentRow = new HBox(10, assignmentLabel, editButton);
                    pane.setContent(assignmentRow);
                    String temp = updatedText.substring(updatedText.indexOf(')') + 1);
                    Color tempColor = subjects.get(temp);
                    assignmentRow.setBackground(new Background(new BackgroundFill(tempColor, new CornerRadii(0), new Insets(0))));
                    assignments.getPanes().add(pane);
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
        String subject = currentText.substring(currentText.indexOf(')') + 1);

        showAssignmentDialog("Edit Assignment", "Edit Assignment Details", name, start, finish, progress, subject,
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
            subjectSelector.getItems().add(temp);

            System.out.println("Subjects updated: " + subjects);
            subjectCreation.hide();
        });

        subjectPanel.getChildren().addAll(subjectPromptLabel, subjectNameField, colorPromptLabel, colorPicker, finishSubjectButton);
        subjectCreation.getContent().add(subjectPanel);
        subjectCreation.show(primaryStage);
    }

    //Random Code I'm Working On :D
    /*

    private GridPane createSubjectDetailsGrid(TextField nameField, TextField startField, TextField finishField, TextField progressField) {
        GridPane subjectGrid = new GridPane();
        subjectGrid.setPadding(new Insets(20));
        subjectGrid.setHgap(10);
        subjectGrid.setVgap(10);
        subjectGrid.setAlignment(Pos.CENTER);
        subjectGrid.add(new Label("Assignment Name:"), 0, 0);
        subjectGrid.add(nameField, 1, 0);
        subjectGrid.add(new Label("Start Point:"), 0, 1);
        subjectGrid.add(startField, 1, 1);
        subjectGrid.add(new Label("Finish Point:"), 0, 2);
        subjectGrid.add(finishField, 1, 2);
        subjectGrid.add(new Label("Progress:"), 0, 3);
        subjectGrid.add(progressField, 1, 3);
        return subjectGrid;
    }

    private void showSubjectDialog(String title, String headerText,
                                   String initialName, String initialStart,
                                   String initialFinish, String initialProgress,
                                   Consumer<String> onSave) {
        Stage subjectDialogStage = new Stage();
        subjectDialogStage.setTitle(title);

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
            subjectDialogStage.close();
        });
        grid.add(saveButton, 0, 4, 2, 1);
        GridPane.setMargin(saveButton, new Insets(10, 0, 0, 0));
        saveButton.setMaxWidth(Double.MAX_VALUE);

        VBox dialogLayout = new VBox(10, headerLabel, grid);
        dialogLayout.setPadding(new Insets(20));

        Scene dialogScene = new Scene(dialogLayout, 350, 300);
        subjectDialogStage.setScene(dialogScene);
        subjectDialogStage.initOwner(primaryStage);
        subjectDialogStage.show();
    }

    private void openSubjectCreationDialog() {
        showSubjectDialog("Create Assignment", "Enter Assignment Details", "", "", "", "",
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

    private void populateSubjects(String name, Colo color{ // pass in name and color
     create an hbox with the name and an edit button
    set the background for the hboxas the color of the subject
    add that to the subject panel

    */
}
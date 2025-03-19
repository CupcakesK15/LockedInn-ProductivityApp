package com.example.lockedinn;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LockedInnApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        Button createAssignmentButton = new Button("Create Assignment");
        createAssignmentButton.setOnAction(event -> openAssignmentCreationDialog());

        VBox layout = new VBox(15);
        layout.setStyle("-fx-padding: 20;");
        layout.getChildren().add(createAssignmentButton);

        Scene scene = new Scene(layout, 400, 200);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Locked Inn - Assignment Tracker");
        primaryStage.show();
    }

    private void openAssignmentCreationDialog() {
        // Implementation for dialog...
    }

    public static void main(String[] args) {
        launch(args);
    }
}
module org.openjfx.triumvirateeoy {
    requires javafx.controls;
    requires javafx.fxml;
    exports com.example.lockedinn;
}

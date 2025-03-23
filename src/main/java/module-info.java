module org.openjfx.triumvirateeoy5 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
    requires org.jetbrains.annotations;

    opens org.openjfx.triumvirateeoy5 to javafx.fxml;
    exports org.openjfx.triumvirateeoy5;

    opens com.example.lockedinn to javafx.fxml;
    exports com.example.lockedinn;
}
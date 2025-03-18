module org.openjfx.triumvirateeoy5 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens org.openjfx.triumvirateeoy5 to javafx.fxml;
    exports org.openjfx.triumvirateeoy5;
}
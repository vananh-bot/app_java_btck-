module com.example.flowtask {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires jbcrypt;

    opens Controller to javafx.fxml;
    opens Test to javafx.graphics, javafx.fxml;
    opens Model to javafx.base;
}
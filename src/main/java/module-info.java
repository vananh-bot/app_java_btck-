module com.example.flowtask {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    opens Controller to javafx.fxml;

    opens Model to java.sql;

    exports Controller;
    exports Model;
    exports Service;
    exports Utils;
    exports Test;
}
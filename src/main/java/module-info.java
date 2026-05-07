module com.example.flowtask {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires jbcrypt;
    requires javafx.graphics;
    requires jdk.jdi;
    requires java.xml.crypto;
    requires mysql.connector.j;
    requires com.google.protobuf;
    requires jakarta.mail;
    requires jdk.jshell;
    requires javafx.base;
    requires com.zaxxer.hikari;

    opens Controller to javafx.fxml;
    opens Test to javafx.graphics, javafx.fxml;
    opens Model to javafx.base;
    opens DTO to javafx.base;
}
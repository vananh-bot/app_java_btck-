module com.example.flowtask {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql; // Thêm dòng này để dùng JDBC

    opens Test to javafx.graphics, javafx.fxml;
    opens Controller to javafx.fxml;
    //opens Database to java.sql; // Cho phép sql truy cập nếu cần

    exports Test;
}
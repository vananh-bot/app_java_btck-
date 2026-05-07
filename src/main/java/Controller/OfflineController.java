package Controller;

import Utils.NetworkUtil;
import Utils.AppErrorHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class OfflineController {

    @FXML
    private Button btnRetry;

    @FXML
    public void initialize() {
        btnRetry.setOnAction(event -> {
            if (NetworkUtil.isOnline()) {
                AppErrorHandler.closeOfflinePopup();
            } else {
                btnRetry.setText("Vẫn chưa có mạng. Thử lại...");
            }
        });
    }
}
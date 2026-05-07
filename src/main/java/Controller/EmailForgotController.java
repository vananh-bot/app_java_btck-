package Controller;

import Service.ForgotPasswordService;
import Utils.DialogManager;
import Utils.ResetPasswordContext;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import Enum.Screen;
import javafx.scene.layout.StackPane;

public class EmailForgotController {

    @FXML private Label lblError;
    @FXML private TextField enteremail;
    @FXML private StackPane overlay;
    private final ForgotPasswordService forgotService = new ForgotPasswordService();
    @FXML
    void sendToken() {
        String email = enteremail.getText().trim();
        if (email.isEmpty()) {
            lblError.setText("Vui lòng nhập địa chỉ email.");
            return;
        }
        if (forgotService.requestOtp(email)) {
            ResetPasswordContext.setEmail(email);
            DialogManager.getInstance().show(Screen.TOKEN_AUTHENTIC);
        } else {
            lblError.setText("Email không tồn tại trong hệ thống.");
        }
    }
    @FXML
    private void handleCancel(ActionEvent event) {
        DialogManager.getInstance().close(overlay);
    }

}

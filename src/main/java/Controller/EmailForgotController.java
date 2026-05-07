package Controller;

import Service.ForgotPasswordService;
import Utils.DialogManager;
import Utils.ResetPasswordContext;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import Enum.Screen;
import javafx.scene.layout.StackPane;

public class EmailForgotController {

    @FXML private Label lblError;
    @FXML private TextField enteremail;
    @FXML private StackPane overlay;
    @FXML
    private Button btnsendtoken;
    @FXML
    private ProgressIndicator loading;
    private final ForgotPasswordService forgotService = new ForgotPasswordService();
    @FXML
    public void initialize(){
        showLoading(false);
    }
    @FXML
    void sendToken() {
        String email = enteremail.getText().trim();
        if (email.isEmpty()) {
            lblError.setText("Vui lòng nhập địa chỉ email.");
            return;
        }
        showLoading(true);

        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return forgotService.requestOtp(email);
            }
        };
        task.setOnSucceeded(e -> {
            showLoading(false);
            if(task.getValue()) {
                ResetPasswordContext.setEmail(email);
                DialogManager.getInstance().show(Screen.TOKEN_AUTHENTIC);
            } else {
                lblError.setText("Email không tồn tại trong hệ thống.");
            }
        });
        task.setOnFailed(e -> {
            loading.setVisible(false);
            lblError.setText("Có lỗi xảy ra ở màn nhập email để đổi mật khẩu");
        });
        new Thread(task).start();
    }
    private void showLoading(boolean b){
        loading.setVisible(b);
        loading.setProgress(-1);
        loading.setManaged(b);
        if(b) btnsendtoken.setText("");
        else btnsendtoken.setText("Gửi mã xác thực");

    }
    @FXML
    private void handleCancel(ActionEvent event) {
        DialogManager.getInstance().close(overlay);
    }

}

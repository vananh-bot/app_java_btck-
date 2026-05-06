package Controller;

import Service.ForgotPasswordService;
import Utils.DialogManager;
import Utils.ResetPasswordContext;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import Enum.Screen;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class TokenForgetController {
    @FXML private StackPane overlay;
    @FXML private TextField txtOtp;
    @FXML private Label lblError;
    @FXML private Label lblResend;

    private final ForgotPasswordService forgotService = new ForgotPasswordService();
    private Timeline timeline;
    private int timeSeconds = 60;

    @FXML
    public void initialize() {
        setupOtpInput();
        startResendCountdown();
    }

    // ==========================================
    // CHỈ CÒN GỌI HÀM VÀ CHUYỂN CẢNH
    // ==========================================
    @FXML
    void handleVerifyToken() {
        String otp = txtOtp.getText().trim();
        String currentEmail = ResetPasswordContext.getEmail();

        // Ném toàn bộ cho Service xử lý đúng/sai/độ dài
        if (forgotService.verifyOtp(currentEmail, otp)) {
            if (timeline != null) timeline.stop();
            DialogManager.getInstance().show(Screen.NEW_PASSWORDD);
        } else {
            lblError.setText("Mã không đủ 6 số, sai hoặc đã hết hạn.");
        }
    }

    @FXML
    void handleResendToken() {
        // Gọi Service gửi lại OTP
        if (forgotService.requestOtp(ResetPasswordContext.getEmail())) {
            lblError.setText("Mã xác thực mới đã được gửi!");
            lblError.setStyle("-fx-text-fill: green;");

            txtOtp.clear();
            txtOtp.requestFocus();
            startResendCountdown(); // Reset giao diện
        } else {
            lblError.setText("Lỗi hệ thống khi gửi lại mã.");
            lblError.setStyle("-fx-text-fill: red;");
        }
    }

    // ==========================================
    // UI LOGIC (BẮT BUỘC PHẢI Ở CONTROLLER)
    // ==========================================

    // UI Logic: Chặn gõ chữ cái trên màn hình
    private void setupOtpInput() {
        txtOtp.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtOtp.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (txtOtp.getText().length() > 6) {
                txtOtp.setText(txtOtp.getText().substring(0, 6));
            }
        });
    }

    // UI Logic: Hiển thị bộ đếm ngược 60s
    private void startResendCountdown() {
        timeSeconds = 60;
        lblResend.setDisable(true);
        lblResend.setStyle("-fx-text-fill: #a0aec0; -fx-cursor: default;");

        if (timeline != null) timeline.stop();

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            timeSeconds--;
            lblResend.setText("Gửi lại mã (" + timeSeconds + "s)");

            if (timeSeconds <= 0) {
                timeline.stop();
                lblResend.setText("Gửi lại mã");
                lblResend.setDisable(false);
                lblResend.setStyle("-fx-text-fill: #2e59d9; -fx-cursor: hand;");
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
    @FXML
    private void handleCancel(ActionEvent event) {
        DialogManager.getInstance().close(overlay);
    }

}
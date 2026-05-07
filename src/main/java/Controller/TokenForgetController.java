package Controller;

import Service.ForgotPasswordService;
import Utils.DialogManager;
import Utils.ResetPasswordContext;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox; // Đổi từ StackPane sang VBox để khớp FXML
import Enum.Screen;
import javafx.util.Duration;

public class TokenForgetController {

    @FXML private StackPane overlay;

    // 6 ô nhập mã thay thế cho txtOtp
    @FXML private TextField otp1, otp2, otp3, otp4, otp5, otp6;

    @FXML private Label lblError;
    @FXML private Label timerLabel;
    @FXML private Hyperlink lblResend; // UI mới dùng Hyperlink thay vì Label

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
        String otp = getOtpString();
        String currentEmail = ResetPasswordContext.getEmail();

        if (otp.length() < 6) {
            showError("Vui lòng nhập đủ 6 chữ số.");
            return;
        }

        // Ném toàn bộ cho Service xử lý đúng/sai/độ dài
        if (forgotService.verifyOtp(currentEmail, otp)) {
            if (timeline != null) timeline.stop();
            lblError.setVisible(false);
            DialogManager.getInstance().show(Screen.NEW_PASSWORDD);
        } else {
            showError("Mã không đúng hoặc đã hết hạn.");
        }
    }

    @FXML
    void handleResendToken() {
        // Gọi Service gửi lại OTP
        if (forgotService.requestOtp(ResetPasswordContext.getEmail())) {
            lblError.setText("Mã xác thực mới đã được gửi!");
            lblError.setStyle("-fx-text-fill: green;");
            lblError.setVisible(true);

            clearOtpFields();
            otp1.requestFocus();
            startResendCountdown(); // Reset giao diện
        } else {
            showError("Lỗi hệ thống khi gửi lại mã.");
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        if (timeline != null) timeline.stop();
        DialogManager.getInstance().close(overlay);
    }

    private void setupOtpInput() {
        TextField[] otpFields = {otp1, otp2, otp3, otp4, otp5, otp6};

        for (int i = 0; i < otpFields.length; i++) {
            int index = i;
            TextField field = otpFields[i];

            field.textProperty().addListener((observable, oldValue, newValue) -> {
                // Chỉ cho phép nhập số
                if (!newValue.matches("\\d*")) {
                    field.setText(newValue.replaceAll("[^\\d]", ""));
                }
                // Giới hạn 1 ký tự
                else if (newValue.length() > 1) {
                    field.setText(newValue.substring(newValue.length() - 1));
                }

                // Tự động nhảy sang ô tiếp theo
                if (field.getText().length() == 1 && index < 5) {
                    otpFields[index + 1].requestFocus();
                }
            });

            // Tự động lùi về ô trước khi nhấn Backspace ở ô trống
            field.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.BACK_SPACE && field.getText().isEmpty() && index > 0) {
                    otpFields[index - 1].requestFocus();
                    otpFields[index - 1].clear();
                }
            });
        }
    }

    // Gộp 6 ô thành 1 chuỗi OTP
    private String getOtpString() {
        return otp1.getText() + otp2.getText() + otp3.getText() +
                otp4.getText() + otp5.getText() + otp6.getText();
    }

    // Xóa trắng dữ liệu nhập
    private void clearOtpFields() {
        otp1.clear(); otp2.clear(); otp3.clear();
        otp4.clear(); otp5.clear(); otp6.clear();
    }

    // UI Logic: Hiển thị bộ đếm ngược 60s cập nhật cho timerLabel và lblResend
    private void startResendCountdown() {
        timeSeconds = 60;

        if (lblResend != null) {
            lblResend.setDisable(true);
        }

        if (timeline != null) timeline.stop();

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            timeSeconds--;

            // Cập nhật text cho Label đếm ngược riêng trong UI mới
            if (timerLabel != null) {
                timerLabel.setText(String.format("Mã OTP sẽ hết hạn trong %02d:%02d giây", timeSeconds / 60, timeSeconds % 60));
            }

            if (timeSeconds <= 0) {
                timeline.stop();
                if (timerLabel != null) {
                    timerLabel.setText("Mã OTP đã hết hạn!");
                }
                if (lblResend != null) {
                    lblResend.setDisable(false);
                }
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    // Hàm phụ trợ hiển thị lỗi
    private void showError(String message) {
        lblError.setText(message);
        lblError.setStyle("-fx-text-fill: red;");
        lblError.setVisible(true);
    }
}
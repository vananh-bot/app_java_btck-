package Controller;

import Service.MailService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.util.Duration;

public class OtpController {
    @FXML private TextField otp1, otp2, otp3, otp4, otp5, otp6;
    @FXML private Label errorLabel;
    @FXML private Label emailDisplayLabel;
    @FXML private Label timerLabel;

    private String generatedOtp;
    private boolean isVerified = false;
    private String userEmail;
    private Timeline timeline;
    private int secondsRemaining = 180;

    @FXML
    public void initialize() {
        setupField(otp1, null, otp2);
        setupField(otp2, otp1, otp3);
        setupField(otp3, otp2, otp4);
        setupField(otp4, otp3, otp5);
        setupField(otp5, otp4, otp6);
        setupField(otp6, otp5, null);

        startTimer();
    }

    private void startTimer() {
        if (timeline != null) timeline.stop();
        secondsRemaining = 180;

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsRemaining--;
            int mins = secondsRemaining / 60;
            int secs = secondsRemaining % 60;
            timerLabel.setText(String.format("Mã OTP sẽ hết hạn trong %02d:%02d giây", mins, secs));

            if (secondsRemaining <= 0) {
                timeline.stop();
                errorLabel.setText("Mã OTP đã hết hạn, vui lòng gửi lại mã!");
                errorLabel.setVisible(true);
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void setupField(TextField current, TextField previous, TextField next) {
        current.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                current.setText(newVal.replaceAll("[^\\d]", ""));
            }
            if (newVal.length() >= 1) {
                current.setText(newVal.substring(newVal.length() - 1));
                if (next != null) next.requestFocus();
            }
        });

        current.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.BACK_SPACE) {
                if (current.getText().isEmpty() && previous != null) {
                    previous.requestFocus();
                    previous.clear();
                }
            }
        });
    }

    public void setGeneratedOtp(String otp) {
        this.generatedOtp = otp;
    }

    public void setUserEmail(String email) {
        this.userEmail = email;
        emailDisplayLabel.setText(email);
    }

    public boolean isVerified() { return isVerified; }

    @FXML
    void handleVerify() {
        if (secondsRemaining <= 0) {
            errorLabel.setText("Mã đã hết hạn!");
            errorLabel.setVisible(true);
            return;
        }

        String inputOtp = otp1.getText() + otp2.getText() + otp3.getText() +
                otp4.getText() + otp5.getText() + otp6.getText();

        if (inputOtp.equals(generatedOtp)) {
            isVerified = true;
            if (timeline != null) timeline.stop(); // Dừng đếm khi xong
            ((Stage) otp1.getScene().getWindow()).close();
        } else {
            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setText("Mã OTP không đúng!");
            errorLabel.setVisible(true);
        }
    }

    @FXML
    void handleResend() {
        generatedOtp = String.valueOf((int) ((Math.random() * 899999) + 100000));
        new MailService().sendEmail(userEmail, "Gửi lại mã xác thực FlowTask", generatedOtp);

        errorLabel.setStyle("-fx-text-fill: #008000;");
        errorLabel.setText("Đã gửi lại mã mới!");
        errorLabel.setVisible(true);

        startTimer();

        otp1.clear(); otp2.clear(); otp3.clear();
        otp4.clear(); otp5.clear(); otp6.clear();
        otp1.requestFocus();
    }
}
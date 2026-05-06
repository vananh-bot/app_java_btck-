package Controller;

import DAO.UserDAO;
import Service.MailService;
import Service.RegisterService;
import Utils.SceneNavigator;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import java.io.IOException;

public class RegisterController {

    @FXML
    private TextField email;

    @FXML
    private Label error;
    @FXML
    private ImageView eyeclose1;
    @FXML
    private ImageView eyeclose2;
    @FXML
    private ImageView eyeopen1;
    @FXML
    private ImageView eyeopen2;

    @FXML
    private Hyperlink login;

    @FXML
    private TextField name;

    @FXML
    private PasswordField password1;

    @FXML
    private PasswordField password2;

    @FXML
    private TextField passwordvisible1;

    @FXML
    private TextField passwordvisible2;

    @FXML
    private Button signup;
    @FXML
    private String currentOtp;
    @FXML
    private ProgressIndicator loading;
    @FXML
    public void initialize() {
        showLoading(false);

        passwordvisible1.setVisible(false);
        passwordvisible2.setVisible(false);

        eyeclose1.setVisible(true);
        eyeclose2.setVisible(true);

        eyeopen1.setVisible(false);
        eyeopen2.setVisible(false);

        passwordvisible1.textProperty().bindBidirectional(password1.textProperty());
        passwordvisible2.textProperty().bindBidirectional(password2.textProperty());
    }

    @FXML
    void hiddenPassword(MouseEvent event) {
        if (event.getSource() == eyeopen1) {
            password1.setVisible(true);
            passwordvisible1.setVisible(false);
            eyeopen1.setVisible(false);
            eyeclose1.setVisible(true);

            // Đưa con trỏ chuột về lại ô password1
            password1.requestFocus();
            password1.positionCaret(password1.getText().length());
        } else {
            password2.setVisible(true);
            passwordvisible2.setVisible(false);
            eyeopen2.setVisible(false);
            eyeclose2.setVisible(true);

            // Đưa con trỏ chuột về lại ô password2
            password2.requestFocus();
            password2.positionCaret(password2.getText().length());
        }
    }

    @FXML
    void showPassword(MouseEvent event) {
        if (event.getSource() == eyeclose1) {
            passwordvisible1.setVisible(true);
            password1.setVisible(false);
            eyeopen1.setVisible(true);
            eyeclose1.setVisible(false);

            // Đưa con trỏ chuột về ô passwordvisible1
            passwordvisible1.requestFocus();
            passwordvisible1.positionCaret(passwordvisible1.getText().length());
        } else {
            passwordvisible2.setVisible(true);
            password2.setVisible(false);
            eyeopen2.setVisible(true);
            eyeclose2.setVisible(false);

            // Đưa con trỏ chuột về ô passwordvisible2
            passwordvisible2.requestFocus();
            passwordvisible2.positionCaret(passwordvisible2.getText().length());
        }
    }
    @FXML
    public void handleRegister(ActionEvent event) {
        String userName = name.getText().trim();
        String userEmail = email.getText().trim();

        String pass1 = passwordvisible1.isVisible()
                ? passwordvisible1.getText()
                : password1.getText();

        String pass2 = passwordvisible2.isVisible()
                ? passwordvisible2.getText()
                : password2.getText();

        showLoading(true);

        Task<String> task = new Task<String>() {
            @Override
            protected String call() throws Exception {
                RegisterService service = new RegisterService(new UserDAO());
                return service.register(userName, userEmail, pass1, pass2);
            }
        };

        task.setOnSucceeded(e -> {
            showLoading(false);

            String checkResult = task.getValue();
            if (!"SUCCESS".equals(checkResult)) {
                error.setStyle("-fx-text-fill: #ff0000;");
                error.setText(checkResult);
                error.setVisible(true);
                return;
            }
            currentOtp = String.valueOf((int) ((Math.random() * 899999) + 100000));

            error.setStyle("-fx-text-fill: #0078d4;");
            error.setText("Đã gửi mã xác nhận vào Email...");
            error.setVisible(true);
            MailService mailService = new MailService();
            mailService.sendOtpEmail(userEmail, currentOtp);
            showOtpVerification(userName, userEmail, pass1);

        });

        task.setOnFailed(e ->{
            showLoading(false);
            error.setText("Lỗi hệ thống!");
        });

        new Thread(task).start();
    }
    private void showOtpVerification(String name, String email, String pass) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/auth/otpview.fxml"));
            Parent root = loader.load();

            OtpController controller = loader.getController();
            controller.setGeneratedOtp(currentOtp);
            controller.setUserEmail(email);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Xác thực FlowTask");
            stage.showAndWait();
            if (controller.isVerified()) {
                RegisterService service = new RegisterService(new UserDAO());
                service.setupUser(name,email,pass);
                error.setStyle("-fx-text-fill: #048000;");
                error.setText("ĐĂNG KÍ THÀNH CÔNG!");
                clearForm();
            } else {
                error.setStyle("-fx-text-fill: #ff0000;");
                error.setText("Đăng kí thất bại vì bạn chưa xác thực OTP");
            }
        } catch (IOException e) {
            e.printStackTrace();
            error.setText("Không thể mở cửa sổ xác thực!");
        }
    }
    @FXML
    void handleGoToLogin(ActionEvent event) {
        SceneNavigator.switchScene(event, SceneNavigator.LOGIN, "FlowTask");
    }
    @FXML
    private void clearForm() {
        name.clear();
        email.clear();

        password1.clear();
        password2.clear();
        passwordvisible1.clear();
        passwordvisible2.clear();

        // reset UI
        password1.setVisible(true);
        password2.setVisible(true);
        passwordvisible1.setVisible(false);
        passwordvisible2.setVisible(false);

        eyeopen1.setVisible(true);
        eyeopen2.setVisible(true);
        eyeclose1.setVisible(false);
        eyeclose2.setVisible(false);
    }
    private void showLoading(boolean b){
        loading.setVisible(b);
        loading.setManaged(b);
        if(b){
            loading.setProgress(-1);
            signup.setText("");
        } else {
            signup.setText("Đăng ký ->");
        }
    }
}
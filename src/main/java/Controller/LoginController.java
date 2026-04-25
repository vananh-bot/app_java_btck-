package Controller;

import DAO.UserDAO;
import Service.LoginService;
import Model.User;
// Nhớ import thêm 2 cái này để hết báo đỏ
import Utils.UserSession;
import Utils.SceneNavigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginController {

    @FXML private ImageView eyeclose, eyeopen;
    @FXML private PasswordField password;
    @FXML private TextField passwordvisible, username;
    @FXML private Button signin;
    @FXML private Hyperlink register;
    @FXML private Label errorLabel;

    // SỬA LỖI 1: Khai báo đối tượng userDAO để gọi được hàm findByName
    private final UserDAO userDAO = new UserDAO();
    private final LoginService loginService = new LoginService();

    @FXML
    void initialize(){
        password.setVisible(true);
        passwordvisible.setVisible(false);
        eyeclose.setVisible(true);
        eyeopen.setVisible(false);
    }

    @FXML
    void handleLogin(ActionEvent event) {
        if (errorLabel == null) {
            System.err.println("LỖI: Bạn chưa đặt fx:id cho errorLabel trong Scene Builder!");
            return;
        }

        String inputName = username.getText().trim();
        String inputPass = password.isVisible() ? password.getText() : passwordvisible.getText();

        String result = loginService.login(inputName, inputPass);

        if ("SUCCESS".equals(result)) {
            // SỬA LỖI 2: Gọi findByName qua đối tượng userDAO (viết thường) thay vì UserDAO (viết hoa)
            User user = userDAO.findByName(inputName);
            if (user != null) {
                UserSession.login(user); // Cất vào kho
                UserSession.setEmail(user.getEmail());
            }

//            errorLabel.setStyle("-fx-text-fill: green;");
//            errorLabel.setText("Đăng nhập thành công!");
//            errorLabel.setVisible(true);

            goToMainScreen(event);
        } else {
            errorLabel.setText(result);
            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setVisible(true);
        }
    }

    // GIỮ NGUYÊN TOÀN BỘ CODE CŨ BÊN DƯỚI
    @FXML
    void goToMainScreen(ActionEvent event) {
        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/layout/MainLayoutView.fxml")
            );

            Parent root = loader.load();

            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Task Manager");
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void hiddenPassword() {
        password.setText(passwordvisible.getText());
        password.setVisible(true);
        passwordvisible.setVisible(false);
        eyeclose.setVisible(true);
        eyeopen.setVisible(false);
    }

    @FXML
    private void showPassword() {
        passwordvisible.setText(password.getText());
        passwordvisible.setVisible(true);
        password.setVisible(false);
        eyeclose.setVisible(false);
        eyeopen.setVisible(true);
    }

    @FXML
    public void goToResigter(ActionEvent actionEvent) {
        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/auth/register.fxml")
            );

            Parent root = loader.load();

            Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.setTitle("Register");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
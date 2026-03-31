package Controller;

import Service.LoginService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
public class LoginController {

    private LoginService loginService;

    public LoginController(LoginService loginService){
        this.loginService = loginService;
    }

    public LoginController() {
    }

    @FXML
    private ImageView eyeclose;

    @FXML
    private ImageView eyeopen;

    @FXML
    private PasswordField password;

    @FXML
    private TextField passwordvisible;

    @FXML
    private Hyperlink register;

    @FXML
    private Button signin;

    @FXML
    void initialize(){
        password.setVisible(true);
        passwordvisible.setVisible(false);

        eyeclose.setVisible(true);
        eyeopen.setVisible(false);
    }

    @FXML
    void goToRegister(ActionEvent event) {

    }

    @FXML
    void handleLogin(ActionEvent event) {

    }

    @FXML
    void hiddenPassword(MouseEvent event) {
        password.setText(passwordvisible.getText());

        password.setVisible(true);
        passwordvisible.setVisible(false);

        eyeclose.setVisible(true);
        eyeopen.setVisible(false);
    }

    @FXML
    void showPassword(MouseEvent event) {
        passwordvisible.setText(password.getText());

        password.setVisible(false);
        passwordvisible.setVisible(true);

        eyeclose.setVisible(false);
        eyeopen.setVisible(true);
    }


}

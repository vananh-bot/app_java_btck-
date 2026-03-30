package Controller;

import Service.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

public class AuthCotroller {

    private AuthService authService;

    public AuthCotroller(AuthService authService) {
        this.authService = authService;
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



    public void handleLogin() {

    }

    public void handleRegister() {

    }

}
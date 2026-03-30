package Controller;

import Service.RegisterService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class RegisterController {

    private RegisterService registerService;

    public RegisterController(RegisterService registerService){
        this.registerService = registerService;
    }

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
    private PasswordField password1;

    @FXML
    private PasswordField password2;

    @FXML
    private TextField passwordvisible;

    @FXML
    private TextField passwordvisible1;

    @FXML
    private Button signin;

    @FXML
    void hiddenPassword1(MouseEvent event) {

    }

    @FXML
    void hiddenPassword2(MouseEvent event) {

    }

    @FXML
    void showPassword1(MouseEvent event) {

    }

    @FXML
    void showPassword2(MouseEvent event) {

    }

}






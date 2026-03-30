package Controller;

import Service.LoginService;
import Service.RegisterService;

public class RegisterController {
    private RegisterService registerService;

    public RegisterController(RegisterService registerService){
        this.registerService = registerService;
    }
}

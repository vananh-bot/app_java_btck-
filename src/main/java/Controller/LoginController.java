package Controller;

import Service.LoginService;

public class LoginController {
    private LoginService loginService;

    public LoginController(LoginService loginService){
        this.loginService = loginService;
    }


}

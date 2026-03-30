package Service;

import DAO.UserDAO;
import Utils.Validator;

public class LoginService {
    private UserDAO userDAO;
    private Validator validator;

    public LoginService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void login(String email, String password) {

    }
}
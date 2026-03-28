package Service;

import DAO.UserDAO;
import Utils.Validator;

public class AuthService {
    private UserDAO userDAO;
    private Validator validator;

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void login(String email, String password) {

    }

    public void register(String email, String password) {

    }
}

package Service;

import DAO.UserDAO;
import Utils.Validator;

public class RegisterService {
    private UserDAO userDAO;
    private Validator validator;

    public RegisterService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void register(String email, String password) {

    }
}

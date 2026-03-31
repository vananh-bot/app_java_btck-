package Service;

import DAO.UserDAO;
import Model.User;
import Utils.Validator;

public class RegisterService {

    private UserDAO userDAO;

    public RegisterService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public String register(String name, String email, String pass1, String pass2) {

        if (Validator.isEmpty(name, email, pass1, pass2)) {
            return "Vui lòng nhập đầy đủ thông tin!";
        }

        if (!Validator.isValidEmail(email)) {
            return "Email không hợp lệ!";
        }

        if (!Validator.isPasswordValid(pass1)) {
            return "Mật khẩu phải >= 6 ký tự!";
        }

        if (!Validator.isPasswordMatch(pass1, pass2)) {
            return "Mật khẩu không khớp!";
        }

        if (userDAO.findByEmail(email) != null) {
            return "Email đã tồn tại!";
        }

        if(userDAO.findByName(name) != null){
            return "Tên đã tồn tại!";
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(pass1);

        userDAO.insert(user);
        return "SUCCESS";
    }
}
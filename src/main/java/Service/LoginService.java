package Service;

import DAO.UserDAO;
import Model.User;
import Utils.Validator;

public class LoginService {
    private UserDAO userDAO = new UserDAO();

    public String login(String username, String password) {
        // 1. Kiểm tra đầu vào trống
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return "Vui lòng nhập đầy đủ thông tin!";
        }

        User user = userDAO.findByName(username);
        if (user == null || !user.getName().equals(username)) {
            return "Tên đăng nhập không tồn tại!";
        }

        if (userDAO.checkLogin(username, password)) {
            return "SUCCESS";
        } else {
            return "Mật khẩu không chính xác!";
        }
    }
}
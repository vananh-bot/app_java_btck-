package Service;

import DAO.UserDAO;
import Model.User;
import Utils.Validator;

public class LoginService {
    private UserDAO userDAO = new UserDAO(); // Khởi tạo để tránh NullPointerException

    public String login(String username, String password) {
        // 1. Kiểm tra đầu vào trống
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return "Vui lòng nhập đầy đủ thông tin!";
        }

        // 2. Kiểm tra tên đăng nhập có tồn tại không
        User user = userDAO.findByName(username); // Sử dụng hàm findByName đã có trong DAO
        if (user == null) {
            return "Tên đăng nhập không tồn tại!";
        }

        // 3. Kiểm tra mật khẩu (Sử dụng hàm checkLogin để xác thực cả cặp)
        if (userDAO.checkLogin(username, password)) {
            return "SUCCESS";
        } else {
            return "Mật khẩu không chính xác!";
        }
    }
}
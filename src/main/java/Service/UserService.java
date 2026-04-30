package Service;

import DAO.UserDAO;
import Model.User;

import java.util.List;

public class UserService {

    private UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    // đăng nhập
    public User login(String email, String password) {
        return userDAO.login(email, password);
    }

    // lấy user theo id
    public User getUserById(Integer id) {
        if (id == null) return null;
        return userDAO.findById(id);
    }

    // lấy user theo email
    public User getUserByEmail(String email) {
        if (email == null || email.isBlank()) return null;
        return userDAO.findByEmail(email);
    }

    // lấy user theo name
    public User getUserByName(String name) {
        if (name == null || name.isBlank()) return null;
        return userDAO.findByName(name);
    }

    // lấy tất cả user
    public List<User> getAllUsers() {
        return userDAO.selectAll();
    }

    // tạo user
    public boolean createUser(User user) {
        return userDAO.insert(user) > 0;
    }

    // update user
    public boolean updateUser(User user) {
        return userDAO.update(user) > 0;
    }

    // xóa user
    public boolean deleteUser(int id) {
        return userDAO.deleteById(id) > 0;
    }
}
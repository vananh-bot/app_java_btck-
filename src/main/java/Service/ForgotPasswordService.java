package Service;

import DAO.PasswordResetDAO;
import DAO.UserDAO;
import Model.User;
import java.util.Random;

public class ForgotPasswordService {

    private final MailService mailService = new MailService();
    private final PasswordResetDAO passwordResetDAO = new PasswordResetDAO();
    private final UserDAO userDAO = new UserDAO();
    public boolean requestOtp(String email) {
        User user = userDAO.findByEmail(email);
        if (user == null) {
            return false; // Email không tồn tại
        }

        String otpToken = generateSixDigitToken();
        passwordResetDAO.insert(email, otpToken);
        mailService.sendForgotPasswordToken(email, otpToken);
        return true;
    }
    public boolean verifyOtp(String email, String otp) {
        return passwordResetDAO.validateToken(email, otp);
    }

    public boolean resetPassword(String email, String newPassword) {
        boolean isUpdated = userDAO.updatePassword(email, newPassword);

        if (isUpdated) {
            // Đổi xong thì xóa mã cũ đi cho sạch DB
            passwordResetDAO.deleteByEmail(email);
            return true;
        }
        return false;
    }

    private String generateSixDigitToken() {
        Random random = new Random();
        int number = random.nextInt(999999);
        return String.format("%06d", number);
    }
}
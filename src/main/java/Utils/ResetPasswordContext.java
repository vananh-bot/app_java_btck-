package Utils; // Hoặc package nào bạn thấy phù hợp

public class ResetPasswordContext {
    private static String email;

    // Lưu email lại
    public static void setEmail(String e) {
        email = e;
    }

    // Lấy email ra dùng
    public static String getEmail() {
        return email;
    }

    // Xóa email sau khi đổi mật khẩu xong cho an toàn
    public static void clear() {
        email = null;
    }
}
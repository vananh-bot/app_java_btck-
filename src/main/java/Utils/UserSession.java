package Utils; // Đảm bảo dòng này khớp với folder chứa file

import Model.User;

public class UserSession {
    private static User currentUser;
    private static String email = "";


    public static void login(User user) {
        currentUser = user;
    }

    public static void logout() {
        currentUser = null;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    // BỔ SUNG HÀM NÀY ĐỂ HẾT BÁO ĐỎ Ở CONTROLLER
    public static int getUserId() {
        return (currentUser != null) ? currentUser.getId() : -1;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String userEmail) {
        email = userEmail;
    }
}
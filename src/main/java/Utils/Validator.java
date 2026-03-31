package Utils;

public class Validator {
    public static boolean isEmpty(String ... fields){
        for(String f : fields){
            if(f == null || f.trim().isEmpty()) return true;
        }
        return false;
    }

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    public static boolean isPasswordValid(String password) {
        if (password == null) return false;
        return password.length() >= 6;
    }

    public static boolean isPasswordMatch(String pass1, String pass2) {
        if (pass1 == null || pass2 == null) return false;
        return pass1.equals(pass2);
    }
}
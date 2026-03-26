package Model;

import java.util.List;

public class User {
    private int id;
    private String email;
    private String password;
    private String name;
    private boolean isVerified;

    private List<Project> projects;

    public boolean checkPassword(String password){
        return this.password.equals(password);
    }

    public int getId() { return id; }
    public String getEmail() { return email; }

}

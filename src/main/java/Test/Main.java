package Test;

import DAO.ProjectDAO;
import Model.Project;
import Model.User;

public class Main {

    public static void main(String[] args) {

        ProjectDAO dao = new ProjectDAO();

        // 🔥 test JOIN
        Project p = dao.getProjectWithMembers(1);

        if (p == null) {
            System.out.println("❌ Không tìm thấy project");
            return;
        }

        System.out.println("📌 Project: " + p.getName());

        if (p.getMembers() == null || p.getMembers().isEmpty()) {
            System.out.println("⚠️ Project chưa có member");
        } else {
            System.out.println("👥 Members:");
            for (User u : p.getMembers()) {
                System.out.println("- " + u.getName());
            }
        }
    }
}
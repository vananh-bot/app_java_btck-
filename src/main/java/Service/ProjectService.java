package Service;

import DAO.InviteDAO;
import DAO.ProjectDAO;
import DAO.UserProjectDAO;
import Model.Project;

import java.util.UUID;

public class ProjectService {
    private ProjectDAO projectDAO;
    private UserProjectDAO userProjectDAO;
    private InviteDAO inviteDAO;

    public ProjectService(ProjectDAO projectDAO,
                          UserProjectDAO userProjectDAO,
                          InviteDAO inviteDAO) {

        this.projectDAO = projectDAO;
        this.userProjectDAO = userProjectDAO;
        this.inviteDAO = inviteDAO;
    }
    public boolean createProject(String name, String description, int userId) {
        if (projectDAO.isProjectNameExists(userId, name)) {
            System.out.println("Tên dự án đã tồn tại!");
            return false;
        }

        Project project = new Project();
        project.setName(name);
        project.setDescription(description);
        project.setOwnerId(userId);

        String inviteCode = UUID.randomUUID().toString().substring(0, 10).toUpperCase();
        project.setInviteCode(inviteCode);

        int projectId = projectDAO.insert(project);

        if (projectId > 0) {
            return userProjectDAO.addMemberToProject(userId, projectId);
        }

        return false;
    }

    public void inviteByEmail(int projectId, String email) {
        System.out.println("Đang gửi lời mời dự án " + projectId + " tới: " + email);
    }

    public String generateInviteLink(int projectId) {
        Project project = projectDAO.findById(projectId);
        if(project != null){
            return project.getInviteCode();
        }
        return null;
    }

    public boolean joinByToken(String token, int userId){
        Project project = projectDAO.findByInviteCode(token);
        if (project != null) {
        return userProjectDAO.addMemberToProject(userId, project.getId());
    }
        return false;
    }

    public boolean isNameDuplicate(int currentUserId, String name) {
            if (name == null || name.isEmpty()) return false;
            return projectDAO.isProjectNameExists(currentUserId, name.trim());
    }
}

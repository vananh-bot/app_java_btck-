package Service;

import DAO.InviteDAO;
import DAO.ProjectDAO;
import DAO.UserProjectDAO;

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

    public void createProject(String name, String description, int userId) {

    }

    public void inviteByEmail(int projectId, String email) {

    }

    public void generateInviteLink(int projectId) {

    }

    public void joinByToken(String token, int userId) {

    }
}

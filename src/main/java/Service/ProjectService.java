package Service;

import DAO.InviteDAO;
import DAO.ProjectDAO;
import DAO.UserProjectDAO;
import DAO.TaskDAO; // Thêm import TaskDAO
import DTO.ProjectCardDTO; // Thêm import DTO
import Model.Project;
import Model.Task;

import java.util.ArrayList;
import java.util.List;

public class ProjectService {
    private ProjectDAO projectDAO;
    private UserProjectDAO userProjectDAO;
    private InviteDAO inviteDAO;
    private TaskDAO taskDAO;

    public ProjectService(ProjectDAO projectDAO,
                          UserProjectDAO userProjectDAO,
                          InviteDAO inviteDAO,
                          TaskDAO taskDAO) {

        this.projectDAO = projectDAO;
        this.userProjectDAO = userProjectDAO;
        this.inviteDAO = inviteDAO;
        this.taskDAO = taskDAO;
    }
    public List<ProjectCardDTO> getDashboardProjects(int userId) {
        List<ProjectCardDTO> dtoList = new ArrayList<>();

        List<Project> rawProjects = projectDAO.findByUserId(userId);

        for (Project p : rawProjects) {
            // ⚡ chỉ trả project, chưa load task
            dtoList.add(new ProjectCardDTO(p, 0, 0, 0));
        }

        return dtoList;
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

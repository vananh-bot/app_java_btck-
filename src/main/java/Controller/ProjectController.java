package Controller;

import DAO.InviteDAO;
import DAO.ProjectDAO;
import DAO.UserProjectDAO;
import Service.ProjectService;

public class ProjectController {
    private ProjectService projectService;

    public ProjectController() {
        this.projectService = new ProjectService(new ProjectDAO(), new UserProjectDAO(), new InviteDAO());
    }

    private int projectId;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    public void setProjectId(int projectId){
        this.projectId = projectId;
    }
}

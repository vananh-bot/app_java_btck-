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
    public List<ProjectCardDTO> sortByScore(List<ProjectCardDTO> list) {
        list.sort((a, b) -> {

            int totalA = a.getTodoCount() + a.getInProgressCount() + a.getDoneCount();
            int totalB = b.getTodoCount() + b.getInProgressCount() + b.getDoneCount();

            boolean isDoneA = totalA > 0 && a.getDoneCount() == totalA;
            boolean isDoneB = totalB > 0 && b.getDoneCount() == totalB;

            //  Rule 1: project DONE hết luôn xuống dưới
            if (isDoneA && !isDoneB) return 1;
            if (!isDoneA && isDoneB) return -1;

            //  Rule 2: chưa done hết → sort theo điểm
            int scoreA = a.getTodoCount() * 2 + a.getInProgressCount() * 3;
            int scoreB = b.getTodoCount() * 2 + b.getInProgressCount() * 3;

            return Integer.compare(scoreB, scoreA); // giảm dần
        });

        return list;
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

package Service;

import Cache.ProjectCache;
import DAO.ProjectDAO;
import DAO.UserProjectDAO;
import DAO.TaskDAO; // Thêm import TaskDAO
import Model.Project;
import DAO.NotificationDAO;
import DAO.UserDAO;

import java.util.List;

import java.util.UUID;

import DTO.ProjectDashboardDTO;


public class ProjectService {
    private ProjectDAO projectDAO;
    private UserProjectDAO userProjectDAO;
    private ProjectCache projectCache = ProjectCache.getInstance();

    public ProjectService(ProjectDAO projectDAO,
                          UserProjectDAO userProjectDAO) {

        this.projectDAO = projectDAO;
        this.userProjectDAO = userProjectDAO;
    }

    public ProjectService(ProjectDAO projectDAO) {
        this.projectDAO = projectDAO;
    }

    public List<ProjectDashboardDTO> getAllMyProjects(int userId) {
        return projectDAO.getAllProjectCardsWithTaskCount(userId);
    }

    public List<ProjectDashboardDTO> sortByScore(List<ProjectDashboardDTO> list) {
        list.sort((a, b) -> {

            int totalA = a.getToDoCount() + a.getInProgressCount() + a.getDoneCount();
            int totalB = b.getToDoCount() + b.getInProgressCount() + b.getDoneCount();

            boolean isDoneA = totalA > 0 && a.getDoneCount() == totalA;
            boolean isDoneB = totalB > 0 && b.getDoneCount() == totalB;

            //  Rule 1: project DONE hết luôn xuống dưới
            if (isDoneA && !isDoneB) return 1;
            if (!isDoneA && isDoneB) return -1;

            //  Rule 2: chưa done hết → sort theo điểm
            int scoreA = a.getToDoCount() * 2 + a.getInProgressCount() * 3;
            int scoreB = b.getToDoCount() * 2 + b.getInProgressCount() * 3;

            return Integer.compare(scoreB, scoreA); // giảm dần
        });

        return list;
    }
    public int createProject(String name, String description, int userId) {
        if (projectDAO.isProjectNameExists(userId, name)) {
            throw new IllegalArgumentException("Tên dự án đã tồn tại!");
        }

        Project project = new Project();
        project.setName(name);
        project.setDescription(description);
        project.setOwnerId(userId);

        String inviteCode = UUID.randomUUID().toString().substring(0, 10).toUpperCase();
        project.setInviteCode(inviteCode);

        int projectId = projectDAO.insert(project);

        if (projectId > 0) {
            boolean added =  userProjectDAO.addMemberToProject(userId, projectId);
            if(added) return projectId;
        }

        return -1;
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
        System.out.println("=== JOIN PROJECT CALLED ===");

        Project project = projectDAO.findByInviteCode(token);

        if (project == null) {
            System.out.println(" Project NOT FOUND with token: " + token);
            return false;
        }

        System.out.println("Found project: " + project.getId());

        boolean added = userProjectDAO.addMemberToProject(userId, project.getId());

        if (!added) {
            System.out.println("Add member FAILED");
            return false;
        }

        System.out.println("✔ Member added SUCCESS");

        // ===== TẠO NOTIFICATION =====
        NotificationDAO notificationDAO = new NotificationDAO();
        UserDAO userDAO = new UserDAO();

        String userName = userDAO.findById(userId).getName();

        List<Integer> memberIds = projectDAO.getMemberIds(project.getId());

        System.out.println("Members: " + memberIds);


        System.out.println("=== JOIN DONE ===");

        return true;
    }

    public boolean isNameDuplicate(int currentUserId, String name) {
            if (name == null || name.isEmpty()) return false;
            return projectDAO.isProjectNameExists(currentUserId, name.trim());
    }

    public String getProjectName(int projectId){
        ProjectDashboardDTO p = projectCache.get(projectId);
        if(p != null) return p.getName();

        Project project = projectDAO.findById(projectId);
        if (project == null) return null;

        ProjectDashboardDTO dto = new ProjectDashboardDTO();
        dto.setId(project.getId());
        dto.setName(project.getName());

        projectCache.put(dto);

        return project.getName();
    }
    public String getDescriptionByProjectId(int projectId){
        return projectDAO.getDescriptionByProjectId(projectId);
    }
    public void updateDescription(int projectId, String description){
        projectDAO.updateDescription(projectId, description);
    }
    public String convertToPreviewDescription(String description){
        if(description == null) {
            return "Không có mô tả dự án";
        }

        return description.length() < 120 ? description : description.substring(0, 120) + "...";
    }
}

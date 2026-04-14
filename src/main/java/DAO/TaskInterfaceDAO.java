package DAO;

import java.util.List;
import Enum.Priority;
import Enum.TaskStatus;

public interface TaskInterfaceDAO<T> {
    int insert(T t);
    int update(T t);
    boolean deleteById(int id);
    boolean existsByTitleAndProject(String title, int projectId);

    T getById(int id);

    //lấy danh sách các công việc trong dự án
    List<T> getTasksByProjectId(int projectId);
    List<T> getTasksByPriority(Priority priority);
    List<T> getTasksBySearch(String title);
    List<T> getUpcomingDeadlines();
    List<T> getTasksByStatus(TaskStatus status);
}

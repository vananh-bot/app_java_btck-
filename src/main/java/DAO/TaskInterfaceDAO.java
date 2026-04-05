package DAO;

import java.util.List;

public interface TaskInterfaceDAO<T> {
    int insert(T t);
    int edit(T t);
    int deleteById(int id);

    T getById(int id);

    //lấy danh sách các công việc trong dự án
    List<T> getTasksByProjectId(int projectId);
    List<T> getTasksByPriority(String priority);
    List<T> getTasksBySearch(String name);
    List<T> getUpcomingDeadlines();
    List<T> getTasksByStatus(String status);
}

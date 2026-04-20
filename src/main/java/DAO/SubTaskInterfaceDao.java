package DAO;

import Model.SubTask;

import java.util.List;

public interface SubTaskInterfaceDao {
    boolean insert(SubTask subTask);
    boolean update(SubTask subTask);
    boolean delete(int id);
    List<SubTask> findByTaskId(int taskId);
}

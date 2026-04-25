package DAO;

import Model.UserProject;

import java.sql.Connection;
import java.util.List;
import Enum.Role;

public interface UserProjectDAOInterface {

    boolean insert(UserProject up);
    // ➕ thêm user vào project

    boolean delete(int userId, int projectId);
    // ❌ xoá user khỏi project

    List<UserProject> findByProjectId(int projectId);
    // 🔥 lấy danh sách member của project

    List<UserProject> findByUserId(int userId);
    // 🔥 lấy danh sách project của user (kèm role)

    UserProject findOne(int userId, int projectId);
    // 🔍 tìm 1 record cụ thể

    boolean exists(int userId, int projectId);
    // 🔥 check user đã join chưa (tránh duplicate)
    boolean addMember(Connection conn, int userId, int projectId);

    boolean addUserToProject(Connection conn, int userId, int projectId, Role role);
}
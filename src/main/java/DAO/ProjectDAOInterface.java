package DAO;

import Model.Project;
import java.util.List;

public interface ProjectDAOInterface {

    int insert(Project project);
    // thêm project → trả về id (AUTO_INCREMENT)

    boolean update(Project project);
    // sửa project → trả true nếu thành công

    boolean delete(int id);
    // xoá project theo id

    Project findById(int id);
    // tìm 1 project theo id

    List<Project> findAll();
    // lấy tất cả project (dùng test/admin)

    List<Project> findByUserId(int userId);
    // 🔥 load dashboard (project user tham gia)

    Project findByInviteCode(String code);
    // tìm project bằng inviteCode (join project)
}
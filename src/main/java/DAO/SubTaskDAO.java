package DAO;

import Model.SubTask;
import database.JDBCUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SubTaskDAO implements SubTaskInterfaceDao {

    @Override
    public boolean insert(SubTask subTask) {
        String sql = "INSERT INTO sub_task(task_id, title, completed) VALUES (?, ?, ?)";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, subTask.getTaskId());
            ps.setString(2, subTask.getTitle());
            ps.setBoolean(3, subTask.isCompleted());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(SubTask subTask) {
        String sql = "UPDATE sub_task SET title = ?, completed = ? WHERE id = ?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, subTask.getTitle());
            ps.setBoolean(2, subTask.isCompleted());
            ps.setInt(3, subTask.getId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM sub_task WHERE id = ?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<SubTask> findByTaskId(int taskId) {
        List<SubTask> list = new ArrayList<>();

        String sql = "SELECT * FROM sub_task WHERE task_id = ?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, taskId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                SubTask s = new SubTask();
                s.setId(rs.getInt("id"));
                s.setTaskId(rs.getInt("task_id"));
                s.setTitle(rs.getString("title"));
                s.setCompleted(rs.getBoolean("completed"));

                list.add(s);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean updateStatus(int id, boolean completed) {
        String sql = "UPDATE sub_task SET completed = ? WHERE id = ?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBoolean(1, completed);
            ps.setInt(2, id);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

package DAO;

import Model.Project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Model.ProjectDashboardDTO;
import database.JDBCUtil;

public class ProjectDAO {

    public List<ProjectDashboardDTO> getDashboardProject(int userId){
        List<ProjectDashboardDTO> projects = new ArrayList<>();

        String sql = "SELECT \n" +
                "    p.id,\n" +
                "    p.name,\n" +
                "\n" +
                "    SUM(CASE WHEN t.status = 'TODO' THEN 1 ELSE 0 END) AS toDoCount,\n" +
                "    SUM(CASE WHEN t.status = 'IN_PROGRESS' THEN 1 ELSE 0 END) AS inProgressCount,\n" +
                "    SUM(CASE WHEN t.status = 'DONE' THEN 1 ELSE 0 END) AS doneCount\n" +
                "\n" +
                "FROM projects p\n" +
                "LEFT JOIN tasks t ON p.id = t.project_id\n" +
                "\n" +
                "WHERE p.owner_id = ?\n" +
                "\n" +
                "GROUP BY p.id, p.name\n" +
                "\n" +
                "ORDER BY\n" +
                "SUM(\n" +
                "    CASE \n" +
                "        WHEN t.status = 'DONE' THEN 0\n" +
                "        WHEN t.status = 'IN_PROGRESS' THEN\n" +
                "            (CASE t.priority \n" +
                "                WHEN 'HIGH' THEN 3\n" +
                "                WHEN 'MEDIUM' THEN 2\n" +
                "                WHEN 'LOW' THEN 1\n" +
                "            END) * 2\n" +
                "        WHEN t.status = 'TODO' THEN\n" +
                "            (CASE t.priority \n" +
                "                WHEN 'HIGH' THEN 3\n" +
                "                WHEN 'MEDIUM' THEN 2\n" +
                "                WHEN 'LOW' THEN 1\n" +
                "            END)\n" +
                "    END\n" +
                ") DESC\n" +
                "\n" +
                "LIMIT 20;";

        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);){

            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int toDoCount = rs.getInt("toDoCount");
                int inProgressCount = rs.getInt("inProgressCount");
                int doneCount = rs.getInt("doneCount");

                ProjectDashboardDTO project = new ProjectDashboardDTO(id, name, toDoCount, inProgressCount, doneCount);

                projects.add(project);
            }


        } catch (SQLException e) {
            throw new RuntimeException("getDashboardProject failed");
        }
        return projects;
    }
}

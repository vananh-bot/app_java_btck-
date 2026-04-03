package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCUtil {

    public static Connection getConnection() {
        Connection c = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String host = "bohvsi0tijufa3uevugn-mysql.services.clever-cloud.com";
            String dbName = "bohvsi0tijufa3uevugn";
            String username = "uyowrnohc9lttn87";
            String password = "8c1BnyChOmV1EGnExXGq";
            String port = "3306";


            String url = "jdbc:mysql://" + host + ":" + port + "/" + dbName +
                    "?useSSL=false" +
                    "&allowPublicKeyRetrieval=true" +
                    "&serverTimezone=UTC" +
                    "&characterEncoding=UTF-8";


            c = DriverManager.getConnection(url, username, password);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return c;
    }


    public static void closeConnection(Connection c) {
        try {
            if (c != null && !c.isClosed()) {
                c.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
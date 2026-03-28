package Test;

import java.sql.Connection;

public class Main {
    static void main(String[] args) {
        Connection connection = database.JDBCUtil.getConnection();
        System.out.println(connection);
        database.JDBCUtil.closeConnection(connection);
        System.out.println(connection);
    }
}

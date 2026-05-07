package database;

import Utils.NetworkUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCUtil {

    private static HikariDataSource dataSource;

    static {

        HikariConfig config = new HikariConfig();

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

        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);

        config.setConnectionTimeout(3000);
        config.setValidationTimeout(2000);
        config.setMaximumPoolSize(3);
        config.setMinimumIdle(1);

        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        if (!NetworkUtil.isOnline()) {
            throw new SQLException("NO_INTERNET");
        }

        return dataSource.getConnection();
    }
}
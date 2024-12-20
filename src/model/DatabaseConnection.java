package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/mydb";
    private static final String USER = "root";
    //static final String PASSWORD = System.getenv("DB_PASSWORD"); // 환경변수 사용
    private static final String PASSWORD = "2022125052"; // 환경변수 사용 x

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Database connection failed.");
            return null;
        }
    }
}

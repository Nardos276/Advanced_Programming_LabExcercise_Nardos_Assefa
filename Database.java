import java.sql.*;

public class Database {

    private static final String URL = "jdbc:sqlite:chat.db";
    

    public static Connection connect() {

    try {

        Class.forName("org.sqlite.JDBC");

        return DriverManager.getConnection(URL);

    } catch (Exception e) {

        System.out.println(e.getMessage());

        return null;
    }
}

    public static void createTable() {

    String sql = "CREATE TABLE IF NOT EXISTS messages (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "username TEXT," +
            "message TEXT" +
            ");";

    Connection conn = connect();

    if (conn == null) {
        System.out.println("Database connection failed!");
        return;
    }

    try (Statement stmt = conn.createStatement()) {

        stmt.execute(sql);

    } catch (SQLException e) {

        System.out.println(e.getMessage());
    }
}

    public static void saveMessage(String username, String message) {

        String sql = "INSERT INTO messages(username, message) VALUES(?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, message);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}

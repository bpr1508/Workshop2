package pl.coderslab;

import org.mindrot.jbcrypt.BCrypt;
import pl.coderslab.entity.User;

import java.sql.*;
import java.util.Scanner;

public class UserDao {
    private static final String CREATE_USER_QUERY =
            "INSERT INTO users(email, username, password) VALUES (?, ?, ?)";
    private static final String READ_USER_QUERY =
            "SELECT * FROM users WHERE id=?";
    private static final String COUNT_ROWS =
            "SELECT COUNT(*) AS COUNT FROM users WHERE id=?";

    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public User create(User user) {
        try (Connection conn = DbUtil.getConnection()) {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Wprowadź e-mail: ");
            user.setEmail(scanner.nextLine());
            System.out.print("Wprowadź nazwę użytkownika: ");
            user.setUsername(scanner.nextLine());
            System.out.print("Wprowadź hasło: ");
            user.setPassword(scanner.nextLine());
            PreparedStatement statement =
                    conn.prepareStatement(CREATE_USER_QUERY, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getUsername());
            statement.setString(3, hashPassword(user.getPassword()));
            statement.executeUpdate();
            //Pobieramy wstawiony do bazy identyfikator, a następnie ustawiamy id obiektu user.
            ResultSet resultSet = statement.getGeneratedKeys();

            if (resultSet.next()) {

                user.setId(resultSet.getInt(1));
            }
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public User read(int userId) {
        String[] columnNames = {"id", "email", "username", "password"};
        User user = new User(0, "", "", "");
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement statement = conn.prepareStatement(READ_USER_QUERY, Statement.RETURN_GENERATED_KEYS);) {
            statement.setString(1, String.valueOf(userId));
            ResultSet resultSet = statement.executeQuery();
            int rows = getQueryRowCount(userId);
            if (rows > 0) {
                while (resultSet.next()) {
                    user.setId(resultSet.getInt(columnNames[0]));
                    user.setEmail(resultSet.getString(columnNames[1]));
                    user.setUsername(resultSet.getString(columnNames[2]));
                    user.setPassword(resultSet.getString(columnNames[3]));
                }
                return user;
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getQueryRowCount(int id) throws SQLException {
        int size = 0;
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement statement = conn.prepareStatement(COUNT_ROWS, Statement.RETURN_GENERATED_KEYS);) {
            statement.setInt(1, id);
            ResultSet standardRS = statement.executeQuery();

            while (standardRS.next()) {
                size = standardRS.getInt("count");
            }
            return size;
        } catch(SQLException e){
            e.printStackTrace();
            return 0;
        }

    }
}

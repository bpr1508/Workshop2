package pl.coderslab;


import org.mindrot.jbcrypt.BCrypt;
import pl.coderslab.entity.User;

import java.sql.*;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserDao {
    private static final String CREATE_USER_QUERY =
            "INSERT INTO users(email, username, password) VALUES (?, ?, ?)";
    private static final String READ_USER_QUERY =
            "SELECT * FROM users WHERE id=?";
    private static final String COUNT_ROWS =
            "SELECT COUNT(*) AS count FROM users WHERE id=?";
    private static final String UPDATE_USER_QUERY =
            "UPDATE users SET email=?, username=?, password=? WHERE id=?";
    private static final String DELETE_USER_QUERY =
            "DELETE FROM users WHERE id=?";
    private static final String FIND_ALL_USERS_QUERY =
            "SELECT * FROM users";
    private static final String[] COLUMN_NAMES = {"id", "email", "username", "password"};

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
            System.out.println("Stworzono nowego użytkownika.");
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
        User user = new User(0, "", "", "");
        try (Connection conn = DbUtil.getConnection();) {
            PreparedStatement statement = conn.prepareStatement(READ_USER_QUERY, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, String.valueOf(userId));
            ResultSet resultSet = statement.executeQuery();
            int rows = getQueryRowCount(userId);
            if (rows > 0) {
                while (resultSet.next()) {
                    user.setId(resultSet.getInt(COLUMN_NAMES[0]));
                    user.setEmail(resultSet.getString(COLUMN_NAMES[1]));
                    user.setUsername(resultSet.getString(COLUMN_NAMES[2]));
                    user.setPassword(resultSet.getString(COLUMN_NAMES[3]));
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

    public void update(User user) {
        Scanner scanner = new Scanner(System.in);
        user.setId(inputId());
        System.out.print("Wprowadź nowy e-mail: ");
        user.setEmail(scanner.nextLine());
        System.out.print("Wprowadź nową nazwę użytkownika: ");
        user.setUsername(scanner.nextLine());
        System.out.print("Wprowadź nowe hasło: ");
        user.setPassword(scanner.nextLine());
        try (Connection conn = DbUtil.getConnection();) {
            PreparedStatement statement = conn.prepareStatement(UPDATE_USER_QUERY, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getUsername());
            statement.setString(3, hashPassword(user.getPassword()));
            statement.setInt(4, user.getId());
            int resultSet = statement.executeUpdate();
            if (resultSet > 0) {
                System.out.println("Edytowano rekord. :-)");
            } else {
                System.out.println("Nie ma takiego id...");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int userId) {
        try (Connection conn = DbUtil.getConnection();
        ) {
            PreparedStatement statement = conn.prepareStatement(DELETE_USER_QUERY, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, userId);
            int resultSet = statement.executeUpdate();
            if (resultSet > 0) {
                System.out.println("Usunięto wiersz.");
            } else {
                System.out.println("Nie ma takiego id...");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User[] findAll() {
        try (Connection conn = DbUtil.getConnection()) {
            User[] users = new User[0];
            PreparedStatement statement = conn.prepareStatement(FIND_ALL_USERS_QUERY, Statement.RETURN_GENERATED_KEYS);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                User user = new User(0, "", "", "");
                user.setId(resultSet.getInt("id"));
                user.setUsername(resultSet.getString("email"));
                user.setEmail(resultSet.getString("username"));
                user.setPassword(resultSet.getString("password"));
                users = addToArray(user, users);
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getQueryRowCount(int id) throws SQLException {
        try (Connection conn = DbUtil.getConnection();
        ) {
            PreparedStatement statement = conn.prepareStatement(COUNT_ROWS, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, id);
            ResultSet standardRS = statement.executeQuery();
            int size = 0;
            while (standardRS.next()) {
                size = standardRS.getInt("count");
            }
            return size;
        }
    }

    public int inputId() {
        System.out.print("Podaj id: ");
        Scanner scan = new Scanner(System.in);
        while (!scan.hasNextInt()) {
            System.out.print("Nieprawidłowe dane. Podaj jeszcze raz id: ");
            scan.nextLine();
        }
        int number = scan.nextInt();
        return number;
    }

    private User[] addToArray(User u, User[] users) {

        User[] tmpUsers = Arrays.copyOf(users, users.length + 1); // Tworzymy kopię tablicy powiększoną o 1.

        tmpUsers[users.length] = u; // Dodajemy obiekt na ostatniej pozycji.

        return tmpUsers; // Zwracamy nową tablicę.

    }
}
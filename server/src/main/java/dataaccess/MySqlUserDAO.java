package dataaccess;

import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySqlUserDAO implements UserDAO {
    public MySqlUserDAO() throws DataAccessException {
        DatabaseManager.createDatabase();
    }

    public void createUser(UserData user) throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String query = "SELECT username FROM users WHERE username = ?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, user.username());
            ResultSet result = statement.executeQuery();
            if (result.isBeforeFirst()) {
                throw new DataAccessException("Username taken");
            } else {
                String insert = "INSERT INTO users (username, password, email) VALUES (?, ?, ?);";
                PreparedStatement insertStatement = connection.prepareStatement(insert);
                insertStatement.setString(1, user.username());
                insertStatement.setString(2, user.password());
                insertStatement.setString(3, user.email());
                insertStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public UserData getUser(String username) throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String query = "SELECT * FROM users WHERE username = ?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            ResultSet result = statement.executeQuery();
            if (result.isBeforeFirst()) {
                result.next();
                return new UserData(username, result.getString("password"), result.getString("email"));
            } else {
                throw new DataAccessException("User not found");
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void clearAll() throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("TRUNCATE TABLE users;");
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}

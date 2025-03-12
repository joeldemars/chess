package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

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
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, user.username());
                try (ResultSet result = statement.executeQuery()) {
                    if (result.isBeforeFirst()) {
                        throw new DataAccessException("Username taken");
                    } else {
                        String insert = "INSERT INTO users (username, password, email) VALUES (?, ?, ?);";
                        try (PreparedStatement insertStatement = connection.prepareStatement(insert)) {
                            insertStatement.setString(1, user.username());
                            insertStatement.setString(2, BCrypt.hashpw(user.password(), BCrypt.gensalt()));
                            insertStatement.setString(3, user.email());
                            insertStatement.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public UserData getUser(String username) throws DataAccessException {
        throw new DataAccessException("Not implemented");
    }

    public void clearAll() throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("TRUNCATE TABLE users;")) {
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}

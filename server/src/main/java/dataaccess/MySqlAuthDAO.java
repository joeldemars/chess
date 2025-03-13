package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySqlAuthDAO implements AuthDAO {
    public MySqlAuthDAO() throws DataAccessException {
        DatabaseManager.createDatabase();
    }

    public void createAuth(AuthData auth) throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String query = "SELECT token FROM auths WHERE token = ?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, auth.authToken());
            ResultSet result = statement.executeQuery();
            if (result.isBeforeFirst()) {
                throw new DataAccessException("Token already exists");
            } else {
                String insert = "INSERT INTO auths (token, username) VALUES (?, ?);";
                PreparedStatement insertStatement = connection.prepareStatement(insert);
                insertStatement.setString(1, auth.authToken());
                insertStatement.setString(2, auth.username());
                insertStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String query = "SELECT * FROM auths WHERE token = ?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, authToken);
            ResultSet result = statement.executeQuery();
            if (result.isBeforeFirst()) {
                result.next();
                return new AuthData(authToken, result.getString("username"));
            } else {
                throw new DataAccessException("Auth not found");
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void deleteAuth(AuthData auth) throws DataAccessException {
        try {
            getAuth(auth.authToken());
            try (Connection connection = DatabaseManager.getConnection()) {
                PreparedStatement statement = connection.prepareStatement("DELETE FROM auths WHERE token = ?;");
                statement.setString(1, auth.authToken());
                statement.executeUpdate();
            }
        } catch (DataAccessException e) {
            throw new DataAccessException("Auth not found");
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    public void clearAll() throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("TRUNCATE TABLE auths;");
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}

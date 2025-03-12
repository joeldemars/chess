package dataaccess;

import com.google.gson.Gson;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

public class MySqlGameDAO implements GameDAO {
    private Gson gson = new Gson();

    public MySqlGameDAO() throws DataAccessException {
        DatabaseManager.createDatabase();
    }

    public void createGame(GameData game) throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String query = "SELECT id FROM games WHERE id = ?;";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, game.gameID());
                try (ResultSet result = statement.executeQuery()) {
                    if (result.isBeforeFirst()) {
                        throw new DataAccessException("Game already exists");
                    } else {
                        String insert = "INSERT INTO games (id, white_username, black_username, game_name, game) "
                                + "VALUES (?, ?, ?, ?, ?);";
                        try (PreparedStatement insertStatement = connection.prepareStatement(insert)) {
                            insertStatement.setInt(1, game.gameID());
                            insertStatement.setString(2, game.whiteUsername());
                            insertStatement.setString(3, game.blackUsername());
                            insertStatement.setString(4, game.gameName());
                            String s = gson.toJson(game.game());
                            insertStatement.setString(5, gson.toJson(game.game()));
                            insertStatement.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public GameData getGame(int gameID) throws DataAccessException {
        throw new DataAccessException("Not implemented");
//        try (Connection connection = DatabaseManager.getConnection()) {
//            String query = "SELECT * FROM users WHERE username = ?;";
//            try (PreparedStatement statement = connection.prepareStatement(query)) {
//                statement.setString(1, username);
//                try (ResultSet result = statement.executeQuery()) {
//                    if (result.isBeforeFirst()) {
//                        result.next();
//                        return new UserData(username, result.getString("password"), result.getString("email"));
//                    } else {
//                        throw new DataAccessException("User not found");
//                    }
//                }
//            }
//        } catch (SQLException e) {
//            throw new DataAccessException(e.getMessage());
//        }
    }

    public Collection<GameData> listGames() throws DataAccessException {
        throw new DataAccessException("Not implemented");
    }

    public void updateGame(int gameID, GameData game) throws DataAccessException {
        throw new DataAccessException("Not implemented");
    }

    public void clearAll() throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("TRUNCATE TABLE games;")) {
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}

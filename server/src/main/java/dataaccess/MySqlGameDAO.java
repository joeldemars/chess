package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
        try (Connection connection = DatabaseManager.getConnection()) {
            String query = "SELECT * FROM games WHERE id = ?;";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, gameID);
                try (ResultSet result = statement.executeQuery()) {
                    if (result.isBeforeFirst()) {
                        result.next();
                        return new GameData(
                                gameID,
                                result.getString("white_username"),
                                result.getString("black_username"),
                                result.getString("game_name"),
                                gson.fromJson(result.getString("game"), ChessGame.class)
                        );
                    } else {
                        throw new DataAccessException("Game not found");
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public Collection<GameData> listGames() throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String query = "SELECT * FROM games;";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                try (ResultSet result = statement.executeQuery()) {
                    ArrayList<GameData> games = new ArrayList<>();
                    while (result.next()) {
                        games.add(new GameData(
                                result.getInt("id"),
                                result.getString("white_username"),
                                result.getString("black_username"),
                                result.getString("game_name"),
                                gson.fromJson(result.getString("game"), ChessGame.class)
                        ));
                    }
                    return games;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void updateGame(int gameID, GameData game) throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String query = "SELECT * FROM games WHERE id = ?;";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, gameID);
                try (ResultSet result = statement.executeQuery()) {
                    if (result.isBeforeFirst()) {
                        String update = "UPDATE games "
                                + "SET white_username = ?, black_username = ?, game_name = ?, game = ? "
                                + "WHERE id = ?";
                        try (PreparedStatement updateStatement = connection.prepareStatement(update)) {
                            updateStatement.setString(1, game.whiteUsername());
                            updateStatement.setString(2, game.blackUsername());
                            updateStatement.setString(3, game.gameName());
                            updateStatement.setString(4, gson.toJson(game.game()));
                            updateStatement.setInt(5, gameID);
                            updateStatement.executeUpdate();
                        }
                    } else {
                        throw new DataAccessException("Game not found");
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
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

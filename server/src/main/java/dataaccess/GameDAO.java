package dataaccess;

import model.GameData;

import java.util.ArrayList;

public interface GameDAO {
    void createGame(GameData game) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    ArrayList<GameData> listGames() throws DataAccessException;

    GameData updateGame(int gameID, GameData game) throws DataAccessException;

    void clearAll() throws DataAccessException;
}

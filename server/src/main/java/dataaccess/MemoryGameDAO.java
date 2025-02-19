package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    private final HashMap<Integer, GameData> games = new HashMap<>();

    @Override
    public void createGame(GameData game) throws DataAccessException {
        if (games.containsKey(game.gameID())) {
            throw new DataAccessException("Game already exists");
        } else {
            games.put(game.gameID(), game);
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        GameData game = games.get(gameID);

        if (game != null) {
            return game;
        } else {
            throw new DataAccessException("Game not found");
        }
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return games.values();
    }

    @Override
    public void updateGame(int gameID, GameData game) throws DataAccessException {
        if (games.containsKey(gameID)) {
            games.put(gameID, game);
        } else {
            throw new DataAccessException("Game not found");
        }
    }

    @Override
    public void clearAll() throws DataAccessException {
        games.clear();
    }
}

package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryGameDAO;
import model.GameData;

public class GameService {
    private MemoryGameDAO games = new MemoryGameDAO();
    private int gameID = 0;

    public ListGamesResult listGames() throws InternalServerErrorException {
        try {
            return new ListGamesResult(games.listGames().toArray(new GameData[games.listGames().size()]));
        } catch (DataAccessException e) {
            throw new InternalServerErrorException("Error: unable to list games");
        }
    }

    public int createGame(String gameName) throws BadRequestException, InternalServerErrorException {
        try {
            if (games.listGames().stream().anyMatch((game) -> game.gameName().equals(gameName))) {
                throw new BadRequestException("Error: bad request");
            } else {
                try {
                    games.createGame(new GameData(gameID, "", "", gameName, new ChessGame()));
                    return gameID++;
                } catch (DataAccessException e) {
                    throw new InternalServerErrorException("Error: failed to create game");
                }
            }
        } catch (DataAccessException e) {
            throw new InternalServerErrorException("Error: failed to create game");
        }
    }

    public void joinGame(JoinGameRequest request)
            throws BadRequestException, ForbiddenException, InternalServerErrorException {
        throw new InternalServerErrorException("Not implemented");
    }
}
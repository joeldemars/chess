package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;

public class GameService {
    private GameDAO games;
    private AuthDAO auths;
    private int gameID;

    GameService(GameDAO games, AuthDAO auths) {
        this.games = games;
        this.auths = auths;
        this.gameID = 0;
    }

    public ListGamesResult listGames(String authToken)
            throws UnauthorizedException, InternalServerErrorException {
        if (!authenticate(authToken)) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        try {
            return new ListGamesResult(games.listGames().toArray(new GameData[games.listGames().size()]));
        } catch (DataAccessException e) {
            throw new InternalServerErrorException("Error: unable to list games");
        }
    }

    public int createGame(String gameName, String authToken)
            throws BadRequestException, UnauthorizedException, InternalServerErrorException {
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

    public void joinGame(JoinGameRequest request, String authToken)
            throws BadRequestException, UnauthorizedException, ForbiddenException, InternalServerErrorException {
        throw new InternalServerErrorException("Not implemented");
    }

    /**
     * Return true if the given authToken is valid and false otherwise.
     */
    private boolean authenticate(String authToken) {
        try {
            auths.getAuth(authToken);
        } catch (DataAccessException e) {
            return false;
        }
        return true;
    }
}
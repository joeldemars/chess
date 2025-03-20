package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import api.exception.BadRequestException;
import api.exception.ForbiddenException;
import api.exception.InternalServerErrorException;
import api.exception.UnauthorizedException;
import api.CreateGameRequest;
import api.JoinGameRequest;
import api.CreateGameResult;
import api.ListGamesResult;

public class GameService {
    private GameDAO games;
    private AuthDAO auths;
    private int gameID;

    public GameService(GameDAO games, AuthDAO auths) {
        this.games = games;
        this.auths = auths;
        this.gameID = 1;
    }

    public ListGamesResult listGames(String authToken)
            throws UnauthorizedException, InternalServerErrorException {
        authenticate(authToken);
        try {
            return new ListGamesResult(games.listGames().toArray(new GameData[games.listGames().size()]));
        } catch (DataAccessException e) {
            throw new InternalServerErrorException("Error: unable to list games");
        }
    }

    public CreateGameResult createGame(CreateGameRequest request, String authToken)
            throws BadRequestException, UnauthorizedException, InternalServerErrorException {
        authenticate(authToken);
        try {
            if (games.listGames().stream().anyMatch((game) -> game.gameName().equals(request.gameName()))) {
                throw new BadRequestException("Error: bad request");
            } else {
                try {
                    games.createGame(new GameData(
                            gameID,
                            null,
                            null,
                            request.gameName(),
                            new ChessGame())
                    );
                    return new CreateGameResult(gameID++);
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
        AuthData auth = authenticate(authToken);
        GameData game;
        try {
            game = games.getGame(request.gameID());
        } catch (DataAccessException e) {
            throw new BadRequestException("Error: bad request");
        }
        try {
            if (request.playerColor() == ChessGame.TeamColor.WHITE) {
                if (game.whiteUsername() == null) {
                    games.updateGame(game.gameID(), new GameData(
                                    game.gameID(),
                                    auth.username(),
                                    game.blackUsername(),
                                    game.gameName(),
                                    game.game()
                            )
                    );
                } else {
                    throw new ForbiddenException("Error: already taken");
                }
            } else if (request.playerColor() == ChessGame.TeamColor.BLACK) {
                if (game.blackUsername() == null) {
                    games.updateGame(game.gameID(), new GameData(
                                    game.gameID(),
                                    game.whiteUsername(),
                                    auth.username(),
                                    game.gameName(),
                                    game.game()
                            )
                    );
                } else {
                    throw new ForbiddenException("Error: already taken");
                }
            } else {
                throw new BadRequestException("Error: bad request");
            }
        } catch (DataAccessException e) {
            throw new BadRequestException("Error: bad request");
        }
    }

    /**
     * Return the corresponding AuthData if the given authToken is valid.
     * Throw UnauthorizedException otherwise
     */
    private AuthData authenticate(String authToken) throws UnauthorizedException {
        try {
            return auths.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new UnauthorizedException("Error: unauthorized");
        }
    }
}
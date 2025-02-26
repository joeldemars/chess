package service;

import dataaccess.MemoryGameDAO;

public class GameService {
    private MemoryGameDAO games = new MemoryGameDAO();

    public ListGamesResult listGames() throws InternalServerErrorException {
        throw new InternalServerErrorException("Not implemented");
    }

    public int createGame(String gameName) throws BadRequestException, InternalServerErrorException {
        throw new InternalServerErrorException("Not implemented");
    }

    public void joinGame(JoinGameRequest request)
            throws BadRequestException, ForbiddenException, InternalServerErrorException {
        throw new InternalServerErrorException("Not implemented");
    }
}
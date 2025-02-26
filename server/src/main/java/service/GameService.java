package service;

import dataaccess.MemoryGameDAO;

public class GameService {
    private MemoryGameDAO games = new MemoryGameDAO();

    public ListGamesResult listGames(String authorization)
            throws UnauthorizedException, InternalServerErrorException {
        throw new InternalServerErrorException("Not implemented");
    }

    public int createGame(String gameName, String authorization)
            throws BadRequestException, UnauthorizedException, InternalServerErrorException {
        throw new InternalServerErrorException("Not implemented");
    }

    public void joinGame(JoinGameRequest request, String authorization)
            throws BadRequestException, UnauthorizedException, ForbiddenException, InternalServerErrorException {
        throw new InternalServerErrorException("Not implemented");
    }
}
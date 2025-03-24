package serverfacade;

import api.*;
import api.exception.*;

public class ServerFacade {
    private final String url;

    public ServerFacade(String url) {
        this.url = url;
    }

    public void clear() throws HttpErrorException {
        throw new RuntimeException("Not implemented");
    }

    public RegisterResult register(RegisterRequest request) throws HttpErrorException {
        throw new RuntimeException("Not implemented");
    }

    public LoginResult login(LoginRequest request) throws HttpErrorException {
        throw new RuntimeException("Not implemented");
    }

    public void logout(String authorization) throws HttpErrorException {
        throw new RuntimeException("Not implemented");
    }

    public ListGamesResult listGames(String authToken) throws HttpErrorException {
        throw new RuntimeException("Not implemented");
    }

    public CreateGameResult createGame(CreateGameRequest request, String authToken) throws HttpErrorException {
        throw new RuntimeException("Not implemented");
    }

    public void joinGame(JoinGameRequest request, String authToken) throws HttpErrorException {
        throw new RuntimeException("Not implemented");
    }
}

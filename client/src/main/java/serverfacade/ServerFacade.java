package serverfacade;

import api.*;
import api.exception.*;
import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;

public class ServerFacade {
    private final String url;
    private final Gson gson = new Gson();

    public ServerFacade(String url) {
        this.url = url;
    }

    public void clear() throws HttpErrorException {
        makeRequest("DELETE", "/db", null, null);
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

    private <T, U> U makeRequest(String method, String path, T request, Class<U> responseType) throws HttpErrorException {
        try {
            HttpURLConnection http = (HttpURLConnection) (new URI(url + path)).toURL().openConnection();
            http.setRequestMethod(method);
            if (request != null) {
                http.setDoOutput(true);
                http.addRequestProperty("Content-Type", "application/json");
                try (OutputStream output = http.getOutputStream()) {
                    output.write(gson.toJson(request).getBytes());
                }
            }
            http.connect();
            if (responseType == null) {
                return null;
            } else {
                try (InputStream input = http.getInputStream()) {
                    return gson.fromJson(new InputStreamReader(input), responseType);
                }
            }
        } catch (HttpErrorException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }
}

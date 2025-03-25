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
    private String authToken = null;

    public ServerFacade(String url) {
        this.url = url;
    }

    public void clear() throws HttpErrorException {
        makeRequest("DELETE", "/db", null, null);
    }

    public RegisterResult register(RegisterRequest request) throws HttpErrorException {
        RegisterResult result = makeRequest("POST", "/user", request, RegisterResult.class);
        authToken = result.authToken();
        return result;
    }

    public LoginResult login(LoginRequest request) throws HttpErrorException {
        LoginResult result = makeRequest("POST", "/session", request, LoginResult.class);
        authToken = result.authToken();
        return result;
    }

    public void logout() throws HttpErrorException {
        makeRequest("DELETE", "/session", null, null);
        this.authToken = null;
    }

    public ListGamesResult listGames() throws HttpErrorException {
        return makeRequest("GET", "/game", null, ListGamesResult.class);
    }

    public CreateGameResult createGame(CreateGameRequest request) throws HttpErrorException {
        return makeRequest("POST", "/game", request, CreateGameResult.class);
    }

    public void joinGame(JoinGameRequest request) throws HttpErrorException {
        makeRequest("PUT", "/game", request, null);
    }

    private <T, U> U makeRequest(String method, String path, T request, Class<U> responseType) throws HttpErrorException {
        try {
            HttpURLConnection http = (HttpURLConnection) (new URI(url + path)).toURL().openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            if (authToken != null) {
                http.addRequestProperty("authorization", authToken);
            }
            if (request != null) {
                http.addRequestProperty("Content-Type", "application/json");
                try (OutputStream output = http.getOutputStream()) {
                    output.write(gson.toJson(request).getBytes());
                }
            }
            http.connect();
            http.getResponseCode();
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
            throw new InternalServerErrorException(e.toString());
        }
    }
}

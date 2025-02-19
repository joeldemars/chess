package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {
    private final HashMap<String, String> usernames = new HashMap<>();

    public void createAuth(AuthData auth) throws DataAccessException {
        if (usernames.containsKey(auth.authToken())) {
            throw new DataAccessException("Token already exists");
        } else {
            usernames.put(auth.authToken(), auth.username());
        }
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        String username = usernames.get(authToken);

        if (username != null) {
            return new AuthData(authToken, username);
        } else {
            throw new DataAccessException("Token not found");
        }
    }

    public void deleteAuth(AuthData auth) throws DataAccessException {
        usernames.remove(auth.authToken());
    }

    public void clearAll() throws DataAccessException {
        usernames.clear();
    }
}

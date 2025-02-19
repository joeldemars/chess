package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    private final HashMap<String, String> passwords = new HashMap<>();
    private final HashMap<String, String> emails = new HashMap<>();

    @Override
    public void createUser(UserData user) throws DataAccessException {
        if (passwords.containsKey(user.username()) || emails.containsKey(user.username())) {
            throw new DataAccessException("Username already in use");
        }
        passwords.put(user.username(), user.password());
        emails.put(user.username(), user.email());
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        String password = passwords.get(username);
        String email = emails.get(username);
        if (password != null && email != null) {
            return new UserData(username, password, email);
        } else {
            throw new DataAccessException("User not found");
        }
    }

    @Override
    public void clearAll() {
        passwords.clear();
        emails.clear();
    }
}

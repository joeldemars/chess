package dataaccess;

import model.UserData;

public class MySqlUserDAO implements UserDAO {
    public void createUser(UserData user) throws DataAccessException {
        throw new DataAccessException("Not implemented");
    }

    public UserData getUser(String username) throws DataAccessException {
        throw new DataAccessException("Not implemented");
    }

    public void clearAll() throws DataAccessException {
        throw new DataAccessException("Not implemented");
    }
}

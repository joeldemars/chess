package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import service.exception.InternalServerErrorException;

public class ResetService {
    private UserDAO users;
    private AuthDAO auths;
    private GameDAO games;

    public ResetService(UserDAO users, AuthDAO auths, GameDAO games) {
        this.users = users;
        this.auths = auths;
        this.games = games;
    }

    public void clearDatabase() throws InternalServerErrorException {
        try {
            users.clearAll();
            auths.clearAll();
            games.clearAll();
        } catch (DataAccessException e) {
            throw new InternalServerErrorException("Error: failed to clear databases");
        }
    }
}

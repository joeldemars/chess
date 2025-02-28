package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

public class ResetService {
    private UserDAO users;
    private AuthDAO auths;
    private GameDAO games;

    ResetService(UserDAO users, AuthDAO auths, GameDAO games) {
    }

    public void clearDatabase() throws InternalServerErrorException {
        throw new InternalServerErrorException("Not implemented");
    }
}

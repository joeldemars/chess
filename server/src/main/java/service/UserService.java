package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {
    MemoryUserDAO users = new MemoryUserDAO();
    MemoryAuthDAO auths = new MemoryAuthDAO();

    public RegisterResult register(RegisterRequest request)
            throws BadRequestException, ForbiddenException, InternalServerErrorException {
        if (request.username().isEmpty() || request.password().isEmpty() || request.email().isEmpty()) {
            throw new BadRequestException("Error: bad request");
        }
        try {
            UserData user = users.getUser(request.username());
            throw new ForbiddenException("Error: already taken");
        } catch (DataAccessException e) {
            try {
                users.createUser(new UserData(request.username(), request.password(), request.email()));
                String authToken = UUID.randomUUID().toString();
                auths.createAuth(new AuthData(authToken, request.username()));
                return new RegisterResult(request.username(), authToken);
            } catch (DataAccessException e2) {
                throw new InternalServerErrorException("Error: failed to register user");
            }
        }
    }

    public LoginResult login(LoginRequest request) throws UnauthorizedException, InternalServerErrorException {
        try {
            UserData user = users.getUser(request.username());
            if (user.password().equals(request.password())) {
                String authToken = UUID.randomUUID().toString();
                try {
                    auths.createAuth(new AuthData(authToken, request.username()));
                    return new LoginResult(request.username(), authToken);
                } catch (DataAccessException e) {
                    throw new InternalServerErrorException("Error: failed to log user in");
                }
            } else {
                throw new UnauthorizedException("Error: unauthorized");
            }
        } catch (DataAccessException e) {
            throw new UnauthorizedException("Error: unauthorized");
        }
    }

    public void logout(String authorization) throws UnauthorizedException, InternalServerErrorException {
        try {
            AuthData auth = auths.getAuth(authorization);
            try {
                auths.deleteAuth(auth);
            } catch (DataAccessException e) {
                throw new InternalServerErrorException("Error: failed to log user out");
            }
        } catch (DataAccessException e) {
            throw new UnauthorizedException("Error: unauthorized");
        }
    }
}

package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import api.exception.BadRequestException;
import api.exception.ForbiddenException;
import api.exception.InternalServerErrorException;
import api.exception.UnauthorizedException;
import api.LoginRequest;
import api.RegisterRequest;
import api.LoginResult;
import api.RegisterResult;

import java.util.UUID;

public class UserService {
    UserDAO users;
    AuthDAO auths;

    public UserService(UserDAO users, AuthDAO auths) {
        this.users = users;
        this.auths = auths;
    }

    public RegisterResult register(RegisterRequest request)
            throws BadRequestException, ForbiddenException, InternalServerErrorException {
        if (request.username() == null || request.password() == null || request.email() == null
                || request.username().isEmpty() || request.password().isEmpty() || request.email().isEmpty()) {
            throw new BadRequestException("Error: bad request");
        }
        try {
            UserData user = users.getUser(request.username());
            throw new ForbiddenException("Error: already taken");
        } catch (DataAccessException e) {
            try {
                String hashedPassword = BCrypt.hashpw(request.password(), BCrypt.gensalt());
                users.createUser(new UserData(request.username(), hashedPassword, request.email()));
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
            if (BCrypt.checkpw(request.password(), user.password())) {
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

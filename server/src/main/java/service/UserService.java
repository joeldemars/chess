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
                throw new InternalServerErrorException("Error: Failed to register user");
            }
        }
    }

    public LoginRequest login(LoginRequest request) throws BadRequestException, InternalServerErrorException {
        throw new InternalServerErrorException("Unimplemented");
    }

    public void logout(String authorization) throws UnauthorizedException, InternalServerErrorException {
        throw new InternalServerErrorException("Unimplemented");
    }
}

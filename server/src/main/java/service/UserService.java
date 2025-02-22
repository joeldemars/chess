package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;

public class UserService {
    MemoryUserDAO users = new MemoryUserDAO();
    MemoryAuthDAO auths = new MemoryAuthDAO();

    public RegisterResult register(RegisterRequest request)
            throws BadRequestException, ForbiddenException, InternalServerErrorException {
//        try {
//            UserData user = users.getUser(request.username());
//        } catch (DataAccessException exception) {
//
//        }
    }

    public LoginRequest login(LoginRequest request) throws BadRequestException, InternalServerErrorException {

    }

    public void logout(String authorization) throws UnauthorizedException, InternalServerErrorException {

    }
}

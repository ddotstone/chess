package handler;

import dataaccess.MemoryAuthDataDAO;
import dataaccess.MemoryUserDataDAO;
import service.UserService;

public clasds SessionHandler {
    private final UserService userService;

    public SessionHandler(){
        this.userService = new UserService(new MemoryUserDataDAO(), new MemoryAuthDataDAO());
    }

}

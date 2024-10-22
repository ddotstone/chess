package dataaccess;

import model.UserData;

import java.util.ArrayList;
import java.util.Collection;

public class MemoryUserDataDAO {

    Collection<UserData> userDataCollection;

    public MemoryUserDataDAO() {
        userDataCollection = new ArrayList<>();
    }

    public void createUser(UserData userData) throws DataAccessException {
        userDataCollection.add(userData);
        return;
    }

    public UserData getUser(String username) throws DataAccessException {
        for (UserData user : userDataCollection) {
            if (user.username().equals(username)) {
                return user;
            }
        }
        return null;
    }
}

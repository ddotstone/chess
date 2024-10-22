package dataaccess;

import model.UserData;

import java.util.ArrayList;
import java.util.Collection;

public class MemoryUserDataDAO implements UserDataDAO {

    public static final Collection<UserData> userDataCollection = new ArrayList<>();

    @Override
    public void clear() {
        userDataCollection.clear();
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        userDataCollection.add(userData);
        return;
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        for (UserData user : userDataCollection) {
            if (user.username().equals(username)) {
                return user;
            }
        }
        return null;
    }
}

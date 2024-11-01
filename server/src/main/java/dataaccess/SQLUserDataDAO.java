package dataaccess;

import model.UserData;

import java.util.ArrayList;
import java.util.Collection;

public class SQLUserDataDAO implements UserDataDAO {

    public static final Collection<UserData> USER_DATA_COLLECTION = new ArrayList<>();

    @Override
    public void clear() {
        USER_DATA_COLLECTION.clear();
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        USER_DATA_COLLECTION.add(userData);
        return;
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        for (UserData user : USER_DATA_COLLECTION) {
            if (user.username().equals(username)) {
                return user;
            }
        }
        return null;
    }
}

package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static dataaccess.DAOTestHelper.clearDatabase;

public class UserDataDAOTest {

    @BeforeEach
    public void clearDataUser() throws DataAccessException {
        clearDatabase();
    }

    @Test
    public void assertCreateUser() throws DataAccessException {
        var userDao = new SQLUserDataDAO();

        userDao.createUser(new UserData("user", "password", "email"));
        var user = userDao.getUser("user");
        Assertions.assertEquals(user.email(), "email");
    }

    @Test
    public void assertNullUsernameFail() throws DataAccessException {
        var userDao = new SQLUserDataDAO();
        Assertions.assertThrows(BadRequestException.class, () -> {
            userDao.createUser(new UserData(null, "password", "email"));
        });
    }

    @Test
    public void assertGetUsername() throws DataAccessException {
        var userDao = new SQLUserDataDAO();

        userDao.createUser(new UserData("newuser", "password", "email"));
        var user = userDao.getUser("newuser");
        Assertions.assertEquals(user.email(), "email");
    }

    @Test
    public void assertBadUsernameFail() throws DataAccessException {
        var userDao = new SQLUserDataDAO();

        userDao.createUser(new UserData("newuser", "password", "email"));
        var user = userDao.getUser("newuser2");
        Assertions.assertNull(user);
    }
}

package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static dataaccess.DAOTestHelper.ClearDatabase;

public class UserDataDAOTest {

    @BeforeEach
    public void ClearData() throws DataAccessException {
        ClearDatabase();
    }

    @Test
    public void AssertCreateUser() throws DataAccessException {
        var userDao = new SQLUserDataDAO();

        userDao.createUser(new UserData("user", "password", "email"));
        var user = userDao.getUser("user");
        Assertions.assertEquals(user.email(), "email");
    }

    @Test
    public void AssertNullUsernameFail() throws DataAccessException {
        var userDao = new SQLUserDataDAO();
        Assertions.assertThrows(BadRequestException.class, () -> {
            userDao.createUser(new UserData(null, "password", "email"));
        });
    }

    @Test
    public void AssertGetUsername() throws DataAccessException {
        var userDao = new SQLUserDataDAO();

        userDao.createUser(new UserData("newuser", "password", "email"));
        var user = userDao.getUser("newuser");
        Assertions.assertEquals(user.email(), "email");
    }

    @Test
    public void AssertBadUsernameFail() throws DataAccessException {
        var userDao = new SQLUserDataDAO();

        userDao.createUser(new UserData("newuser", "password", "email"));
        var user = userDao.getUser("newuser2");
        Assertions.assertNull(user);
    }
}

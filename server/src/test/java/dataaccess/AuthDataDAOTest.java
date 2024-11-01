package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static dataaccess.DAOTestHelper.clearDatabase;

public class AuthDataDAOTest {
    @BeforeEach
    public void clearDataAuth() throws DataAccessException {
        clearDatabase();
    }

    @Test
    public void assertClear() throws DataAccessException {
        var authDAO = new SQLAuthDataDAO();
        authDAO.createAuth(new AuthData("111111111", "banana"));

        authDAO.clear();

        var result = authDAO.getAuth("111111111");
        Assertions.assertNull(result);
    }

    @Test
    public void assertAddToken() throws DataAccessException {

        var authDAO = new SQLAuthDataDAO();
        authDAO.createAuth(new AuthData("111111111", "banana"));

        var result = authDAO.getAuth("111111111");
        Assertions.assertEquals(result.username(), "banana");
    }


    @Test
    public void assertFailDuplicateAuth() throws DataAccessException {

        var authDAO = new SQLAuthDataDAO();
        authDAO.createAuth(new AuthData("111111111", "banana"));

        Assertions.assertThrows(AlreadyTakenException.class, () -> {
            authDAO.createAuth(new AuthData("111111111", "banana"));
        });

    }

    @Test
    public void assertGetToken() throws DataAccessException {

        var authDAO = new SQLAuthDataDAO();
        authDAO.createAuth(new AuthData("111111111", "banana"));

        var result = authDAO.getAuth("111111111");
        Assertions.assertEquals(result.username(), "banana");
    }

    @Test
    public void assertFailGetToken() throws DataAccessException {

        var authDAO = new SQLAuthDataDAO();
        var result = authDAO.getAuth("111111111");
        Assertions.assertNull(result);
    }

    @Test
    public void assertDeleteToken() throws DataAccessException {

        var authDAO = new SQLAuthDataDAO();
        authDAO.createAuth(new AuthData("111111111", "banana"));

        authDAO.deleteAuth("111111111");

        var result = authDAO.getAuth("111111111");
        Assertions.assertNull(result);
    }


    @Test
    public void assertInvalidDeleteFailToken() throws DataAccessException {

        var authDAO = new SQLAuthDataDAO();
        authDAO.createAuth(new AuthData("111111111", "banana"));

        Assertions.assertThrows(UnauthorizedException.class, () -> {
            authDAO.deleteAuth("22222222");
        });
    }

}

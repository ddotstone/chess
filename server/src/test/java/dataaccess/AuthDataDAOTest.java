package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static dataaccess.DAOTestHelper.ClearDatabase;

public class AuthDataDAOTest {
    @BeforeEach
    public void ClearData() throws DataAccessException {
        ClearDatabase();
    }

    @Test
    public void AssertClear() throws DataAccessException {
        var authDAO = new SQLAuthDataDAO();
        authDAO.createAuth(new AuthData("111111111", "banana"));

        authDAO.clear();

        var result = authDAO.getAuth("111111111");
        Assertions.assertNull(result);
    }

    @Test
    public void AssertAddToken() throws DataAccessException {

        var authDAO = new SQLAuthDataDAO();
        authDAO.createAuth(new AuthData("111111111", "banana"));

        var result = authDAO.getAuth("111111111");
        Assertions.assertEquals(result.username(), "banana");
    }


    @Test
    public void AssertFailDuplicateAuth() throws DataAccessException {

        var authDAO = new SQLAuthDataDAO();
        authDAO.createAuth(new AuthData("111111111", "banana"));

        Assertions.assertThrows(AlreadyTakenException.class, () -> {
            authDAO.createAuth(new AuthData("111111111", "banana"));
        });

    }

    @Test
    public void AssertGetToken() throws DataAccessException {

        var authDAO = new SQLAuthDataDAO();
        authDAO.createAuth(new AuthData("111111111", "banana"));

        var result = authDAO.getAuth("111111111");
        Assertions.assertEquals(result.username(), "banana");
    }

    @Test
    public void AssertFailGetToken() throws DataAccessException {

        var authDAO = new SQLAuthDataDAO();
        var result = authDAO.getAuth("111111111");
        Assertions.assertNull(result);
    }

    @Test
    public void AssertDeleteToken() throws DataAccessException {

        var authDAO = new SQLAuthDataDAO();
        authDAO.createAuth(new AuthData("111111111", "banana"));

        authDAO.deleteAuth("111111111");

        var result = authDAO.getAuth("111111111");
        Assertions.assertNull(result);
    }


    @Test
    public void AssertInvalidDeleteFailToken() throws DataAccessException {

        var authDAO = new SQLAuthDataDAO();
        authDAO.createAuth(new AuthData("111111111", "banana"));

        Assertions.assertThrows(UnauthorizedException.class, () -> {
            authDAO.deleteAuth("22222222");
        });
    }

}

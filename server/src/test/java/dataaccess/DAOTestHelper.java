package dataaccess;

public class DAOTestHelper {
    public static void clearDatabase() throws DataAccessException {
        new SQLAuthDataDAO().clear();
        new SQLUserDataDAO().clear();
        new SQLGameDataDAO().clear();
    }
}

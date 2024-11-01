package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static dataaccess.DAOTestHelper.ClearDatabase;

public class GameDataDAOTest {
    @BeforeEach
    public void ClearData() throws DataAccessException {
        ClearDatabase();
    }

    @Test
    public void AssertClear() throws DataAccessException {
        var gameDAO = new SQLGameDataDAO();
        int id = gameDAO.createGame(new GameData(0, "a", "b", "newGame", new ChessGame()));

        var games = gameDAO.listGames();
        Assertions.assertEquals(games.size(), 1);

        gameDAO.clear();

        games = gameDAO.listGames();
        Assertions.assertEquals(games.size(), 0);
    }

    @Test
    public void AssertAddGame() throws DataAccessException {
        var gameDAO = new SQLGameDataDAO();
        int id = gameDAO.createGame(new GameData(0, "a", "b", "newGame", new ChessGame()));

        var games = gameDAO.getGame(id);
        Assertions.assertEquals(games.gameName(), "newGame");
    }


    @Test
    public void AssertFailAddGame() throws DataAccessException {
        var gameDAO = new SQLGameDataDAO();
        Assertions.assertThrows(BadRequestException.class, () -> {
            gameDAO.createGame(new GameData(0, "a", "b", null, new ChessGame()));
        });
    }

    @Test
    public void AssertListGame() throws DataAccessException {
        var gameDAO = new SQLGameDataDAO();
        int id = gameDAO.createGame(new GameData(0, "a", "b", "newGame", new ChessGame()));
        id = gameDAO.createGame(new GameData(0, "a", "b", "newGame1", new ChessGame()));
        id = gameDAO.createGame(new GameData(0, "a", "b", "newGame2", new ChessGame()));

        var games = gameDAO.listGames();
        Assertions.assertEquals(games.size(), 3);
    }

    @Test
    public void AssertListGameAfterClear() throws DataAccessException {
        var gameDAO = new SQLGameDataDAO();
        int id = gameDAO.createGame(new GameData(0, "a", "b", "newGame", new ChessGame()));
        id = gameDAO.createGame(new GameData(0, "a", "b", "newGame1", new ChessGame()));
        id = gameDAO.createGame(new GameData(0, "a", "b", "newGame2", new ChessGame()));

        gameDAO.clear();

        var games = gameDAO.listGames();
        Assertions.assertEquals(games.size(), 0);
    }

    @Test
    public void AssertGetGame() throws DataAccessException {
        var gameDAO = new SQLGameDataDAO();
        int id = gameDAO.createGame(new GameData(0, "a", "b", "newGame", new ChessGame()));
        id = gameDAO.createGame(new GameData(0, "a", "b", "newGame1", new ChessGame()));
        id = gameDAO.createGame(new GameData(0, "a", "b", "newGame2", new ChessGame()));

        var games = gameDAO.getGame(2);
        Assertions.assertEquals(games.gameName(), "newGame1");
    }

    @Test
    public void AssertBadIDFail() throws DataAccessException {
        var gameDAO = new SQLGameDataDAO();
        int id = gameDAO.createGame(new GameData(0, "a", "b", "newGame", new ChessGame()));
        id = gameDAO.createGame(new GameData(0, "a", "b", "newGame1", new ChessGame()));
        id = gameDAO.createGame(new GameData(0, "a", "b", "newGame2", new ChessGame()));

        var games = gameDAO.getGame(4);
        Assertions.assertNull(games);
    }

    @Test
    public void AssertUpdateGame() throws DataAccessException {
        var gameDAO = new SQLGameDataDAO();
        int id = gameDAO.createGame(new GameData(0, "a", "b", "newGame", new ChessGame()));

        gameDAO.updateGame(new GameData(id, "z", "y", "changedName", null));

        var game = gameDAO.getGame(id);
        Assertions.assertNull(game.game());
        Assertions.assertEquals("z", game.whiteUsername());
    }

    @Test
    public void AssertBadIDUpdateFail() throws DataAccessException {
        var gameDAO = new SQLGameDataDAO();
        Assertions.assertThrows(BadRequestException.class, () -> {
            gameDAO.updateGame(new GameData(7, "z", "y", "changedName", null));
        });
    }

}

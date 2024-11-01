package dataaccess;

import chess.ChessGame;
import model.GameData;

import com.google.gson.Gson;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import static dataaccess.DatabaseManager.configureDatabase;
import static dataaccess.DatabaseManager.executeUpdate;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLGameDataDAO implements GameDataDAO {

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  game (
              `gameid` int NOT NULL AUTO_INCREMENT,
              `whiteusername` varchar(256) DEFAULT NULL,
              `blackusername` varchar(256) DEFAULT NULL,
              `gamename` varchar(256) NOT NULL,
              `game`  TEXT,
              PRIMARY KEY (`gameid`),
              INDEX(`gamename`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    public SQLGameDataDAO() throws DataAccessException {
        configureDatabase(createStatements);
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE game";
        executeUpdate(statement);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameid, whiteusername, blackusername, gamename, game FROM game WHERE gameid=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public int createGame(GameData gameData) throws DataAccessException {
        if (gameData.gameName() == null) {
            throw new BadRequestException();
        }
        String json = new Gson().toJson(gameData.game(), ChessGame.class);
        var statement = "INSERT INTO game (whiteusername, blackusername, gamename, game) VALUES (?, ?, ?, ?)";
        return executeUpdate(statement, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), json);
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        if (getGame(gameData.gameID()) == null) {
            throw new BadRequestException();
        }

        var statement = "UPDATE game SET whiteusername=?, blackusername=?, gamename=?, game=? WHERE gameid=?";
        String gameJson = new Gson().toJson(gameData.game(), ChessGame.class);
        executeUpdate(statement, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), gameJson, gameData.gameID());
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        var result = new ArrayList<GameData>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameid, whiteusername, blackusername, gamename, game FROM game";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readGame(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return result;
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        var id = rs.getInt("gameid");
        var whiteusername = rs.getString("whiteusername");
        var blackusername = rs.getString("blackusername");
        var gamename = rs.getString("gamename");
        var game = rs.getString("game");
        var gameobject = new Gson().fromJson(game, ChessGame.class);
        return new GameData(id, whiteusername, blackusername, gamename, gameobject);
    }

}

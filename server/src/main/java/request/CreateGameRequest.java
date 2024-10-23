package request;

public record CreateGameRequest(String gameName) {
    @Override
    public String toString() {
        return "createGameRequest{" +
                "gameName='" + gameName + '\'' +
                '}';
    }
};
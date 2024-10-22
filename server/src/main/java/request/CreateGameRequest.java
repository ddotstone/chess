package request;

public record CreateGameRequest(String gameName) {
    @Override
    public String toString() {
        return "CreateGameRequest{" +
                "gameName='" + gameName + '\'' +
                '}';
    }
};
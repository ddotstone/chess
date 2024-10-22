package response;

public record CreateGameResponse(int gameID) {
    @Override
    public String toString() {
        return "CreateGameResponse{" +
                "gameID=" + gameID +
                '}';
    }
}

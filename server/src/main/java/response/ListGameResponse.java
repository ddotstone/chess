package response;

import java.util.Collection;

import model.GameData;

public record ListGameResponse(Collection<GameData> games) {
    @Override
    public String toString() {
        return "ListGameResponse{" +
                "games=" + games +
                '}';
    }
}

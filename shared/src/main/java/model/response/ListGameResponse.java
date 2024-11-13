package model.response;

import model.GameData;

import java.util.Collection;

public record ListGameResponse(Collection<GameData> games) {
    @Override
    public String toString() {
        return "ListGameResponse{" +
                "games=" + games +
                '}';
    }
}

package response;

import java.util.Collection;

import model.GameData;

public record ListGameResponse(Collection<GameData> gameDataCollection) {
}

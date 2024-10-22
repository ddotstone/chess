package response;

import java.util.Collection;

import model.GameData;

public record ListGameReponse(Collection<GameData> gameDataCollection) {
}

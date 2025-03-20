package api;

import model.GameData;

public record ListGamesResult(GameData[] games) {
}

package com.kalah.model;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class GameRoom {

	private static ConcurrentHashMap<Integer, Game> gamesMap = new ConcurrentHashMap<Integer, Game>();

	public static void addGameToRoom(Game game) {

		gamesMap.putIfAbsent(game.getId(), game);

	}

	public static Game getGameById(int gameId) {

		return gamesMap.get(gameId);

	}

}

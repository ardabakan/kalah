package com.kalah.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GameSettings {

	public static int NUMBER_OF_STONES_PER_PIT;

	public static int NUMBER_OF_PITS_PER_PLAYER;
	
	public static String SERVER_HOSTNAME;
	
	public static int SERVER_PORT;
	
	public static final int PLAYER_TURN_1 = 1;

	public static final int PLAYER_TURN_2 = 2;

	@Value("${server.stonecount}")
	public void setNumberOfStonesPerPit(String numberOfStones) {
		NUMBER_OF_STONES_PER_PIT = Integer.parseInt(numberOfStones);
	}

	@Value("${server.pitcount}")
	public void setNumberOfPitsPerPlayer(String numberOfPitsPerPlayer) {
		NUMBER_OF_PITS_PER_PLAYER = Integer.parseInt(numberOfPitsPerPlayer);
	}

}

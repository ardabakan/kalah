package com.kalah.util;

public enum GameExceptionCodes {

	GAME_NOT_FOUND(0, "Game not found"),
	PIT_NOT_FOUND(1, "Pit not found"),
	CHOOSE_A_PIT_NOT_THE_KALAH(2, "You can not play with the Kalah"),
	CAN_NOT_PLAY_WITH_EMPTY_PIT(3, "You can not play with an empty pit"),
	CAN_NOT_PLAY_NOT_YOUR_TURN(4, "Not your turn to play"),
	CAN_NOT_PLAY_A_FINISHED_GAME(5, "You can not play a finished game");

	private final int id;
	private final String msg;

	GameExceptionCodes(int id, String msg) {
		this.id = id;
		this.msg = msg;
	}

	public int getId() {
		return this.id;
	}

	public String getMsg() {
		return this.msg;
	}
}

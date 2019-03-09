package com.kalah.util;

public class GameException extends Exception {

	private int errorCode;
	private String errorMsg;

	public GameException(GameExceptionCodes code) {
		this.errorMsg = code.getMsg();
		this.errorCode = code.getId();
	}

	public int getErrorCode() {
		return errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}
}

package com.kalah.model;

import java.util.Stack;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.kalah.util.GameSettings;

@AllArgsConstructor
@Getter
@Setter
public class Pit {

	private int index;

	@JsonIgnore
	private Stack<Stone> stoneStack;

	// for ex 7 or 14 for a 6 pits game
	public boolean isKalah() {

		return getIndex() % (GameSettings.NUMBER_OF_PITS_PER_PLAYER + 1) == 0;
	}

	public Pit(int currentIndex) {
		setIndex(currentIndex);
		stoneStack = new Stack<Stone>();
	}

	public boolean isEmpty() {
		return getStoneStack().isEmpty();
	}

	public int getStoneCount() {
		return stoneStack.size();
	}

	public boolean hasOneStone() {
		return getStoneCount() == 1;
	}

	public void addStone(Stone stone) {
		getStoneStack().push(stone);
	}

	public Stone removeStone() {
		return getStoneStack().pop();
	}

}

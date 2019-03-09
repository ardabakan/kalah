package com.kalah.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kalah.util.GameException;
import com.kalah.util.GameExceptionCodes;
import com.kalah.util.GameSettings;

@Getter
@Setter
public class Game {

	private int id;

	private String uri;

	@JsonIgnore
	private int whoseTurn = GameSettings.PLAYER_TURN_1;

	@JsonIgnore
	private static final AtomicInteger counter = new AtomicInteger();

	@JsonIgnore
	private boolean finished;

	@JsonIgnore
	private int winner;

	@JsonIgnore
	private List<Pit> pits;

	private Map<Integer, String> status;

	@JsonProperty("status")
	public Map printStatus() {

		status = new HashMap<Integer, String>();

		for (Pit pit : pits) {

			status.put(pit.getIndex(), "" + pit.getStoneCount());

		}

		return status;

	}

	public static int nextValue() {
		return counter.getAndIncrement();
	}

	public Game() {

		setId(nextValue());
		setStatus(status);
	}

	public Pit getPit(int pitId) {

		Pit result = null;

		if (getPits() != null) {

			result = getPits().get(pitId - 1);

		}

		return result;
	}

	public boolean areAllPitsEmptyForPlayer(int playerNo) {

		boolean result = true;

		List<Pit> playersPits = getPlayersPits(playerNo);

		for (Pit pit : playersPits) {

			if (!pit.isKalah() && pit.isEmpty()) {

				result = false;

				break;

			}

		}

		return result;

	}

	public List<Pit> getPlayersPits(int playerNo) {

		if (playerNo == GameSettings.PLAYER_TURN_1) {

			return getPits().subList(0, GameSettings.NUMBER_OF_PITS_PER_PLAYER);

		} else {
			return getPits().subList(
					GameSettings.NUMBER_OF_PITS_PER_PLAYER + 1,
					GameSettings.NUMBER_OF_PITS_PER_PLAYER * 2);
		}

	}

	// we have a conquering pit! Get all the stones at the opposite player's
	// corresponding pit, ie with index (14 - pitId)
	// for ex, pit3 conquers pit11, pit4 conquers pit10 and so on
	public Pit getConqueredPit(Pit playingPit) {
		int indexToConquer = 2 * (GameSettings.NUMBER_OF_PITS_PER_PLAYER + 1)
				- playingPit.getIndex();

		return getPit(indexToConquer);
	}

	// returns the kalah for the given pit, for ex kalah for pit8 --> pit14
	public Pit getKalahOfPit(Pit playingPit) {

		return getPit((playingPit.getIndex()
				/ GameSettings.NUMBER_OF_PITS_PER_PLAYER + 1)
				* (GameSettings.NUMBER_OF_PITS_PER_PLAYER + 1));

	}

	// returns the opponents kalah,
	// for ex pit2 --> pit14
	// pit8 --> pit7
	public Pit getOpponentsKalah(Pit playingPit) {

		int kalahPlayer1 = GameSettings.NUMBER_OF_PITS_PER_PLAYER + 1;
		int kalahPlayer2 = 2 * kalahPlayer1;

		if (playingPit.getIndex() > kalahPlayer1) {
			return getPit(kalahPlayer1);
		} else {
			return getPit(kalahPlayer2);
		}
	}

	// This method returns the next pit that we can
	// transfer the stones(including our own kalah).
	// Suppose pit5 is playing. Its possible transfer pits are
	// pit6, pit7(its own kalah), pit8, pit9, pit10, pit11, pit12, pit13, pit1,
	// pit2 and so on
	// This goes circular and omits the opponents kalah
	public Pit nextReceiverPit(Pit playingPit, int lastIndexThatWasTransferredTo) {

		lastIndexThatWasTransferredTo++;

		if (lastIndexThatWasTransferredTo == getOpponentsKalah(playingPit)
				.getIndex()) {
			lastIndexThatWasTransferredTo++;
		}
		// board round complete, pit1 is the next
		if (lastIndexThatWasTransferredTo > 2 * (GameSettings.NUMBER_OF_PITS_PER_PLAYER + 1)) {
			lastIndexThatWasTransferredTo = 1;
		}
		return getPit(lastIndexThatWasTransferredTo);

	}

	public void mutateWhoseTurn() {

		if (getWhoseTurn() == 1) {
			setWhoseTurn(2);
		} else {
			setWhoseTurn(1);
		}
	}

	public boolean pitBelongsToPlayer(Pit pit, int playerNo) {

		// for ex,
		// for pit 8, if turn is at player 2, 7 < 8 < 14 within boundaries
		// for pit 3, if turn is at player 1, 0 < 3 < 7 within boundaries
		boolean bPitWithinBoundaries = playerNo
				* (GameSettings.NUMBER_OF_PITS_PER_PLAYER + 1) > pit.getIndex()

				&&

				(playerNo - 1) * GameSettings.NUMBER_OF_PITS_PER_PLAYER < pit
							.getIndex();

		return bPitWithinBoundaries;
	}

	public void checkPlayConditions(Pit pit) throws GameException {

		// game is finished
		if (isFinished()) {
			throw new GameException(
					GameExceptionCodes.CAN_NOT_PLAY_A_FINISHED_GAME);
		}
		// pit not found
		if (pit == null) {
			throw new GameException(GameExceptionCodes.PIT_NOT_FOUND);
		}
		// can not play with a Kalah
		if (pit.isKalah()) {
			throw new GameException(
					GameExceptionCodes.CHOOSE_A_PIT_NOT_THE_KALAH);

		}
		// pit is empty
		if (pit.isEmpty()) {
			throw new GameException(
					GameExceptionCodes.CAN_NOT_PLAY_WITH_EMPTY_PIT);

		}

		if (!pitBelongsToPlayer(pit, getWhoseTurn())) {

			throw new GameException(
					GameExceptionCodes.CAN_NOT_PLAY_NOT_YOUR_TURN);

		}

	}
}

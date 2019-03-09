package com.kalah.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kalah.model.Game;
import com.kalah.model.GameRoom;
import com.kalah.model.Pit;
import com.kalah.model.Stone;
import com.kalah.util.GameSettings;

@Service
public class KalahServiceImpl implements KalahService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public Game startNewGame() {

		logger.info("Initializing a new game..");

		Game game = new Game();

		logger.info("Game number " + game.getId() + " initialized");

		game.setPits(generatePitsFilledWithStones());

		logger.info("Pits initialized for " + game.getId());

		game.setUri("http://" + GameSettings.SERVER_HOSTNAME + ":"
				+ GameSettings.SERVER_PORT + "/games");

		logger.info("Game " + game.getId()
				+ " waiting for your moves on address " + game.getUri());

		GameRoom.addGameToRoom(game);

		return game;

	}

	@Override
	public Pit distributePitStones(Game game, Pit playingPit) {

		int latestReceiverIndex = playingPit.getIndex();

		Pit latestReceiver = null;

		while (!playingPit.isEmpty()) {

			latestReceiver = game.nextReceiverPit(playingPit,
					latestReceiverIndex);
			
			logger.debug("Giving a stone from " + playingPit.getIndex() + " to " + latestReceiver.getIndex());

			// give the stone to the latest receiver
			latestReceiver.addStone(playingPit.removeStone());
			
			latestReceiverIndex++;

		}

		return latestReceiver;

	}

	@Override
	public Game getGameById(int gameId) {
		return GameRoom.getGameById(gameId);

	}

	// fill each non-kalah pit with stones according to game settings, for ex 6
	// pits 6 stones
	@Override
	public List<Pit> generatePitsFilledWithStones() {

		List<Pit> pitList = Collections.synchronizedList(new ArrayList<Pit>());

		int iCurrentIndex = 1;

		int iTotalNumberOfPits = GameSettings.NUMBER_OF_PITS_PER_PLAYER * 2 + 2;

		for (int i = 0; i < iTotalNumberOfPits; i++) {

			Pit pit = new Pit(iCurrentIndex++);

			if (!pit.isKalah()) {

				for (int j = 0; j < GameSettings.NUMBER_OF_STONES_PER_PIT; j++) {

					pit.addStone(new Stone());

				}

			}

			pitList.add(pit);

		}

		return pitList;

	}

	@Override
	public void transferStones(Pit fromPit, Pit toPit, int stoneCountToTransfer) {

		if (fromPit != null && toPit != null && !fromPit.isEmpty()) {

			while (stoneCountToTransfer > 0) {

				toPit.addStone(fromPit.removeStone());

				stoneCountToTransfer--;

			}

		}

	}

	@Override
	public void transferAllStones(Pit fromPit, Pit toPit) {
		transferStones(fromPit, toPit, fromPit.getStoneStack().size());

	}
}

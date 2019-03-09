package com.kalah.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.kalah.model.Game;
import com.kalah.model.GameDTO;
import com.kalah.model.Pit;
import com.kalah.service.KalahService;
import com.kalah.util.GameException;
import com.kalah.util.GameExceptionCodes;
import com.kalah.util.GameSettings;

@RestController
public class KalahController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private KalahService kalahService;

	@RequestMapping("/")
	String home() {
		return "Welcome to Kalah Game!";
	}

	@GetMapping(value = "/games/{g}")
	public ResponseEntity<Game> getGameSituation(@PathVariable("g") int gameId)
			throws GameException {

		Game game = kalahService.getGameById(gameId);

		return new ResponseEntity<Game>(game, HttpStatus.OK);

	}

	@RequestMapping(value = "/games/{g}/pits/{p}", method = RequestMethod.PUT)
	public ResponseEntity<Game> makeAMove(@PathVariable("g") int gameId,
			@PathVariable("p") int pitId) throws GameException {

		Game game = kalahService.getGameById(gameId);

		if (game == null) {
			throw new GameException(GameExceptionCodes.GAME_NOT_FOUND);
		}

		Pit playingPit = game.getPit(pitId);

		game.checkPlayConditions(playingPit);

		// if initial checks are passed, let's pass to the stone distribution

		Pit latestReceiverPit = kalahService.distributePitStones(game,
				playingPit);

		// now let's apply special rules

		// Rule : conquering opponents pit if last receiver has one stone and is
		// a pit of his own

		if (!latestReceiverPit.isKalah()
				&& latestReceiverPit.hasOneStone()
				&& game.pitBelongsToPlayer(latestReceiverPit,
						game.getWhoseTurn())) {

			Pit pitToConquer = game.getConqueredPit(playingPit);

			// if Player1 is playing, transfer to kalah1 (pit7), if Player2
			// transfer to kalah2 (pit14)
			Pit kalahOfPlayingPit = game.getKalahOfPit(playingPit);

			// transfer first the conquering pits stones and then the
			// conquered
			// pits stones to the players Kalah
			kalahService.transferAllStones(playingPit, kalahOfPlayingPit);
			kalahService.transferAllStones(pitToConquer, kalahOfPlayingPit);

		}

		// has the player finished all of his pits and ended the game?
		if (game.areAllPitsEmptyForPlayer(game.getWhoseTurn())) {

			game.setFinished(true);

			int otherPlayer = 1;

			if (game.getWhoseTurn() == 1) {

				otherPlayer = 2;
			}

			// now put the other players remaining stones to his Kalah
			List<Pit> otherPlayersPits = game.getPlayersPits(otherPlayer);

			for (Pit pit : otherPlayersPits) {

				if (!pit.isKalah() && !pit.isEmpty()) {
					// transfer the pit content to its kalah
					kalahService
							.transferAllStones(pit, game.getKalahOfPit(pit));

				}

			}

			logger.info("Game is over, players have respectively ");
			logger.info("Player 1 has "
					+ game.getPlayersPits(1)
							.get(GameSettings.NUMBER_OF_PITS_PER_PLAYER - 1)
							.getStoneCount());
			logger.info("Player 2 has "
					+ game.getPlayersPits(2)
							.get(GameSettings.NUMBER_OF_PITS_PER_PLAYER - 1)
							.getStoneCount());

		}

		if (!latestReceiverPit.isKalah()) {

			game.mutateWhoseTurn();
		}

		// if the last receiver is Kalah, the player can play again, we leave it
		// up to the player to call

		return new ResponseEntity<Game>(game, HttpStatus.OK);

	}

	@PostMapping(value = "/games")
	@ResponseBody
	public ResponseEntity<GameDTO> createGame() {

		Game game = kalahService.startNewGame();

		// Send location in response
		return new ResponseEntity<GameDTO>(new GameDTO(game.getId(),
				game.getUri()), HttpStatus.CREATED);

	}

}

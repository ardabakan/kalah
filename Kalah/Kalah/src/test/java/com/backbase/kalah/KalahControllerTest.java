package com.backbase.kalah;

import java.util.Map;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.kalah.KalahGameBackend;
import com.kalah.model.Game;
import com.kalah.model.GameDTO;
import com.kalah.util.GameSettings;

@RunWith(SpringRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SpringBootTest(classes = KalahGameBackend.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class KalahControllerTest {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@LocalServerPort
	private int port;

	TestRestTemplate restTemplate = new TestRestTemplate();

	private int idOfCreatedGame;

	@Test
	public void test_1_IfGameStartsWell() throws Exception {

		ResponseEntity<GameDTO> response = restTemplate.exchange(
				createTestUri("/games"), HttpMethod.POST, null, GameDTO.class);

		logger.info("Received game with id "
				+ response.getBody().getId()
				+ " for checkGameStartsWell test, seems like it is CREATED without problem!");

		idOfCreatedGame = response.getBody().getId();

	}

	@Test
	public void test_2_GameStatusAfterStart() throws Exception {

		// let's check the inner status of the game idOfCreatedGame

		ResponseEntity<Game> response = restTemplate.exchange(
				createTestUri("/games/" + idOfCreatedGame), HttpMethod.GET,
				null, Game.class);

		logger.info("Received game with id "
				+ response.getBody().getId()
				+ " for checkGameStartsWell test, seems like it is LIVING without problem!");

		Map<Integer, String> statusMap = response.getBody().getStatus();

		Assert.assertTrue(!statusMap.isEmpty());

		logger.info("Game inner status map not empty, a good sign!");

		Assert.assertTrue(statusMap.size() == 2 * (GameSettings.NUMBER_OF_PITS_PER_PLAYER + 1));

		logger.info("Game inner status contains " + 2
				* (GameSettings.NUMBER_OF_PITS_PER_PLAYER + 1)
				+ " pits, a good sign!");

	}

	@Test
	public void test_3_MakeAMove() throws Exception {

		// let's check the inner status of the game idOfCreatedGame, after we
		// make a move.
		// For example let's play pit 1.
		// Pit1 stone count should be equal to zero

		logger.info("I am making an initial move, pit 1");

		ResponseEntity<Game> response = restTemplate.exchange(
				createTestUri("/games/" + idOfCreatedGame + "/pits/1"),
				HttpMethod.PUT, null, Game.class);

		logger.info("Received game with id "
				+ response.getBody().getId()
				+ " for checkGameStartsWell test, seems like it is LIVING without problem!");

		Map<Integer, String> statusMap = response.getBody().getStatus();

		logger.info("Status of the game is as follows : "
				+ statusMap.toString());

		String pit1StoneCount = statusMap.get(1);

		Assert.assertTrue(pit1StoneCount.equals("0"));

		logger.info("Pit1 contains " + pit1StoneCount + " stones, a good sign!");

	}

	private String createTestUri(String uri) {
		return "http://localhost:" + port + uri;
	}

}

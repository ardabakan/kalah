package com.kalah.service;

import java.util.List;

import com.kalah.model.Game;
import com.kalah.model.Pit;

public interface KalahService {

	public Game startNewGame();

	public Game getGameById(int gameId);

	public void transferStones(Pit fromPit, Pit toPit, int stoneCountToTransfer);

	public void transferAllStones(Pit fromPit, Pit toPit);

	public List<Pit> generatePitsFilledWithStones();

	public Pit distributePitStones(Game game, Pit playingPit);

}
package com.fathzer.chess.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.fathzer.chess.test.utils.FENUtils;
import com.fathzer.chess.utils.adapters.chesslib.ChessLibAdapter;
import com.fathzer.chess.utils.adapters.chesslib.ChessLibMoveGenerator;
import com.fathzer.games.ai.time.RemainingMoveCountPredictor;

class VuckovicSolakOracleTest {
	private static class MyOracle extends VuckovicSolakOracle<ChessLibMoveGenerator> implements ChessLibAdapter {
		
	}

	@Test
	void test() {
		final RemainingMoveCountPredictor<ChessLibMoveGenerator> oracle = new MyOracle();
		ChessLibMoveGenerator mg = FENUtils.from("4k3/6p1/3q4/8/8/8/8/1N2K3 w - - 0 1");
		assertEquals (23, oracle.getRemainingHalfMoves(mg));
		mg =FENUtils.from("4k3/6p1/3q4/8/8/8/5Q2/1N2K3 w - - 0 1");
		assertEquals (30, oracle.getRemainingHalfMoves(mg));
		mg =FENUtils.from("4k3/r4bpr/2nqnp2/8/8/P4B1N/3R1QPP/RN2K3 w Q - 0 1");
		assertEquals (46, oracle.getRemainingHalfMoves(mg));
	}

}

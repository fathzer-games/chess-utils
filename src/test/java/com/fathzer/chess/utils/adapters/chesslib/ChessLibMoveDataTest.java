package com.fathzer.chess.utils.adapters.chesslib;

import static org.junit.jupiter.api.Assertions.*;

import static com.github.bhlangonijr.chesslib.Square.*;

import org.junit.jupiter.api.Test;

class ChessLibMoveDataTest {
	@Test
	void test() {
		final ChessLibMoveData mv = new ChessLibMoveData();
		assertEquals(0, mv.getIndex(A8));
		assertEquals(63, mv.getIndex(H1));

	}

}

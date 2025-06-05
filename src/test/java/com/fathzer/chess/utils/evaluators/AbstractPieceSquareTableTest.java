package com.fathzer.chess.utils.evaluators;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.fathzer.chess.test.utils.FENUtils;
import com.fathzer.chess.utils.adapters.chesslib.ChessLibBoardExplorer;
import com.fathzer.chess.utils.adapters.chesslib.ChessLibMoveGenerator;
import com.fathzer.chess.utils.evaluators.pesto.EndGamePieceSquareTable;

class AbstractPieceSquareTableTest {

	@Test
	void test() {
		ChessLibMoveGenerator board = FENUtils.from("3qk3/7P/8/8/8/N7/B4r2/4K3 w - - 0 1");
		int expected = 43-936 +28 +187+94 -23+281 -14+297 -3-512 -28;
		assertEquals(expected, new EndGamePieceSquareTable().getRawEvaluation(new ChessLibBoardExplorer(board.getBoard())));
	}

}

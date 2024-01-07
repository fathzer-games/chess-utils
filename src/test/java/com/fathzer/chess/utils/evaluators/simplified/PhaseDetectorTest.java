package com.fathzer.chess.utils.evaluators.simplified;

import static org.junit.jupiter.api.Assertions.*;
import static com.fathzer.chess.utils.evaluators.simplified.Phase.*;

import org.junit.jupiter.api.Test;

import com.fathzer.chess.test.utils.FENUtils;
import com.fathzer.chess.utils.adapters.BoardExplorer;
import com.fathzer.chess.utils.adapters.chesslib.ChessLibBoardExplorer;


class PhaseDetectorTest {

	@Test
	void test() {
		// Queen + rook => middle game
		assertEquals(MIDDLE_GAME, getPhase("r2qk3/8/8/8/8/8/8/4K3 w q - 0 1"));
		// Queen + bishop vs two rooks => end game
		assertEquals(END_GAME, getPhase("3qk3/7b/8/8/8/8/8/R3K2R w - - 0 1"));
		// 2 Queens => end game
		assertEquals(END_GAME, getPhase("3qk3/8/8/8/8/7Q/8/4K3 w - - 0 1"));
	}
	
	private Phase getPhase(String fen) {
		final PhaseDetector pd = new PhaseDetector();
		final BoardExplorer explorer = new ChessLibBoardExplorer(FENUtils.from(fen).getBoard());
		do {
			final int p = explorer.getPiece();
			pd.add(p);
		} while (explorer.next());
		return pd.getPhase();
	}
}

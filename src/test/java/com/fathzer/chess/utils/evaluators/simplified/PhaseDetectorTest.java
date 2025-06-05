package com.fathzer.chess.utils.evaluators.simplified;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.fathzer.chess.test.utils.FENUtils;
import com.fathzer.chess.utils.adapters.BoardExplorer;
import com.fathzer.chess.utils.adapters.chesslib.ChessLibBoardExplorer;

class PhaseDetectorTest {

	@Test	
	void testStaticComputation() {	
		// Queen + rook => middle game	
		assertFalse(isEndGamePhase("r2qk3/8/8/8/8/8/8/4K3 w q - 0 1"));	
		// Queen + bishop vs two rooks => end game	
		assertTrue(isEndGamePhase("3qk3/7b/8/8/8/8/8/R3K2R w - - 0 1"));	
		// 2 Queens => end game	
		assertTrue(isEndGamePhase("3qk3/8/8/8/8/7Q/8/4K3 w - - 0 1"));
		// No queen => end game
		assertTrue(isEndGamePhase("rnb1kbnr/ppp1pppp/8/8/8/8/PPP1PPPP/RNB1KBNR w KQkq - 0 1"));
		
		// Black queen has 2 minor pieces 
		assertFalse(isEndGamePhase("3k1q2/b1n5/8/3P4/8/8/8/4K3 w - - 0 1"));	
	}	

	private boolean isEndGamePhase(String fen) {	
		final FastPhaseDetector pd = new FastPhaseDetector();	
		final BoardExplorer explorer = new ChessLibBoardExplorer(FENUtils.from(fen).getBoard());	
		do {	
			final int p = explorer.getPiece();	
			pd.add(p);	
		} while (explorer.next());	
		return pd.isEndGamePhase();	
	}	
}

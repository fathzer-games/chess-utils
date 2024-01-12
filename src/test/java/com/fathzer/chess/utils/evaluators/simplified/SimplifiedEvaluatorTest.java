package com.fathzer.chess.utils.evaluators.simplified;

import static com.fathzer.chess.utils.evaluators.simplified.Phase.END_GAME;
import static com.fathzer.chess.utils.evaluators.simplified.Phase.MIDDLE_GAME;
import static org.junit.jupiter.api.Assertions.*;

import java.util.function.IntFunction;

import org.junit.jupiter.api.Test;

import com.fathzer.chess.test.utils.FENUtils;
import com.fathzer.chess.utils.adapters.BoardExplorer;
import com.fathzer.chess.utils.adapters.chesslib.ChessLibBoardExplorer;
import com.fathzer.chess.utils.adapters.chesslib.ChessLibMoveGenerator;
import com.fathzer.chess.utils.evaluators.simplified.AbstractSimplifiedEvaluator.PhaseDetector;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.move.Move;

class SimplifiedEvaluatorTest {
	private static class MyEval extends AbstractSimplifiedEvaluator<Move, ChessLibMoveGenerator> {
		@Override
		public BoardExplorer getExplorer(ChessLibMoveGenerator board) {
			return new ChessLibBoardExplorer(board.getBoard());
		}
	}
	
	@Test
	void test() {
		ChessLibMoveGenerator board = new ChessLibMoveGenerator(new Board());
		AbstractSimplifiedEvaluator<Move, ChessLibMoveGenerator> ev = new MyEval();
		assertEquals(0, ev.evaluateAsWhite(board));
		board = FENUtils.from("3k4/8/8/3Pp3/8/8/8/4K3 w - - 0 1");
		assertEquals(5, ev.evaluateAsWhite(board));
		
		testVerticalSymetry(1, "pawn");
		testVerticalSymetry(2, "knight");
		testVerticalSymetry(3, "bishop");
		testVerticalSymetry(4, "rook");
		
		// Pawel's suggestion with white queen at b3
		board = FENUtils.from("rnbqkbnr/pp1ppppp/8/8/8/1Q6/PP1PPPPP/RNB1KBNR w KQkq - 0 1");
		assertEquals(10, ev.evaluateAsWhite(board));
		// Pawel's suggestion with both queen's at b3 and c7
		board = FENUtils.from("rnb1kbnr/ppqppppp/8/8/8/1Q6/PP1PPPPP/RNB1KBNR w KQkq - 0 1");
		assertEquals(0, ev.evaluateAsWhite(board));
		
	}
	
	private void testVerticalSymetry(int pieceKind, String name) {
		testVerticalSymetry(name, i -> AbstractSimplifiedEvaluator.getPositionValue(pieceKind, i));
}

	private void testVerticalSymetry(String wording, IntFunction<Integer> valueGetter) {
		for (int row=0;row<8;row++) {
			final int startOFrowIndex = row*8;
			for (int col=0;col<4;col++) {
				final int index=startOFrowIndex + col;
				final int sym = startOFrowIndex + 7 - col;
				assertEquals (valueGetter.apply(index), valueGetter.apply(sym), "No symetry for "+wording+" on indexes "+index+" & "+sym);
			}
		}
	}
	
	@Test
	void testPhaseDetector() {
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

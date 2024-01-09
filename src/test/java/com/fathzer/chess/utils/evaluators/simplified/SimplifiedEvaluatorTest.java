package com.fathzer.chess.utils.evaluators.simplified;

import static org.junit.jupiter.api.Assertions.*;

import java.util.function.IntFunction;

import org.junit.jupiter.api.Test;

import com.fathzer.chess.test.utils.FENUtils;
import com.fathzer.chess.utils.adapters.BoardExplorer;
import com.fathzer.chess.utils.adapters.chesslib.ChessLibBoardExplorer;
import com.fathzer.chess.utils.adapters.chesslib.ChessLibMoveGenerator;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.move.Move;

class SimplifiedEvaluatorTest {
	private static class MyEval extends SimplifiedEvaluator<Move, ChessLibMoveGenerator> {
		@Override
		protected BoardExplorer getExplorer(ChessLibMoveGenerator board) {
			return new ChessLibBoardExplorer(board.getBoard());
		}
	}
	
	@Test
	void test() {
		ChessLibMoveGenerator board = new ChessLibMoveGenerator(new Board());
		SimplifiedEvaluator<Move, ChessLibMoveGenerator> ev = new MyEval();
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
		testVerticalSymetry(name, i -> SimplifiedEvaluator.getPositionValue(pieceKind, i));
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
}

package com.fathzer.chess.utils.evaluators.pesto;

import static com.github.bhlangonijr.chesslib.Square.E1;
import static com.github.bhlangonijr.chesslib.Square.F2;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.fathzer.chess.test.utils.FENUtils;
import com.fathzer.chess.utils.adapters.BoardExplorer;
import com.fathzer.chess.utils.adapters.MoveData;
import com.fathzer.chess.utils.adapters.chesslib.ChessLibBoardExplorer;
import com.fathzer.chess.utils.adapters.chesslib.ChessLibMoveData;
import com.fathzer.chess.utils.adapters.chesslib.ChessLibMoveGenerator;
import com.fathzer.games.MoveGenerator.MoveConfidence;
import com.github.bhlangonijr.chesslib.move.Move;

class AbstractIncrementalPestoEvaluatorTest {

	private final class MyEval extends AbstractIncrementalPestoEvaluator<Move, ChessLibMoveGenerator> {
		public MyEval() {
			super();
		}

		public MyEval(PestoState state) {
			super(state);
		}

		@Override
		public MoveData<Move, ChessLibMoveGenerator> get() {
			return new ChessLibMoveData();
		}

		@Override
		public BoardExplorer getExplorer(ChessLibMoveGenerator board) {
			return new ChessLibBoardExplorer(board.getBoard());
		}

		@Override
		protected AbstractIncrementalPestoEvaluator<Move, ChessLibMoveGenerator> fork(PestoState state) {
			return new MyEval(state);
		}

		@Override
		public PestoState getState() {
			return super.getState();
		}
	}
	
	@Test
	void test() {
		// Start with black queen and rook vs a pawn, a knight and a bishop for black => mixed between middle and end of game
		MyEval ev = new MyEval();
		ChessLibMoveGenerator board = FENUtils.from("3qk3/7P/8/8/8/N7/B4r2/4K3 w - - 0 1");
		ev.init(board);
		int expectedMiddleGame = -10-1025 -8 -11+82 -23+337 +4+365 -67-477 +8;
		int expectedEndGame = 43-936 +28 +187+94 -23+281 -14+297 -3-512 -28;
		int expected = (expectedMiddleGame*8 + expectedEndGame*16)/24;
		assertEquals(expected, ev.evaluateAsWhite(board));
		
		MyEval forked = (MyEval) ev.fork();
		ChessLibMoveGenerator forkedMg = (ChessLibMoveGenerator) board.fork();
		assertEquals(expected, forked.evaluateAsWhite(board));
		
		// Take black rook => phase ratio changes
		Move mv = new Move(E1, F2);
		forked.prepareMove(forkedMg, mv);
		assertTrue(forkedMg.makeMove(mv, MoveConfidence.UNSAFE));
		forked.commitMove();
		expectedMiddleGame = expectedMiddleGame + 67+477 -8-16;
		expectedEndGame = expectedEndGame + 3+512 + 28+4;
		int forkedExpected1 = (expectedMiddleGame*6 + expectedEndGame*18)/24; // phase ratio changes
		assertEquals(forkedExpected1, forked.evaluateAsWhite(forkedMg));
		assertEquals(expected, ev.evaluateAsWhite(board));	}

}

package com.fathzer.chess.utils.evaluators.simplified;

import static org.junit.jupiter.api.Assertions.*;

import static com.github.bhlangonijr.chesslib.Square.*;

import static com.fathzer.chess.utils.evaluators.simplified.Phase.END_GAME;
import static com.fathzer.chess.utils.evaluators.simplified.Phase.MIDDLE_GAME;

import org.junit.jupiter.api.Test;

import com.fathzer.chess.test.utils.FENUtils;
import com.fathzer.chess.utils.adapters.BoardExplorer;
import com.fathzer.chess.utils.adapters.MoveData;
import com.fathzer.chess.utils.adapters.chesslib.ChessLibBoardExplorer;
import com.fathzer.chess.utils.adapters.chesslib.ChessLibMoveData;
import com.fathzer.chess.utils.adapters.chesslib.ChessLibMoveGenerator;
import com.fathzer.games.MoveGenerator.MoveConfidence;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.move.Move;

class AbstractIncrementalSimplifiedEvaluatorTest {

	private final class MyEval extends AbstractIncrementalSimplifiedEvaluator<Move, ChessLibMoveGenerator> {
		public MyEval() {
			super();
		}

		public MyEval(State state) {
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
		protected AbstractIncrementalSimplifiedEvaluator<Move, ChessLibMoveGenerator> fork(State state) {
			return new MyEval(state);
		}
	}

	@Test
	void testStaticPhaseDetector() {
		// Queen + rook => middle game
		assertEquals(MIDDLE_GAME, getPhase("r2qk3/8/8/8/8/8/8/4K3 w q - 0 1"));
		// Queen + bishop vs two rooks => end game
		assertEquals(END_GAME, getPhase("3qk3/7b/8/8/8/8/8/R3K2R w - - 0 1"));
		// 2 Queens => end game
		assertEquals(END_GAME, getPhase("3qk3/8/8/8/8/7Q/8/4K3 w - - 0 1"));
	}
	
	private Phase getPhase(String fen) {
		final AbstractIncrementalSimplifiedEvaluator<Move, ChessLibMoveGenerator> pd = new MyEval();
		pd.init(FENUtils.from(fen));
		return pd.getState().getPhase();
	}

	@Test
	void testIncrementalThings() {
		// Start with black queen and rook vs a pawn, a knight and a bishop for black => Middle game
		MyEval ev = new MyEval();
		ChessLibMoveGenerator board = FENUtils.from("3qk3/7P/8/8/8/N7/B4r2/4K3 w - - 0 1");
		ev.init(board);
		assertEquals(MIDDLE_GAME, ev.getState().getPhase());
		int expected = 150+290+320-895-510;
		assertEquals(expected, ev.evaluateAsWhite(board));
		
		MyEval forked = (MyEval) ev.fork();
		ChessLibMoveGenerator forkedMg = (ChessLibMoveGenerator) board.fork();
		assertEquals(MIDDLE_GAME, forked.getState().getPhase());
		assertEquals(expected, forked.evaluateAsWhite(board));
		
		// Take black rook => END_GAME
		Move mv = new Move(E1, F2);
		forked.prepareMove(forkedMg, mv);
		assertTrue(forkedMg.makeMove(mv, MoveConfidence.UNSAFE));
		forked.commitMove();
		int forkedExpected1 = expected+510+30; // 30 points because black king has a 30 points position penalty in END_GAME phase
		assertEquals(END_GAME, forked.getState().getPhase());
		assertEquals(forkedExpected1, forked.evaluateAsWhite(forkedMg));
		assertEquals(MIDDLE_GAME, ev.getState().getPhase()); // No side effect
		assertEquals(expected, ev.evaluateAsWhite(board));
		
		// Make a stupid move with the queen :-)
		mv = new Move(D8, E7);
		forked.prepareMove(forkedMg, mv);
		assertTrue(forkedMg.makeMove(mv, MoveConfidence.UNSAFE));
		forked.commitMove();
		int forkedExpected2 = forkedExpected1 - 5;
		assertEquals(END_GAME, forked.getState().getPhase());
		assertEquals(forkedExpected2, forked.evaluateAsWhite(forkedMg));
		assertEquals(MIDDLE_GAME, ev.getState().getPhase()); // Still no side effect
		
		// A promotion to a queen :-) => come back to Middle game
		mv = new Move(H7, H8, Piece.WHITE_QUEEN);
		forked.prepareMove(forkedMg, mv);
		assertTrue(forkedMg.makeMove(mv, MoveConfidence.UNSAFE));
		forked.commitMove();
		int forkedExpected3 = forkedExpected2 + 880 - 150 - 30; // Warning, we change again the phase => king's positions values changes
		assertEquals(forkedExpected3, forked.evaluateAsWhite(forkedMg));
		assertEquals(MIDDLE_GAME, forked.getState().getPhase());
		
		forked.unmakeMove();
		assertEquals(forkedExpected2, forked.evaluateAsWhite(forkedMg));
		forked.unmakeMove();
		assertEquals(forkedExpected1, forked.evaluateAsWhite(forkedMg));
		forked.unmakeMove();
		assertEquals(expected, forked.evaluateAsWhite(forkedMg));
		
		// Other tests on another board
		board = FENUtils.from("4k2r/8/8/8/8/8/6p1/3QK2R b Kk - 0 1");
		ev.init(board);
		assertEquals(745, ev.evaluateAsWhite(board));
		assertEquals(MIDDLE_GAME, ev.getState().getPhase());
		// Test castling
		mv = new Move(E8, G8);
		ev.prepareMove(board, mv);
		ev.commitMove();
		assertEquals(745-30, ev.evaluateAsWhite(board));
		
		// Test promotion with capture
		ev.unmakeMove();
		mv = new Move(G2, H1, Piece.BLACK_QUEEN);
		ev.prepareMove(board, mv);
		ev.commitMove();
		assertEquals(745+150-880-500, ev.evaluateAsWhite(board));
	}
}

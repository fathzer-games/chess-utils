package com.fathzer.chess.utils.evaluators.simplified;

import static org.junit.jupiter.api.Assertions.*;

import java.util.function.IntFunction;

import static com.github.bhlangonijr.chesslib.Square.*;

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

		public MyEval(SimplifiedState state) {
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
		protected AbstractIncrementalSimplifiedEvaluator<Move, ChessLibMoveGenerator> fork(SimplifiedState state) {
			return new MyEval(state);
		}

		@Override
		public SimplifiedState getState() {
			return super.getState();
		}
	}

	@Test
	void testIncrementalThings() {
		// Start with black queen and rook vs a pawn, a knight and a bishop for black => Middle game
		MyEval ev = new MyEval();
		ChessLibMoveGenerator board = FENUtils.from("3qk3/7P/8/8/8/N7/B4r2/4K3 w - - 0 1");
		ev.init(board);
		assertFalse(ev.getState().isEndGamePhase());
		int expected = 150+290+320-895-510;
		assertEquals(expected, ev.evaluateAsWhite(board));
		
		MyEval forked = (MyEval) ev.fork();
		ChessLibMoveGenerator forkedMg = (ChessLibMoveGenerator) board.fork();
		assertFalse(forked.getState().isEndGamePhase());
		assertEquals(expected, forked.evaluateAsWhite(board));
		
		// Take black rook => END_GAME phase
		Move mv = new Move(E1, F2);
		forked.prepareMove(forkedMg, mv);
		assertTrue(forkedMg.makeMove(mv, MoveConfidence.UNSAFE));
		forked.commitMove();
		int forkedExpected1 = expected+510+30; // 30 points because black king has a 30 points position penalty in END_GAME phase
		assertTrue(forked.getState().isEndGamePhase());
		assertEquals(forkedExpected1, forked.evaluateAsWhite(forkedMg));
		assertFalse(ev.getState().isEndGamePhase()); // No side effect
		assertEquals(expected, ev.evaluateAsWhite(board));
		
		// Make a stupid move with the queen :-)
		mv = new Move(D8, E7);
		forked.prepareMove(forkedMg, mv);
		assertTrue(forkedMg.makeMove(mv, MoveConfidence.UNSAFE));
		forked.commitMove();
		int forkedExpected2 = forkedExpected1 - 5;
		assertTrue(forked.getState().isEndGamePhase());
		assertEquals(forkedExpected2, forked.evaluateAsWhite(forkedMg));
		assertFalse(ev.getState().isEndGamePhase()); // Still no side effect
		
		// A promotion to a queen :-) => come back to Middle game
		mv = new Move(H7, H8, Piece.WHITE_QUEEN);
		forked.prepareMove(forkedMg, mv);
		assertTrue(forkedMg.makeMove(mv, MoveConfidence.UNSAFE));
		forked.commitMove();
		int forkedExpected3 = forkedExpected2 + 880 - 150 - 30; // Warning, we change again the phase => king's positions values changes
		assertFalse(forked.getState().isEndGamePhase());
		assertEquals(forkedExpected3, forked.evaluateAsWhite(forkedMg));
		
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
		assertFalse(ev.getState().isEndGamePhase());
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
		
		// Test that illegal move does not throw exception
		ev.unmakeMove();
		mv = new Move(A7, A6);
		ev.prepareMove(board, mv);
		mv = new Move(H8, H1, Piece.BLACK_QUEEN);
		ev.prepareMove(board, mv);
	}
	
	@Test
	void testPositionValuesSymetry() {
		testVerticalSymetry(1, "pawn");	
		testVerticalSymetry(2, "knight");	
		testVerticalSymetry(3, "bishop");	
		testVerticalSymetry(4, "rook");
		
		// Pawel's suggestion with white queen at b3	
		ChessLibMoveGenerator board = FENUtils.from("rnbqkbnr/pp1ppppp/8/8/8/1Q6/PP1PPPPP/RNB1KBNR w KQkq - 0 1");
		MyEval ev = new MyEval();
		ev.init(board);
		assertEquals(10, ev.evaluateAsWhite(board));	
		// Pawel's suggestion with both queen's at b3 and c7	
		board = FENUtils.from("rnb1kbnr/ppqppppp/8/8/8/1Q6/PP1PPPPP/RNB1KBNR w KQkq - 0 1");
		ev.init(board);
		assertEquals(0, ev.evaluateAsWhite(board));	
	}
	
	private void testVerticalSymetry(int pieceKind, String name) {	
		testVerticalSymetry(name, i -> new PiecesOnlySquareTable().get(pieceKind, i));	
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

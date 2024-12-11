package com.fathzer.chess.utils.evaluators.simplified;

import static org.junit.jupiter.api.Assertions.*;

import java.util.function.IntFunction;

import static com.github.bhlangonijr.chesslib.Square.*;

import org.junit.jupiter.api.Test;

import com.fathzer.chess.test.utils.FENUtils;
import com.fathzer.chess.utils.adapters.BoardExplorer;
import com.fathzer.chess.utils.adapters.BoardExplorerBuilder;
import com.fathzer.chess.utils.adapters.MoveData;
import com.fathzer.chess.utils.adapters.chesslib.ChessLibBoardExplorer;
import com.fathzer.chess.utils.adapters.chesslib.ChessLibMoveData;
import com.fathzer.chess.utils.adapters.chesslib.ChessLibMoveGenerator;
import com.fathzer.chess.utils.evaluators.simplified.two.PiecesOnlySquareTable;
import com.fathzer.games.MoveGenerator.MoveConfidence;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.move.Move;

class AbstractIncrementalSimplifiedEvaluatorTest2 {

	private final class MyEval extends PiecesOnlySquareTable<Move, ChessLibMoveGenerator> implements BoardExplorerBuilder<ChessLibMoveGenerator> {
		private MoveData<Move, ChessLibMoveGenerator> moveData = new ChessLibMoveData();
		
		public MyEval() {
			super();
		}

		public MyEval(int state) {
			super(state);
		}

		@Override
		public void prepareMove(ChessLibMoveGenerator board, Move move) {
			moveData.update(move, board);
			prepareMove(moveData);
		}

		@Override
		public BoardExplorer getExplorer(ChessLibMoveGenerator board) {
			return new ChessLibBoardExplorer(board.getBoard());
		}

		@Override
		protected PiecesOnlySquareTable<Move, ChessLibMoveGenerator> fork(int state) {
			return new MyEval(state);
		}
	}
	
	private int getStaticEval(ChessLibMoveGenerator board) {
		MyEval eval = new MyEval();
		eval.init(board);
		return eval.evaluateAsWhite(board);
	}

	@Test
	void testIncrementalThings() {
		Move mv;
		// Start with black queen and rook vs a pawn, a knight and a bishop for black => Middle game
		MyEval ev = new MyEval();
		ChessLibMoveGenerator board = FENUtils.from("3qk3/7P/8/8/8/N7/B4r2/4K3 w - - 0 1");
/*		ev.init(board);
		int expected = 150+290+320-895-510;
System.out.println(expected);
		assertEquals(expected, ev.evaluateAsWhite(board));
		
		MyEval forked = (MyEval) ev.fork();
		ChessLibMoveGenerator forkedMg = (ChessLibMoveGenerator) board.fork();
		assertEquals(expected, forked.evaluateAsWhite(board));
		
		// Take black rook => END_GAME
		mv = new Move(E1, F2);
		forked.prepareMove(forkedMg, mv);
		assertTrue(forkedMg.makeMove(mv, MoveConfidence.UNSAFE));
		forked.commitMove();
		int forkedExpected1 = expected+510;
		{
			ChessLibMoveGenerator b = (ChessLibMoveGenerator) board.fork();
			b.makeMove(mv, MoveConfidence.UNSAFE);
			assertEquals(forkedExpected1, getStaticEval(b));
		}
		assertEquals(forkedExpected1, forked.evaluateAsWhite(forkedMg));
		assertEquals(expected, ev.evaluateAsWhite(board));
		
		// Make a stupid move with the queen :-)
		mv = new Move(D8, E7);
		forked.prepareMove(forkedMg, mv);
		assertTrue(forkedMg.makeMove(mv, MoveConfidence.UNSAFE));
		forked.commitMove();
		int forkedExpected2 = forkedExpected1 - 5;
		assertEquals(forkedExpected2, forked.evaluateAsWhite(forkedMg));
		
		// A promotion to a queen :-) => come back to Middle game
		mv = new Move(H7, H8, Piece.WHITE_QUEEN);
		forked.prepareMove(forkedMg, mv);
		assertTrue(forkedMg.makeMove(mv, MoveConfidence.UNSAFE));
		forked.commitMove();
		int forkedExpected3 = forkedExpected2 + 880 - 150;
		assertEquals(forkedExpected3, forked.evaluateAsWhite(forkedMg));
		
		forked.unmakeMove();
		assertEquals(forkedExpected2, forked.evaluateAsWhite(forkedMg));
		forked.unmakeMove();
		assertEquals(forkedExpected1, forked.evaluateAsWhite(forkedMg));
		forked.unmakeMove();
		assertEquals(expected, forked.evaluateAsWhite(forkedMg));
*/		
		// Other tests on another board
		board = FENUtils.from("r3k3/8/8/8/8/8/1p6/R3KQ2 b q - 1 1");
		ev.init(board);
		assertEquals(740, ev.evaluateAsWhite(board));
		// Test castling
		mv = new Move(E8, C8);
		{
			ChessLibMoveGenerator b = (ChessLibMoveGenerator) board.fork();
			b.makeMove(mv, MoveConfidence.UNSAFE);
			assertEquals(740-5, getStaticEval(b));
		}
		ev.prepareMove(board, mv);
		ev.commitMove();
		assertEquals(740-5, ev.evaluateAsWhite(board));
		
		// Test promotion with capture
		ev.unmakeMove();
		mv = new Move(B2, A1, Piece.BLACK_QUEEN);
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
		testVerticalSymetry(name, i -> SimplifiedEvaluatorBase.getPositionValue(pieceKind, i));	
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

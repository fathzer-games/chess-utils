package com.fathzer.chess.utils.evaluators;

import static com.fathzer.games.Color.WHITE;
import static com.github.bhlangonijr.chesslib.Square.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.fathzer.chess.utils.adapters.chesslib.ChessLibAdapter;
import com.fathzer.games.Color;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.move.Move;

class NaiveEvaluatorTest {
	private static class ChessLibNaiveEvaluator extends NaiveEvaluator<Move, Board> implements ChessLibAdapter {
		protected ChessLibNaiveEvaluator(Board board) {
			super(board);
		}
		
		private ChessLibNaiveEvaluator(int score) {
			super(score);
		}

		@Override
		public void setViewPoint(Color color) {
			if (color==null) {
				this.viewPoint = 0;
			} else {
				this.viewPoint = color==WHITE ? 1 : -1;
			}
		}

		@Override
		public NaiveEvaluator<Move, Board> fork(int score) {
			return new ChessLibNaiveEvaluator(score);
		}
	}

	private static class ATest {
		private final ChessLibNaiveEvaluator eval;
		private final Board mvg;
		
		private ATest(String fen, Color viewPoint, int expectedEval) {
			this.mvg = new Board();
			mvg.loadFromFen(fen);
			this.eval = new ChessLibNaiveEvaluator(mvg);
			eval.setViewPoint(viewPoint);
			assertEquals(expectedEval, eval.evaluate(mvg));
		}
		
		private void test(Move move, int expectedEval) {
			eval.prepareMove(mvg, move);
			assertTrue(mvg.doMove(move, true));
			eval.commitMove();
			final int incEvaluation = eval.evaluate(mvg);
			if (expectedEval!=incEvaluation) {
				mvg.undoMove();
				eval.unmakeMove();
			}
			assertEquals (expectedEval, incEvaluation, "Error for move "+move+" on "+mvg.getFen());
		}
		
		private int unmakeMove() {
			mvg.undoMove();
			eval.unmakeMove();
			return eval.evaluate(mvg);
		}
	}
	
	@Test
	void testBlack() {
		ATest test = new ATest("rn1qkb1r/1ppb1ppp/4pn2/pP1p4/3P1B2/4P3/P1P2PPP/RN1QKBNR w KQkq a6 0 6", Color.BLACK, 0);
		// En passant from white
		test.test(new Move(B5, A6), -100);
		// No capture from black
		test.test(new Move(F8, D6), -100);
		// Capture from white
		test.test(new Move(F4, D6), -400);
		// Capture from black
		test.test(new Move(C7, D6), -100);
		// No Capture from white
		test.test(new Move(B1, C3), -100);
		// Castling from black
		test.test(new Move(E8, G8), -100);
		// Unmake moves
		assertEquals(-100,test.unmakeMove());
		assertEquals(-100,test.unmakeMove());
		assertEquals(-400,test.unmakeMove());
		assertEquals(-100,test.unmakeMove());
		assertEquals(-100,test.unmakeMove());
		assertEquals(0,test.unmakeMove());
	}

	@Test
	void testWhite() {
		ATest test = new ATest("r2qkb1r/1ppb1ppp/4pn2/pP1p4/3P1B2/4P3/P1P2PPP/RN1QKBNR w KQkq a6 0 6", Color.WHITE, 300);
		// En passant from white
		test.test(new Move(B5, A6), 400);
		// No capture from black
		test.test(new Move(F8, D6), 400);
		// Capture from white
		test.test(new Move(F4, D6), 700);
		// Capture from black
		test.test(new Move(C7, D6), 400);
		// No Capture from white
		test.test(new Move(B1,B3), 400);
		// Castling from black
		test.test(new Move(E8, G8), 400);
	}

	@Test
	void testCurrentPlayer() {
		ATest test = new ATest("rn1qkb1r/1ppb1ppp/4pn2/pP1p4/3P1B2/4P3/P1P2PPP/RN1QKBNR w KQkq a6 0 6", null, 0);
		// En passant from white
		test.test(new Move(B5, A6), -100);
		// No capture from black
		test.test(new Move(F8, D6), 100);
		// Capture from white
		test.test(new Move(F4, D6), -400);
		// Capture from black
		test.test(new Move(C7, D6), 100);
		// No Capture from white
		test.test(new Move(B1,B3), -100);
		// Castling from black
		test.test(new Move(E8, G8), 100);
	}
	
	@Test
	void theBlackPromotionCase() {
		ATest test = new ATest("8/4P1n1/8/5P2/8/QK5k/1P3p2/8 b - - 0 1", Color.WHITE, 800);
		// En passant from white
		test.test(new Move(F2, F1, Piece.BLACK_QUEEN), 0);
	}
}

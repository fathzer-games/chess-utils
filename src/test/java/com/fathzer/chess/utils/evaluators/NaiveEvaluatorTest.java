package com.fathzer.chess.utils.evaluators;

import static com.github.bhlangonijr.chesslib.Square.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.fathzer.chess.test.utils.FENUtils;
import com.fathzer.chess.utils.adapters.chesslib.ChessLibMoveGenerator;
import com.fathzer.games.MoveGenerator.MoveConfidence;
import com.fathzer.games.ai.evaluation.Evaluator;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.move.Move;

class NaiveEvaluatorTest {
	private static class ATest {
		private final ChessLibNaiveEvaluator eval;
		private final ChessLibMoveGenerator mvg;
		
		private ATest(String fen, int expectedEval) {
			this.mvg = FENUtils.from(fen);
			this.eval = new ChessLibNaiveEvaluator();
			this.eval.init(mvg);
			assertEquals(expectedEval, eval.evaluate(mvg));
		}
		
		private void test(Move move, int expectedEval) {
			eval.prepareMove(mvg, move);
			assertTrue(mvg.makeMove(move, MoveConfidence.UNSAFE));
			eval.commitMove();
			final int incEvaluation = eval.evaluate(mvg);
			if (expectedEval!=incEvaluation) {
				mvg.unmakeMove();
				eval.unmakeMove();
			}
			assertEquals (expectedEval, incEvaluation, "Error for move "+move+" on "+mvg.getBoard().getFen());
		}
	}
	
	@Test
	void testCurrentPlayer() {
		ATest test = new ATest("rn1qkb1r/1ppb1ppp/4pn2/pP1p4/3P1B2/4P3/P1P2PPP/RN1QKBNR w KQkq a6 0 6", 0);
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
		ATest test = new ATest("8/4P1n1/8/8/8/QK5k/1P3p2/8 b - - 0 1", -700);
		// En passant from white
		test.test(new Move(F2, F1, Piece.BLACK_QUEEN), -100);
	}
	
	@Test
	void testFork() {
		ChessLibMoveGenerator mvg = FENUtils.from("r2qkb1r/1ppb1ppp/4pn2/pP1p4/3P1B2/4P3/P1P2PPP/RN1QKBNR w KQkq a6 0 6");
		Evaluator<Move, ChessLibMoveGenerator> eval = new ChessLibNaiveEvaluator();
		eval.init(mvg);
		assertEquals(300, eval.evaluate(mvg));
		
		ChessLibMoveGenerator mvg2 = (ChessLibMoveGenerator) mvg.fork();
		Evaluator<Move, ChessLibMoveGenerator> eval2 = eval.fork();
		assertEquals(300, eval2.evaluate(mvg2));
		// En passant from white
		Move move = new Move(B5, A6);
		eval2.prepareMove(mvg2, move);
		assertTrue(mvg2.makeMove(move, MoveConfidence.UNSAFE));
		eval2.commitMove();
		assertEquals(-400, eval2.evaluate(mvg2));
		assertEquals(300, eval.evaluate(mvg));
	}
}

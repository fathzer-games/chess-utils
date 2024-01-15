package com.fathzer.chess.utils.evaluators;

import static com.github.bhlangonijr.chesslib.Square.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.fathzer.chess.test.utils.FENUtils;
import com.fathzer.chess.utils.adapters.BoardExplorer;
import com.fathzer.chess.utils.adapters.chesslib.BasicMoveDecoder;
import com.fathzer.chess.utils.adapters.chesslib.ChessLibBoardExplorer;
import com.fathzer.chess.utils.adapters.chesslib.ChessLibMoveGenerator;
import com.fathzer.games.Color;
import com.fathzer.games.MoveGenerator.MoveConfidence;
import com.fathzer.games.ai.evaluation.Evaluator;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.move.Move;

class NaiveEvaluatorTest {
	private static class ChessLibNaiveEvaluator extends AbstractNaiveEvaluator<Move, ChessLibMoveGenerator> {
		protected ChessLibNaiveEvaluator() {
			super();
		}
		
		private ChessLibNaiveEvaluator(int score) {
			super(score);
		}

		@Override
		public AbstractNaiveEvaluator<Move, ChessLibMoveGenerator> fork(int score) {
			final ChessLibNaiveEvaluator result = new ChessLibNaiveEvaluator(score);
			result.viewPoint = this.viewPoint;
			return result;
		}

		@Override
		public BoardExplorer getExplorer(ChessLibMoveGenerator board) {
			return new ChessLibBoardExplorer(board.getBoard());
		}

		@Override
		protected int getCapturedType(ChessLibMoveGenerator board, Move move) {
			return BasicMoveDecoder.getCapturedType(board, move);
		}

		@Override
		protected int getPromotionType(ChessLibMoveGenerator board, Move move) {
			return BasicMoveDecoder.getPromotionType(board, move);
		}
	}

	private static class ATest {
		private final ChessLibNaiveEvaluator eval;
		private final ChessLibMoveGenerator mvg;
		
		private ATest(String fen, Color viewPoint, int expectedEval) {
			this.mvg = FENUtils.from(fen);
			this.eval = new ChessLibNaiveEvaluator();
			this.eval.init(mvg);
			eval.setViewPoint(viewPoint);
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
		
		private int unmakeMove() {
			mvg.unmakeMove();
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
	
	@Test
	void testFork() {
		ChessLibMoveGenerator mvg = FENUtils.from("r2qkb1r/1ppb1ppp/4pn2/pP1p4/3P1B2/4P3/P1P2PPP/RN1QKBNR w KQkq a6 0 6");
		Evaluator<Move, ChessLibMoveGenerator> eval = new ChessLibNaiveEvaluator();
		eval.init(mvg);
		eval.setViewPoint(Color.BLACK);
		assertEquals(-300, eval.evaluate(mvg));
		
		ChessLibMoveGenerator mvg2 = (ChessLibMoveGenerator) mvg.fork();
		Evaluator<Move, ChessLibMoveGenerator> eval2 = eval.fork();
		assertEquals(-300, eval2.evaluate(mvg2));
		// En passant from white
		Move move = new Move(B5, A6);
		eval2.prepareMove(mvg, move);
		assertTrue(mvg2.makeMove(move, MoveConfidence.UNSAFE));
		eval2.commitMove();
		assertEquals(-400, eval2.evaluate(mvg2));
		assertEquals(-300, eval.evaluate(mvg));
	}
}

package com.fathzer.chess.utils.evaluators.quiesce;

import static com.github.bhlangonijr.chesslib.Square.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.fathzer.chess.utils.adapters.chesslib.ChessLibMoveGenerator;
import com.fathzer.chess.utils.evaluators.ChessLibNaiveEvaluator;
import com.fathzer.games.MoveGenerator.MoveConfidence;
import com.fathzer.games.ai.SearchContext;
import com.fathzer.games.ai.evaluation.Evaluator;
import com.fathzer.games.ai.evaluation.QuiesceEvaluator;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.move.Move;

class MinimaxTest {
	static class SimpleQuiesce extends AbstractBasicQuiesceEvaluator<Move, ChessLibMoveGenerator> {
		private SimpleQuiesce() {
			super();
		}

		@Override
		protected boolean isCheck(SearchContext<Move, ChessLibMoveGenerator> context) {
			return context.getGamePosition().getBoard().isKingAttacked();
		}

		@Override
		protected List<Move> getMoves(SearchContext<Move, ChessLibMoveGenerator> context, int quiesceDepth) {
			final Board board = context.getGamePosition().getBoard();
			return isCheck(context) ? board.pseudoLegalMoves() : board.pseudoLegalCaptures();
		}
	}

	@Test
	void testQuiesce() {
		final Evaluator<Move, ChessLibMoveGenerator> ev = new ChessLibNaiveEvaluator();
		final Board board = new Board();
		board.loadFromFen("3n1rk1/1pp2p1p/2r2bq1/2P1p1p1/3pP3/PQ1P2PP/1R3PB1/2B2RK1 w - - 2 26");
		final ChessLibMoveGenerator mg = new ChessLibMoveGenerator(board);
		final SearchContext<Move, ChessLibMoveGenerator> context = SearchContext.get(mg, ()->ev);
		final QuiesceEvaluator<Move, ChessLibMoveGenerator> qev = new SimpleQuiesce();
		
		assertTrue(context.makeMove(new Move(B3, F7), MoveConfidence.UNSAFE));
		assertEquals(800, qev.evaluate(context, 0, Integer.MIN_VALUE+1, Integer.MAX_VALUE));
		context.unmakeMove();

		assertTrue(context.makeMove(new Move(B3, B7), MoveConfidence.UNSAFE));
		assertEquals(600, qev.evaluate(context, 0, Integer.MIN_VALUE+1, Integer.MAX_VALUE));
		context.unmakeMove();
	}
	
	@Test
	void testQuiesceOnCheck() {
		final Evaluator<Move, ChessLibMoveGenerator> ev = new ChessLibNaiveEvaluator();
		final Board board = new Board();
		final ChessLibMoveGenerator mg = new ChessLibMoveGenerator(board);
		final QuiesceEvaluator<Move, ChessLibMoveGenerator> qev = new SimpleQuiesce();

		board.loadFromFen("rnbqkr2/pppp1p1p/5p1b/6pn/8/BP2R2P/P1PPPPP1/RN1QKBN1 b - - 0 1");
		assertEquals(-900, qev.evaluate(SearchContext.get(mg, ()->ev), 0, Integer.MIN_VALUE+1, Integer.MAX_VALUE));
		
		board.loadFromFen("rnb1krq1/ppBp1p1p/2p2p1b/6pn/8/1P2R2P/P1PPPPP1/RN1QKBN1 b - - 0 1");
		assertEquals(-ev.getWinScore(0), qev.evaluate(SearchContext.get(mg, ()->ev), 0, Integer.MIN_VALUE+1, Integer.MAX_VALUE));
	}
}

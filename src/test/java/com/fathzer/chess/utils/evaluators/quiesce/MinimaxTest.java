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
		protected List<Move> getMoves(SearchContext<Move, ChessLibMoveGenerator> context, int quiesceDepth) {
			return context.getGamePosition().getBoard().pseudoLegalCaptures();
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
		assertEquals(800, qev.evaluate(context, Integer.MIN_VALUE+1, Integer.MAX_VALUE));
		context.unmakeMove();

		assertTrue(context.makeMove(new Move(B3, B7), MoveConfidence.UNSAFE));
		assertEquals(600, qev.evaluate(context, Integer.MIN_VALUE+1, Integer.MAX_VALUE));
		context.unmakeMove();
	}
}

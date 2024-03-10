package com.fathzer.chess.utils.evaluators.quiesce;

import java.util.List;

import com.fathzer.games.MoveGenerator;
import com.fathzer.games.MoveGenerator.MoveConfidence;
import com.fathzer.games.ai.SearchContext;
import com.fathzer.games.ai.SearchStatistics;
import com.fathzer.games.ai.evaluation.QuiesceEvaluator;

/** A basic quiescence search evaluator.
 */
public abstract class AbstractBasicQuiesceEvaluator<M, B extends MoveGenerator<M>> implements QuiesceEvaluator<M,B> {
	/** Constructor.
	 */
	protected AbstractBasicQuiesceEvaluator() {
		super();
	}

	@Override
	public int evaluate(SearchContext<M, B> context, int depth, int alpha, int beta) {
		return quiesce(context, alpha, beta, depth, 0);
	}

	private int quiesce(SearchContext<M, B> context, int alpha, int beta, int rootDepth, int quiesceDepth) {
		final SearchStatistics statistics = context.getStatistics();
		final boolean check = isCheck(context);
		if (!check) {
			final int standPat = context.getEvaluator().evaluate(context.getGamePosition());
			statistics.evaluationDone();
			if (standPat>=beta) {
				return beta;
			}
			if (alpha < standPat) {
				alpha = standPat;
			}
		}
		final List<M> moves = getMoves(context, quiesceDepth);
    	statistics.movesGenerated(moves.size());
    	boolean mate = check;
        for (M move : moves) {
            if (makeMove(context, move)) {
            	mate = false;
                statistics.movePlayed();
	            final int score = -quiesce(context, -beta, -alpha, rootDepth, quiesceDepth+1);
	            context.unmakeMove();
	            if (score >= beta) {
	                return beta;
	            }
	            if (score > alpha) {
	            	alpha = score;
	            }
            }
        }
       	return mate ? -context.getEvaluator().getWinScore(rootDepth+quiesceDepth) : alpha;
	}
	
	protected abstract boolean isCheck(SearchContext<M, B> context);
	
	/** Gets the list of quiesce moves.
	 * @param context The search context (can be used to get the board)
	 * @param quiesceDepth The quiesce depth. 0 for the first level
	 * @return A list of moves to analyze deeper, an empty list to stop deepening.
	 */
	protected abstract List<M> getMoves(SearchContext<M, B> context, int quiesceDepth);
	
	/** Make a move.
	 * <br>The default implementation make the move with a {@link MoveConfidence#PSEUDO_LEGAL} confidence.
	 * @param context The context on which to apply the move
	 * @param move The move to play
	 * @return true if the move was successfully played, false if the move is illegal
	 */
	protected boolean makeMove(SearchContext<M, B> context, M move) {
		return context.makeMove(move, MoveConfidence.PSEUDO_LEGAL);
	}
}

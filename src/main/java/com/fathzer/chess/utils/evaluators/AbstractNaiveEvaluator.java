package com.fathzer.chess.utils.evaluators;

import static com.fathzer.chess.utils.Pieces.*;

import com.fathzer.chess.utils.adapters.MoveAdapter;
import com.fathzer.chess.utils.adapters.BoardExplorerBuilder;
import com.fathzer.games.MoveGenerator;
import com.fathzer.games.ai.evaluation.AbstractEvaluator;
import com.fathzer.games.ai.evaluation.Evaluator;
import com.fathzer.games.util.Stack;

/** A naive <a href="https://www.chessprogramming.org/Incremental_Updates">incremental</a> evaluator that only uses the <a href="https://en.wikipedia.org/wiki/Chess_piece_relative_value">standard valuation of pieces</a>. 
 * @param <M> The type of moves
 * @param <B> The type of chess board
 */
public abstract class AbstractNaiveEvaluator<M, B extends MoveGenerator<M>> extends AbstractEvaluator<M, B> implements Evaluator<M, B>, MoveAdapter<M, B>, BoardExplorerBuilder<B> {
	private final Stack<Integer> scores;
	private int toCommit;
	
	/** Default constructor
	 */
	protected AbstractNaiveEvaluator() {
		this.scores = new Stack<>(null);
	}
	
	/** Constructor.
	 * @param score The score to initialize the evaluator
	 */
	protected AbstractNaiveEvaluator(int score) {
		this.scores = new Stack<>(null);
		scores.set(score);
	}
		
	@Override
	public void init(B board) {
		scores.clear();
		scores.set(getPieces(board).map(p -> p>0?getPoints(p):-getPoints(-p)).sum());
	}
	
	@Override
	public int evaluateAsWhite(B board) {
		return 100*scores.get();
	}
	
	/** Creates a new instance initialized with a score.
	 * @param score The initial score.
	 * @return a new evaluator of the same class as this, this the same view point, and initialized with the score.
	 */
	protected abstract AbstractNaiveEvaluator<M, B> fork(int score);
	
	@Override
	public AbstractNaiveEvaluator<M, B> fork() {
		return fork(scores.get());
	}

	@Override
	public void prepareMove(B board, M move) {
		int increment = 0;
        final int capturedPiece = getCapturedType(board, move);
        if (capturedPiece!=0) {
	        increment = getPoints(capturedPiece);
        }
        final int promotion = getPromotionType(board, move);
        if (promotion!=0) {
	        increment = increment + getPoints(promotion)-getPoints(PAWN);
        }
		if (!board.isWhiteToMove()) {
			increment = -increment;
		}
		toCommit = scores.get()+increment;
	}
	
	@Override
	public void commitMove() {
		scores.next();
		scores.set(toCommit);
	}

	@Override
	public void unmakeMove() {
		scores.previous();
	}
}

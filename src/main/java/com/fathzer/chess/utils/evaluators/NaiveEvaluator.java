package com.fathzer.chess.utils.evaluators;

import com.fathzer.chess.utils.adapters.ColorAdapter;
import com.fathzer.chess.utils.adapters.MoveAdapter;
import com.fathzer.chess.utils.adapters.PieceEvaluator;
import com.fathzer.chess.utils.adapters.PieceStreamer;
import com.fathzer.games.ai.evaluation.Evaluator;
import com.fathzer.games.util.Stack;

/** A naive <a href="https://www.chessprogramming.org/Incremental_Updates">incremental</a> evaluator that only uses the <a href="https://en.wikipedia.org/wiki/Chess_piece_relative_value">standard valuation of pieces</a>. 
 * @param <M> The type of moves
 * @param <B> The type of chess board
 * @param <C> The class of the color
 * @param <P> The class of a Piece
 */
public abstract class NaiveEvaluator<M, B, C, P> implements Evaluator<M, B>, ColorAdapter<B,C,P>, MoveAdapter<M, B, P>, PieceStreamer<B, P>, PieceEvaluator<P> {
	private final Stack<Integer> scores;
	private int toCommit;
	/** The evaluator point of view.
	 */
	protected C viewPoint;
	
	private NaiveEvaluator() {
		this.scores = new Stack<>(null);
	}
	
	/** Constructor.
	 * @param board The board that will be evaluated.
	 * <br>This board is only used to get the initial estimation.
	 */
	protected NaiveEvaluator(B board) {
		this();
		scores.set(getPieces(board).mapToInt(p -> isWhite(getColor(p))?getValue(p):-getValue(p)).sum());
	}
	
	/** Constructor.
	 * @param score The score to initialize the evaluator
	 */
	protected NaiveEvaluator(int score) {
		this.scores = new Stack<>(null);
		scores.set(score);
	}
		
	@Override
	public int evaluate(B board) {
		int points = 100*scores.get();
		if (!isWhite(viewPoint==null?getSideToMove(board):viewPoint)) {
			points = -points;
		}
		return points;
	}
	
	/** Creates a new instance initialized with a score.
	 * @param score The initial score.
	 * @return a new evaluator of the same class as this, initialized with the score.
	 */
	public abstract NaiveEvaluator<M, B, C, P> fork(int score);
	
	@Override
	public NaiveEvaluator<M, B, C, P> fork() {
		return fork(scores.get());
	}

	@Override
	public void prepareMove(B board, M move) {
		int increment = 0;
        final P movingPiece = getMoving(board, move);
        final P capturedPiece = getCaptured(board, move);
        if (!isNone(capturedPiece)) {
	        increment = getValue(capturedPiece);
        }
        final P promotion = getPromotion(board, move);
        if (!isNone(promotion)) {
	        increment = increment + getValue(promotion)-getValue(movingPiece);
        }
		if (!isWhite(getColor(movingPiece))) {
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

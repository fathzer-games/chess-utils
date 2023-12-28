package com.fathzer.chess.utils.evaluators;

import static com.fathzer.chess.utils.Pieces.getPoints;

import com.fathzer.chess.utils.adapters.MoveAdapter;
import com.fathzer.chess.utils.adapters.PieceStreamer;
import com.fathzer.games.ai.evaluation.Evaluator;
import com.fathzer.games.util.Stack;

/** A naive <a href="https://www.chessprogramming.org/Incremental_Updates">incremental</a> evaluator that only uses the <a href="https://en.wikipedia.org/wiki/Chess_piece_relative_value">standard valuation of pieces</a>. 
 * @param <M> The type of moves
 * @param <B> The type of chess board
 */
public abstract class NaiveEvaluator<M, B> implements Evaluator<M, B>, MoveAdapter<M, B>, PieceStreamer<B> {
	private final Stack<Integer> scores;
	private int toCommit;
	/** The evaluator point of view (1 for white, -1 for black, 0 for current player.
	 */
	protected int viewPoint;
	
	private NaiveEvaluator() {
		this.scores = new Stack<>(null);
	}
	
	/** Constructor.
	 * @param board The board that will be evaluated.
	 * <br>This board is only used to get the initial estimation.
	 */
	protected NaiveEvaluator(B board) {
		this();
		scores.set(getPieces(board).mapToInt(p -> p>0?getPoints(p):-getPoints(-p)).sum());
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
		if ((viewPoint==0 && !isWhiteToMove(board)) || viewPoint<0) {
			points = -points;
		}
		return points;
	}
	
	/** Creates a new instance initialized with a score.
	 * @param score The initial score.
	 * @return a new evaluator of the same class as this, initialized with the score.
	 */
	public abstract NaiveEvaluator<M, B> fork(int score);
	
	@Override
	public NaiveEvaluator<M, B> fork() {
		return fork(scores.get());
	}

	@Override
	public void prepareMove(B board, M move) {
		int increment = 0;
        final int capturedPiece = getCapturedType(board, move);
        if (capturedPiece!=0) {
	        increment = getPoints(capturedPiece);
        }
        final int movingPiece = getMovingPiece(board, move);
        final int promotion = getPromotionType(board, move);
        if (promotion!=0) {
	        increment = increment + getPoints(promotion)-getPoints(Math.abs(movingPiece));
        }
		if (movingPiece<0) {
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

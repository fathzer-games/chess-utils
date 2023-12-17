package com.fathzer.chess.utils;

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
	protected C viewPoint;
	
	private NaiveEvaluator() {
		this.scores = new Stack<>(null);
	}
	
	protected NaiveEvaluator(B board) {
		this();
		scores.set(getPoints(board));
	}
	
	protected NaiveEvaluator(int score) {
		this.scores = new Stack<>(null);
		scores.set(score);
	}
	
	public boolean hasViewPoint() {
		return viewPoint!=null;
	}
	
	@Override
	public int evaluate(B board) {
		int points = 100*scores.get();
		if (!isWhite(viewPoint==null?getSideToMove(board):viewPoint)) {
			points = -points;
		}
		return points;
	}
	
	public int getPoints(B board) {
		return getPieces(board).filter(p -> !isNone(p)).mapToInt(p -> isWhite(getColor(p))?getValue(p):-getValue(p)).sum();
	}
	
	public abstract Evaluator<M, B> fork(int score);
	
	@Override
	public Evaluator<M, B> fork() {
		return fork(scores.get());
	}

	@Override
	public void prepareMove(B board, M move) {
		int increment = 0;
		if (!isCastle(board, move)) {
	        final P movingPiece = getMovingPiece(board, move);
	        final P capturedPiece = getCapturedPiece(board, move);
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

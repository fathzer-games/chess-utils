package com.fathzer.chess.utils;

import java.util.function.ToIntFunction;

import com.fathzer.chess.utils.adapters.MoveAdapter;
import com.fathzer.chess.utils.adapters.PieceEvaluator;

/** A move evaluator that considers a catch is better than other moves and taking a high value piece with a small value piece is better than the opposite.
 * @param <M> The type of moves
 * @param <B> The type of chess board
 * @param <P> The class of a Piece
 */
public abstract class DefaultMoveEvaluator<M, B, P> implements ToIntFunction<M>, PieceEvaluator<P>, MoveAdapter<M, B, P> {
	/** The chess board on which the comparison are made.
	 */
	protected final B board;
	
	/** Constructor.
	 * @param board The chess board on which the comparison is made.
	 */
	protected DefaultMoveEvaluator(B board) {
		this.board = board;
	}

	/** Gets the value of a move.
	 * @param move The move
	 * @return a value for a move.
	 * <br>The default implementation guarantees that the returned value is always &gt;=0, and &gt;0 for promotions and captures.
	 */
	@Override
	public int applyAsInt(M move) {
		final P promotion = getPromotion(board, move);
		int value = isNone(promotion) ? 0 : (getValue(promotion)-1)*16;
		final P captured = getCaptured(board, move);
		if (isNone(captured)) {
			return value==0 ? Integer.MIN_VALUE : value;
		} else {
			value += getValue(captured)*16;
			final P catching = getMoving(board, move);
			return value - getValue(catching);
		}
	}
}

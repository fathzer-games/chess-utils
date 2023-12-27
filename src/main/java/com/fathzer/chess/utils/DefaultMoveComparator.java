package com.fathzer.chess.utils;

import com.fathzer.chess.utils.adapters.MoveAdapter;
import com.fathzer.chess.utils.adapters.PieceEvaluator;
import com.fathzer.games.util.SelectiveComparator;

/** A selective move comparator that considers a catch is better than other moves and taking a high value piece with a small value piece is better than the opposite and all other moves are equivalent.
 * @param <M> The type of moves
 * @param <B> The type of chess board
 * @param <P> The class of a Piece
 */
public abstract class DefaultMoveComparator<M, B, P> implements SelectiveComparator<M>, PieceEvaluator<P>, MoveAdapter<M, B, P> {
	/** The chess board on which the comparison are made.
	 */
	protected final B board;
	
	/** Constructor.
	 * @param board The chess board on which the comparison is made.
	 */
	protected DefaultMoveComparator(B board) {
		this.board = board;
	}
	
	@Override
	public int compare(M o1, M o2) {
		return Integer.compare(evaluate(o2), evaluate(o1));
	}

	@Override
	public boolean test(M t) {
		return evaluate(t)!=0;
	}

	/** Gets the value of a move.
	 * @param move The move
	 * @return a value for a move.
	 * <br>The default implementation guarantees that the returned value is always &gt;=0, and &gt;0 for promotions and captures.
	 */
	public int evaluate(M move) {
		final P promotion = getPromotion(board, move);
		int value = isNone(promotion) ? 0 : (getValue(promotion)-1)*16;
		final P captured = getCaptured(board, move);
		if (isNone(captured)) {
			return value;
		} else {
			value += getValue(captured)*16;
			final P catching = getMoving(board, move);
			return value - getValue(catching);
		}
	}
}

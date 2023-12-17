package com.fathzer.chess.utils;

import java.util.Comparator;

import com.fathzer.chess.utils.adapters.PieceEvaluator;

/** A move comparator that considers a catch is better than other moves and taking a high value piece with a small value piece is better than the opposite.
 */
public abstract class DefaultMoveComparator<M, B, P> implements Comparator<M>, PieceEvaluator<P> {
	protected final B board;
	
	protected DefaultMoveComparator(B board) {
		this.board = board;
	}

	@Override
	public int compare(M m1, M m2) {
		// Important sort from higher to lower scores
		return getMoveValue(m2) - getMoveValue(m1);
	}

	public int getMoveValue(M m) {
		final P promotion = getPromotion(m);
		int value = isNone(promotion) ? 0 : (getValue(promotion)-1)*16;
		final P captured = getCaptured(m);
		if (isNone(captured)) {
			return value;
		} else {
			value += getValue(captured)*16;
			final P catching = getMoving(m);
			return value - getValue(catching);
		}
	}
	
	protected abstract P getMoving(M move);
	protected abstract P getPromotion(M move);
	protected abstract P getCaptured(M move);
}

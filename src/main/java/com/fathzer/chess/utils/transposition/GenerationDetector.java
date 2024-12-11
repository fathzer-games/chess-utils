package com.fathzer.chess.utils.transposition;

import java.util.function.Predicate;

/** A generation detector that triggers a new generation if a pawn is move or a capture occurred.
 */
public abstract class GenerationDetector<B> implements Predicate<B> {
	private long lastHash;
	
	@Override
	public boolean test(B t) {
		final long hash = getHash(t);
		if (hash==lastHash) {
			// Seems the position didn't changes
			return false;
		}
		lastHash = hash;
		return getHalfMoveCount(t)<2;
	}
	
	protected abstract long getHash(B board);
	protected abstract int getHalfMoveCount(B board);
}

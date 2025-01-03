package com.fathzer.chess.utils.transposition;

import java.util.function.Predicate;

/** An abstract generation detector that triggers a new generation if a pawn is moved or a capture occurred.
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
	
	/** Gets the <a href="https://en.wikipedia.org/wiki/Zobrist_hashing">hash key</a> of a board.
	 * @param board The board
	 * @return a long
	 */
	protected abstract long getHash(B board);
	
	/** Gets the current <a href="https://www.chessprogramming.org/Halfmove_Clock">half moves counter</a> of a board.
	 * @param board The board
	 * @return an positive or null int
	 */
	protected abstract int getHalfMoveCount(B board);
}

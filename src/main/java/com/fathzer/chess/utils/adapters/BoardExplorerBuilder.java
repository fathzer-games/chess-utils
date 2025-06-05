package com.fathzer.chess.utils.adapters;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

import com.fathzer.chess.utils.Pieces;

/** A class that can create an explorer to list all the pieces of a board and their positions.
 * @param <B> The type of chess board
 */
public interface BoardExplorerBuilder<B> {
	/** builds an explorer on a board.
	 * @param board The board to explore
	 * @return A new board explorer
	 */
	BoardExplorer getExplorer(B board);
	
	/** Gets the stream of all pieces on a chess board.
	 * <br>The default implementation returns a Stream built upon the explorer returned by {@link #getExplorer(Object)}
	 * @param board The chess board
	 * @return A stream of real pieces found on the board (no empty cell should be returned).
	 * See {@link Pieces} to learn which integer corresponds to which piece.
	 */
	default IntStream getPieces(B board) {
		final BoardExplorer exp = getExplorer(board);
		final AtomicBoolean more = new AtomicBoolean(true);
		return IntStream.iterate(exp.getPiece(), i -> more.get(), i -> {
			if (!exp.next()) {
				more.set(false);
				return Integer.MAX_VALUE; // The value will be ignored
			} else {
				return exp.getPiece();
			}
		});
	}
}

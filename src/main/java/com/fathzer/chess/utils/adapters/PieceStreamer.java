package com.fathzer.chess.utils.adapters;

import java.util.stream.Stream;

/** A class that can stream all the pieces of a board.
 * @param <B> The type of chess board
 * @param <P> The class of pieces
 */
public interface PieceStreamer<B, P> {
	/** Gets the stream of all pieces on a chess board.
	 * @param board The chess board
	 * @return A stream of concrete piece (no special P instance that denotes an empty chess board cell should be returned).
	 */
	Stream<P> getPieces(B board);
}

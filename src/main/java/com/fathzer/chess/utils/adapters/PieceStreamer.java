package com.fathzer.chess.utils.adapters;

import java.util.stream.Stream;

import com.fathzer.chess.utils.Pieces;

/** A class that can stream all the pieces of a board.
 * @param <B> The type of chess board
 */
public interface PieceStreamer<B> {
	/** Gets the stream of all pieces on a chess board.
	 * @param board The chess board
	 * @return A stream of real pieces found on the board (no empty cell should be returned).
	 * See {@link Pieces} to learn which integer corresponds to which piece.
	 */
	Stream<Integer> getPieces(B board);
}

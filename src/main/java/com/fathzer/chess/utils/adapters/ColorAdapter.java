package com.fathzer.chess.utils.adapters;

/** A class that returns data about colors.
 * @param <B> The type of chess board
 * @param <C> The colors type
 * @param <P> The class of pieces
 */
public interface ColorAdapter<B, C, P> {
	/** Gets the color of a piece.
	 * @param piece A piece
	 * @return a color
	 */
	C getColor(P piece);
	
	/** Checks if color is white.
	 * @param color A color
	 * @return true if color is white.
	 */
	boolean isWhite(C color);
	
	/** Gets the color that should make next move.
	 * @param board The chess board
	 * @return The color to move
	 */
	C getSideToMove(B board);
}

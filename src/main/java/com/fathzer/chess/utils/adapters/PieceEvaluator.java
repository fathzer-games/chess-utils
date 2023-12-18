package com.fathzer.chess.utils.adapters;

/** A class that can evaluate pieces.
 * @param <P> The class of pieces
 */
public interface PieceEvaluator<P> {
	/** Gets the value of a piece.
	 * @param piece A piece.
	 * @return The <a href="https://en.wikipedia.org/wiki/Chess_piece_relative_value">standard value</a> of the piece.
	 */
	int getValue(P piece);
	
	/** Checks whether a piece is an imaginary piece that means its cell is empty.
	 * @param piece A piece
	 * @return true if the piece corresponds to an empty cell
	 */
	boolean isNone(P piece);
}

package com.fathzer.chess.utils.adapters;

import com.fathzer.chess.utils.Pieces;

/** A class that allows to browse board content.
 */
public interface BoardExplorer {
	/** Moves to next piece
	 * @return false if there's no more pieces
	 */
	boolean next();
	
	/** Gets the cell's index of current piece.
	 * @return an int.
	 * <br>index 0 corresponds to cell a8, 1 to b8, ... 8 to a7, ... 63 to h1
	 */
	int getIndex();
	
	/** Gets the piece in the current cell.
	 * @return A piece code as defined in {@link Pieces}
	 */
	int getPiece();
}

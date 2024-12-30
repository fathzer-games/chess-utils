package com.fathzer.chess.utils.evaluators.utils;

import com.fathzer.chess.utils.Pieces;
import com.fathzer.chess.utils.adapters.BoardExplorer;

/** An abstract <a href="https://www.chessprogramming.org/Piece-Square_Tables"> piece square table</a> implementation.
 */
public abstract class AbstractPieceSquareTable {
	/** Constructor.
	 */
	protected AbstractPieceSquareTable() {
		super();
	}

	/** Gets the evaluation using a board explorer.
	 * @param explorer The explorer to inspect the board
	 * @return an int, the evaluation based on this piece square table
	 */
	public int getRawEvaluation(BoardExplorer explorer) {
		int points = 0;
		do {
			final int p = explorer.getPiece();
			final int piece = Math.abs(p);
			final int index = explorer.getIndex();
			final boolean isBlack = p<0;
			points += get(piece, isBlack, index);
		} while (explorer.next());
		return points;
	}

	/** Gets the position value associated with a white piece at an index.
	 * @param piece The piece type as define in {@link Pieces}
	 * @param index The index of the piece on the board as defined in {@link BoardExplorer}
	 * @return an integer
	 */
	protected abstract int get(int piece, int index);
	
	/** Gets the value (from the white point of view) of a piece at a position.
	 * @param pieceType The piece type as define in {@link Pieces}
	 * @param black true if piece is black
	 * @param index The index of the piece on the board as defined in {@link BoardExplorer}
	 * @return an integer
	 */
	public final int get(int pieceType, boolean black, int index) {
		if (black) {
			final int row = 7 - index/8;
			final int col = index%8;
			index = row*8 + col;
			return -get(pieceType, index);
		} else {
			return get(pieceType, index);
		}
	}
}

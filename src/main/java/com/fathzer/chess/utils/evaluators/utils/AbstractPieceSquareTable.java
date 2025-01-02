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
		return black ? -get(pieceType, index^56) : get(pieceType, index);
	}
	
	/** Adds a piece specific constants to every values in a piece square table represented as an array.
	 * <br>The typical usage is to add the raw centi pawn value of every piece to every value of this piece square table.
	 * <br>It helps to make the code clean (The positions value can be defined independently of the piece value) but fast
	 * (adding the raw values to the table in a static initializer prevent from adding the managing the pieces raw values
	 * separately from the positions evaluation).
	 * @param pieceSquareTable The piece square table as an array. Every element is the array for a piece index.
	 * @param pieceValues The array of piece raw values in the same order as the elements of {@code pieceSquareTable}.
	 */
	protected static void addPiecesValues(int[][] pieceSquareTable, int[] pieceValues) {
		for (int i = 0; i < pieceValues.length; i++) {
			add(pieceSquareTable[i], pieceValues[i]);
		}
		
	}
	
	private static void add(int[] array, int value) {
		for (int i = 0; i < array.length; i++) {
			array[i] = array[i]+value;
		}
	}
}

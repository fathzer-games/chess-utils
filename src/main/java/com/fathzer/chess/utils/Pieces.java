package com.fathzer.chess.utils;

/** Pieces related data
 * <br>In this library, types of pieces are identified by positive integers:<ul>
 * <li>Pawn: 1</li>
 * <li>Knight: 2</li>
 * <li>Bishop: 3</li>
 * <li>Rook: 4</li>
 * <li>Queen: 5</li>
 * <li>King: 6</li>
 * <li>0 is reserved for empty cells</li>
 * </ul>
 * Pieces (with their color) are identified by a signed integer. Its absolute value
 * is the piece's type identifier. White pieces have a positive id, black ones have
 * a negative one. 
 */
public final class Pieces {
	/** The standard number of points associated to pieces (or empty cell).
	 * <br>The piece types order is empty, pawn, knight, bishop, rook, queen, king 
	 */
	private static final int[] VALUES = new int[] {0,1,3,3,5,9,10};

	/** Pawn */
	public static final int PAWN = 1;
	/** Knight */
	public static final int KNIGHT = 2;
	/** Bishop */
	public static final int BISHOP = 3;
	/** Rook */
	public static final int ROOK = 4;
	/** Queen */
	public static final int QUEEN = 5;
	/** King */
	public static final int KING = 6;
	
	private Pieces() {
		// Prevents subclasses
	}

	/** Gets the <a href="https://en.wikipedia.org/wiki/Chess_piece_relative_value">standard number of points</a> of a piece (or empty cell).
	 * @param type The piece type
	 * @return the number of points
	 */
	public static int getPoints(int type) {
		return VALUES[type];
	}
}

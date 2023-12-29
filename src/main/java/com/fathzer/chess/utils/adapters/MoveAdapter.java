package com.fathzer.chess.utils.adapters;

import com.fathzer.chess.utils.Pieces;

/** A class that returns data about moves.
 * @param <M> The move type
 * @param <B> The type of chess board
 */
public interface MoveAdapter<M, B> {
	/** Get the moving piece.
	 * @param board The board
	 * @param move The move
	 * @return a piece index. See {@link Pieces} to learn which integer corresponds to which piece.
	 */
	int getMovingPiece(B board, M move);
	
	/** Get the piece captured by the move.
	 * @param board The board
	 * @param move The move
	 * @return a piece type index. See {@link Pieces} to learn which integer corresponds to which piece type.
	 */
	int getCapturedType(B board, M move);
	
	/** Get the promotion made by the move.
	 * @param board The board
	 * @param move The move
	 * @return a piece type index. See {@link Pieces} to learn which integer corresponds to which piece type.
	 */
	int getPromotionType(B board, M move);
}

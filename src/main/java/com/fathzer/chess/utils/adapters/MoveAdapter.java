package com.fathzer.chess.utils.adapters;

/** A class that returns data about moves.
 * @param <M> The move type
 * @param <B> The type of chess board
 * @param <P> The class of pieces
 */
public interface MoveAdapter<M, B, P> {
	/** Get the moving piece.
	 * @param board The board
	 * @param move The move
	 * @return a Piece
	 */
	P getMoving(B board, M move);
	
	/** Get the piece captured by the move.
	 * @param board The board
	 * @param move The move
	 * @return a Piece or null.
	 * <br>Please note that one can return null when no captured is made or a special P instance.
	 * This library's classes uses {@link PieceEvaluator#isNone(Object)} to check if there's a capture or not.
	 */
	P getCaptured(B board, M move);
	
	/** Get the promotion made by the move.
	 * @param board The board
	 * @param move The move
	 * @return a Piece or null.
	 * <br>Please note that one can return null when no captured is made or a special P instance.
	 * This library's classes uses {@link PieceEvaluator#isNone(Object)} to check if there's a capture or not. 
	 */
	P getPromotion(B board, M move);
}

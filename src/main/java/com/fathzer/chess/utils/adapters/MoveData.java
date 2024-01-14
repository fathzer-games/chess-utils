package com.fathzer.chess.utils.adapters;

import com.fathzer.chess.utils.Pieces;

/** Information about a move 
 */
public interface MoveData<M, B> {
	/** Gets the index of the moving piece.
	 * @return an integer (see {@link BoardExplorer#getIndex()} to learn which index corresponds to which cell).
	 */
	int getMovingIndex();
	
	/** Get the moving piece.
	 * @return a piece index. See {@link Pieces} to learn which integer corresponds to which piece.
	 */
	int getMovingPiece();
	
	/** Gets the destination index of the moving piece.
	 * @return an integer (see {@link BoardExplorer#getIndex()} to learn which index corresponds to which cell).
	 */
	int getMovingDestination();
	
	/** Get the type of the piece captured by the move.
	 * @return a piece type index (See {@link Pieces} to learn which integer corresponds to which piece type), 0 if the move makes no capture.
	 */
	int getCapturedType();
	
	/** Gets the index of the captured piece.
	 * <br>Be aware that this index is not equals to {@link #getMovingDestination} for en-passant captures.
	 * <br>Calling this method when {@link #getCapturedType()} returns 0 may leads to unpredictable result.
	 * @return an integer (see {@link BoardExplorer#getIndex()} to learn which index corresponds to which cell).
	 */
	int getCapturedIndex();
	
	/** Get the promotion made by the move.
	 * @return a piece type (See {@link Pieces} to learn which integer corresponds to which piece type), 0 if the move is not a promotion.
	 */
	int getPromotionType();
	
	/** Gets the starting index of the rook involved in castling.
	 * @return The rook index (see {@link BoardExplorer#getIndex()} to learn which index corresponds to which cell). -1 if the move is not a castling.
	 */
	int getCastlingRookIndex();

	/** Gets the destination index of the rook involved in castling.
	 * <br>Calling this method when {@link #getCapturedType()} returns 0 may leads to unpredictable result.
	 * @return The rook's destination index (see {@link BoardExplorer#getIndex()} to learn which index corresponds to which cell).
	 */
	int getCastlingRookDestinationIndex();
	
	/** Updates this instance accordingly to a move.
	 * <br><b>Warning</b>: In some very rare (but not impossible case), this method can be called on an move that is not a pseudo-legal move.
	 * Typically, it could happen on a move recovered from the transposition table, in case of 
	 * <a href="https://www.chessprogramming.org/Zobrist_Hashing#Collisions">key collision</a> (same hash key for two different positions).
	 * It is wise to manage this case and return false when it occurs. 
	 * @param move The move
	 * @param board The board on which the move occurs.
	 * @return true if the update was done and false if the move seems inconsistent (in such a case, calling methods of the instance could have unpredictable results).
	 */
	boolean update(M move, B board);
}

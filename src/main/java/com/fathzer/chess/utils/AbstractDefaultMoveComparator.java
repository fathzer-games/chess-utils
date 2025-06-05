package com.fathzer.chess.utils;

import com.fathzer.games.util.SelectiveComparator;

/** A selective move comparator that considers a catch is better than other moves and taking a high value piece with a small value piece is better than the opposite and all other moves are equivalent.
 * @param <M> The type of moves
 * @param <B> The type of chess board
 */
public abstract class AbstractDefaultMoveComparator<M, B> implements SelectiveComparator<M> {
	/** The chess board on which the comparison are made.
	 */
	protected final B board;
	
	/** Constructor.
	 * @param board The chess board on which the comparison is made.
	 */
	protected AbstractDefaultMoveComparator(B board) {
		this.board = board;
	}
	
	/** Get the moving piece.
	 * @param board The board
	 * @param move The move
	 * @return a piece index. See {@link Pieces} to learn which integer corresponds to which piece.
	 */
	protected abstract int getMovingPiece(B board, M move);

	/** Get the piece captured by the move.
	 * @param board The board
	 * @param move The move
	 * @return a piece type index. See {@link Pieces} to learn which integer corresponds to which piece type.
	 */
	protected abstract int getCapturedType(B board, M move);
	
	/** Get the promotion made by the move.
	 * @param board The board
	 * @param move The move
	 * @return a piece type index. See {@link Pieces} to learn which integer corresponds to which piece type.
	 */
	protected abstract int getPromotionType(B board, M move);

	
	@Override
	public int compare(M o1, M o2) {
		return Integer.compare(evaluate(o2), evaluate(o1));
	}

	@Override
	public boolean test(M t) {
		return evaluate(t)!=0;
	}

	/** Gets the value of a move.
	 * @param move The move
	 * @return a value for a move.
	 * <br>The default implementation guarantees that the returned value is always &gt;=0, and &gt;0 for promotions and captures.
	 */
	public int evaluate(M move) {
		final int promotion = getPromotionType(board, move);
		int value = promotion==0 ? 0 : (Pieces.getPoints(promotion)-1)*16;
		final int captured = getCapturedType(board, move);
		if (captured==0) {
			return value;
		} else {
			value += Pieces.getPoints(captured)*16;
			return value - Pieces.getPoints(Math.abs(getMovingPiece(board, move)));
		}
	}
}

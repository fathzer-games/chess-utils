package com.fathzer.chess.utils.adapters;

public interface MoveAdapter<M, B, P> {
	boolean isCastle(B board, M move);
	P getMovingPiece(B board, M move);
	P getCapturedPiece(B board, M move);
	P getPromotion(B board, M move);
}

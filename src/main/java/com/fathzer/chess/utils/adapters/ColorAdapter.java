package com.fathzer.chess.utils.adapters;

public interface ColorAdapter<B, C, P> {
	C getColor(P piece);
	boolean isWhite(C color);
	C getSideToMove(B board);
}

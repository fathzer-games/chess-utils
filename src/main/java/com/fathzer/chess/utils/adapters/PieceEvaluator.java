package com.fathzer.chess.utils.adapters;

public interface PieceEvaluator<P> {
	int getValue(P piece);
	boolean isNone(P piece);
}

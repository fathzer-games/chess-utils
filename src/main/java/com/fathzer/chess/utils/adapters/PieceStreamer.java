package com.fathzer.chess.utils.adapters;

import java.util.stream.Stream;

public interface PieceStreamer<B, P> {
	Stream<P> getPieces(B board);
}

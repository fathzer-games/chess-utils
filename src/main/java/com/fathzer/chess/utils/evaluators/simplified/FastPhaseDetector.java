package com.fathzer.chess.utils.evaluators.simplified;

import static com.fathzer.chess.utils.Pieces.KING;

/** A class that tracks the current phase.
 */
class FastPhaseDetector {
	private static final long BLACK_QUEEN_VALUE = 0x01L;
	private static final long BLACK_QUEEN_MASK = 0xFFL;
	private static final long WHITE_QUEEN_VALUE = 0x0100L;
	private static final long WHITE_QUEEN_MASK = 0xFF00L;
	private static final long BLACK_ROOK_VALUE = 0x010000L;
	private static final long BLACK_ROOK_MASK = 0xFF0000L;
	private static final long WHITE_ROOK_VALUE = 0x01000000L;
	private static final long WHITE_ROOK_MASK = 0xFF000000L;
	private static final long BLACK_MINOR_VALUE = 0x0100000000L;
	private static final long BLACK_MINOR_MASK = 0xFF00000000L;
	private static final long WHITE_MINOR_VALUE = 0x010000000000L;
	private static final long WHITE_MINOR_MASK = 0xFF0000000000L;
	
	private static final long[] PIECE_KIND_TO_VALUES = new long[] {0L, BLACK_QUEEN_VALUE, BLACK_ROOK_VALUE, BLACK_MINOR_VALUE, BLACK_MINOR_VALUE, 0L, 0L, 0L, WHITE_MINOR_VALUE, WHITE_MINOR_VALUE, WHITE_ROOK_VALUE, WHITE_QUEEN_VALUE, 0L};
	
	private long state;
	
	void add(int piece) {
		state += PIECE_KIND_TO_VALUES[piece + KING];
	}
	
	void remove(int piece) {
		state -= PIECE_KIND_TO_VALUES[piece + KING];
	}
	
	boolean isEndGamePhase() {
		final boolean whiteQueen = (state & WHITE_QUEEN_MASK) != 0L;
		final boolean blackQueen = (state & BLACK_QUEEN_MASK) != 0L;
		if (!blackQueen && !whiteQueen) {
			return true;
		}
		return !((blackQueen && (hasBlackRook() || hasManyBlackMinor())) || (whiteQueen && (hasWhiteRook() || hasManyWhiteMinor())));
	}
	
	boolean hasWhiteRook() {
		return (state & WHITE_ROOK_MASK) != 0;
	}

	boolean hasBlackRook() {
		return (state & BLACK_ROOK_MASK) != 0;
	}
	
	boolean hasManyBlackMinor() {
		return (state & BLACK_MINOR_MASK) > BLACK_MINOR_VALUE;
	}

	boolean hasManyWhiteMinor() {
		return (state & WHITE_MINOR_MASK) > WHITE_MINOR_VALUE;
	}

	void copyTo(FastPhaseDetector other) {
		other.state = state;
	}
}
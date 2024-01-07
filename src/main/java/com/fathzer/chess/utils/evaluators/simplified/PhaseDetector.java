package com.fathzer.chess.utils.evaluators.simplified;

import static com.fathzer.chess.utils.Pieces.*;

class PhaseDetector {
	private boolean blackQueen = false;
	private boolean whiteQueen = false;
	private boolean whiteRook = false;
	private boolean blackRook = false;
	private int whiteMinor = 0;
	private int blackMinor = 0;
	
	public void add(int piece) {
		switch (piece) {
			case -QUEEN : blackQueen=true; break;
			case QUEEN : whiteQueen=true; break;
			case -ROOK : blackRook=true; break;
			case ROOK : whiteRook=true; break;
			case -BISHOP : blackMinor++; break;
			case -KNIGHT : blackMinor++; break;
			case BISHOP : whiteMinor++; break;
			case KNIGHT : whiteMinor++; break;
		default:
			break;
		}
	}

	public Phase getPhase() {
		if (!blackQueen && !whiteQueen) {
			return Phase.END_GAME;
		}
		if ((blackQueen && (blackRook || blackMinor>1)) || (whiteQueen && (whiteRook || whiteMinor>1))) {
			return Phase.MIDDLE_GAME;
		}
		return Phase.END_GAME;
	}
}

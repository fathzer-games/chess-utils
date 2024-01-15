package com.fathzer.chess.utils.evaluators.simplified;

import static com.fathzer.chess.utils.Pieces.BISHOP;
import static com.fathzer.chess.utils.Pieces.KNIGHT;
import static com.fathzer.chess.utils.Pieces.QUEEN;
import static com.fathzer.chess.utils.Pieces.ROOK;

/** The state of the evaluator.
 */
class BasicPhaseDetector {
	protected int blackQueen;
	protected int blackRook;
	protected int blackMinor;
	protected int whiteQueen;
	protected int whiteRook;
	protected int whiteMinor;

	public void add(int piece) {
		switch (piece) {
			case -QUEEN : blackQueen++; break;
			case QUEEN : whiteQueen++; break;
			case -ROOK : blackRook++; break;
			case ROOK : whiteRook++; break;
			case -BISHOP : blackMinor++; break;
			case -KNIGHT : blackMinor++; break;
			case BISHOP : whiteMinor++; break;
			case KNIGHT : whiteMinor++; break;
		default:
			break;
		}
	}
	
	public void remove(int piece) {
		switch (piece) {
			case -QUEEN : blackQueen--; break;
			case QUEEN : whiteQueen--; break;
			case -ROOK : blackRook--; break;
			case ROOK : whiteRook--; break;
			case -BISHOP : blackMinor--; break;
			case -KNIGHT : blackMinor--; break;
			case BISHOP : whiteMinor--; break;
			case KNIGHT : whiteMinor--; break;
		default:
			break;
		}
	}
	
	public Phase getPhase() {
		if (blackQueen==0 && whiteQueen==0) {
			return Phase.END_GAME;
		}
		if ((blackQueen!=0 && (blackRook!=0 || blackMinor>1)) || (whiteQueen!=0 && (whiteRook!=0 || whiteMinor>1))) {
			return Phase.MIDDLE_GAME;
		}
		return Phase.END_GAME;
	}
	
	public void copyTo(BasicPhaseDetector other) {
		other.blackQueen = blackQueen;
		other.whiteQueen = whiteQueen;
		other.whiteRook = whiteRook;
		other.blackRook = blackRook;
		other.whiteMinor = whiteMinor;
		other.blackMinor = blackMinor;
	}
}
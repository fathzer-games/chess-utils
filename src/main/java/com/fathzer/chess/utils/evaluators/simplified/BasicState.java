package com.fathzer.chess.utils.evaluators.simplified;

import static com.fathzer.chess.utils.Pieces.BISHOP;
import static com.fathzer.chess.utils.Pieces.KING;
import static com.fathzer.chess.utils.Pieces.KNIGHT;
import static com.fathzer.chess.utils.Pieces.QUEEN;
import static com.fathzer.chess.utils.Pieces.ROOK;

import com.fathzer.chess.utils.adapters.BoardExplorer;

/** The state of the evaluator.
 */
class BasicState {
	protected int blackQueen;
	protected int blackRook;
	protected int blackMinor;
	protected int whiteQueen;
	protected int whiteRook;
	protected int whiteMinor;
	protected int points;
	protected int whiteKingIndex;
	protected int blackKingIndex;
	
	BasicState() {
		super();
	}
	
	BasicState(BoardExplorer explorer) {
		this.points = 0;
		do {
			final int p = explorer.getPiece();
			add(p);
			final int kind = Math.abs(p);
			final int index = explorer.getIndex();
			final boolean isBlack = p<0;
			if (kind!=KING) {
				int inc = SimplifiedEvaluatorBase.getRawValue(kind);
				inc += SimplifiedEvaluatorBase.getPositionValue(kind, isBlack, index);
				if (isBlack) {
					points -= inc;
				} else {
					points += inc;
				}
			} else if (isBlack) {
				this.blackKingIndex = index;
			} else {
				this.whiteKingIndex = index;
			}
		} while (explorer.next());
	}

	protected void add(int piece) {
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
	
	protected Phase getPhase() {
		if (blackQueen==0 && whiteQueen==0) {
			return Phase.END_GAME;
		}
		if ((blackQueen!=0 && (blackRook!=0 || blackMinor>1)) || (whiteQueen!=0 && (whiteRook!=0 || whiteMinor>1))) {
			return Phase.MIDDLE_GAME;
		}
		return Phase.END_GAME;
	}

	int getPoints() {
		return points;
	}

	protected int evaluateAsWhite() {
		return points + SimplifiedEvaluatorBase.getKingPositionsValue(whiteKingIndex, blackKingIndex, getPhase());
	}
}
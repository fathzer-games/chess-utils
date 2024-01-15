package com.fathzer.chess.utils.evaluators.simplified;

import static com.fathzer.chess.utils.Pieces.KING;

import com.fathzer.chess.utils.adapters.BoardExplorer;

/** The state of the evaluator.
 */
class BasicState extends BasicPhaseDetector {
	protected int points;
	protected int whiteKingIndex;
	protected int blackKingIndex;
	
	BasicState() {
		super();
	}
	
	public void copyTo(IncrementalState other) {
		super.copyTo(other);
		other.points = points;
		other.blackKingIndex = blackKingIndex;
		other.whiteKingIndex = whiteKingIndex;
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

	public int evaluateAsWhite() {
		return points + SimplifiedEvaluatorBase.getKingPositionsValue(whiteKingIndex, blackKingIndex, getPhase());
	}
}
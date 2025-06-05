package com.fathzer.chess.utils.evaluators.simplified;

/** The state of a simplified evaluator.
 */
public class SimplifiedState extends FastPhaseDetector {
	int points;
	int whiteKingIndex;
	int blackKingIndex;
	
	SimplifiedState() {
		super();
	}
	
	void copyTo(SimplifiedState other) {
		super.copyTo(other);
		other.points = points;
		other.blackKingIndex = blackKingIndex;
		other.whiteKingIndex = whiteKingIndex;
	}
}
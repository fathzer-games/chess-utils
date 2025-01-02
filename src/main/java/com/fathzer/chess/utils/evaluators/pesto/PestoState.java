package com.fathzer.chess.utils.evaluators.pesto;

/** The state of a simplified evaluator.
 */
public class PestoState {
	int mgPoints;
	int egPoints;
	int phasePoints;
	
	
	PestoState() {
		super();
	}
	
	void copyTo(PestoState other) {
		other.mgPoints = mgPoints;
		other.egPoints = egPoints;
		other.phasePoints = phasePoints;
	}
	
	void clear() {
		mgPoints = 0;
		egPoints = 0;
		phasePoints = 0;
	}
}
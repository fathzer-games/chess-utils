package com.fathzer.chess.utils.evaluators.simplified;

import static com.fathzer.chess.utils.Pieces.*;
import static com.fathzer.chess.utils.evaluators.simplified.SimplifiedEvaluatorBase.*;

import com.fathzer.chess.utils.adapters.BoardExplorer;
import com.fathzer.chess.utils.adapters.MoveData;

/** The state of the evaluator.
 */
class IncrementalState extends BasicState {
	IncrementalState() {
		super();
	}
	
	IncrementalState(BoardExplorer exp) {
		super(exp);
	}

	void remove(int piece) {
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
	
	void copyTo(IncrementalState other) {
		other.blackQueen = blackQueen;
		other.whiteQueen = whiteQueen;
		other.whiteRook = whiteRook;
		other.blackRook = blackRook;
		other.whiteMinor = whiteMinor;
		other.blackMinor = blackMinor;
		other.points = points;
		other.blackKingIndex = blackKingIndex;
		other.whiteKingIndex = whiteKingIndex;
	}
	
	void update(MoveData<?,?> move) {
		points += getIncrement(move);
	}

	private int getIncrement(MoveData<?,?> move) {
		final boolean isBlack = move.getMovingPiece()<0;
		int moving = Math.abs(move.getMovingPiece());
		final int movingIndex = move.getMovingIndex();
		int inc;
		if (moving==KING) {
			// The position value of kings is not evaluated incrementally
			int rookIndex = move.getCastlingRookIndex();
			if (rookIndex>=0) {
				// It's a castling move, update rook positions values
				inc =  getPositionValue(ROOK, isBlack, move.getCastlingRookDestinationIndex()) - getPositionValue(ROOK, isBlack, rookIndex);
			} else {
				inc = doCapture(isBlack, move);
			}
			// Update king's position
			if (isBlack) {
				this.blackKingIndex = move.getMovingDestination();
			} else {
				this.whiteKingIndex = move.getMovingDestination();
			}
		} else {
			// Remove the position value of the moving piece
			inc = - getPositionValue(moving, isBlack, movingIndex);
			final int promoType = move.getPromotionType();
			if (promoType!=0) {
				// If promotion, add raw value points, update phase
				inc += getRawValue(promoType)-getRawValue(PAWN);
				moving = promoType;
				add(isBlack ? -promoType : promoType);
			}
			inc += doCapture(isBlack, move);
			// Adds the position value of the 
			inc += getPositionValue(moving, isBlack, move.getMovingDestination());
		}
		return isBlack ? -inc : +inc;
	}
	
	private int doCapture(boolean isBlack, MoveData<?,?> move) {
		int captured = move.getCapturedType();
		if (captured!=0) {
			// A piece was captured
			// Update the phase detector
			remove(isBlack ? captured : -captured);
			// Then add its raw value and its position value
			return getRawValue(captured) + getPositionValue(captured, !isBlack, move.getCapturedIndex());
		} else {
			return 0;
		}
	}
}
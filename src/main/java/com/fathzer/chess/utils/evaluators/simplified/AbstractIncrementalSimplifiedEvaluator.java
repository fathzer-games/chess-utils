package com.fathzer.chess.utils.evaluators.simplified;

import static com.fathzer.chess.utils.Pieces.BISHOP;
import static com.fathzer.chess.utils.Pieces.KING;
import static com.fathzer.chess.utils.Pieces.KNIGHT;
import static com.fathzer.chess.utils.Pieces.QUEEN;
import static com.fathzer.chess.utils.Pieces.ROOK;

import java.util.function.Supplier;

import com.fathzer.chess.utils.adapters.BoardExplorer;
import com.fathzer.chess.utils.adapters.MoveData;
import com.fathzer.chess.utils.evaluators.AbstractNaiveEvaluator;
import com.fathzer.games.MoveGenerator;
import com.fathzer.games.ai.evaluation.Evaluator;
import com.fathzer.games.util.Stack;

/** The simplified evaluator described at <a href="https://www.chessprogramming.org/Simplified_Evaluation_Function">https://www.chessprogramming.org/Simplified_Evaluation_Function</a>
 * <br>This only work with 8*8 games
 */
public abstract class AbstractIncrementalSimplifiedEvaluator<M, B extends MoveGenerator<M>> extends SimplifiedEvaluatorBase<M, B> implements Evaluator<M,B>, Supplier<MoveData<M,B>> {
	public static class State {
		private int blackQueen;
		private int whiteQueen;
		private int whiteRook;
		private int blackRook;
		private int whiteMinor;
		private int blackMinor;
		private int points;
		int whiteKingIndex;
		int blackKingIndex;
		
		State() {
			super();
		}
		
		State(BoardExplorer exp) {
			this.whiteKingIndex=-1;
			this.blackKingIndex=-1;
			do {
				final int p = exp.getPiece();
				add(p);
				final int kind = Math.abs(p);
				final int index = exp.getIndex();
				final boolean isBlack = p<0;
				if (kind!=KING) {
					int inc = getRawValue(kind);
					inc += getPositionValue(kind, isBlack, index);
					if (isBlack) {
						points -= inc;
					} else {
						points += inc;
					}
				} else if (isBlack) {
					blackKingIndex = index;
				} else {
					whiteKingIndex = index;
				}
			} while (exp.next());
		}

		void add(int piece) {
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

		Phase getPhase() {
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

		void copyTo(State other) {
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
	}
	
	private final Stack<State> states;
	private State toCommit;
	private MoveData<M, B> moveData;
	
	/** Default constructor
	 */
	protected AbstractIncrementalSimplifiedEvaluator() {
		this.states = new Stack<>(State::new);
		this.moveData = get();
	}

	@Override
	public Evaluator<M, B> fork() {
		return fork(states.get());
	}
	
	/** Creates a new instance initialized with current state that will become the initial state of created instance.
	 * @param score The initial state.
	 * @return a new evaluator of the same class as this, this the same view point, and initialized with the state.
	 */
	protected abstract AbstractNaiveEvaluator<M, B> fork(State state);

	@Override
	public void init(B board) {
		states.clear();
		states.set(new State(getExplorer(board)));
	}

	@Override
	public void prepareMove(B board, M move) {
		buildToCommit();
		moveData.update(move, board);
		toCommit.points += getPointIncrement();
	}

	private void buildToCommit() {
		final State current = states.get();
		states.next();
		toCommit = states.get();
		states.previous();
		current.copyTo(toCommit);
	}
	
	private int getPointIncrement() {
		final boolean isBlack = moveData.getMovingPiece()<0;
		int moving = Math.abs(moveData.getMovingPiece());
		final int movingIndex = moveData.getMovingIndex();
		int inc;
		if (moving==KING) {
			// The position value of kings is not evaluated incrementally
			int rookIndex = moveData.getCastlingRookIndex();
			if (rookIndex>=0) {
				// It's a castling move, update rook positions values
				inc =  getPositionValue(ROOK, isBlack, moveData.getCastlingRookDestinationIndex()) - getPositionValue(ROOK, isBlack, rookIndex);
			} else {
				inc = updateCapture(isBlack);
			}
			// Update king's position
			if (isBlack) {
				toCommit.blackKingIndex = moveData.getMovingDestination();
			} else {
				toCommit.whiteKingIndex = moveData.getMovingDestination();
			}
		} else {
			// Remove the position value of the moving piece
			inc = - getPositionValue(moving, isBlack, movingIndex);
			final int promoType = moveData.getPromotionType();
			if (promoType!=0) {
				// If promotion, add raw value points, update phase
				inc += getRawValue(promoType)-1;
				moving = promoType;
				toCommit.add(isBlack ? -promoType : promoType);
			}
			inc += updateCapture(isBlack);
			// Adds the position value of the 
			inc += getPositionValue(moving, isBlack, moveData.getMovingDestination());
		}
		return isBlack ? -inc : +inc;
	}
	
	private int updateCapture(boolean isBlack) {
		int captured = moveData.getCapturedType();
		if (captured!=0) {
			// A piece was captured
			// Update the phase detector
			toCommit.remove(isBlack ? captured : -captured);
			// Then add its raw value and its position value
			return getRawValue(captured) + getPositionValue(captured, isBlack, moveData.getCapturedIndex());
		} else {
			return 0;
		}
	}

	@Override
	public void commitMove() {
		states.next();
		states.set(toCommit);
	}

	@Override
	public void unmakeMove() {
		states.previous();
	}

	protected int evaluateAsWhite(B board) {
		final State state = states.get();
		return state.points + getKingPositionsValue(state.whiteKingIndex, state.blackKingIndex, state.getPhase());
	}
}

package com.fathzer.chess.utils.evaluators.simplified;

import static com.fathzer.chess.utils.Pieces.BISHOP;
import static com.fathzer.chess.utils.Pieces.KING;
import static com.fathzer.chess.utils.Pieces.KNIGHT;
import static com.fathzer.chess.utils.Pieces.QUEEN;
import static com.fathzer.chess.utils.Pieces.ROOK;

import com.fathzer.chess.utils.adapters.BoardExplorer;
import com.fathzer.games.MoveGenerator;
import com.fathzer.games.ai.evaluation.Evaluator;
import com.fathzer.games.util.Stack;

/** The simplified evaluator described at <a href="https://www.chessprogramming.org/Simplified_Evaluation_Function">https://www.chessprogramming.org/Simplified_Evaluation_Function</a>
 * <br>This only work with 8*8 games
 */
public abstract class AbstractIncrementalSimplifiedEvaluator<M, B extends MoveGenerator<M>> extends SimplifiedEvaluatorBase<M, B> implements Evaluator<M,B> {
	public static class State {
		private int blackQueen = 0;
		private int whiteQueen = 0;
		private int whiteRook = 0;
		private int blackRook = 0;
		private int whiteMinor = 0;
		private int blackMinor = 0;
		private int points = 0;
		int whiteKingIndex=-1;
		int blackKingIndex=-1;
		
		State(BoardExplorer exp) {
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
	}
	
	private final Stack<State> states;
	private State toCommit;
	
	/** Default constructor
	 */
	protected AbstractIncrementalSimplifiedEvaluator() {
		this.states = new Stack<>(null);
	}

	@Override
	public Evaluator<M, B> fork() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init(B board) {
		states.clear();
		states.set(new State(getExplorer(board)));
	}

	@Override
	public void prepareMove(B board, M move) {
		// TODO Auto-generated method stub
	}

	@Override
	public void commitMove() {
		// TODO Auto-generated method stub
	}

	@Override
	public void unmakeMove() {
		// TODO Auto-generated method stub
	}

	protected int evaluateAsWhite(B board) {
		final State state = states.get();
		return state.points + getKingPositionsValue(state.whiteKingIndex, state.blackKingIndex, state.getPhase());
	}
}

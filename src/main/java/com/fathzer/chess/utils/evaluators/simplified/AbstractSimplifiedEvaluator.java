package com.fathzer.chess.utils.evaluators.simplified;

import static com.fathzer.chess.utils.Pieces.BISHOP;
import static com.fathzer.chess.utils.Pieces.KING;
import static com.fathzer.chess.utils.Pieces.KNIGHT;
import static com.fathzer.chess.utils.Pieces.QUEEN;
import static com.fathzer.chess.utils.Pieces.ROOK;

import com.fathzer.chess.utils.adapters.BoardExplorer;
import com.fathzer.games.MoveGenerator;
import com.fathzer.games.ai.evaluation.StaticEvaluator;

/** The simplified evaluator described at <a href="https://www.chessprogramming.org/Simplified_Evaluation_Function">https://www.chessprogramming.org/Simplified_Evaluation_Function</a>
 * <br>This only work with 8*8 games
 */
public abstract class AbstractSimplifiedEvaluator<M, B extends MoveGenerator<M>> extends SimplifiedEvaluatorBase<M, B> implements StaticEvaluator<M,B> {
	static class PhaseDetector {
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
	
	protected int evaluateAsWhite(B board) {
		final BoardExplorer exp = getExplorer(board);
		int points = 0;
		int whiteKingIndex=-1;
		int blackKingIndex=-1;
		final PhaseDetector phaseDetector = new PhaseDetector();
		do {
			final int p = exp.getPiece();
			phaseDetector.add(p);
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
		return points + getKingPositionsValue(whiteKingIndex, blackKingIndex, phaseDetector.getPhase());
	}
}

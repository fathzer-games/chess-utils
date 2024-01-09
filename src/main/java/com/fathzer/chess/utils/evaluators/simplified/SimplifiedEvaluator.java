package com.fathzer.chess.utils.evaluators.simplified;

import com.fathzer.chess.utils.adapters.BoardExplorer;
import com.fathzer.chess.utils.evaluators.AbstractEvaluator;

import static com.fathzer.chess.utils.Pieces.*;

import com.fathzer.chess.utils.Pieces;
import com.fathzer.games.MoveGenerator;

/** The simplified evaluator described at <a href="https://www.chessprogramming.org/Simplified_Evaluation_Function">https://www.chessprogramming.org/Simplified_Evaluation_Function</a>
 * <br>This only work with 8*8 games
 */
public abstract class SimplifiedEvaluator<M, B extends MoveGenerator<M>> extends AbstractEvaluator<M, B> {
	private static final int[] PIECE_VALUES = {0, 100, 320, 330, 500, 900, 20000};
	private static final int[] KING_MID_GAME_EVAL = new int[] {
			-30,-40,-40,-50,-50,-40,-40,-30,
			-30,-40,-40,-50,-50,-40,-40,-30,
			-30,-40,-40,-50,-50,-40,-40,-30,
			-30,-40,-40,-50,-50,-40,-40,-30,
			-20,-30,-30,-40,-40,-30,-30,-20,
			-10,-20,-20,-20,-20,-20,-20,-10,
			 20, 20,  0,  0,  0,  0, 20, 20,
			 20, 30, 10,  0,  0, 10, 30, 20};

	private static final int[] KING_END_GAME_EVAL = new int[] {
			-50,-40,-30,-20,-20,-30,-40,-50,
			-30,-20,-10,  0,  0,-10,-20,-30,
			-30,-10, 20, 30, 30, 20,-10,-30,
			-30,-10, 30, 40, 40, 30,-10,-30,
			-30,-10, 30, 40, 40, 30,-10,-30,
			-30,-10, 20, 30, 30, 20,-10,-30,
			-30,-30,  0,  0,  0,  0,-30,-30,
			-50,-30,-30,-30,-30,-30,-30,-50};
	
	private static final int [][] PIECE_POSITION_VALUES = new int[][] {
		// Just to have index equals to piece type codes
		new int[0],
		// PAWN
		new int[] {
			0,  0,  0,  0,  0,  0,  0,  0,
			50, 50, 50, 50, 50, 50, 50, 50,
			10, 10, 20, 30, 30, 20, 10, 10,
			 5,  5, 10, 25, 25, 10,  5,  5,
			 0,  0,  0, 20, 20,  0,  0,  0,
			 5, -5,-10,  0,  0,-10, -5,  5,
			 5, 10, 10,-20,-20, 10, 10,  5,
			 0,  0,  0,  0,  0,  0,  0,  0},
		// KNIGHT
		new int[] {
			-50,-40,-30,-30,-30,-30,-40,-50,
			-40,-20,  0,  0,  0,  0,-20,-40,
			-30,  0, 10, 15, 15, 10,  0,-30,
			-30,  5, 15, 20, 20, 15,  5,-30,
			-30,  0, 15, 20, 20, 15,  0,-30,
			-30,  5, 10, 15, 15, 10,  5,-30,
			-40,-20,  0,  5,  5,  0,-20,-40,
			-50,-40,-30,-30,-30,-30,-40,-50},
		// BISHOP
		new int[] {
			-20,-10,-10,-10,-10,-10,-10,-20,
			-10,  0,  0,  0,  0,  0,  0,-10,
			-10,  0,  5, 10, 10,  5,  0,-10,
			-10,  5,  5, 10, 10,  5,  5,-10,
			-10,  0, 10, 10, 10, 10,  0,-10,
			-10, 10, 10, 10, 10, 10, 10,-10,
			-10,  5,  0,  0,  0,  0,  5,-10,
			-20,-10,-10,-10,-10,-10,-10,-20},
		// ROOK
		new int[] {
			  0,  0,  0,  0,  0,  0,  0,  0,
			  5, 10, 10, 10, 10, 10, 10,  5,
			 -5,  0,  0,  0,  0,  0,  0, -5,
			 -5,  0,  0,  0,  0,  0,  0, -5,
			 -5,  0,  0,  0,  0,  0,  0, -5,
			 -5,  0,  0,  0,  0,  0,  0, -5,
			 -5,  0,  0,  0,  0,  0,  0, -5,
			  0,  0,  0,  5,  5,  0,  0,  0},
		// QUEEN
		new int[] {
			-20,-10,-10, -5, -5,-10,-10,-20,
			-10,  0,  0,  0,  0,  0,  0,-10,
			-10,  0,  5,  5,  5,  5,  0,-10,
			 -5,  0,  5,  5,  5,  5,  0, -5,
			  0,  0,  5,  5,  5,  5,  0, -5,
			-10,  5,  5,  5,  5,  5,  0,-10,
			-10,  0,  5,  0,  0,  0,  0,-10,
			-20,-10,-10, -5, -5,-10,-10,-20
	}};
	
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
				int inc = PIECE_VALUES[kind];
				final int[] positionMap = PIECE_POSITION_VALUES[kind];
				inc += getPositionValue(positionMap, index, isBlack);
				if (p>0) {
					points += inc;
				} else {
					points -= inc;
				}
			} else if (isBlack) {
				blackKingIndex = index;
			} else {
				whiteKingIndex = index;
			}
		} while (exp.next());
		final int[] kingMap = phaseDetector.getPhase()==Phase.MIDDLE_GAME ? KING_MID_GAME_EVAL : KING_END_GAME_EVAL;
		points += getPositionValue(kingMap, whiteKingIndex, false); 
		points -= getPositionValue(kingMap, blackKingIndex, true); 
		return points;
	}
	
	/** Gets a board explorer.
	 * @param board The board to explore
	 * @return A new board explorer
	 */
	protected abstract BoardExplorer getExplorer(B board);

	private static int getPositionValue(int[] positionMap, int index, boolean black) {
		if (black) {
			final int row = 7 - index/8;
			final int col = index%8;
			index = row*8 + col;
		}
		return positionMap[index];
	}
	
	/** Gets the position value associated with a type of piece and an index.
	 * @param type The piece type as define in {@link Pieces}
	 * @param index The index of the piece on the board as defined in {@link BoardExplorer}
	 * @return an integer
	 */
	protected static int getPositionValue(int type, int index) {
		return PIECE_POSITION_VALUES[type][index];
	}
}

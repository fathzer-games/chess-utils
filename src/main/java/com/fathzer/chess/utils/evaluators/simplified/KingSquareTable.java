package com.fathzer.chess.utils.evaluators.simplified;

import com.fathzer.chess.utils.evaluators.AbstractPieceSquareTable;

class KingSquareTable extends AbstractPieceSquareTable {
	private static final int[] EVAL = new int[] {
			-30,-40,-40,-50,-50,-40,-40,-30,
			-30,-40,-40,-50,-50,-40,-40,-30,
			-30,-40,-40,-50,-50,-40,-40,-30,
			-30,-40,-40,-50,-50,-40,-40,-30,
			-20,-30,-30,-40,-40,-30,-30,-20,
			-10,-20,-20,-20,-20,-20,-20,-10,
			 20, 20,  0,  0,  0,  0, 20, 20,
			 20, 30, 10,  0,  0, 10, 30, 20};

	@Override
	protected int get(int piece, int index) {
		return EVAL[index];
	}
}

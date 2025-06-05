package com.fathzer.chess.test.utils;

import com.fathzer.chess.utils.adapters.chesslib.ChessLibMoveGenerator;
import com.github.bhlangonijr.chesslib.Board;

public abstract class FENUtils {
	private FENUtils() {
		super();
	}
	
	public static ChessLibMoveGenerator from(String fen) {
		final ChessLibMoveGenerator mvg = new ChessLibMoveGenerator(new Board());
		mvg.getBoard().loadFromFen(fen);
		return mvg;
	}
}

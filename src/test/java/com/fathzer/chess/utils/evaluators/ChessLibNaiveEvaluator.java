package com.fathzer.chess.utils.evaluators;

import com.fathzer.chess.utils.adapters.BoardExplorer;
import com.fathzer.chess.utils.adapters.chesslib.BasicMoveDecoder;
import com.fathzer.chess.utils.adapters.chesslib.ChessLibBoardExplorer;
import com.fathzer.chess.utils.adapters.chesslib.ChessLibMoveGenerator;
import com.github.bhlangonijr.chesslib.move.Move;

public class ChessLibNaiveEvaluator extends AbstractNaiveEvaluator<Move, ChessLibMoveGenerator> {
	public ChessLibNaiveEvaluator() {
		super();
	}
	
	private ChessLibNaiveEvaluator(int score) {
		super(score);
	}

	@Override
	public AbstractNaiveEvaluator<Move, ChessLibMoveGenerator> fork(int score) {
		return new ChessLibNaiveEvaluator(score);
	}

	@Override
	public BoardExplorer getExplorer(ChessLibMoveGenerator board) {
		return new ChessLibBoardExplorer(board.getBoard());
	}

	@Override
	protected int getCapturedType(ChessLibMoveGenerator board, Move move) {
		return BasicMoveDecoder.getCapturedType(board, move);
	}

	@Override
	protected int getPromotionType(ChessLibMoveGenerator board, Move move) {
		return BasicMoveDecoder.getPromotionType(move);
	}
}
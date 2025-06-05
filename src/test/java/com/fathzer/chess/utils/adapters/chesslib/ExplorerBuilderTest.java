package com.fathzer.chess.utils.adapters.chesslib;

import com.fathzer.chess.test.utils.FENUtils;
import com.fathzer.chess.utils.adapters.BoardExplorer;
import com.fathzer.chess.utils.adapters.BoardExplorerBuilder;
import com.fathzer.chess.utils.test.AbstractBoardExplorerBuilderTest;

class ExplorerBuilderTest extends AbstractBoardExplorerBuilderTest<ChessLibMoveGenerator> implements BoardExplorerBuilder<ChessLibMoveGenerator>{

	@Override
	protected BoardExplorerBuilder<ChessLibMoveGenerator> getBuilder() {
		return this;
	}

	@Override
	public ChessLibMoveGenerator toBoard(String fen) {
		return FENUtils.from(fen);
	}

	@Override
	public BoardExplorer getExplorer(ChessLibMoveGenerator board) {
		return new ChessLibBoardExplorer(board.getBoard());
	}
}

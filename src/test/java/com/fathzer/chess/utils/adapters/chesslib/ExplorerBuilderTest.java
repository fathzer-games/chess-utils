package com.fathzer.chess.utils.adapters.chesslib;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.fathzer.chess.test.utils.FENUtils;
import com.fathzer.chess.utils.adapters.BoardExplorer;
import com.fathzer.chess.utils.adapters.BoardExplorerBuilder;

class ExplorerBuilderTest {

	@Test
	void test() {
		final BoardExplorerBuilder<ChessLibMoveGenerator> builder = new BoardExplorerBuilder<ChessLibMoveGenerator>() {
			@Override
			public BoardExplorer getExplorer(ChessLibMoveGenerator board) {
				return new ChessLibBoardExplorer(board.getBoard());
			}
		};
		
		ChessLibMoveGenerator mg = FENUtils.from("4k3/6p1/3q4/8/8/8/8/1N2K3 w HAha - 0 1");
		Set<Integer> result = builder.getPieces(mg).boxed().collect(Collectors.toSet());
		assertEquals(Set.of(-6,-5,-1,6,2), result);
	}
}

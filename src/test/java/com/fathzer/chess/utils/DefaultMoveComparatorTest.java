package com.fathzer.chess.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.fathzer.chess.utils.adapters.chesslib.ChessLibAdapter;
import com.fathzer.games.util.MoveList;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;

class DefaultMoveComparatorTest {
	private static final class ChessLibDefaultMoveComparator extends DefaultMoveComparator<Move, Board, Piece> implements ChessLibAdapter {
		private ChessLibDefaultMoveComparator(Board board) {
			super(board);
		}
	}

	@Test
	void test() {
		final Board board = new Board();
		board.loadFromFen("Q2n4/4P3/8/5P2/8/qK3p1k/1P6/8 w - - 0 1");
		final DefaultMoveComparator<Move, Board, Piece> cmp = new ChessLibDefaultMoveComparator(board);
		final Move queenPawnCatch = new Move(Square.A8, Square.F3);
		final Move queenQueenCatch = new Move(Square.A8, Square.A3);
		final Move kingCatch = new Move(Square.B3, Square.A3);
		final Move pawnMove = new Move(Square.F5, Square.F6);
		final Move pawnPromo = new Move(Square.E7, Square.E8, Piece.WHITE_QUEEN);
		final Move pawnCatchPromo = new Move(Square.E7, Square.D8, Piece.WHITE_QUEEN);
		
		Move[] moves = new Move[] {pawnMove, queenQueenCatch, queenPawnCatch, kingCatch, pawnCatchPromo, pawnPromo};
		Arrays.stream(moves).map(m -> m.toString()+":"+cmp.evaluate(m)).forEach(System.out::println);
		final MoveList<Move> sorted = new MoveList<>();
		sorted.setComparator(cmp);
		sorted.addAll(Arrays.asList(moves));
		sorted.sort();
		assertEquals(Arrays.asList(pawnCatchPromo, queenQueenCatch, kingCatch, pawnPromo, queenPawnCatch, pawnMove), sorted);
		assertFalse(cmp.test(pawnMove));
		assertTrue(cmp.compare(kingCatch, queenQueenCatch)>0);
		assertTrue(cmp.compare(queenPawnCatch, kingCatch)>0);
	}
}

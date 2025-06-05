package com.fathzer.chess.utils;

import static org.junit.jupiter.api.Assertions.*;

import static com.github.bhlangonijr.chesslib.Square.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.fathzer.chess.utils.adapters.chesslib.BasicMoveDecoder;
import com.fathzer.chess.utils.adapters.chesslib.ChessLibMoveGenerator;
import com.fathzer.games.util.MoveList;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.move.Move;

class DefaultMoveComparatorTest {
	private static final class ChessLibDefaultMoveComparator extends AbstractDefaultMoveComparator<Move, ChessLibMoveGenerator> {
		private ChessLibDefaultMoveComparator(ChessLibMoveGenerator board) {
			super(board);
		}

		@Override
		public int getMovingPiece(ChessLibMoveGenerator board, Move move) {
			return BasicMoveDecoder.getMovingPiece(board, move);
		}

		@Override
		public int getCapturedType(ChessLibMoveGenerator board, Move move) {
			return BasicMoveDecoder.getCapturedType(board, move);
		}

		@Override
		public int getPromotionType(ChessLibMoveGenerator board, Move move) {
			return BasicMoveDecoder.getPromotionType(move);
		}
	}

	@Test
	void test() {
		final ChessLibMoveGenerator board = new ChessLibMoveGenerator(new Board());
		board.getBoard().loadFromFen("Q2n4/4P3/8/5P2/8/qK3p1k/1P6/8 w - - 0 1");
		final AbstractDefaultMoveComparator<Move, ChessLibMoveGenerator> cmp = new ChessLibDefaultMoveComparator(board);
		final Move queenPawnCatch = new Move(A8, F3);
		final Move queenQueenCatch = new Move(A8, A3);
		final Move kingCatch = new Move(B3, A3);
		final Move pawnMove = new Move(F5, F6);
		final Move pawnPromo = new Move(E7, E8, Piece.WHITE_QUEEN);
		final Move pawnCatchPromo = new Move(E7, D8, Piece.WHITE_QUEEN);
		
		Move[] moves = new Move[] {pawnMove, queenQueenCatch, queenPawnCatch, kingCatch, pawnCatchPromo, pawnPromo};
		final MoveList<Move> sorted = new MoveList<>();
		sorted.setComparator(cmp);
		sorted.addAll(Arrays.asList(moves));
		sorted.sort();
		assertEquals(Arrays.asList(pawnCatchPromo, queenQueenCatch, kingCatch, pawnPromo, queenPawnCatch, pawnMove), sorted);
		assertFalse(cmp.test(pawnMove));
		assertTrue(cmp.compare(kingCatch, queenQueenCatch)>0);
		assertTrue(cmp.compare(queenPawnCatch, kingCatch)>0);
		
		board.getBoard().loadFromFen("3n4/4P3/8/5P2/1p6/QK5k/1P6/8 b - - 0 1");
		final Move pawnCatch = new Move(B4, A3);
		final Move knightMove = new Move(D8, C6);
		assertTrue(cmp.compare(knightMove, pawnCatch)>0);
	}
}

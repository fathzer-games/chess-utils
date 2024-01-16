package com.fathzer.chess.utils.test;

import static org.junit.jupiter.api.Assertions.*;

import static com.fathzer.chess.utils.Pieces.*;
import static com.fathzer.chess.utils.test.Cell.*;

import org.junit.jupiter.api.Test;

import com.fathzer.chess.utils.adapters.BoardExplorer;
import com.fathzer.chess.utils.adapters.MoveData;

/** A generic test of {@link MoveData} implementation
 * <br>Have a look at <a href="https://github.com/fathzer-games/chess-utils/wiki/AbstractMoveDataTest">chess-utils wiki</a> to see an usage example.
 * @param <M> The type of a move
 * @param <B> The type of chess board
*/
public abstract class AbstractMoveDataTest<M, B> {
	/** Creates the MoveData instance to test.
	 * @return The instance to test
	 */
	protected abstract MoveData<M, B> buildMoveData();
	
	/** Converts a <a href="https://en.wikipedia.org/wiki/Forsyth%E2%80%93Edwards_Notation">fen representation</a> to a board.
	 * @param fen The FEN to convert.
	 * @return The board that corresponds to the <i>fen</i> argument.
	 */
	protected abstract B toBoard(String fen);
	
	/** Creates a move.
	 * @param from The index of the moving piece (see {@link BoardExplorer#getIndex()} to learn which index corresponds to which cell).
	 * @param to The destination index of the moving piece (see {@link BoardExplorer#getIndex()} to learn which index corresponds to which cell).
	 * <br>The king castles by moving its castling destination.
	 * @param promotionType The promotion type (in case of a promotion, 0 if there's no promotion).
	 * @param board The board on which the move occurs.
	 * @return The move
	 */
	protected abstract M toMove(int from, int to, int promotionType, B board);
	
	private M toMove(Cell from, Cell to, B board) {
		return toMove(from.ordinal(), to.ordinal(), 0, board);
	}

	private M toMove(Cell from, Cell to, int promotionType, B board) {
		return toMove(from.ordinal(), to.ordinal(), promotionType, board);
	}
	
	@Test
	void test() {
		final MoveData<M, B> mv = buildMoveData();

		B mg;
		
		mg = toBoard("r2qkb1r/1Ppb1ppp/4pn2/p2p4/3P1B2/4P3/P1P2PPP/RN1QK2R w KQkq - 0 6");
		// No capture, no promotion, no castling
		assertTrue(mv.update(toMove(F4, G5, mg), mg));
		assertEquals(BISHOP, mv.getMovingPiece());
		assertEquals(0, mv.getCapturedType());
		assertEquals(0, mv.getPromotionType());
		assertEquals(-1, mv.getCastlingRookIndex());

		// Capture and promotion
		assertTrue(mv.update(toMove(B7, A8, KNIGHT, mg), mg));
		assertEquals(PAWN, mv.getMovingPiece());
		assertEquals(9, mv.getMovingIndex());
		assertEquals(0, mv.getMovingDestination());
		assertEquals(0, mv.getCapturedIndex());
		assertEquals(ROOK, mv.getCapturedType());
		assertEquals(KNIGHT, mv.getPromotionType());
		assertEquals(-1, mv.getCastlingRookIndex());
		
		// Castling
		assertTrue(mv.update(toMove(E1, G1, mg), mg));
		assertEquals(KING, mv.getMovingPiece());
		assertEquals(60, mv.getMovingIndex());
		assertEquals(62, mv.getMovingDestination());
		assertEquals(0, mv.getCapturedIndex());
		assertEquals(0, mv.getCapturedType());
		assertEquals(0, mv.getPromotionType());
		assertEquals(63, mv.getCastlingRookIndex());
		assertEquals(61, mv.getCastlingRookDestinationIndex());
		
		// Promotion with no capture
		assertTrue(mv.update(toMove(B7, B8, QUEEN, mg), mg));
		assertEquals(PAWN, mv.getMovingPiece());
		assertEquals(9, mv.getMovingIndex());
		assertEquals(1, mv.getMovingDestination());
		assertEquals(0, mv.getCapturedType());
		assertEquals(QUEEN, mv.getPromotionType());
		assertEquals(-1, mv.getCastlingRookIndex());
		
		mg = toBoard("rn1qkb1r/1ppb1ppp/4pn2/pP1p4/3P1B2/4P3/P1P2PPP/RN1QKBNR w KQkq a6 0 6");
		// Illegal move (no moving piece)
		assertFalse(mv.update(toMove(B2, B3, mg), mg));
		
		// En passant
		assertTrue (mv.update(toMove(B5, A6, mg), mg));
		assertEquals(PAWN, mv.getMovingPiece());
		assertEquals(25, mv.getMovingIndex());
		assertEquals(16, mv.getMovingDestination());
		assertEquals(24, mv.getCapturedIndex());
		assertEquals(PAWN, mv.getCapturedType());
		assertEquals(0, mv.getPromotionType());
		assertEquals(-1, mv.getCastlingRookIndex());
	}
}

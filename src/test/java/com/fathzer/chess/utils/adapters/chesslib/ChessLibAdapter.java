package com.fathzer.chess.utils.adapters.chesslib;

import static com.fathzer.chess.utils.Pieces.*;
import static com.fathzer.chess.utils.adapters.chesslib.BoardExplorerBuilder.*;

import java.util.stream.Stream;

import com.fathzer.chess.utils.adapters.MoveAdapter;
import com.fathzer.chess.utils.adapters.PieceStreamer;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.PieceType;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;

public interface ChessLibAdapter extends PieceStreamer<Board>, MoveAdapter<Move, Board> {

	@Override
	default boolean isWhiteToMove(Board board) {
		return board.getSideToMove()==Side.WHITE;
	}

	@Override
	default Stream<Integer> getPieces(Board board) {
		return BoardExplorerBuilder.getPieces(board);
	}

	@Override
	default int getMovingPiece(Board board, Move move) {
		return toPiece(board.getPiece(move.getFrom()));
	}
	
	@Override
	default int getCapturedType(Board board, Move move) {
		final Piece moving = board.getPiece(move.getFrom());
		if (Square.NONE!=board.getEnPassantTarget() && PieceType.PAWN==moving.getPieceType() &&
        move.getTo().getFile()!=move.getFrom().getFile()) {
			return PAWN; // A pawn is captured
		} else {
			final Piece piece = board.getPiece(move.getTo());
			// Be aware of castling in chess 960 where we can consider the king captures its own rook!
			return piece.getPieceSide()!=moving.getPieceSide() ? fromPieceType(piece.getPieceType()) : 0;
		}
	}
	
	@Override
	default int getPromotionType(Board board, Move move) {
		return fromPieceType(move.getPromotion().getPieceType());
	}
}

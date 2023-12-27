package com.fathzer.chess.utils.adapters.chesslib;

import java.util.stream.Stream;

import com.fathzer.chess.utils.adapters.ColorAdapter;
import com.fathzer.chess.utils.adapters.MoveAdapter;
import com.fathzer.chess.utils.adapters.PieceEvaluator;
import com.fathzer.chess.utils.adapters.PieceStreamer;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.PieceType;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;

public interface ChessLibAdapter extends ColorAdapter<Board, Side, Piece>, PieceStreamer<Board, Piece>, PieceEvaluator<Piece>, MoveAdapter<Move, Board, Piece> {
	@Override
	default boolean isWhite(Side color) {
		return color==Side.WHITE;
	}

	@Override
	default Side getSideToMove(Board board) {
		return board.getSideToMove();
	}

	@Override
	default Stream<Piece> getPieces(Board board) {
		return BoardExplorerBuilder.getPieces(board);
	}

	@Override
	default Side getColor(Piece piece) {
		return piece.getPieceSide();
	}

	@Override
	default int getValue(Piece piece) {
		return PieceValues.get(piece.getPieceType());
	}

	@Override
	default Piece getMoving(Board board, Move move) {
		return board.getPiece(move.getFrom());
	}

	@Override
	default Piece getCaptured(Board board, Move move) {
		final Piece moving = board.getPiece(move.getFrom());
		if (Square.NONE!=board.getEnPassantTarget() && PieceType.PAWN==moving.getPieceType() &&
        move.getTo().getFile()!=move.getFrom().getFile()) {
			return moving; // Color of piece doesn't matter
		} else {
			final Piece piece = board.getPiece(move.getTo());
			return piece.getPieceSide()!=moving.getPieceSide() ? piece : Piece.NONE;
		}
	}
	
	@Override
	default Piece getPromotion(Board board, Move move) {
		return move.getPromotion();
	}
	
	@Override
	default boolean isNone(Piece piece) {
		return piece==Piece.NONE;
	}
}

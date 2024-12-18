package com.fathzer.chess.utils.adapters.chesslib;

import static com.github.bhlangonijr.chesslib.CastleRight.*;

import static com.fathzer.chess.utils.Pieces.*;

import com.fathzer.chess.utils.adapters.MoveData;

import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.game.GameContext;
import com.github.bhlangonijr.chesslib.move.Move;

public class ChessLibMoveData implements MoveData<Move, ChessLibMoveGenerator> {
	private int movingIndex;
	private int movingPiece;
	private int movingDestination;
	private int capturedIndex;
	private int capturedType;
	private int promotionType;
	private int castlingRookIndex;
	private int castlingRookDestinationIndex;

	@Override
	public int getMovingIndex() {
		return movingIndex;
	}

	@Override
	public int getMovingPiece() {
		return movingPiece;
	}

	@Override
	public int getMovingDestination() {
		return movingDestination;
	}

	@Override
	public int getCapturedType() {
		return capturedType;
	}

	@Override
	public int getCapturedIndex() {
		return capturedIndex;
	}

	@Override
	public int getPromotionType() {
		return promotionType;
	}

	@Override
	public int getCastlingRookIndex() {
		return castlingRookIndex;
	}

	@Override
	public int getCastlingRookDestinationIndex() {
		return castlingRookDestinationIndex;
	}

	@Override
	public boolean update(Move move, ChessLibMoveGenerator board) {
		this.movingIndex = getIndex(move.getFrom());
		this.movingPiece = ChessLibBoardExplorer.toPiece(board.getBoard().getPiece(move.getFrom()));
		int movingType = Math.abs(movingPiece);
		if (movingType==0) {
			return false;
		} else if (movingType==KING) {
			this.promotionType = 0;
			final GameContext context = board.getBoard().getContext();
			if (context.isCastleMove(move)) {
				final Side side = board.getBoard().getSideToMove();
				this.capturedType = 0;
				this.movingDestination = getIndex(move.getTo());
				final Move rookMove = context.getRookCastleMove(side, context.isKingSideCastle(move) ? KING_SIDE : QUEEN_SIDE);
				this.castlingRookIndex = getIndex(rookMove.getFrom());
				this.castlingRookDestinationIndex = getIndex(rookMove.getTo());
			} else {
				this.castlingRookIndex = -1;
				this.movingDestination = getIndex(move.getTo());
				this.capturedType = ChessLibBoardExplorer.fromPieceType(board.getBoard().getPiece(move.getTo()).getPieceType());
				if (this.capturedType!=0) {
					this.capturedIndex = this.movingDestination;
				}
			}
		} else {
			// Not a king move => no castling
			this.castlingRookIndex=-1;
			this.movingDestination = getIndex(move.getTo());
			if (movingType==PAWN && move.getTo()==board.getBoard().getEnPassant()) {
				this.capturedType = PAWN;
				this.capturedIndex = getIndex(board.getBoard().getEnPassantTarget());
				this.promotionType = 0;
			} else {
				this.promotionType = ChessLibBoardExplorer.fromPieceType(move.getPromotion().getPieceType());
				this.capturedType = ChessLibBoardExplorer.fromPieceType(board.getBoard().getPiece(move.getTo()).getPieceType());
				if (this.capturedType!=0) {
					this.capturedIndex = this.movingDestination;
				}
			}
		}
		return true;
	}
	
	int getIndex(Square sq) {
		return 8*(7-sq.getRank().ordinal())+sq.getFile().ordinal();
	}
}

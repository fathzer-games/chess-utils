package com.fathzer.chess.utils.notation;

import static com.fathzer.chess.utils.Pieces.*;

import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import com.fathzer.chess.utils.Pieces;
import com.fathzer.chess.utils.adapters.MoveData;
import com.fathzer.games.MoveGenerator;
import com.fathzer.games.MoveGenerator.MoveConfidence;

/**
 * A move to <a href="https://en.wikipedia.org/wiki/Algebraic_notation_(chess)">Standard Algebraic Notation (SAN)</a> converter.
 * <br>As SAN is not a very strict standard, this class is highly customisable in order to accept usual variations (see the setters).
 */
public abstract class AbstractSANConverter<M, B extends MoveGenerator<M>> implements Supplier<MoveData<M,B>> {
	private static final String[] DEFAULT_PIECE_NOTATIONS = new String[] {"", "", "N", "B", "R", "Q", "K"};

	/** The default symbol used to indicate a check ("+") */
	public static final String DEFAULT_CHECK_SYMBOL = "+";
	/** The default symbol used to indicate a checkmate ("#") */
	public static final String DEFAULT_CHECKMATE_SYMBOL = "#";
	/** The default symbol used to indicate a capture ("x") */
	public static final char DEFAULT_CAPTURE_SYMBOL = 'x';
	/** The usual optional symbol used to indicate an en passant capture (default: "") */
	public static final String USUAL_EN_PASSANT_SYMBOL = " e.p.";

	private String checkSymbol = DEFAULT_CHECK_SYMBOL;
	private String checkmateSymbol = DEFAULT_CHECKMATE_SYMBOL;
	private char captureSymbol = DEFAULT_CAPTURE_SYMBOL;
	private String enPassantSymbol = "";
	private Function<Boolean, String> castlingSymbolBuilder = kingSide -> Boolean.TRUE.equals(kingSide) ? "O-O" : "O-O-O";
	private IntFunction<String> pieceNotationBuilder = p -> DEFAULT_PIECE_NOTATIONS[p];
	private IntFunction<String> promotionSymbolBuilder = p -> "="+pieceNotationBuilder.apply(p);

	/**
	 * Constructor.
	 * <br>See the setters for the default values.
	 */
	protected AbstractSANConverter() {
		// Nothing to do
	}

	/**
	 * Sets the symbol used to indicate a check.
	 * @param checkSymbol the symbol to use (default: "+")
	 */
	public void setCheckSymbol(String checkSymbol) {
		this.checkSymbol = checkSymbol;
	}

	/**
	 * Sets the symbol used to indicate a checkmate.
	 * @param checkmateSymbol the symbol to use (default: "#")
	 */
	public void setCheckmateSymbol(String checkmateSymbol) {
		this.checkmateSymbol = checkmateSymbol;
	}

	/**
	 * Sets the symbol used to indicate a capture.
	 * @param captureSymbol the symbol to use (default: "x")
	 */
	public void setCaptureSymbol(char captureSymbol) {
		this.captureSymbol = captureSymbol;
	}

	/**
	 * Sets the symbol used to indicate an en passant capture.
	 * @param enPassantSymbol the symbol to use (default: "")
	 */
	public void setEnPassantSymbol(String enPassantSymbol) {
		this.enPassantSymbol = enPassantSymbol;
	}

	/**
	 * Sets the symbol used to indicate a castling move.
	 * @param castlingSymbolBuilder the function used to build the castling symbol (default: "O-O" for king side, "O-O-O" for queen side).
	 * <br>The function takes a boolean parameter indicating if the castling is king side (true) or queen side (false).
	 */
	public void setCastlingSymbolBuilder(Function<Boolean, String> castlingSymbolBuilder) {
		this.castlingSymbolBuilder = castlingSymbolBuilder;
	}

	/**
	 * Sets the symbol used to indicate the moving piece.
	 * @param pieceNotationBuilder the function used to build the piece notation (default: nothing for pawns, piece notation in English for other pieces).
	 * <br>The function takes an integer parameter indicating the piece type.
	 * @see Pieces
	 */
	public void setPieceNotationBuilder(IntFunction<String> pieceNotationBuilder) {
		this.pieceNotationBuilder = pieceNotationBuilder;
	}

	/**
	 * Sets the symbol used to indicate a promotion.
	 * @param promotionSymbolBuilder the function used to build the promotion symbol (default: "=" + piece notation).
	 * <br>The function takes an integer parameter indicating the piece type to promote to.
	 * @see Pieces
	 */
	public void setPromotionSymbolBuilder(IntFunction<String> promotionSymbolBuilder) {
		this.promotionSymbolBuilder = promotionSymbolBuilder;
	}

	/**
	 * Returns the notation for the given move.
	 * @param move the move to convert.
	 * @param board the board on which the move is to be made.
	 * @return the move in SAN notation.
	 * @throws IllegalArgumentException if the move is not legal.
	 */
	public String get(M move, B board) {
		final MoveData<M, B> moveData = get();
		if (!moveData.update(move, board)) {
			throw new IllegalArgumentException();
		}

		final MoveData<M, B> mvData = get();
		final List<M> candidates = board.getLegalMoves().stream().filter(m -> {
			mvData.update(m, board);
			return mvData.getMovingDestination() == moveData.getMovingDestination() && mvData.getMovingPiece() == moveData.getMovingPiece();}).
			toList();
		if (candidates.isEmpty()) {
			// Move is not legal
			throw new IllegalArgumentException();
		}

		if (moveData.getCastlingRookIndex()>=0) {
			// If move is a castling
			return castlingSymbolBuilder.apply(isKingSideCastling(moveData.getMovingIndex(), moveData.getMovingDestination()));
		}
		final StringBuilder notation = new StringBuilder();
		final int movingPieceType = Math.abs(moveData.getMovingPiece());
		notation.append(pieceNotationBuilder.apply(movingPieceType));

		if (movingPieceType != PAWN && movingPieceType != KING && candidates.size() > 1) {
			addDisambiguation(notation, board, moveData, candidates, mvData);
		}

		if (moveData.getCapturedType()>0) {
			// If move is a capture
			if (movingPieceType == PAWN) {
				notation.append(fileNotation(getFile(moveData.getMovingIndex())));
			}
			notation.append(captureSymbol);
		}

		// Adds destination square
		addCellNotation(notation, moveData.getMovingDestination());

		// Add promotion piece type
		if (moveData.getPromotionType() != 0) {
			notation.append(promotionSymbolBuilder.apply(moveData.getPromotionType()));
		}

		// Adds check or checkmate
		board.makeMove(move, MoveConfidence.LEGAL);
		try {
			if (isCheck(board)) {
				List<M> legalMoves = board.getLegalMoves();
				notation.append(legalMoves.isEmpty() ? checkmateSymbol : checkSymbol);
			}
		} finally {
			board.unmakeMove();
		}

		if (moveData.getCapturedType()>0 && moveData.getMovingDestination() != moveData.getCapturedIndex()) {
			// En passant
			notation.append(enPassantSymbol);
		}
		return notation.toString();
	}

	/**
	 * Checks if any ambiguity exists in notation and adds disambiguation if needed (e.g. if e2 can be reached via Nfe2 and Nbe2)
	 * @param notation the notation to add disambiguation to
	 * @param board the board on which the move is played
	 * @param moveData the move data of the move to add disambiguation for
	 * @param candidates the list of candidate moves
	 * @param mvData a move data instance to use for disambiguation processing
	 */
	private void addDisambiguation(StringBuilder notation, B board, MoveData<M, B> moveData, List<M> candidates, MoveData<M, B> mvData) {
		// Disambiguation is required
		final int pieceFile = getFile(moveData.getMovingIndex());
		final boolean fileIsEnough = candidates.stream().filter(m -> { mvData.update(m, board); return pieceFile == getFile(mvData.getMovingIndex());}).count() == 1;
		if (fileIsEnough) {
			notation.append(fileNotation(pieceFile));
		} else {
			final int pieceRank = getRank(moveData.getMovingIndex());
			final boolean rankIsEnough = candidates.stream().filter(m -> { mvData.update(m, board); return pieceRank == getRank(mvData.getMovingIndex());}).count() == 1;
			if (rankIsEnough) {
				notation.append(rankNotation(getRank(moveData.getMovingIndex())));
			} else {
				addCellNotation(notation, moveData.getMovingIndex());
			}
		}
	}

	/**
	 * Gets the side of a castling move.
	 * @param kingFrom the index of the king before the castling
	 * @param kingTo the index of the king after the castling
	 * @return true if the castling is king side, false otherwise. The default implementation returns true if kingTo &gt; kingFrom.
	 */
	private boolean isKingSideCastling(int kingFrom, int kingTo) {
		return kingTo > kingFrom;
	}

	/**
	 * Gets the file of a cell.
	 * @param cellIndex the index of the cell
	 * @return the file of the cell (0 for a, 1 for b, etc.). The default implementation returns cellIndex % 8.
	 */
	private int getFile(int cellIndex) {
		return cellIndex & 7;
	}

	/**
	 * Gets the rank of a cell.
	 * @param cellIndex the index of the cell
	 * @return the rank of the cell (0 for 1, 1 for 2, etc.). The default implementation returns cellIndex / 8.
	 */
	private int getRank(int cellIndex) {
		return 7 - (cellIndex >>> 3);
	}

	/**
	 * Checks if the given board is in check.
	 * @param board the board to check
	 * @return true if the board is in check, false otherwise
	 */
	protected abstract boolean isCheck(B board);

	private char fileNotation(int file) {
		return (char)('a' + file);
	}

	private char rankNotation(int rank) {
		return (char)('1' + rank);
	}

	private void addCellNotation(StringBuilder notation, int cellIndex) {
		notation.append(fileNotation(getFile(cellIndex)));
		notation.append(rankNotation(getRank(cellIndex)));
	}
}

package com.fathzer.chess.utils.evaluators.utils;

import static com.fathzer.chess.utils.Pieces.KING;
import static com.fathzer.chess.utils.Pieces.ROOK;

import java.util.concurrent.atomic.AtomicInteger;

import com.fathzer.chess.utils.Pieces;
import com.fathzer.chess.utils.adapters.BoardExplorer;
import com.fathzer.chess.utils.adapters.BoardExplorerBuilder;
import com.fathzer.chess.utils.adapters.MoveData;
import com.fathzer.games.MoveGenerator;
import com.fathzer.games.ai.evaluation.Evaluator;
import com.fathzer.games.util.Stack;

/** An incremental evaluator based on a <a href="https://www.chessprogramming.org/Piece-Square_Tables"> piece square table</a> implementation.
 */
public abstract class AbstractPieceSquareTableEvaluator<M, B extends MoveGenerator<M>> implements ComposableEvaluator<M,B>, BoardExplorerBuilder<B> {
	
	private final Stack<AtomicInteger> states; //TODO implement something lighter
	private int toCommit;
	
	protected AbstractPieceSquareTableEvaluator() {
		this.states = new Stack<>(AtomicInteger::new);
	}

	protected AbstractPieceSquareTableEvaluator(int eval) {
		this();
		this.states.get().set(eval);
	}

	@Override
	public void init(B board) {
		states.clear();
		states.set(new AtomicInteger(getRawEvaluation(getExplorer(board))));
	}
	
	private int getRawEvaluation(BoardExplorer explorer) {
		int points = 0;
		do {
			final int p = explorer.getPiece();
			final int piece = Math.abs(p);
			final int index = explorer.getIndex();
			final boolean isBlack = p<0;
			points += getPositionValue(piece, isBlack, index);
		} while (explorer.next());
		return points;
	}
	
	@Override
	public void prepareMove(MoveData<M, B> moveData) {
		final boolean isBlack = moveData.getMovingPiece()<0;
		int moving = Math.abs(moveData.getMovingPiece());
		final int movingIndex = moveData.getMovingIndex();
		int inc = 0;
		if (moving==KING) {
			// Be cautious with castling
			int rookIndex = moveData.getCastlingRookIndex();
			if (rookIndex>=0) {
				// It's a castling move, update rook positions values
				inc = getPositionValue(ROOK, isBlack, moveData.getCastlingRookDestinationIndex()) - getPositionValue(ROOK, isBlack, rookIndex);
			}
		}
		// Remove the old position value of the moving piece
		inc -= getPositionValue(moving, isBlack, movingIndex);
		final int promoType = moveData.getPromotionType();
		if (promoType!=0) {
			// If promotion, update the moving piece
			moving = promoType;
		}
		// Adds the new position value of the 
		inc += getPositionValue(moving, isBlack, moveData.getMovingDestination());
		int captured = moveData.getCapturedType();
		if (captured!=0) {
			// If the move is a capture add its position value
			inc -= getPositionValue(captured, !isBlack, moveData.getCapturedIndex());
		}
		toCommit = states.get().get() + inc;
	}

	@Override
	public void commitMove() {
		states.next();
		states.set(new AtomicInteger(toCommit));
	}

	@Override
	public void unmakeMove() {
		states.previous();
	}

	@Override
	public Evaluator<M, B> fork() {
		return fork(states.get().get());
	}

	@Override
	public int evaluateAsWhite(B board) {
		return states.get().get();
	}
	
	/** Gets the position value associated with a white piece at an index.
	 * @param piece The piece type as define in {@link Pieces}
	 * @param index The index of the piece on the board as defined in {@link BoardExplorer}
	 * @return an integer
	 */
	protected abstract int getPositionValue(int piece, int index);
	
	/** Gets the value (from the white point of view) of a piece at a position.
	 * @param piece The piece type as define in {@link Pieces}
	 * @param black true if piece is black
	 * @param index The index of the piece on the board as defined in {@link BoardExplorer}
	 * @return an integer
	 */
	private int getPositionValue(int piece, boolean black, int index) {
		if (black) {
			final int row = 7 - index/8;
			final int col = index%8;
			index = row*8 + col;
			return -getPositionValue(piece, index);
		} else {
			return getPositionValue(piece, index);
		}
	}
	
	/** Creates a new instance initialized with current evaluation that will become the initial state of created instance.
	 * @param state The initial state.
	 * @return a new evaluator of the same class as this, the same view point, and initialized with the state.
	 */
	protected abstract AbstractPieceSquareTableEvaluator<M, B> fork(int evaluation);
}

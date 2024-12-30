package com.fathzer.chess.utils.evaluators.utils;

import static com.fathzer.chess.utils.Pieces.*;

import java.util.function.Supplier;

import com.fathzer.chess.utils.adapters.BoardExplorer;
import com.fathzer.chess.utils.adapters.BoardExplorerBuilder;
import com.fathzer.chess.utils.adapters.MoveData;
import com.fathzer.games.MoveGenerator;
import com.fathzer.games.ai.evaluation.Evaluator;
import com.fathzer.games.ai.evaluation.ZeroSumEvaluator;
import com.fathzer.games.util.Stack;

/** An abstract incremental chess evaluator.
 * @param <M> The class that represents a move.
 * @param <B> The class that represents the move generator 
 * @param <S> The class that represents the state of the evaluator.
 */
public abstract class AbstractChessEvaluator<M, B extends MoveGenerator<M>, S> implements ZeroSumEvaluator<M,B>, BoardExplorerBuilder<B>, Supplier<MoveData<M,B>> {
	private final Stack<S> states;
	/** The current state that will be prepared during the {@link #init(BoardExplorer)} and {@link #prepareMove(MoveData)} methods
	 */
	protected S toCommit;
	private MoveData<M, B> moveData;
	
	/** Constructor.
	 * @param stateBuilder A constructor to build states
	 */
	protected AbstractChessEvaluator(Supplier<S> stateBuilder) {
		this.states = new Stack<>(stateBuilder);
		toCommit = stateBuilder.get();
		moveData = get();
	}
	
	/** Constructor.
	 * @param stateBuilder A constructor to build states
	 * @param state The initial state
	 */
	protected AbstractChessEvaluator(Supplier<S> stateBuilder, S state) {
		this(stateBuilder);
		copy(state, toCommit);
		states.set(state);
	}
	
	@Override
	public void init(B board) {
		states.clear();
		clear(toCommit);
		init(getExplorer(board));
		states.set(toCommit);
	}
	
	/** Initializes the evaluator state using a board explorer.
	 * <br>{@link #toCommit} is cleared before this method is called. This method should then initialize
	 * {@link #toCommit} using the explorer.
	 * <br>By default it calls {@link #put(int, boolean, int)} for every piece on the board.
	 * The developer can override this method in order to change this behavior. 
	 * @param explorer The explorer
	 */
	protected void init(BoardExplorer explorer) {
		do {
			final int p = explorer.getPiece();
			final int kind = Math.abs(p);
			final int index = explorer.getIndex();
			final boolean isBlack = p<0;
			put(kind, isBlack, index);
		} while (explorer.next());
	}

	@Override
	public final void prepareMove(B board, M move) {
		if (moveData.update(move, board)) {
			buildToCommit();
			prepareMove(moveData);
		}
	}
	
	private void buildToCommit() {
		final S current = states.get();
		states.next();
		toCommit = states.get();
		states.previous();
		copy(current, toCommit);
	}
	
	@Override
	public final void commitMove() {
		states.next();
		states.set(toCommit);
	}

	@Override
	public final void unmakeMove() {
		states.previous();
	}

	/** Updates {@link #toCommit} accordingly to a move.
	 * <br>This method is called by {@link #prepareMove(MoveGenerator, Object)}.
	 * <br>By default, it calls the {@link #move(int, boolean, int, int)} or {@link #remove(int, boolean, int)} and {@link #add(int, boolean, int)}
	 * methods accordingly to the move.
	 * <br>For instance, for a castling, it will call {@link #move(int, boolean, int, int)} twice ; for the king and for the rook.
	 * For a promotion, it will {@link #remove(int, boolean, int)} the pawn before {@link #add(int, boolean, int)} the promoted
	 * piece.
	 * <br>One could override this method to perform different or extra processing.
	 * @param moveData The data describing the move.
	 */
	protected void prepareMove(MoveData<M, B> moveData) {
		final boolean isBlack = moveData.getMovingPiece()<0;
		int pieceType = Math.abs(moveData.getMovingPiece());
		final int movingIndex = moveData.getMovingIndex();
		final int rookIndex = moveData.getCastlingRookIndex();
		if (rookIndex>=0) {
			// It's a castling move, update rook positions values
			move(ROOK, isBlack, rookIndex, moveData.getCastlingRookDestinationIndex());
			move(KING, isBlack, movingIndex, moveData.getMovingDestination());
			return;
		}
		final int captured = moveData.getCapturedType();
		if (captured!=0) {
			// If the move is a capture add its position value
			remove(captured, !isBlack, moveData.getCapturedIndex());
		}
		final int promoType = moveData.getPromotionType();
		if (promoType!=0) {
			// If promotion, replace the moving pawn by its promotion
			remove(pieceType, isBlack, movingIndex);
			pieceType = promoType;
			add(pieceType, isBlack, moveData.getMovingDestination());
		} else {
			// Move the piece to its new position
			move(pieceType, isBlack, movingIndex, moveData.getMovingDestination());
		}
	}
	
	@Override
	public Evaluator<M, B> fork() {
		return fork(states.get());
	}
	
	/** Creates a new instance initialized with current state that will become the initial state of created instance.
	 * @param state The initial state.
	 * @return a new evaluator of the same class as this, from the same view point, and initialized with the state.
	 */
	protected abstract AbstractChessEvaluator<M, B, S> fork(S state);

	/** Clears a state.
	 * @param state The state to clear.
	 */
	protected abstract void clear(S state);
	/** Copy a state.
	 * @param from The source state
	 * @param to The state where to copy the source state
	 */
	protected abstract void copy(S from, S to);
	
	/** Gets the current committed state.
	 * <br>Warning: This state may be different from {@link #toCommit}
	 * @return a non null state
	 */
	protected S getState() {
		return states.get();
	}
	
	/** Adds a piece to the board.
	 * <br>Typically called when a pawn is promoted or by the {@link #put(int, boolean, int)} method.
	 * @param pieceType The piece type
	 * @param isBlack true if piece is black
	 * @param to The index of the cell where the piece is added
	 */
	protected abstract void add(int pieceType, boolean isBlack, int to);
	
	/** Moves a piece on the board.
	 * @param pieceType The piece type
	 * @param isBlack true if piece is black
	 * @param from The cell's index of the moved piece
	 * @param to The destination cell's index of the moved piece
	 */
	protected abstract void move(int pieceType, boolean isBlack, int from, int to);

	/** Removes a piece from the board.
	 * <br>Typically called when piece is captured.
	 * @param pieceType The piece type
	 * @param isBlack true if piece is black
	 * @param from The index of the cell where the piece was
	 */
	protected abstract void remove(int pieceType, boolean isBlack, int from);
	
	/** A specialized {@link #add(int, boolean, int)} method called during {@link #init(BoardExplorer)}
	 * <br>By default it just calls {@link #add(int, boolean, int)} 
	 * @param pieceType The piece type
	 * @param isBlack true if piece is black
	 * @param to The index of the cell where the piece is added. 
	 */
	protected void put(int pieceType, boolean isBlack, int to) {
		add(pieceType, isBlack, to);
	}
}

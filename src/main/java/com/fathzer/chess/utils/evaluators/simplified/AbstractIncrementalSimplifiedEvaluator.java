package com.fathzer.chess.utils.evaluators.simplified;

import static com.fathzer.chess.utils.Pieces.KING;

import com.fathzer.chess.utils.evaluators.utils.AbstractChessEvaluator;
import com.fathzer.chess.utils.evaluators.utils.AbstractPieceSquareTable;
import com.fathzer.games.MoveGenerator;

/** An incremental implementation of the simplified evaluator described at <a href="https://www.chessprogramming.org/Simplified_Evaluation_Function">https://www.chessprogramming.org/Simplified_Evaluation_Function</a>
 * <br>It only works with 8*8 games and exactly one king per Color.
 */
public abstract class AbstractIncrementalSimplifiedEvaluator <M, B extends MoveGenerator<M>> extends AbstractChessEvaluator<M, B, SimplifiedState> {
	private static final AbstractPieceSquareTable POS_TABLE = new PiecesOnlySquareTable();
	private static final AbstractPieceSquareTable MIDDLE_GAME_KING_TABLE = new KingSquareTable();
	private static final AbstractPieceSquareTable END_GAME_KING_TABLE = new EndGameKingSquareTable();
	
	/** Constructor
	 */
	protected AbstractIncrementalSimplifiedEvaluator() {
		super(SimplifiedState::new);
	}
	
	/** Constructor.
	 * @param state The initial state of the evaluator.
	 */
	protected AbstractIncrementalSimplifiedEvaluator(SimplifiedState state) {
		super(SimplifiedState::new, state);
	}

	@Override
	protected void clear(SimplifiedState state) {
		state.points = 0;
	}
	
	@Override
	protected void copy(SimplifiedState from, SimplifiedState to) {
		from.copyTo(to);
	}

	@Override
	protected void put(int pieceType, boolean isBlack, int to) {
		add(pieceType, isBlack, to);
		updateKingPositions(pieceType, isBlack, to);
	}

	@Override
	protected void add(int pieceType, boolean isBlack, int to) {
		toCommit.points += POS_TABLE.get(pieceType, isBlack, to);
		toCommit.add(isBlack ? -pieceType : pieceType);
	}

	@Override
	protected void move(int pieceType, boolean isBlack, int from, int to) {
		toCommit.points -= POS_TABLE.get(pieceType, isBlack, from);
		toCommit.points += POS_TABLE.get(pieceType, isBlack, to);
		updateKingPositions(pieceType, isBlack, to);
	}

	@Override
	protected void remove(int pieceType, boolean isBlack, int from) {
		toCommit.points -= POS_TABLE.get(pieceType, isBlack, from);
		toCommit.remove(isBlack ? -pieceType : pieceType);
	}

	private void updateKingPositions(int pieceType, boolean isBlack, int to) {
		if (pieceType==KING) {
			if (isBlack) {
				toCommit.blackKingIndex = to;
			} else {
				toCommit.whiteKingIndex = to;
			}			
		}
	}

	@Override
	public int evaluateAsWhite(B board) {
		final SimplifiedState state = getState();
		final AbstractPieceSquareTable kingsTable = state.isEndGamePhase() ? END_GAME_KING_TABLE : MIDDLE_GAME_KING_TABLE;
		return state.points + kingsTable.get(KING, false, state.whiteKingIndex) + kingsTable.get(KING, true, state.blackKingIndex);
	}
}

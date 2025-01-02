package com.fathzer.chess.utils.evaluators.pesto;

import com.fathzer.chess.utils.evaluators.utils.AbstractChessEvaluator;
import com.fathzer.chess.utils.evaluators.utils.AbstractPieceSquareTable;
import com.fathzer.games.MoveGenerator;

/** An incremental implementation of the PESTO evaluator described at <a href="https://www.chessprogramming.org/PeSTO%27s_Evaluation_Function">https://www.chessprogramming.org/PeSTO%27s_Evaluation_Function</a>
 * <br>It only works with 8*8 games.
 */
public abstract class AbstractIncrementalPestoEvaluator<M, B extends MoveGenerator<M>> extends AbstractChessEvaluator<M, B, PestoState> {
	private static final AbstractPieceSquareTable MIDDLE_GAME_TABLE = new MiddleGamePieceSquareTable();
	private static final AbstractPieceSquareTable END_GAME_TABLE = new EndGamePieceSquareTable();
	private static final int[] GAME_PHASE_WEIGHT = new int[]{0,0,1,1,2,4,0};
	
	/** Constructor
	 */
	protected AbstractIncrementalPestoEvaluator() {
		super(PestoState::new);
	}
	
	/** Constructor.
	 * @param state The initial state of the evaluator.
	 */
	protected AbstractIncrementalPestoEvaluator(PestoState state) {
		super(PestoState::new, state);
	}
	
    @Override
	protected void clear(PestoState state) {
		state.clear();
	}

	@Override
	protected void copy(PestoState from, PestoState to) {
		from.copyTo(to);
	}
	
	@Override
	protected void add(int pieceType, boolean isBlack, int to) {
		toCommit.mgPoints += MIDDLE_GAME_TABLE.get(pieceType, isBlack, to);
		toCommit.egPoints += END_GAME_TABLE.get(pieceType, isBlack, to);
		toCommit.phasePoints += GAME_PHASE_WEIGHT[pieceType];
	}

	@Override
	protected void move(int pieceType, boolean isBlack, int from, int to) {
		toCommit.mgPoints -= MIDDLE_GAME_TABLE.get(pieceType, isBlack, from);
		toCommit.mgPoints += MIDDLE_GAME_TABLE.get(pieceType, isBlack, to);
		toCommit.egPoints -= END_GAME_TABLE.get(pieceType, isBlack, from);
		toCommit.egPoints += END_GAME_TABLE.get(pieceType, isBlack, to);
	}

	@Override
	protected void remove(int pieceType, boolean isBlack, int from) {
		toCommit.mgPoints -= MIDDLE_GAME_TABLE.get(pieceType, isBlack, from);
		toCommit.egPoints -= END_GAME_TABLE.get(pieceType, isBlack, from);
		toCommit.phasePoints -= GAME_PHASE_WEIGHT[pieceType];
	}

	@Override
	public int evaluateAsWhite(B board) {
		final PestoState state = getState();
		int mgPhase = Math.min(state.phasePoints, 24);
        int egPhase = 24 - mgPhase;
        return (state.mgPoints*mgPhase + state.egPoints*egPhase) / 24;
    }
}

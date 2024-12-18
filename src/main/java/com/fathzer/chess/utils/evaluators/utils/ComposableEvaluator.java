package com.fathzer.chess.utils.evaluators.utils;

import com.fathzer.chess.utils.adapters.MoveData;
import com.fathzer.games.MoveGenerator;
import com.fathzer.games.ai.evaluation.ZeroSumEvaluator;

public interface ComposableEvaluator<M, B extends MoveGenerator<M>> extends ZeroSumEvaluator<M, B> {
	void prepareMove(MoveData<M, B> moveData);

	@Override
	default void prepareMove(B board, M move) {
		throw new UnsupportedOperationException("Prepare move is not implemented");
	}
}
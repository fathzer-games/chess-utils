package com.fathzer.chess.utils.evaluators.simplified.two;

import java.util.function.Supplier;

import com.fathzer.chess.utils.adapters.MoveData;
import com.fathzer.games.MoveGenerator;
import com.fathzer.games.ai.evaluation.ZeroSumEvaluator;

public abstract class AbstractSimplifiedEvaluator2 <M, B extends MoveGenerator<M>> implements ZeroSumEvaluator<M,B>, Supplier<MoveData<M,B>> {
	
}

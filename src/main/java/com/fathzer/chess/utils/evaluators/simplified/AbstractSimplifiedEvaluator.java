package com.fathzer.chess.utils.evaluators.simplified;

import com.fathzer.chess.utils.adapters.BoardExplorerBuilder;
import com.fathzer.games.MoveGenerator;
import com.fathzer.games.ai.evaluation.AbstractEvaluator;
import com.fathzer.games.ai.evaluation.StaticEvaluator;

/** The simplified evaluator described at <a href="https://www.chessprogramming.org/Simplified_Evaluation_Function">https://www.chessprogramming.org/Simplified_Evaluation_Function</a>
 * <br>This only works with 8*8 games and exactly one king per Color.
 * <br>You should consider using {@link AbstractIncrementalSimplifiedEvaluator} which does the same evaluation ... faster.
 */
public abstract class AbstractSimplifiedEvaluator<M, B extends MoveGenerator<M>> extends AbstractEvaluator<M, B> implements StaticEvaluator<M,B>, BoardExplorerBuilder<B> {
	@Override
	protected int evaluateAsWhite(B board) {
		return new BasicState(getExplorer(board)).evaluateAsWhite();
	}
}

package com.fathzer.chess.utils.evaluators;

import static com.fathzer.games.Color.WHITE;

import com.fathzer.games.Color;
import com.fathzer.games.MoveGenerator;
import com.fathzer.games.ai.evaluation.Evaluator;

/** An abstract evaluator that is extended by other evaluators of this library.
 * @param <M> The type of moves
 * @param <B> The type of the chess move generator
 */
public abstract class AbstractEvaluator<M, B extends MoveGenerator<M>> implements Evaluator<M, B> {
	/** The evaluator point of view (1 for white, -1 for black, 0 for current player.
	 */
	protected int viewPoint;

	@Override
	public void setViewPoint(Color color) {
		if (color==null) {
			this.viewPoint = 0;
		} else {
			this.viewPoint = color==WHITE ? 1 : -1;
		}
	}
}

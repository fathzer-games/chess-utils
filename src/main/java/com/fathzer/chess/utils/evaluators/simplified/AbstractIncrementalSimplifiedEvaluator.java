package com.fathzer.chess.utils.evaluators.simplified;

import java.util.function.Supplier;

import com.fathzer.chess.utils.adapters.MoveData;
import com.fathzer.games.MoveGenerator;
import com.fathzer.games.ai.evaluation.Evaluator;
import com.fathzer.games.util.Stack;

/** An incremental implementation of the simplified evaluator described at <a href="https://www.chessprogramming.org/Simplified_Evaluation_Function">https://www.chessprogramming.org/Simplified_Evaluation_Function</a>
 * <br>This only works with 8*8 games and exactly one king per Color.
 */
public abstract class AbstractIncrementalSimplifiedEvaluator<M, B extends MoveGenerator<M>> extends SimplifiedEvaluatorBase<M, B> implements Evaluator<M,B>, Supplier<MoveData<M,B>> {
	private final Stack<IncrementalState> states;
	private IncrementalState toCommit;
	private MoveData<M, B> moveData;
	
	/** Default constructor
	 */
	protected AbstractIncrementalSimplifiedEvaluator() {
		this.states = new Stack<>(IncrementalState::new);
		this.moveData = get();
	}
	
	/** Constructor.
	 * @param state The state to initialize the evaluator
	 */
	protected AbstractIncrementalSimplifiedEvaluator(IncrementalState state) {
		this();
		IncrementalState other = new IncrementalState();
		state.copyTo(other);
		states.set(state);
	}


	@Override
	public Evaluator<M, B> fork() {
		final AbstractIncrementalSimplifiedEvaluator<M, B> result = fork(states.get());
		result.viewPoint = this.viewPoint;
		return result;
	}
	
	/** Creates a new instance initialized with current state that will become the initial state of created instance.
	 * @param state The initial state.
	 * @return a new evaluator of the same class as this, this the same view point, and initialized with the state.
	 */
	protected abstract AbstractIncrementalSimplifiedEvaluator<M, B> fork(IncrementalState state);

	@Override
	public void init(B board) {
		states.clear();
		states.set(new IncrementalState(getExplorer(board)));
	}

	@Override
	public void prepareMove(B board, M move) {
		if (moveData.update(move, board)) {
			buildToCommit();
			toCommit.update(moveData);
		}
	}
	
	private void buildToCommit() {
		final IncrementalState current = states.get();
		states.next();
		toCommit = states.get();
		states.previous();
		current.copyTo(toCommit);
	}

	@Override
	public void commitMove() {
		states.next();
		states.set(toCommit);
	}

	@Override
	public void unmakeMove() {
		states.previous();
	}

	@Override
	protected int evaluateAsWhite(B board) {
		return states.get().evaluateAsWhite();
	}
	
	IncrementalState getState() {
		return states.get();
	}
}

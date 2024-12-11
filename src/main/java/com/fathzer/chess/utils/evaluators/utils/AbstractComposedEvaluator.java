package com.fathzer.chess.utils.evaluators.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

import com.fathzer.chess.utils.adapters.BoardExplorerBuilder;
import com.fathzer.chess.utils.adapters.MoveData;
import com.fathzer.games.MoveGenerator;
import com.fathzer.games.ai.evaluation.ZeroSumEvaluator;

public abstract class AbstractComposedEvaluator<M, B extends MoveGenerator<M>> implements ZeroSumEvaluator<M,B>, BoardExplorerBuilder<B>, Supplier<MoveData<M,B>> {
	private MoveData<M, B> moveData;
	private List<ComposableEvaluator<M, B>> evaluators;
	
	protected AbstractComposedEvaluator() {
		moveData = get();
		evaluators = new LinkedList<>();
	}
	
	@Override
	public void prepareMove(B board, M move) {
		if (!moveData.update(move, board)) {
			return;
		}
		evaluators.forEach(ev -> ev.prepareMove(moveData));
	}

	@Override
	public void init(B board) {
		evaluators.forEach(ev -> ev.init(board));
	}

	@Override
	public void commitMove() {
		evaluators.forEach(ZeroSumEvaluator::commitMove);
	}

	@Override
	public void unmakeMove() {
		evaluators.forEach(ev -> ev.unmakeMove());
	}

	@Override
	public int evaluateAsWhite(B board) {
		return evaluators.stream().mapToInt(ev -> ev.evaluateAsWhite(board)).sum();
	}
}

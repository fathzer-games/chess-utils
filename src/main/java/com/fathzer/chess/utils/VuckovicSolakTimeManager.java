package com.fathzer.chess.utils;

import com.fathzer.chess.utils.adapters.PieceEvaluator;
import com.fathzer.chess.utils.adapters.PieceStreamer;
import com.fathzer.games.ai.time.RemainingMoveCountPredictor;

/** A {@link RemainingMoveCountPredictor} that uses the function described in chapter 4 of <a href="http://facta.junis.ni.ac.rs/acar/acar200901/acar2009-07.pdf">Vuckovic and Solak paper</a>.
 * <br>A chess engine has to determine how much time it can spent searching the best move. This class is a ready to use implementation of the research mentioned above.
 * <br>In order to get the right results, the {@link PieceEvaluator#getValue(Object)} method should return the <a href="https://en.wikipedia.org/wiki/Chess_piece_relative_value">standard valuation of pieces</a>
 */
public abstract class VuckovicSolakTimeManager<B, P> implements RemainingMoveCountPredictor<B>, PieceStreamer<B, P>, PieceEvaluator<P> {
	
	@Override
	public int getRemainingHalfMoves(B board) {
		final int points = getPieces(board).filter(this::isNotKing).mapToInt(this::getValue).sum();
		final int remainingMoves;
		if (points<20) {
			remainingMoves = points+10;
		} else if (points<60) {
			remainingMoves = 3*points/8+22;
		} else {
			remainingMoves = 5*points/4-30;
		}
		return remainingMoves;
	}
	
	protected abstract boolean isNotKing(P piece);
}

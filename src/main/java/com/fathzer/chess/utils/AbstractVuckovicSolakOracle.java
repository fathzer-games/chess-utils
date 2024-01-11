package com.fathzer.chess.utils;

import static com.fathzer.chess.utils.Pieces.*;

import com.fathzer.chess.utils.adapters.BoardExplorerBuilder;
import com.fathzer.games.ai.time.RemainingMoveCountPredictor;

/** A {@link RemainingMoveCountPredictor} that uses the function described in chapter 4 of <a href="http://facta.junis.ni.ac.rs/acar/acar200901/acar2009-07.pdf">Vuckovic and Solak paper</a>.
 * <br>A chess engine has to determine how much time it can spent searching the best move. This class is a ready to use implementation of the research mentioned above.
 */
public abstract class AbstractVuckovicSolakOracle<B> implements RemainingMoveCountPredictor<B>, BoardExplorerBuilder<B> {
	
	@Override
	public int getRemainingHalfMoves(B board) {
		final int points = getPieces(board).map(Math::abs).filter(t->t!=KING).map(Pieces::getPoints).sum();
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
}

package com.fathzer.chess.utils.evaluators;

/** Utility class to convert centi pawns evaluations to win probability.
 * <br>This class implements the functions found in <a href="https://www.chessprogramming.org/Pawn_Advantage,_Win_Percentage,_and_Elo">Chess programming Wiki</a>
 */
public final class WdlPawnsConverter {
	private WdlPawnsConverter() {
		super();
	}
	
	/** Converts a win probability to a centi pawns evaluation.
	 * @param wdl The win probability (a double strictly between 0.0 and 1.0)
	 * @return a centi pawn evaluation
	 * @throws IllegalArgumentException if wdl is &lt;=0.0 or &gt;=1.0.
	 */
	public static int toCentiPawns(double wdl) {
		if (wdl>=1.0 || wdl<=0.0) {
			throw new IllegalArgumentException();
		}
		return (int)Math.round(400.0*Math.log10(wdl/(1-wdl)));
	}
	
	/** Converts a centi pawns evaluation to a win probability.
	 * @param centiPawns A centi pawn evaluation
	 * @return The win probability (a double strictly between 0.0 and 1.0).
	 * 0.5 means both players have equals chances.
	 */
	public static double toWdl(int centiPawns) {
		return 1.0/(1.0+Math.pow(10, -(double)centiPawns/400));
	}
}

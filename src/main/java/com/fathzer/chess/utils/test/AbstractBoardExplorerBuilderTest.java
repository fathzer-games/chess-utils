package com.fathzer.chess.utils.test;

import static org.junit.jupiter.api.Assertions.*;

import static com.fathzer.chess.utils.Pieces.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import com.fathzer.chess.utils.adapters.BoardExplorer;
import com.fathzer.chess.utils.adapters.BoardExplorerBuilder;

/** A generic test of {@link BoardExplorerBuilder} implementation
 * <br>Have a look at <a href="https://github.com/fathzer-games/chess-utils/wiki/AbstractBoardExplorerBuilderTest">chess-utils wiki</a> to see an usage example.
 * @param <B> The type of chess board
*/
public abstract class AbstractBoardExplorerBuilderTest<B> {
	
	/** Creates the builder to test.
	 * @return The builder to test
	 */
	protected abstract BoardExplorerBuilder<B> getBuilder();
	
	/** Converts a <a href="https://en.wikipedia.org/wiki/Forsyth%E2%80%93Edwards_Notation">fen representation</a> to a board.
	 * @param fen The FEN to convert.
	 * @return The board that corresponds to the <i>fen</i> argument.
	 */
	protected abstract B toBoard(String fen);

	@Test
	void test() {
		final BoardExplorerBuilder<B> builder = getBuilder();
		B board = toBoard("3k1q2/b1n5/5p2/3P2rp/P7/8/2B3N1/2Q1K2R w - - 0 1");
		Map<Integer, Integer> expected = Map.ofEntries(Map.entry(3, -KING), Map.entry(5, -QUEEN),
				Map.entry(8, -BISHOP), Map.entry(10, -KNIGHT), Map.entry(21, -PAWN),
				Map.entry(27, PAWN), Map.entry(30,-ROOK), Map.entry(31, -PAWN), 
				Map.entry(32, PAWN), Map.entry(50, BISHOP),
				Map.entry(54, KNIGHT), Map.entry(58, QUEEN), Map.entry(60, KING), Map.entry(63, ROOK));
		Map<Integer, Integer> map = toMap(builder.getExplorer(board));
		assertEquals(expected, map);
		testStream(expected, builder.getPieces(board));
		
		board = toBoard("b2k1q2/2n5/5p2/3P2r1/8/8/2B3N1/2Q1KR2 w - - 0 1");
		expected = Map.ofEntries(Map.entry(0, -BISHOP), Map.entry(3, -KING), Map.entry(5, -QUEEN),
				Map.entry(10, -KNIGHT), Map.entry(21, -PAWN), Map.entry(27, PAWN),
				Map.entry(30,-ROOK), Map.entry(50, BISHOP), Map.entry(54, KNIGHT),
				Map.entry(58, QUEEN), Map.entry(60, KING), Map.entry(61, ROOK));
		map = toMap(builder.getExplorer(board));
		assertEquals(expected, map);
		testStream(expected, builder.getPieces(board));
	}

	private Map<Integer, Integer> toMap(BoardExplorer exp) {
		final Map<Integer, Integer> result = new HashMap<Integer, Integer>();
		do {
			result.put(exp.getIndex(), exp.getPiece());
		} while (exp.next());
		return result;
	}
	
	private void testStream(Map<Integer, Integer> expected, IntStream pieces) {
		final List<Integer> expectedValues = new ArrayList<>(expected.values());
		Collections.sort(expectedValues);
		final List<Integer> actual = pieces.boxed().collect(Collectors.toList());
		Collections.sort(actual);
		assertEquals(expectedValues, actual);
	}
}

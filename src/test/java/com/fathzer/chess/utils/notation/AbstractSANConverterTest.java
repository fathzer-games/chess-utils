package com.fathzer.chess.utils.notation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.fathzer.chess.test.utils.ChessLibAdapter;
import com.fathzer.chess.utils.adapters.chesslib.ChessLibMoveGenerator;
import com.fathzer.chess.utils.model.Variant;
import com.fathzer.chess.utils.test.SANTest;
import com.github.bhlangonijr.chesslib.move.Move;

class AbstractSANConverterTest extends SANTest<ChessLibMoveGenerator, Move> {

    @Test
    void custom() {
        final String[] pieceNotations = new String[] {"", "", "C", "F", "T", "D", "R"};
        final AbstractSANConverter<Move, ChessLibMoveGenerator> converter = new ChessLibAdapter.SANConverter();
        converter.setCheckSymbol("!");
        converter.setCheckmateSymbol("!!");
        converter.setCaptureSymbol('#');
        converter.setEnPassantSymbol(AbstractSANConverter.USUAL_EN_PASSANT_SYMBOL);
        converter.setCastlingSymbolBuilder(kingSide -> Boolean.TRUE.equals(kingSide) ? "0-0" : "0-0-0");
        converter.setPieceNotationBuilder(p -> pieceNotations[p]);
        converter.setPromotionSymbolBuilder(p -> ">>" + pieceNotations[p]);

        testSAN(converter, "rnbqkbnr/pppp1ppp/8/4p3/3P3P/8/PPP1PPP1/RNBQKBNR b KQkq d3 0 2", "e5d4", "e#d4");
        testSAN(converter, "r1b1k2r/ppp2ppp/5n2/7P/Pq1n4/6P1/1P1Q1P2/1R2KBNR b Kkq - 1 13", "b4d2", "D#d2!");
        testSAN(converter, "r5k1/pp3ppp/2p2n2/P5PP/KP3P2/2r5/8/1bq5 b - - 0 28", "c1a3", "Da3!!");
        testSAN(converter, "r3k2r/pppnqppp/3b1n2/5b2/8/4P3/PPP2PPP/RNBQKBNR b KQkq - 3 6", "e8g8", "0-0");
        testSAN(converter, "r3k2r/pppnqppp/3b1n2/5b2/8/4P3/PPP2PPP/RNBQKBNR b KQkq - 3 6", "e8c8", "0-0-0");
        testSAN(converter, "rnbqkbnr/p1pppppp/8/PpP5/8/8/1P1PPPPP/RNBQKBNR w KQkq b6 0 1", "a5b6", "a#b6 e.p.");
        testSAN(converter, "2k1r3/Ppp1pP2/3p4/8/2P5/1P3K2/2PP2P1/R1B1Q3 w - - 0 1", "f7f8b", "f8>>F");
        testSAN(converter, "2k1r3/Ppp1pP2/3p4/8/2P5/1P3K2/2PP2P1/R1B1Q3 w - - 0 1", "f7e8q", "f#e8>>D!!");
        testSAN(converter, "2kr3r/pppppppp/8/R7/2P1Q2Q/1P3K2/2PP2PP/RNB4Q w - - 0 1", "a1a3", "T1a3");
    }

    private void testSAN(AbstractSANConverter<Move, ChessLibMoveGenerator> converter, String fen, String uciMove, String expectedSan) {
		final ChessLibMoveGenerator board = adapter.fenToBoard(fen, Variant.STANDARD);
		assertEquals(expectedSan, converter.get(board.toMove(uciMove), board));
	}
}

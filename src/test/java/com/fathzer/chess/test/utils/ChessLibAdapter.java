package com.fathzer.chess.test.utils;

import com.fathzer.chess.utils.adapters.MoveData;
import com.fathzer.chess.utils.adapters.chesslib.ChessLibMoveData;
import com.fathzer.chess.utils.adapters.chesslib.ChessLibMoveGenerator;
import com.fathzer.chess.utils.model.TestAdapter;
import com.fathzer.chess.utils.model.Variant;
import com.fathzer.chess.utils.notation.AbstractSANConverter;
import com.fathzer.chess.utils.test.SANTest.SANConverter;
import com.github.bhlangonijr.chesslib.move.Move;

public class ChessLibAdapter implements TestAdapter<ChessLibMoveGenerator, Move>, SANConverter<ChessLibMoveGenerator, Move> {
    @Override
    public ChessLibMoveGenerator fenToBoard(String fen, Variant variant) {
       return FENUtils.from(fen);
    }

    @Override
    public String getSAN(ChessLibMoveGenerator board, Move move) {
        return new SANConverter().get(move, board);
    }

    public static class SANConverter extends AbstractSANConverter<Move, ChessLibMoveGenerator> {
        @Override
        protected boolean isCheck(ChessLibMoveGenerator board) {
            return board.getBoard().isKingAttacked();
        }

        @Override
        public MoveData<Move, ChessLibMoveGenerator> get() {
            return new ChessLibMoveData();
        }
    }
}
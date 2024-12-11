package com.fathzer.chess.utils.evaluators.pesto;

public class PestoEvaluator {
    // Piece types
    public static final int PAWN = 0;
    public static final int KNIGHT = 1;
    public static final int BISHOP = 2;
    public static final int ROOK = 3;
    public static final int QUEEN = 4;
    public static final int KING = 5;

    // Board representation
    public static final int WHITE = 0;
    public static final int BLACK = 1;

    // Piece definitions
    public static final int WHITE_PAWN = (2 * PAWN + WHITE);
    public static final int BLACK_PAWN = (2 * PAWN + BLACK);
    public static final int WHITE_KNIGHT = (2 * KNIGHT + WHITE);
    public static final int BLACK_KNIGHT = (2 * KNIGHT + BLACK);
    public static final int WHITE_BISHOP = (2 * BISHOP + WHITE);
    public static final int BLACK_BISHOP = (2 * BISHOP + BLACK);
    public static final int WHITE_ROOK = (2 * ROOK + WHITE);
    public static final int BLACK_ROOK = (2 * ROOK + BLACK);
    public static final int WHITE_QUEEN = (2 * QUEEN + WHITE);
    public static final int BLACK_QUEEN = (2 * QUEEN + BLACK);
    public static final int WHITE_KING = (2 * KING + WHITE);
    public static final int BLACK_KING = (2 * KING + BLACK);
    public static final int EMPTY = BLACK_KING + 1;

    public static int sideToMove;
    public static int[] board = new int[64];

    public static int[] mgValue = {82, 337, 365, 477, 1025, 0};
    public static int[] egValue = {94, 281, 297, 512, 936, 0};

    public static int[][] mgPestoTable = { /* Add mg tables here */ };
    public static int[][] egPestoTable = { /* Add eg tables here */ };

    public static int[][] mgTable = new int[12][64];
    public static int[][] egTable = new int[12][64];

    public static final int[] gamePhaseInc = {0, 0, 1, 1, 1, 1, 2, 2, 4, 4, 0, 0};

    public static void initTables() {
        for (int p = PAWN, pc = WHITE_PAWN; p <= KING; pc += 2, p++) {
            for (int sq = 0; sq < 64; sq++) {
                mgTable[pc][sq] = mgValue[p] + mgPestoTable[p][sq];
                egTable[pc][sq] = egValue[p] + egPestoTable[p][sq];
                mgTable[pc + 1][sq] = mgValue[p] + mgPestoTable[p][flip(sq)];
                egTable[pc + 1][sq] = egValue[p] + egPestoTable[p][flip(sq)];
            }
        }
    }

    public static int flip(int square) {
        return square ^ 56;
    }

    public static int other(int side) {
        return side ^ 1;
    }

    public static int pcolor(int p) {
        return p & 1;
    }

    public static int eval() {
        int[] mg = {0, 0};
        int[] eg = {0, 0};
        int gamePhase = 0;

        for (int sq = 0; sq < 64; ++sq) {
            int pc = board[sq];
            if (pc != EMPTY) {
                mg[pcolor(pc)] += mgTable[pc][sq];
                eg[pcolor(pc)] += egTable[pc][sq];
                gamePhase += gamePhaseInc[pc];
            }
        }

        int mgScore = mg[sideToMove] - mg[other(sideToMove)];
        int egScore = eg[sideToMove] - eg[other(sideToMove)];
        int mgPhase = Math.min(gamePhase, 24);
        int egPhase = 24 - mgPhase;

        return (mgScore * mgPhase + egScore * egPhase) / 24;
    }
}

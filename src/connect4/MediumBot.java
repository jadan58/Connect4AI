package connect4;

import java.util.List;

public class MediumBot implements Player {

    private static final int AI_PLAYER = 2;
    private static final int HUMAN_PLAYER = 1;

    @Override
    public Move getMove(Board board) {
        List<Integer> validCols = board.getValidCols();
        int bestScore = Integer.MIN_VALUE;
        int bestCol = validCols.get(0);

        for (int col : validCols) {
            Board copy = copyBoard(board);
            copy.dropPiece(new Move(AI_PLAYER, col));
            int score = scoreMove(copy, AI_PLAYER);

            if (copy.checkWin(AI_PLAYER)) {
                System.out.println("MediumBot chooses column: " + col);
                return new Move(AI_PLAYER, col);
            }

            Board humanCheck = copyBoard(board);
            humanCheck.dropPiece(new Move(HUMAN_PLAYER, col));
            if (humanCheck.checkWin(HUMAN_PLAYER)) {
                score += 90;
            }

            if (col == 4) {
                score += 50;
            }

            if (col == 1 || col == 7) {
                score += 30;
            }

            if (col == 2 || col == 3 || col == 5 || col == 6) {
                score += 10;
            }

            if (score > bestScore) {
                bestScore = score;
                bestCol = col;
            }
        }

        System.out.println("MediumBot chooses column: " + bestCol);
        return new Move(AI_PLAYER, bestCol);
    }

    private int scoreMove(Board board, int player) {
        int score = 0;
        if (board.checkWin(player)) {
            score += 100;
        }
        return score;
    }

    private Board copyBoard(Board original) {
        Board copy = new Board();
        for (int i = 0; i < 6; i++) {
            System.arraycopy(original.board[i], 0, copy.board[i], 0, 7);
        }
        return copy;
    }
}
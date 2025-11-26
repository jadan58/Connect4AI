package connect4;

import java.util.List;

public class HardBot implements Player {

    private static final int AI_PLAYER = 2;
    private static final int HUMAN_PLAYER = 1;
    private static final int MAX_DEPTH = 5;

    @Override
    public Move getMove(Board board) {
        List<Integer> validCols = board.getValidCols();

        int bestScore = Integer.MIN_VALUE;
        int bestCol = validCols.get(0); // fallback
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        for (int col : validCols) {
            Board boardCopy = copyBoard(board);
            boardCopy.dropPiece(new Move(AI_PLAYER, col));

            int score = minimax(boardCopy, MAX_DEPTH - 1, alpha, beta, false);

            if (score > bestScore) {
                bestScore = score;
                bestCol = col;
            }

            alpha = Math.max(alpha, bestScore);
        }

        System.out.println("HardBot chooses column: " + bestCol);
        return new Move(AI_PLAYER, bestCol);
    }

    //MINIMAX + ALPHA-BETA
    private int minimax(Board board, int depth, int alpha, int beta, boolean maximizingPlayer) {
        if (board.checkWin(AI_PLAYER)) {
            return 100;   //AI wins
        }
        if (board.checkWin(HUMAN_PLAYER)) {
            return -100;  //Human wins
        }
        if (board.isFull() || depth == 0) {
            return evaluateBoard(board);
        }

        List<Integer> validCols = board.getValidCols();
        if (validCols.isEmpty()) {
            return 0;//draw
        }

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (int col : validCols) {
                Board copy = copyBoard(board);
                copy.dropPiece(new Move(AI_PLAYER, col));

                int eval = minimax(copy, depth - 1, alpha, beta, false);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break; // beta cut-off
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (int col : validCols) {
                Board copy = copyBoard(board);
                copy.dropPiece(new Move(HUMAN_PLAYER, col));

                int eval = minimax(copy, depth - 1, alpha, beta, true);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break; // alpha cut-off
                }
            }
            return minEval;
        }
    }

    //Board Copy Helper
    private Board copyBoard(Board original) {
        Board copy = new Board();
        for (int i = 0; i < 6; i++) {
            System.arraycopy(original.board[i], 0, copy.board[i], 0, 7);
        }
        return copy;
    }

    // Heuristic evaluation of the board from AI perspective.
    private int evaluateBoard(Board board) {
        int score = 0;
        int[][] b = board.board;
        //Prefer center column (good Connect 4 heuristic)
        int centerCol = 3;
        int centerCount = 0;
        for (int i = 0; i < 6; i++) {
            if (b[i][centerCol] == AI_PLAYER) {
                centerCount++;
            }
        }
        score += centerCount * 3;

        // Horizontal windows
        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 7 - 3; c++) {
                int[] window = { b[r][c], b[r][c + 1], b[r][c + 2], b[r][c + 3] };
                score += evaluateWindow(window);
            }
        }

        // Vertical windows
        for (int c = 0; c < 7; c++) {
            for (int r = 0; r < 6 - 3; r++) {
                int[] window = { b[r][c], b[r + 1][c], b[r + 2][c], b[r + 3][c] };
                score += evaluateWindow(window);
            }
        }

        // Diagonal (bottom-left to top-right)
        for (int r = 0; r < 6 - 3; r++) {
            for (int c = 0; c < 7 - 3; c++) {
                int[] window = {
                    b[r][c], b[r + 1][c + 1], b[r + 2][c + 2], b[r + 3][c + 3]
                };
                score += evaluateWindow(window);
            }
        }

        // Diagonal
        for (int r = 3; r < 6; r++) {
            for (int c = 0; c < 7 - 3; c++) {
                int[] window = {
                    b[r][c], b[r - 1][c + 1], b[r - 2][c + 2], b[r - 3][c + 3]
                };
                score += evaluateWindow(window);
            }
        }

        return score;
    }

    /*
     * Evaluate a 4-cell window.
     * - Reward AI 3-in-a-row with an empty spot.
     * - Small reward for 2-in-a-row.
     * - Penalize when human has 3-in-a-row with an empty spot.
     */
    private int evaluateWindow(int[] window) {
        int aiCount = 0;
        int humanCount = 0;
        int emptyCount = 0;

        for (int cell : window) {
            if (cell == AI_PLAYER) aiCount++;
            else if (cell == HUMAN_PLAYER) humanCount++;
            else emptyCount++;
        }

        int score = 0;
        if (aiCount == 4) {
            score += 100;
        } else if (aiCount == 3 && emptyCount == 1) {
            score += 5;
        } else if (aiCount == 2 && emptyCount == 2) {
            score += 2;
        }

        if (humanCount == 3 && emptyCount == 1) {
            score -= 4;
        } else if (humanCount == 4) {
            score -= 100;
        }

        return score;
    }
}

package connect4;

public class Game {
    private Board board;
    private Player player1; // Human
    private Player player2; // AI
    private int currentPlayer;

    public Game(Player p1, Player p2) {
        board = new Board();
        player1 = p1;
        player2 = p2;
        currentPlayer = 1;
    }

    public void start() {
        boolean gameOver = false;

        while (!gameOver) {
            board.printBoard();
            if (board.checkWin(currentPlayer)) {
                board.printBoard();
                System.out.println("Player " + currentPlayer + " wins!");
                gameOver = true;
            } 
            else if(board.isFull()) {
            	board.printBoard();
                System.out.println("Draw!");
                gameOver = true;
            }
            
            Player current = (currentPlayer == 1) ? player1 : player2;
            Move move = current.getMove(board);

            boolean success = board.dropPiece(move);
            if (!success) {
                System.out.println("Invalid move, try again.");
                continue;
            }

            if (board.checkWin(currentPlayer)) {
                board.printBoard();
                System.out.println("Player " + currentPlayer + " wins!");
                gameOver = true;
            } else if (board.isFull()) {
                board.printBoard();
                System.out.println("Draw!");
                gameOver = true;
            } else {
                switchPlayer();
            }
        }
    }

    private void switchPlayer() {
        currentPlayer = (currentPlayer == 1) ? 2 : 1;
    }
}

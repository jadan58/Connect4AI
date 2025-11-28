package connect4;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Connect4UI.java
 *
 * Drop this file into the connect4 package in your repo.
 * Compile & run this class (it has a main) to launch a GUI that uses your Easy/Medium/Hard bots.
 *
 * Notes:
 * - Uses Board.dropPiece(new Move(player, col)) where col is 1-based (matches your Board implementation).
 * - Bot classes (EasyBot, MediumBot, HardBot) implement Player and return Move objects.
 * - Human uses clicks to pick a column (top-left is column 1).
 */
public class Connect4UI extends JPanel implements MouseListener {

    private static final int ROWS = 6;
    private static final int COLS = 7;
    private static final int CELL = 90;         // pixels per cell (adjust for larger/smaller UI)
    private static final int PADDING = 6;       // space inside cell for circle

    private Board board;
    private Player bot;                         // current bot (Easy/Medium/Hard)
    private boolean playerTurn;                 // true when it's human's turn (player 1)
    private boolean gameOver;

    // UI controls
    private final JComboBox<String> difficultyBox;
    private final JButton newGameBtn;
    private JLabel statusLabel = new JLabel();

    public Connect4UI() {
        setPreferredSize(new Dimension(COLS * CELL, ROWS * CELL + 40)); // extra for controls label
        setBackground(Color.DARK_GRAY);
        addMouseListener(this);

        // init game
        board = new Board();
        bot = new EasyBot();   // default
        playerTurn = true;
        gameOver = false;

        // controls (we'll overlay them at the bottom using a glass pane in main frame)
        difficultyBox = new JComboBox<>(new String[]{"Easy", "Medium", "Hard"});
        difficultyBox.setSelectedIndex(0);
        difficultyBox.addActionListener(e -> {
            String sel = (String) difficultyBox.getSelectedItem();
            switch (sel) {
                case "Easy" -> bot = new EasyBot();
                case "Medium" -> bot = new MediumBot();
                case "Hard" -> bot = new HardBot();
            }
            statusLabel.setText("Difficulty: " + sel);
        });

        newGameBtn = new JButton("New Game");
        newGameBtn.addActionListener(e -> startNewGame());

        statusLabel = new JLabel("Difficulty: Easy");
    }

    private void startNewGame() {
        board = new Board();
        playerTurn = true;
        gameOver = false;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // draw board background
        int boardHeight = ROWS * CELL;
        int boardWidth = COLS * CELL;
        g.setColor(new Color(10, 90, 160)); // blue-ish board
        g.fillRect(0, 0, boardWidth, boardHeight);

        // draw cells (holes) and pieces
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                int x = c * CELL;
                int y = r * CELL;
                // empty circle
                g.setColor(Color.WHITE);
                g.fillOval(x + PADDING, y + PADDING, CELL - PADDING * 2, CELL - PADDING * 2);

                // piece: board.board is package-private int[][]; same package -> accessible
                int cellValue = board.board[r][c]; // 0 empty, 1 human, 2 bot
                if (cellValue == 1) {
                    g.setColor(Color.RED);
                    g.fillOval(x + PADDING, y + PADDING, CELL - PADDING * 2, CELL - PADDING * 2);
                } else if (cellValue == 2) {
                    g.setColor(Color.YELLOW);
                    g.fillOval(x + PADDING, y + PADDING, CELL - PADDING * 2, CELL - PADDING * 2);
                }
            }
        }

        // Draw a small status bar at bottom
        g.setColor(getBackground());
        g.fillRect(0, boardHeight, boardWidth, 40);
        g.setColor(Color.WHITE);
        String s = gameOver ? "Game over. Click New Game to play again." : (playerTurn ? "Your turn (click a column)" : "Bot is thinking...");
        g.drawString(s, 8, boardHeight + 20);
    }

    // helper to drop piece for a player (1 or 2), using Move and Board.dropPiece
    private boolean dropPieceForPlayer(int player, int colOneBased) {
        Move m = new Move(player, colOneBased);
        return board.dropPiece(m);
    }

    // When human clicks: compute column, try to drop, then trigger bot if game not over
    @Override
    public void mouseClicked(MouseEvent e) {
        if (gameOver) return;

        if (!playerTurn) return; // ignore clicks while bot turn

        int x = e.getX();
        int col = x / CELL;     // 0-based
        if (col < 0 || col >= COLS) return;

        int colOneBased = col + 1; // your Board uses 1..7
        if (!board.isValidMove(colOneBased)) {
            // invalid move (column full)
            Toolkit.getDefaultToolkit().beep();
            return;
        }

        // Human move (player 1)
        boolean success = dropPieceForPlayer(1, colOneBased);
        if (!success) return; // should not happen, double-check

        repaint();

        // Check for human win or draw
        if (board.checkWin(1)) {
            gameOver = true;
            repaint();
            JOptionPane.showMessageDialog(this, "You win!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
            return;
        } else if (board.isFull()) {
            gameOver = true;
            repaint();
            JOptionPane.showMessageDialog(this, "Draw!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Now bot's turn
        playerTurn = false;
        repaint(); // show "Bot is thinking..."
        // run bot move synchronously (bots are fast). If you ever add expensive computation, use SwingWorker.
        SwingUtilities.invokeLater(() -> {
            try {
                Move botMove = bot.getMove(board); // bots expect Board and return Move (bot.player, col)
                // botMove.col is 1-based; drop it:
                if (botMove != null) {
                    boolean ok = dropPieceForPlayer(botMove.player, botMove.col);
                    if (!ok) {
                        // fallback: if bot returned invalid col (shouldn't happen), pick random valid
                        java.util.List<Integer> valid = board.getValidCols();
                        if (!valid.isEmpty()) dropPieceForPlayer(2, valid.get(0));
                    }
                }
            } catch (Exception ex) {
                // In case of any error, show a message and prevent crash
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Bot error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

            repaint();

            // Check for bot win or draw
            if (board.checkWin(2)) {
                gameOver = true;
                repaint();
                JOptionPane.showMessageDialog(this, "Bot wins!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
            } else if (board.isFull()) {
                gameOver = true;
                repaint();
                JOptionPane.showMessageDialog(this, "Draw!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
            } else {
                playerTurn = true;
                repaint();
            }
        });
    }

    // unused mouse events
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    // Build a frame with controls and attach this panel
    private JFrame buildAndShowFrame() {
        JFrame frame = new JFrame("Connect4 — Play vs Bot");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // add the game board panel (this)
        frame.add(this, BorderLayout.CENTER);

        // control panel at bottom
        JPanel controls = new JPanel();
        controls.setBackground(new Color(40, 40, 40));
        controls.setLayout(new FlowLayout(FlowLayout.LEFT));
        controls.add(new JLabel("Difficulty:"));
        controls.add(difficultyBox);
        controls.add(newGameBtn);
        controls.add(Box.createHorizontalStrut(20));
        controls.add(statusLabel);

        // wire difficultyBox initial selection (already set in constructor)
        String sel = (String) difficultyBox.getSelectedItem();
        if ("Easy".equals(sel)) bot = new EasyBot();
        else if ("Medium".equals(sel)) bot = new MediumBot();
        else bot = new HardBot();

        frame.add(controls, BorderLayout.SOUTH);

        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        return frame;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Connect4UI ui = new Connect4UI();
            ui.buildAndShowFrame();
        });
    }
}

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SudokuSolver extends JFrame {
    private final JTextField[][] grid = new JTextField[9][9];
    private final int[][] sudoku = {
        {5, 3, 0, 0, 7, 0, 0, 0, 0},
        {6, 0, 0, 1, 9, 5, 0, 0, 0},
        {0, 9, 8, 0, 0, 0, 0, 6, 0},
        {8, 0, 0, 0, 6, 0, 0, 0, 3},
        {4, 0, 0, 8, 0, 3, 0, 0, 1},
        {7, 0, 0, 0, 2, 0, 0, 0, 6},
        {0, 6, 0, 0, 0, 0, 2, 8, 0},
        {0, 0, 0, 4, 1, 9, 0, 0, 5},
        {0, 0, 0, 0, 8, 0, 0, 7, 9}
    };

    public SudokuSolver() {
        setTitle("Sudoku Solver");
        setSize(600, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(9, 9));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initializeGrid(gridPanel);
        populateGrid();

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2));

        JButton solveButton = new JButton("Solve");
        solveButton.setFont(new Font("Arial", Font.BOLD, 20));
        solveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(() -> {
                    readGrid();
                    if (solveSudoku(sudoku)) {
                        displayGrid();
                    } else {
                        JOptionPane.showMessageDialog(null, "No solution exists!");
                    }
                }).start();
            }
        });

        JButton hintButton = new JButton("Hint");
        hintButton.setFont(new Font("Arial", Font.BOLD, 20));
        hintButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                provideHint();
            }
        });

        buttonPanel.add(solveButton);
        buttonPanel.add(hintButton);

        add(gridPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

    private void initializeGrid(JPanel gridPanel) {
        Border blackline = BorderFactory.createLineBorder(Color.black);

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                grid[row][col] = new JTextField();
                grid[row][col].setHorizontalAlignment(JTextField.CENTER);
                grid[row][col].setFont(new Font("Arial", Font.BOLD, 20));
                grid[row][col].setBorder(blackline);
                gridPanel.add(grid[row][col]);

                if ((row < 3 || row > 5) && (col < 3 || col > 5) ||
                    (row >= 3 && row <= 5) && (col >= 3 && col <= 5)) {
                    grid[row][col].setBackground(new Color(220, 220, 220));
                }
            }
        }
    }

    private void populateGrid() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (sudoku[row][col] != 0) {
                    grid[row][col].setText(String.valueOf(sudoku[row][col]));
                    grid[row][col].setEditable(false);
                    grid[row][col].setBackground(new Color(180, 180, 180));
                }
            }
        }
    }

    private void readGrid() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                String text = grid[row][col].getText();
                if (!text.equals("")) {
                    sudoku[row][col] = Integer.parseInt(text);
                } else {
                    sudoku[row][col] = 0;
                }
            }
        }
    }

    private void displayGrid() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                grid[row][col].setText(String.valueOf(sudoku[row][col]));
            }
        }
    }

    private boolean isSafe(int[][] board, int row, int col, int num) {
        for (int d = 0; d < 9; d++) {
            if (board[row][d] == num || board[d][col] == num) {
                return false;
            }
        }

        int sqrt = (int) Math.sqrt(board.length);
        int boxRowStart = row - row % sqrt;
        int boxColStart = col - col % sqrt;

        for (int r = boxRowStart; r < boxRowStart + sqrt; r++) {
            for (int d = boxColStart; d < boxColStart + sqrt; d++) {
                if (board[r][d] == num) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean solveSudoku(int[][] board) {
        int n = board.length;
        int row = -1;
        int col = -1;
        boolean isEmpty = true;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j] == 0) {
                    row = i;
                    col = j;
                    isEmpty = false;
                    break;
                }
            }
            if (!isEmpty) {
                break;
            }
        }

        if (isEmpty) {
            return true;
        }

        for (int num = 1; num <= n; num++) {
            if (isSafe(board, row, col, num)) {
                board[row][col] = num;
                updateGrid(row, col, num, true);
                if (solveSudoku(board)) {
                    return true;
                }
                board[row][col] = 0;
                updateGrid(row, col, num, false);
            }
        }
        return false;
    }

    private void updateGrid(int row, int col, int num, boolean isCorrect) {
        SwingUtilities.invokeLater(() -> {
            grid[row][col].setText(num == 0 ? "" : String.valueOf(num));
            if (isCorrect) {
                grid[row][col].setBackground(Color.GREEN);
            } else {
                grid[row][col].setBackground(Color.RED);
            }
        });
        try {
            Thread.sleep(30); // Adjust the delay as needed
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void provideHint() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (sudoku[row][col] == 0) {
                    for (int num = 1; num <= 9; num++) {
                        if (isSafe(sudoku, row, col, num)) {
                            sudoku[row][col] = num;
                            grid[row][col].setText(String.valueOf(num));
                            grid[row][col].setBackground(Color.YELLOW);
                            return;
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        new SudokuSolver();
    }
}

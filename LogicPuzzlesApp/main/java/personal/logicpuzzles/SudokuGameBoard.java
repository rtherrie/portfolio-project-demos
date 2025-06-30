package personal.logicpuzzles;

public class SudokuGameBoard {
    private int[][] board;
    private int size;
    private Sudoku puzzle;

    public SudokuGameBoard(int size)
    {
        this.size = size;

        // Generate sudoku board
        puzzle = new Sudoku();
        board = puzzle.generate();
    }

    public void set(int value, int x, int y){
        if(check(value, x, y))
            board[x][y] = value;
    }

    public boolean check(int value, int x, int y){
        int a, b, i, j;

        // Check row
        for (j = 0; j < 9; j++)
            if (j != y && board[x][j] == value)
                return false; // False = cannot put there

        // Check column
        for (i = 0; i < 9; i++)
            if (i != x && board[i][y] == value)
                return false; // False = cannot put there

        // Check 3x3
        a = (x/3)*3; b = (y/3)*3;
        for (i = 0; i < 3; i++)
            for (j = 0; j < 3; j++)
                if ((a + i != x) && (b + j != y) && board[a+i][b+j] == value)
                    return false; // False = cannot put there

        return true; // True = okay to place
    }

    public int[][] getBoard(){
        return board;
    }

    public void setBoard(int[][] b){
        board = b;
    }
}


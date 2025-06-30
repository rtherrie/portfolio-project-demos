package personal.logicpuzzles;

import java.util.Random;

public class Sudoku
{
    public int[][] generate()
    {
        Random rand = new Random();

        // Initialize empty 9x9 board
        int[][] board = new int[9][9];
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++)
                board[i][j] = 0;

        // Create randomized order of numbers for each cell
        int[][][] order = new int[9][9][9];
        for (int x = 0; x < 9; x++)
            for (int y = 0; y < 9; y++)
            {
                for (int i = 0; i < 9; i++)
                    order[x][y][i] = i+1;

                for (int i = 0; i < 9; i++)
                {
                    int j = i + rand.nextInt(9-i);
                    int temp = order[x][y][i];
                    order[x][y][i] = order[x][y][j];
                    order[x][y][j] = temp;
                }
            }

        // Fill the board
        fill(board, order, 0);

        // Remove some cells to make a puzzle
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++)
                if (rand.nextDouble() < 0.4)
                    board[i][j] = board[i][j];
                else
                    board[i][j] = 0;

        return board;
    }

    private boolean fill(int[][] board, int[][][] order, int location)
    {
        int x = location/9;
        int y = location%9;
        int k;

        // All cells filled
        if (location > 80)
            return true;
        // Skip filled cells
        else if (board[x][y] != 0)
            return fill(board, order, location+1);
        else
        {
            // Try each number in a randomized order
            for (k = 0; k < 9; k++)
            {
                board[x][y] = order[x][y][k];
                if (check(board, x, y) && fill(board, order, location+1))
                    return true;
            }
            board[x][y] = 0;
            return false;
        }
    }

    private boolean check(int[][] board, int x, int y)
    {
        int a, b, i, j;

        // Check row
        for (j = 0; j < 9; j++)
            if (j != y && board[x][j] == board[x][y])
                return false;

        // Check column
        for (i = 0; i < 9; i++)
            if (i != x && board[i][y] == board[x][y])
                return false;

        // Check 3x3 box
        a = (x/3)*3; b = (y/3)*3;
        for (i = 0; i < 3; i++)
            for (j = 0; j < 3; j++)
                if ((a + i != x) && (b + j != y) && board[a+i][b+j] == board[x][y])
                    return false;

        return true;
    }
}
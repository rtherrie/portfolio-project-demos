package personal.logicpuzzles;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NonogramGenerator {

    private int size; // Size of the grid
    private int[][] grid; // The nonogram grid (0 for empty, 1 for filled)

    // Constructor to initialize the grid size
    public NonogramGenerator(int size) {
        this.size = size;
        this.grid = new int[size][size]; // Initialize the grid with the given size
        generatePattern();
    }

    // Generate a random nonogram pattern
    public void generatePattern() {
        Random rand = new Random();

        // Set the threshold for getting a 1 (70% chance)
        double probability = 0.70;

        // Randomly fill the grid
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                grid[i][j] = rand.nextDouble() < probability ? 1 : 0;
            }
        }
    }

    // Get the nonogram grid
    public int[][] getGrid() {
        return grid;
    }

    // Get row clues
    public List<List<Integer>> getColumnClues() {
        List<List<Integer>> rowClues = new ArrayList<>();
        for (int i = 0; i < grid.length; i++) {
            rowClues.add(getCluesForLine(grid[i]));
        }
        return rowClues;
    }

    // Get column clues
    public List<List<Integer>> getRowClues() {
        List<List<Integer>> columnClues = new ArrayList<>();
        for (int j = 0; j < grid.length; j++) {
            int[] column = new int[grid.length];
            for (int i = 0; i < grid.length; i++) {
                column[i] = grid[i][j];
            }
            columnClues.add(getCluesForLine(column));
        }
        return columnClues;
    }

    // Get clues for a single row or column
    private List<Integer> getCluesForLine(int[] line) {
        List<Integer> clues = new ArrayList<>();
        int count = 0;

        for (int i = 0; i < line.length; i++) {
            int cell = line[i];
            if (cell == 1) {
                count++;
            } else {
                if (count > 0) {
                    clues.add(count);
                    count = 0;
                }
            }
        }

        if (count > 0) {
            clues.add(count);
        }

        return clues;
    }

    public static void main(String[] args) {
        NonogramGenerator generator = new NonogramGenerator(10);
        int[][] grid = generator.getGrid();

        // Print grid
        for (int[] row : grid) {
            for (int cell : row) {
                System.out.print(cell == 1 ? "█" : " ");
            }
            System.out.println();
        }

        // Print clues
        System.out.println("Row Clues:");
        generator.getRowClues().forEach(System.out::println);

        System.out.println("Column Clues:");
        generator.getColumnClues().forEach(System.out::println);
    }
}

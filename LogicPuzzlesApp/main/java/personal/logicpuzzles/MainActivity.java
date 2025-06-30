package personal.logicpuzzles;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;


//Main program
public class MainActivity extends AppCompatActivity {
    SudokuGameBoard sudokuGameBoard;
    SudokuInterface sudokuAppInterface;

    CryptogramInterface cryptogramInterface;
    Cryptogram cryptogram;

    String currentScreen;

    private int[][] initialBoard = new int[9][9];
    private final String FILE_NAME = "currentBoard";
    private final String FILE_NAME_INITIAL = "initial";
    int screenSize;

    //Main screen of app
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentScreen = "Main";

        screenSize = getWindowManager().getCurrentWindowMetrics().getBounds().width();
    }


    /////////////////////////////// SUDOKU ///////////////////////////////
    ///
    public void sudokuButton(View v){
        sudoku();
    }
    public void sudoku(){
        currentScreen = "Sudoku";
        int SIZE = 9;
        // Create new game
        sudokuGameBoard = new SudokuGameBoard(SIZE);
        // Create new game and store the initial state
        initialBoard = copyBoard(sudokuGameBoard.getBoard());

        // Create interface
        int width = screenSize/SIZE;
        sudokuAppInterface = new SudokuInterface(this, SIZE, width);
        // Set content view
        setContentView(sudokuAppInterface);
        // Display initial board
        sudokuAppInterface.drawInitialBoard(sudokuGameBoard.getBoard());
        // Attach event handlers
        for(int i=0; i<sudokuGameBoard.getBoard().length; i++){
            for(int j=0; j<sudokuGameBoard.getBoard().length; j++){
                SudokuTextChangeHandler textchange = new SudokuTextChangeHandler(i, j);
                sudokuAppInterface.setTextChangeListener(textchange, i, j);
            }
        }

        // Attach OnClickListener for New Board button
        sudokuAppInterface.getNewButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sudokuGameBoard = new SudokuGameBoard(SIZE);
                initialBoard = sudokuGameBoard.getBoard(); // Reset initial board to new current
                sudokuAppInterface.drawInitialBoard(initialBoard);
            }
        });

        // Attach OnClickListener for Save Board button
        sudokuAppInterface.getSaveButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFile(FILE_NAME, sudokuGameBoard.getBoard()); // Save the modified board
                saveFile(FILE_NAME_INITIAL, initialBoard);  // Save the initial board
            }
        });

        // Attach OnClickListener for Retrieve Board button
        sudokuAppInterface.getRetrieveButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[][] loadedInitialBoard = readFile(FILE_NAME_INITIAL);
                initialBoard = copyBoard(loadedInitialBoard);
                // Set the board to the saved board and draw it on screen
                sudokuGameBoard.setBoard(loadedInitialBoard);
                sudokuAppInterface.drawInitialBoard(loadedInitialBoard);

                int[][] board = readFile(FILE_NAME);
                // Iterate through and set the user entered numbers that were saved
                for(int i=0; i<board.length; i++){
                    for(int j=0; j<board.length; j++){
                        if(initialBoard[i][j] != board[i][j]) {
                            sudokuGameBoard.set(board[i][j], i, j); // Set number in board
                            sudokuAppInterface.setText(board[i][j]+"", i, j); // Set text on screen
                        }
                    }
                }
            }
        });
    }

    public boolean sudokuCheckWin(){
        for(int i=0; i<sudokuGameBoard.getBoard().length; i++){
            for(int j=0; j<sudokuGameBoard.getBoard().length; j++){
                // If all spots are not full
                if(sudokuGameBoard.getBoard()[i][j] == 0){
                    return false; // Return false = user can keep playing
                }
            }
        }
        return true; // All spots are full = return true = user has won
    }

    // Utility method to deep copy the board (to preserve the initial state)
    private int[][] copyBoard(int[][] originalBoard) {
        int[][] copy = new int[9][9];
        for (int i = 0; i < 9; i++) {
            System.arraycopy(originalBoard[i], 0, copy[i], 0, 9);
        }
        return copy;
    }

    private void saveFile(String fileName, int[][] boardToSave){
        try {
            // Open file to write to
            FileOutputStream fout = openFileOutput(fileName, Context.MODE_PRIVATE);

            String boardAsString = "";

            // Turn board into string
            for(int i=0; i<boardToSave.length; i++){
                for(int j=0; j<boardToSave.length; j++){
                    boardAsString += boardToSave[i][j] + " ";
                }
            }
            // Convert to bytes and write the string to file
            fout.write(boardAsString.getBytes());
            fout.close();
        } catch (IOException e) {
            // error occured
        }
    }

    private int[][] readFile(String fileName){
        int[][] board = new int[9][9];
        try {
            // Open file and setup BufferedReader
            FileInputStream fin = openFileInput(fileName);
            InputStreamReader inputReader = new InputStreamReader(fin);
            BufferedReader bufferedReader = new BufferedReader(inputReader);

            // Read the file into a string
            String boardInputString = bufferedReader.readLine();

            // Use a tokenizer with space as the delimiter
            StringTokenizer tokenizer = new StringTokenizer(boardInputString, " ");

            // Recreate an int[][] for board
            for(int i=0; i<9; i++){
                for(int j=0; j<9; j++){
                    String nextToken = tokenizer.nextToken();
                    // Turn next number into an int and put it in board
                    board[i][j] = Integer.parseInt(nextToken);
                }
            }

            fin.close();
            inputReader.close();
            bufferedReader.close();

        } catch (IOException e) {
            // error occured
        }
        return board;
    }

    private class SudokuTextChangeHandler implements TextWatcher
    {
        int x;
        int y;
        public SudokuTextChangeHandler(int x, int y){
            this.x = x;
            this.y = y;
        }
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        // Do this when the user inputs a number on board
        @Override
        public void afterTextChanged(Editable editable) {
            String input = sudokuAppInterface.getInput(x, y);
            if(input.equals("")){ // If input is blank
                sudokuGameBoard.set(0, x, y); // Set 0
            }else if(input.equals("0")){ // If input is 0
                sudokuGameBoard.set(0, x, y); // Set 0
                sudokuAppInterface.clear(x, y); // Set blank on display
            }else if(input.length() > 1){ // If input is too long
                sudokuGameBoard.set(0, x, y); // Set 0
                sudokuAppInterface.clear(x, y); // Set blank on display
            }else{
                int value = Integer.parseInt(input);
                if(sudokuGameBoard.check(value, x, y)){ // If you can place that number there
                    sudokuGameBoard.set(value, x, y); // Set value
                }else{ // If you cannot place that number there
                    sudokuGameBoard.set(0, x, y); // Set value to 0
                    sudokuAppInterface.clear(x, y); // Clear value
                }
            }
            if(sudokuCheckWin()) { // If the user has won
                showDialogBox(); // Display winning message
            }
        }
    }

    /////////////////////////////// NONOGRAM ///////////////////////////////
    NonogramGenerator nonogramGameBoard;
    NonogramInterface nonogramAppInterface;

    public void nonogramButton (View v){
        nonogram();
    }
    public void nonogram(){
        currentScreen = "Nonogram";
        // Set content view
        setContentView(R.layout.activity_nonogram_size);
    }
    public void nonogramSize(View v){
        int SIZE = -1;
        if(v.getId() == R.id.buttonFive) {
            SIZE = 5;
        }
        //  SUPPORT FOR OTHER SIZES ?? (not yet implemented)
//        }else if(v.getId() == R.id.buttonTen){
//            SIZE = 10;
//        }
//        else if(v.getId() == R.id.buttonFifteen){
//            SIZE = 15;
//        }
        // Create new game
        nonogramGameBoard = new NonogramGenerator(SIZE);

        // Create interface
        int width = screenSize/SIZE/4;
        width = width*3;
        nonogramAppInterface = new NonogramInterface(this, SIZE, width, nonogramGameBoard);
        // Set content view
        setContentView(nonogramAppInterface);
    }

    public boolean nonogramCheckWin(){
        int[][] goalGrid = nonogramGameBoard.getGrid();
        int[][] currentGrid = nonogramAppInterface.getButtonStates();

        for(int i=0; i<goalGrid.length; i++){
            for(int j=0; j<goalGrid.length; j++){
                // If all spots are not the same
                if(goalGrid[i][j] != currentGrid[i][j]){
                    return false; // Return false = user can keep playing
                }
            }
        }
        showDialogBox();
        return true;
    }


    /////////////////////////////// CRYPTOGRAM ///////////////////////////////
    public void cryptogramButton(View v){
        cryptogram();
    }
    public void cryptogram(){
        currentScreen = "Cryptogram";
        int SIZE = 9;
        // Create new game and interface
        cryptogram = new Cryptogram();
        cryptogramInterface = new CryptogramInterface(this, cryptogram.getNumbers(), cryptogram.getCurrentNumbers(), cryptogram.getQuote(), cryptogram.getPerson());

        // Set content view
        setContentView(cryptogramInterface);
    }

    public void cryptogramOnWin(){
        showDialogBox();
    }

    /////////////////////////////// DIALOG BOX ///////////////////////////////
    private void showDialogBox(){
        // Create dialog box
        AlertDialog.Builder dialogBox = new AlertDialog.Builder(this);
        dialogBox.setMessage("You Win! Do you want to play again?");

        DialogBoxListener temp = new DialogBoxListener();

        dialogBox.setPositiveButton("Yes", temp);
        dialogBox.setNegativeButton("No", temp);
        dialogBox.setNeutralButton("Cancel", temp);

        dialogBox.show();
    }

    private class DialogBoxListener implements DialogInterface.OnClickListener{

        @Override
        public void onClick(DialogInterface dialogInterface, int id) {
            if(id == -1){
                //positive code - yes button
                if(currentScreen.equals("Sudoku")){
                    sudoku();
                }else if(currentScreen.equals("Nonogram")){
                    nonogram();
                } else if(currentScreen.equals("Cryptogram")){
                    cryptogram();
                }
            }else if(id == -2){
                //negative code - no button
                setContentView(R.layout.activity_main);
            }else{
                //neutral code - cancel button
            }
        }
    }
}


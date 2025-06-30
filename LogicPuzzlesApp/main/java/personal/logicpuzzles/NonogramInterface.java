package personal.logicpuzzles;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

public class NonogramInterface extends RelativeLayout {
    private Button[][] currentBoard;  // Grid of Buttons
    private int[][] buttonState;  // Grid to track the button states (0 = not clicked, 1 = clicked)

    private TextView rowClues;  // TextView for row clues above the grid
    private TextView columnClues;  // TextView for column clues left of the grid
    private MainActivity mainActivity;

    private static int size;

    private Switch modeSwitch;  // Switch to toggle between Box and X modes

    public NonogramInterface(Context context, int size, int width, NonogramGenerator generator) {
        super(context);

        this.size = size;
        final int DP = (int)(getResources().getDisplayMetrics().density);

        // Create the Back Button
        Button backButton = new Button(context);
        backButton.setText("Back");
        backButton.setTextColor(Color.WHITE);
        backButton.setBackgroundColor(Color.parseColor("#8A84E2"));
        backButton.setTextSize(16);
        backButton.setGravity(Gravity.CENTER);

        // Set layout parameters for the back button
        RelativeLayout.LayoutParams backParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        backParams.setMargins(100, 100, 0, 0);  // Position it at the top-left corner
        backButton.setLayoutParams(backParams);

        // Set the click listener for the back button
        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) context).setContentView(R.layout.activity_main);
            }
        });

        // Initialize buttonState array with the size of the grid
        buttonState = new int[size][size];

        // Get column clues
        List<List<Integer>> columnList = generator.getColumnClues();

        // Create the layout for column clues (TextView)
        columnClues = new TextView(context);
        columnClues.setText(formatColumnClues(columnList));  // This should be replaced with actual column clues
        columnClues.setTextColor(Color.BLACK);
        columnClues.setGravity(Gravity.END);
        columnClues.setTextSize(20);
        RelativeLayout.LayoutParams relparams = null;
        if(size == 5) {
            columnClues.setLineSpacing(90f, 1.0f);
            relparams = new RelativeLayout.LayoutParams(
                    170, 750);
        }
        else if(size == 10) {
            columnClues.setLineSpacing(19f, 1.0f);
            relparams = new RelativeLayout.LayoutParams(
                    170, 815);
        }
        relparams.addRule(RelativeLayout.CENTER_VERTICAL);  // Centers vertically
        relparams.setMargins(0, 0, 30, 0);  // Add right padding
        columnClues.setLayoutParams(relparams);
        columnClues.setId(View.generateViewId());  // Assign a random ID


        // Get row clues
        List<List<Integer>> rowList = generator.getRowClues();

        // Create the layout for row clues (TextView)
        rowClues = new TextView(context);
        rowClues.setText(formatRowClues(rowList));
        rowClues.setTextColor(Color.BLACK);
        rowClues.setGravity(Gravity.CENTER);
        rowClues.setTextSize(20);
        relparams = new RelativeLayout.LayoutParams(
                800, 200);
        relparams.setMargins(200, 550, 0, 0);  // Add right padding
        rowClues.setLayoutParams(relparams);
        rowClues.setId(View.generateViewId());  // Assign a random ID

        // Create current board (GridLayout)
        GridLayout currentBoardLayout = new GridLayout(context);
        currentBoardLayout.setRowCount(size);
        currentBoardLayout.setColumnCount(size);
        currentBoardLayout.setId(View.generateViewId());

        // Set layout parameters for current board
        RelativeLayout.LayoutParams paramsRelative = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsRelative.addRule(RelativeLayout.RIGHT_OF, columnClues.getId());
        paramsRelative.addRule(RelativeLayout.ALIGN_BOTTOM, columnClues.getId());
        paramsRelative.addRule(RelativeLayout.CENTER_HORIZONTAL);
        paramsRelative.topMargin = 70 * DP;  // Adjust the vertical margin
        currentBoardLayout.setLayoutParams(paramsRelative);

        // Initialize currentBoard with Buttons instead of TextViews
        currentBoard = new Button[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                currentBoard[i][j] = new Button(context);
                currentBoard[i][j].setBackgroundColor(Color.parseColor("#AFAFDC"));  // Button color
                if (size == 5)
                    currentBoard[i][j].setTextSize(20);
                else if (size == 10)
                    currentBoard[i][j].setTextSize(10);
                currentBoard[i][j].setGravity(Gravity.CENTER);  // Center text inside buttons

                // Set the layout parameters for the buttons inside the grid
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = width;
                params.height = width;
                params.rowSpec = GridLayout.spec(i, 1);  // Button position in the grid
                params.columnSpec = GridLayout.spec(j, 1);
                params.topMargin = params.bottomMargin = 1;
                params.leftMargin = params.rightMargin = 1;
                currentBoard[i][j].setLayoutParams(params);

                // Set the onClick listener for each button to change color or set X based on switch
                final int row = i;
                final int col = j;
                currentBoard[i][j].setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Get the clicked button
                        Button clickedButton = (Button) v;
                        int currentColor = ((ColorDrawable) clickedButton.getBackground()).getColor();

                        // Check the state of the switch
                        if (modeSwitch.isChecked()) {
                            if (currentColor == Color.parseColor("#AFAFDC")) {  // If the box is not filled
                                // X Mode: Toggle between X and empty text
                                if ("X".equals(clickedButton.getText().toString())) {
                                    clickedButton.setText("");  // Remove "X"
                                } else {
                                    clickedButton.setText("X");  // Set "X"
                                }
                            }
                        } else {
                            if (!"X".equals(clickedButton.getText().toString())) {
                                // Box Mode: Toggle the color of the button
                                if (currentColor == Color.parseColor("#AFAFDC")) {
                                    clickedButton.setBackgroundColor(Color.parseColor("#8A84E2"));  // Change to new color
                                    buttonState[row][col] = 1;  // Update the grid state to 1 (clicked)
                                    // Cast context to MainActivity
                                    if (context instanceof MainActivity) {
                                        mainActivity = (MainActivity) context;
                                    }
                                    // Example of calling the nonogramCheckWin method
                                    if (mainActivity != null && mainActivity.nonogramCheckWin()) {
                                        disableButtons(); // Disable buttons if the game is won
                                    }
                                } else {
                                    clickedButton.setBackgroundColor(Color.parseColor("#AFAFDC"));  // Reset to original color
                                    buttonState[row][col] = 0;  // Update the grid state to 0 (not clicked)
                                }
                            }
                        }
                    }
                });

                currentBoardLayout.addView(currentBoard[i][j]);
            }
        }

        // Create the switch for toggling Box/X mode
        modeSwitch = new Switch(context);
        modeSwitch.setText("Fill");  // Default text when Box mode is selected
        modeSwitch.setTextColor(Color.BLACK);
        modeSwitch.setTextSize(30);
        modeSwitch.setGravity(Gravity.CENTER);
        modeSwitch.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        modeSwitch.setPadding(0, 500, 0, 30);
        modeSwitch.setId(View.generateViewId());

        // Set layout parameters for the switch
        RelativeLayout.LayoutParams switchParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        switchParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        switchParams.addRule(RelativeLayout.BELOW, currentBoardLayout.getId());  // Position below the grid
        modeSwitch.setLayoutParams(switchParams);

        // Add a listener to update the switch text dynamically when toggled
        modeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                modeSwitch.setText("X");  // Change text to "X Mode"
            } else {
                modeSwitch.setText("Fill");  // Change text to "Box Mode"
            }
        });

        // Create a RelativeLayout to hold everything (rowClues, columnClues, and grid)
        RelativeLayout mainLayout = new RelativeLayout(context);
        mainLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        // Add the TextViews (rowClues and columnClues), the grid, and the switch to the layout
        mainLayout.addView(backButton);
        mainLayout.addView(rowClues);
        mainLayout.addView(columnClues);
        mainLayout.addView(currentBoardLayout);
        mainLayout.addView(modeSwitch);

        // Add the main layout to the RelativeLayout
        addView(mainLayout);

        // Set background color of the overall RelativeLayout
        setBackgroundColor(Color.parseColor("#ffffff"));
    }

    // Method to get the current button states
    public int[][] getButtonStates() {
        return buttonState;
    }

    // Method to disable all buttons
    public void disableButtons() {
        for (int i = 0; i < currentBoard.length; i++) {
            for (int j = 0; j < currentBoard[i].length; j++) {
                currentBoard[i][j].setEnabled(false);  // Disable all buttons
            }
        }
    }

    public static String formatRowClues(List<List<Integer>> rowClues) {
        StringBuilder sb = new StringBuilder();

        // Find the maximum number of clues in any row (this will determine the number of columns)
        int maxClues = 0;
        for (List<Integer> row : rowClues) {
            maxClues = Math.max(maxClues, row.size());
        }

        // For each clue position (1st, 2nd, 3rd, etc.)
        for (int i = 0; i < maxClues; i++) {
            // For each row, append the clue at the current position (if it exists)
            for (int j = 0; j < rowClues.size(); j++) {
                List<Integer> row = rowClues.get(j);
                if (i < row.size()) {
                    sb.append(row.get(i).toString());  // Append the clue
                } else {
                    sb.append("  ");  // If the row doesn't have a clue for this column, append a space
                }

                // Add space between clues in the same row
                if (j < rowClues.size() - 1) {
                    if(size == 5)
                        sb.append("           ");
                    else if(size == 10)
                        sb.append("    ");
                }
            }
            // After each row of clues, add a new line (for the next clue column)
            sb.append("\n");
        }
        return sb.toString();  // Return the formatted string
    }



    public static String formatColumnClues(List<List<Integer>> columnClues) {
        StringBuilder sb = new StringBuilder();
        // Iterate over each column (List<Integer>)
        for (List<Integer> column : columnClues) {
            // Convert each integer in the column to a string and join them with a space
            for (int i = 0; i < column.size(); i++) {
                sb.append(column.get(i).toString());
                if (i < column.size() - 1) {
                    sb.append(" ");  // Add space between integers in the same column
                }
            }
            sb.append("\n");  // Add newline after each column
        }

        return sb.toString();  // Return the formatted string
    }
}


package personal.logicpuzzles;

import android.app.Activity;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.graphics.Color;
import android.content.Context;
import android.widget.GridLayout;
import android.widget.RelativeLayout;

public class SudokuInterface extends RelativeLayout
{
    private int size;
    private EditText[][] edits;
    private GridLayout gridLayout;
    private Button newButton, saveButton, retrieveButton;

    public SudokuInterface(Context context, int size, int width)
    {
        super(context);

        this.size = size;

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

        addView(backButton);

        // Create and set up the GridLayout
        gridLayout = new GridLayout(context);
        gridLayout.setRowCount(size);
        gridLayout.setColumnCount(size);

        edits = new EditText[size][size];
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
            {
                edits[i][j] = new EditText(context);
                edits[i][j].setBackgroundColor(Color.parseColor("#AFAFDC"));
                edits[i][j].setTextColor(Color.parseColor("#ffffff"));
                edits[i][j].setTextSize((int)(width * 0.2));
                edits[i][j].setGravity(Gravity.CENTER);
                edits[i][j].setInputType(InputType.TYPE_CLASS_NUMBER);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = width;
                params.height = width;
                params.rowSpec = GridLayout.spec(i, 1);
                params.columnSpec = GridLayout.spec(j, 1);
                params.topMargin = params.bottomMargin = 1;
                params.leftMargin = params.rightMargin = 1;
                if (i == 0) params.topMargin = 400;
                edits[i][j].setLayoutParams(params);
                gridLayout.addView(edits[i][j]);
            }

        // Add the GridLayout to the RelativeLayout
        RelativeLayout.LayoutParams gridParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );
        gridParams.addRule(RelativeLayout.CENTER_IN_PARENT);  // Center the GridLayout inside RelativeLayout
        gridLayout.setLayoutParams(gridParams);

        // Add the GridLayout to the RelativeLayout
        addView(gridLayout);

        // Create and set up buttons (New, Save, Retrieve)
        newButton = new Button(context);
        newButton.setBackgroundColor(Color.parseColor("#8A84E2"));
        newButton.setText("New");
        RelativeLayout.LayoutParams newButtonParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        newButtonParams.addRule(RelativeLayout.BELOW, gridLayout.getId());  // Position below the grid
        newButtonParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);  // Align to the left side
        newButtonParams.setMargins(0, 1700, 0, 0);
        newButton.setLayoutParams(newButtonParams);
        addView(newButton);

        saveButton = new Button(context);
        saveButton.setBackgroundColor(Color.parseColor("#8A84E2"));
        saveButton.setText("Save");
        RelativeLayout.LayoutParams saveButtonParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        saveButtonParams.addRule(RelativeLayout.BELOW, gridLayout.getId());  // Position below the grid
        saveButtonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);  // Center the button
        saveButtonParams.setMargins(0, 1700, 0, 0);
        saveButton.setLayoutParams(saveButtonParams);
        addView(saveButton);

        retrieveButton = new Button(context);
        retrieveButton.setBackgroundColor(Color.parseColor("#8A84E2"));
        retrieveButton.setText("Retrieve");
        RelativeLayout.LayoutParams retrieveButtonParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        retrieveButtonParams.addRule(RelativeLayout.BELOW, gridLayout.getId());  // Position below the grid
        retrieveButtonParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);  // Align to the right side
        retrieveButtonParams.setMargins(0, 1700, 0, 0);
        retrieveButton.setLayoutParams(retrieveButtonParams);
        addView(retrieveButton);

        setBackgroundColor(Color.parseColor("#ffffff"));
    }

    public void drawInitialBoard(int[][] board)
    {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                // If value is not 0
                if (board[i][j] != 0) {
                    edits[i][j].setText(board[i][j] + ""); // Set text to value
                    edits[i][j].setBackgroundColor(Color.parseColor("#8A84E2")); // Set background color
                    edits[i][j].setEnabled(false); // Disable edit text
                } else {
                    // If value is 0
                    edits[i][j].setText(""); // Set text to empty
                    edits[i][j].setBackgroundColor(Color.parseColor("#AFAFDC")); // Set background color
                    edits[i][j].setEnabled(true); // Enable EditText for empty cells
                }
            }
        }
    }

    // Set text at a given location
    public void setText(String text, int x, int y){
        edits[x][y].setText(text);
    }
    // Return value at a given location
    public String getInput(int x, int y)
    {
        return edits[x][y].getText().toString();
    }

    // Clear spot on board
    public void clear(int x, int y)
    {
        edits[x][y].setText("");
    }

    // Add text change listener
    public void setTextChangeListener(TextWatcher textWatcher, int x, int y)
    {
        edits[x][y].addTextChangedListener(textWatcher);
    }

    public Button getNewButton() {
        return newButton;
    }

    public Button getSaveButton() {
        return saveButton;
    }

    public Button getRetrieveButton() {
        return retrieveButton;
    }
}


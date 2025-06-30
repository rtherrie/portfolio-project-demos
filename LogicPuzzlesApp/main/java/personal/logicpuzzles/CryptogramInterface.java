package personal.logicpuzzles;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CryptogramInterface extends LinearLayout {

    private final List<EditText> letterInputs = new ArrayList<>(); // holds inputs reference
    private final List<Integer> activeNumbers = new ArrayList<>(); // holds active numbers (non 0 or 100)

    public CryptogramInterface(Context context, int[] numbers, int[] currentNumbers, String quote, String person) {
        super(context);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        setBackgroundColor(Color.parseColor("#AFAFDC"));

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

        final int maxItemsPerRow = 8;
        LinearLayout currentRow = null;
        int indexInRow = 0;

        for (int i = 0; i < currentNumbers.length; i++) {
            if (indexInRow == 0) {
                currentRow = new LinearLayout(context);
                currentRow.setOrientation(LinearLayout.HORIZONTAL);
                currentRow.setGravity(Gravity.CENTER_HORIZONTAL);
                currentRow.setLayoutParams(new LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT
                ));
                addView(currentRow);
            }

            if (currentNumbers[i] == 100) {
                // Spacer for space character
                Space space = new Space(context);
                LinearLayout.LayoutParams spaceParams = new LinearLayout.LayoutParams(60, LayoutParams.WRAP_CONTENT);
                spaceParams.setMargins(10, 0, 10, 0);
                space.setLayoutParams(spaceParams);
                currentRow.addView(space);
            } else {
                // Column with EditText and number label
                LinearLayout letterColumn = new LinearLayout(context);
                letterColumn.setOrientation(LinearLayout.VERTICAL);
                letterColumn.setGravity(Gravity.CENTER_HORIZONTAL);
                letterColumn.setLayoutParams(new LinearLayout.LayoutParams(
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT
                ));
                letterColumn.setPadding(10, 8, 10, 8);

                // Editable letter input
                EditText letterInput = new EditText(context);
                letterInput.setEms(1);
                letterInput.setGravity(Gravity.CENTER);
                letterInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                letterInput.setTextSize(24f);
                letterInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});
                letterInput.setLayoutParams(new LinearLayout.LayoutParams(
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT
                ));

                if(i%4==0){
                    letterInput.setText(quote.charAt(i)+"");
                }

                if (currentNumbers[i] != 100 && currentNumbers[i] != 0) {
                    letterInputs.add(letterInput);
                    activeNumbers.add(currentNumbers[i]);
                }

                // Text change listener
                letterInput.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}

                    @Override
                    public void afterTextChanged(Editable s) {
                        for (int i = 0; i < letterInputs.size(); i++) {
                            EditText input = letterInputs.get(i);
                            String letter = input.getText().toString().toUpperCase();

                            if (letter.length() != 1 || letter.charAt(0) < 'A' || letter.charAt(0) > 'Z') {
                                return;
                            }

                            int letterValue = numbers[letter.charAt(0) - 'A'];

                            if (letterValue != activeNumbers.get(i)) {
                                return;
                            }
                        }
                        onWin();
                    }
                });

                // Number display
                TextView numberText = new TextView(context);
                numberText.setText(String.valueOf(currentNumbers[i]));
                numberText.setGravity(Gravity.CENTER);
                numberText.setTextSize(20f);
                numberText.setLayoutParams(new LinearLayout.LayoutParams(
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT
                ));

                // Add views
                letterColumn.addView(letterInput);
                letterColumn.addView(numberText);
                currentRow.addView(letterColumn);
            }

            indexInRow++;

            // If row is full or last element, check if word continues to next line
            if (indexInRow == maxItemsPerRow || i == currentNumbers.length - 1) {
                int nextIndex = i + 1;
                while (nextIndex < currentNumbers.length && currentNumbers[nextIndex] == 100) {
                    nextIndex++; // skip spaces
                }

                boolean isEndOfLine = ((i + 1) % maxItemsPerRow == 0);
                //int nextIndex = i + 1;

                if (isEndOfLine &&
                        nextIndex < currentNumbers.length &&
                        currentNumbers[i] != 100 &&  // current is not space
                        currentNumbers[nextIndex] != 100) {  // next is not space

                    // Word continues on next line, so add hyphen
                    TextView hyphen = new TextView(getContext());
                    hyphen.setText("-");
                    hyphen.setTextSize(24f);
                    hyphen.setPadding(10, 0, 0, 0);
                    currentRow.addView(hyphen);
                }



                indexInRow = 0; // reset for next row
            }
        }

        // Add quote person at the bottom
        TextView personText = new TextView(context);
        personText.setText(person);
        personText.setGravity(Gravity.CENTER_HORIZONTAL);
        personText.setTextSize(18f);
        personText.setPadding(0, 50, 0, 20);
        personText.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        ));

        addView(personText);
    }

    // Method called for on win
    private void onWin() {
        if (getContext() instanceof MainActivity) {
            ((MainActivity) getContext()).cryptogramOnWin();
        }
    }
}

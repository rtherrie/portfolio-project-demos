package personal.logicpuzzles;

import java.util.Random;

public class Cryptogram {

    // Quotes for cryptogram
    private String[][] quotes = {
            {"Stay hungry, stay foolish", "- Steve Jobs"},
            {"What we think, we become", "- Buddha"},
            {"Be yourself; everyone else is taken", "- Oscar Wilde"},
            {"Simplicity is the ultimate sophistication", "- Leonardo da Vinci"},
            {"Happiness depends upon ourselves", "- Aristotle"},
            {"Dream big and dare to fail", "- Norman Vaughan"},
            {"Action is the foundational key to success", "- Pablo Picasso"},
            {"Turn your wounds into wisdom", "- Oprah Winfrey"},
            {"Whatever you are be a good one", "- Abraham Lincoln"},
            {"Success is getting what you want", "- Dale Carnegie"},
            {"Life is what happens when youre busy making other plans", "- John Lennon"},
            {"Time flies over us, but leaves its shadow behind", "- Nathaniel Hawthorne"},
            {"In the middle of difficulty lies opportunity", "- Albert Einstein"},
            {"The only way to do great work is to love what you do", "- Steve Jobs"},
            {"Success is not final, failure is not fatal", "- Winston Churchill"},
            {"Success usually comes to those who are too busy to be looking for it", "- Henry David Thoreau"},
            {"Dont watch the clock; do what it does", "- Sam Levenson"},
            {"The best way to predict the future is to create it", "- Abraham Lincoln"},
            {"It does not matter how slowly you go as long as you do not stop", "- Confucius"},
            {"Our lives begin to end the day we become silent about things that matter", "- Martin Luther King Jr"},
            {"You only live once, but if you do it right, once is enough", "- Mae West"},
            {"In three words I can sum up everything Ive learned about life: it goes on", "- Robert Frost"},
            {"Dont cry because its over, smile because it happened", "- Dr Seuss"},
            {"Be the change that you wish to see in the world", "- Mahatma Gandhi"},
            {"The purpose of life is not to be happy", "- Albert Einstein"},
            {"Life is really simple, but we insist on making it complicated", "- Confucius"},
            {"Life isnt about finding yourself Its about creating yourself", "- George Bernard Shaw"},
            {"The only impossible journey is the one you never begin", "- Tony Robbins"},
            {"You must be the change you wish to see in the world", "- Mahatma Gandhi"},
            {"The only limit to our realization of tomorrow is our doubts of today", "- Franklin D Roosevelt"},
            {"Act as if what you do makes a difference", "- William James"},
            {"It always seems impossible until its done", "- Nelson Mandela"},
            {"You must be the change you wish to see in the world", "- Mahatma Gandhi"},
            {"The best way to predict the future is to create it", "- Peter Drucker"},
            {"The only way to do great work is to love what you do", "- Steve Jobs"}};



    private int index;
    private int[] numbers; // Numbers in alphabet order

    private int[] currentNumbers; // Numbers in order they appear in the quote


    public Cryptogram(){
        // Pick a random quote
        Random rand = new Random();
        index = rand.nextInt(quotes.length);

        // Initialize numbers array
        numbers = new int[26];
        int i=0;
        while(i<numbers.length){
            int number = rand.nextInt(26)+1;

            // Check if value is unique
            boolean isDifferent = true;
            for(int j=0; j<numbers.length; j++){
                if(numbers[j] == number) {
                    // Not unique
                    isDifferent = false;
                }
            }

            if(isDifferent){
                // Is unique
                numbers[i] = number;
                i++;
            }
        }

        // Set currentNumbers
        char[] characters = quotes[index][0].toUpperCase().toCharArray();
        currentNumbers = new int[characters.length];
        for(int j=0; j<characters.length; j++){
            if (characters[j] == ' ') {
                currentNumbers[j] = 100;
            } else if (characters[j] >= 'A' && characters[j] <= 'Z') {
                currentNumbers[j] = numbers[characters[j] - 'A']; // A=0, B=1, ..., Z=25 (use as index)
            }
        }
    }

    // Getters and setters
    public String getQuote(){
        return quotes[index][0];
    }

    public int[] getNumbers(){
        return numbers;
    }

    public int[] getCurrentNumbers(){
        return currentNumbers;
    }

    public String getPerson(){
        return quotes[index][1];
    }
}

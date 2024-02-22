package ascii_art;

import ascii_art.img_to_char.BrightnessImgCharMatcher;
import ascii_output.AsciiOutput;
import ascii_output.ConsoleAsciiOutput;
import ascii_output.HtmlAsciiOutput;
import image.Image;

import java.util.HashSet;
import java.util.Scanner;

public class Shell {
    private static final String DEFAULT_FONT = "Courier New";
    private static final String FILENAME = "out.html";
    private static final int INITIAL_CHARS_IN_ROW = 2;
    private static final int MIN_PIXELS_PER_CHAR = 2;
    public static final String CHANGE_RES_ERROR_MSG = "Did not change due to incorrect format";
    public static final String CHANGE_RES_OUTOFBOUND = "Did not change due to exceeding boundaries";
    public static final String WRONG_CMD_MSG = "Did not executed due to incorrect command";
    public static final String ADDING_ERROR_MSG = "Did not add due to incorrect format";
    public static final String REMOVE_ERROR_MSG = "Did not add remove to incorrect format";
    private Image image;
    private AsciiOutput asciiOutput;
    private HashSet<Character> hashSet;
    private Scanner scanner;
    private BrightnessImgCharMatcher brightnessImgCharMatcher;
    private int minCharsInRow;
    private int maxCharsInRow;
    private int charsInRow;
    ConsoleAsciiOutput consoleAsciiOutput;

    public Shell(Image img) {
        this.image = img;
        this.hashSet = new HashSet<>();
        for (int i = 48; i <= 57; i++) {
            this.hashSet.add((char) i);
        }
        this.asciiOutput = new HtmlAsciiOutput(FILENAME, DEFAULT_FONT);
        this.brightnessImgCharMatcher = new BrightnessImgCharMatcher(this.image, DEFAULT_FONT);
        this.minCharsInRow = Math.max(1, img.getWidth() / img.getHeight());
        this.maxCharsInRow = img.getWidth() / MIN_PIXELS_PER_CHAR;
        this.charsInRow = Math.max(Math.min(INITIAL_CHARS_IN_ROW, maxCharsInRow), minCharsInRow);
        this.consoleAsciiOutput = new ConsoleAsciiOutput();
    }

    /**
     * runner of the program
     */
    public void run() {
        // Initialize a scanner to read input from the console
        scanner = new Scanner(System.in);
        // Run the loop indefinitely
        while (true) {
            // Print a prompt for the user
            System.out.print("<<< ");
            // Read the user's input
            String command = scanner.nextLine();

            // Check if the user entered the "exit" command
            if (command.equals("exit")) {
                // If so, return from the function to stop the loop
                return;
            }
            // Check if the user entered the "chars" command
            else if (command.equals("chars")) {
                // Print all the characters in the hash set
                for (Character c : this.hashSet) {
                    System.out.print(c + " ");
                }
                // Print a new line
                System.out.println();
            }
            // Check if the user entered a command starting with "add "
            else if (command.startsWith("add ")) {
                // Call the add function with the user's input
                add(command);
            }
            // Check if the user entered a command starting with "remove "
            else if (command.startsWith("remove ")) {
                // Call the remove function with the user's input
                remove(command);
            }
            // Check if the user entered a command starting with "res "
            else if (command.startsWith("res ")) {
                // Call the changeRes function with the user's input
                changeRes(command);
            }
            // Check if the user entered the "console" command
            else if (command.equals("console")) {
                // Set the asciiOutput field to a new ConsoleAsciiOutput object
                asciiOutput = new ConsoleAsciiOutput();

                // Check if the user entered the "render" command
            } else if (command.equals("render")) {
                // Call the render function
                render();

            } else {
                // If the user entered an unrecognized command, print an error message
                System.out.println(WRONG_CMD_MSG);
            }
        }
    }

    /**
     * render
     */
    private void render() {
        // Create a new Character array with the same size as the hashSet field
        Character[] charSet = new Character[this.hashSet.size()];
        // Initialize a counter variable
        int i = 0;
        // Iterate over the elements in the hashSet field
        for (Character character : this.hashSet) {
            // Add each element to the charSet array
            charSet[i] = character;
            // Increment the counter variable
            i += 1;
        }
        // Use the asciiOutput field to output the result of the chooseChars method of the brightnessImgCharMatcher field
        // Pass in the charsInRow field and the charSet array as arguments to the chooseChars method
        asciiOutput.output(brightnessImgCharMatcher.chooseChars(this.charsInRow, charSet));
    }


    /**
     * change resoltion of the image by increaseing the number of the chars in the row
     *
     * @param command
     */
    private void changeRes(String command) {
        // Split the command string into an array of substrings using a space character as the delimiter
        String[] res = command.split(" ");

        // Check the second element of the array (res[1]) to determine the direction of the resolution change
        if (res[1].equals("up")) {
            // If the current width of the display (charsInRow) can be safely doubled...
            if (charsInRow * 2 <= maxCharsInRow) {
                // ...double the width and print a message to the console indicating the new width
                charsInRow *= 2;
                System.out.println("Width set to " + charsInRow);

                // If the current width cannot be safely doubled...
            } else {
                // ...print a message to the console indicating that the resolution change is out of bounds
                System.out.println(CHANGE_RES_OUTOFBOUND);
            }

            // If the second element of the res array is not "up"...
        } else if (res[1].equals("down")) {
            // ...check whether it is "down"
            // If the current width of the display (charsInRow) can be safely halved...
            if (charsInRow / 2 >= minCharsInRow) {
                // ...halve the width and print a message to the console indicating the new width
                charsInRow /= 2;
                System.out.println("Width set to " + charsInRow);

                // If the current width cannot be safely halved...
            } else {
                // ...print a message to the console indicating that the resolution change is out of bounds
                System.out.println(CHANGE_RES_OUTOFBOUND);
            }

            // If the second element of the res array is neither "up" nor "down"...
        } else {
            // ...print an error message to the console indicating that the command was not recognized
            System.out.println(CHANGE_RES_ERROR_MSG);
        }
    }

    /**
     * add chars to hash set
     *
     * @param command
     */
    private void add(String command) {
        // Split the command string into an array of substrings using a space character as the delimiter
        String[] res = command.split(" ");

        // Check if the command string contains exactly two elements
        if (res.length == 2) {
            // Check if the second element of the res array is the string "all"
            if (res[1].equals("all")) {
                // TODO: Add all characters to the hashSet field
                for (int i = 32; i <= 126; i++) {
                    this.hashSet.add((char) i);
                }
                // If the second element of the res array is not "all"...
            } else if (res[1].equals("space")) {
                // ...check if it is the string "space"
                // If it is, add a space character to the hashSet field
                hashSet.add(' ');

                // If the second element of the res array is neither "all" nor "space"...
            } else if (res[1].length() == 3 && res[1].split("-").length == 2) {
                // ...check if it has a length of 3 and contains a hyphen character
                // If it meets these conditions, split the string around the hyphen character
                // to get an array of two strings
                String[] strChars = res[1].split("-");
                // Get the first character of the first string and store it in the start variable
                char start = strChars[0].charAt(0);
                // Get the first character of the second string and store it in the end variable
                char end = strChars[1].charAt(0);
                // Get the ASCII value of the start character and store it in the startOrd variable
                int startOrd = (int) start;
                // Get the ASCII value of the end character and store it in the endOrd variable
                int endOrd = (int) end;
                // If the ASCII value of the start character is greater than the ASCII value of the end character...
                if (startOrd > endOrd) {
                    // ...swap the values of startOrd and endOrd
                    int temp = startOrd;
                    startOrd = endOrd;
                    endOrd = temp;
                }
                // Iterate from the ASCII value of the start character to the ASCII value of the end character
                for (int i = startOrd; i <= endOrd; i++) {
                    // Add each character to the hashSet field
                    hashSet.add((char) i);
                }

                // If the second element of the res array is a single character...
            } else if (res[1].length() == 1) {
                // ...add it to the hashSet field
                hashSet.add(res[1].charAt(0));

                // If the second element of the res array does not meet any of the above conditions...
            } else {
                // ...print an error message to the console
                System.out.println(ADDING_ERROR_MSG);
            }

            // If the command string does not contain exactly two elements...
        } else {
            // ...print an error message to the console
            System.out.println(ADDING_ERROR_MSG);
        }
    }

    /**
     * remove chars from the hash set
     *
     * @param command
     */
    private void remove(String command) {
        // Split the command string by space to get the individual words
        String[] res = command.split(" ");
        // Check if there are two words (indicating a valid remove command)
        if (res.length == 2) {
            // Check if the second word is "all"
            if (res[1].equals("all")) {
                // Clear the hash set
                hashSet.clear();

                // Check if the second word is "space"
            } else if (res[1].equals("space")) {
                // Remove the space character from the hash set
                hashSet.remove(' ');

                // Check if the second word is a range of characters (indicated by a dash)
            } else if (res[1].length() == 3 && res[1].split("-").length == 2) {
                // Split the range by the dash
                String[] strChars = res[1].split("-");
                // Get the starting and ending characters
                char start = strChars[0].charAt(0);
                char end = strChars[1].charAt(0);
                // Convert the characters to their ASCII values
                int startOrd = (int) start;
                int endOrd = (int) end;
                // Check if the starting character has a higher ASCII value than the ending character
                // If so, swap the values
                if (startOrd > endOrd) {
                    int temp = startOrd;
                    startOrd = endOrd;
                    endOrd = temp;
                }
                // Iterate through the range of ASCII values and remove each character from the hash set
                for (int i = startOrd; i <= endOrd; i++) {
                    hashSet.remove((char) i);
                }
                // Check if the second word is a single character
            } else if (res[1].length() == 1) {
                // Remove the character from the hash set
                hashSet.remove(res[1].charAt(0));
            } else {
                // If none of the conditions above are met, print an error message
                System.out.println(REMOVE_ERROR_MSG);
            }
        } else {
            // If the command does not have two words, print an error message
            System.out.println(REMOVE_ERROR_MSG);
        }
    }

}


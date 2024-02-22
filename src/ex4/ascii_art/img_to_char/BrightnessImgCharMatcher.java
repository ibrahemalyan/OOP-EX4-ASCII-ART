package ascii_art.img_to_char;

import image.Image;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class BrightnessImgCharMatcher {
    public static final int MAX_RGB_VALUE = 255;
    public static final double RED_PERCENTAGE = 0.2126;
    public static final double GREEN_PERCENTAGE = 0.7152;
    public static final double BLUE_PERCENTAGE = 0.0722;
    private static final int DEFAULT_RESOLUTION = 16;
    String fontName;
    Image image;

    public BrightnessImgCharMatcher(Image img, String font) {
        this.image = img;
        this.fontName = font;
    }

    /**
     * This method calculates the brightness of a given character.
     * The brightness is defined as the ratio of the number of white pixels
     * in the character's image representation to the total number of pixels.
     */
    private float getBrightnessOfChar(Character character) {
        // Initialize a counter for the number of white pixels.
        int whitePixelCounter = 0;

        // Get a boolean representation of the character's image,
        // where white pixels are represented by true and black pixels by false.
        boolean[][] booleanImageRepresent = CharRenderer.getImg(character, DEFAULT_RESOLUTION, fontName);

        // Iterate through the image representation and count the number of white pixels.
        for (int i = 0; i < DEFAULT_RESOLUTION; i++) {
            for (int j = 0; j < DEFAULT_RESOLUTION; j++) {
                if (booleanImageRepresent[i][j]) {
                    whitePixelCounter += 1;
                }
            }
        }

        // Calculate and return the brightness as the ratio of white pixels to total pixels.
        return (float) whitePixelCounter / (float) Math.pow(DEFAULT_RESOLUTION, 2);
    }


    /**
     * This method normalizes the values in an array of brightness levels.
     * The brightness levels are assumed to be in the range [0, 1].
     *
     * @param levels
     */
    private void normalImageBrightness(float[] levels) {
        // Find the minimum and maximum brightness level.
        float min = levels[0];
        float max = levels[levels.length - 1];

        // Iterate through the array and normalize each value.
        // The normalized value is calculated as (value - min) / (max - min).
        for (int i = 0; i < levels.length; i++) {
            levels[i] = (levels[i] - min) / (max - min);
        }
    }

    /**
     * Calculates the average brightness of a sub-image.
     *
     * @return the average brightness of the sub-image
     */
    public float calculateAverageBrightness(ArrayList<ArrayList<Color>> subImage) {
        // Initialize a total brightness value
        double totalBrightness = 0;
        // Get the dimensions of the image
        int height = subImage.size();
        int width = subImage.get(0).size();
        // Iterate through all the pixels in the image
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Get the color of the current pixel
                Color color = subImage.get(x).get(y);
                // Convert the color to grey scale
                double greyPixel = color.getRed() * RED_PERCENTAGE
                        + color.getGreen() * GREEN_PERCENTAGE + color.getBlue() * BLUE_PERCENTAGE;
                // Add the grey pixel value to the total brightness
                totalBrightness += greyPixel;
            }
        }
        // Calculate and return the average brightness
        return (float) totalBrightness / (width * height * MAX_RGB_VALUE);
    }

    /**
     * chooseChars returns a 2D char array representing an ASCII
     * art version of the image, with each character in the array
     * being chosen from the given charSet and being as close as possible in
     * brightness to the corresponding region of the image
     */
    public char[][] chooseChars(int numCharsInRow, Character[] charSet) {
        // Calculate the number of pixels in each character
        int pixelNum = image.getWidth() / numCharsInRow;
        // Initialize counter to 0
        int counter = 0;
        // Initialize asciiArt to be a 2D char array with the same number of rows as the
        // image and numCharsInRow columns
        char[][] asciiArt = new char[image.getHeight() / pixelNum][numCharsInRow];
        // Split the image into a list of sub-images, each sub-image representing a region of the image
        ArrayList<ArrayList<ArrayList<Color>>> arrayList = image.splitImageAlgo(image, pixelNum);
        // Iterate through the sub-images
        for (ArrayList<ArrayList<Color>> subImg : arrayList) {
            // Find the character in charSet that is closest in brightness to the sub-image
            // and store it in asciiArt
            asciiArt[counter / numCharsInRow][counter % numCharsInRow] = findClosestCharacter(subImg, charSet);
            // Increment counter
            counter++;
        }
        // Return the ASCII art version of the image
        return asciiArt;
    }


    /**
     * findClosestCharacter returns the character in charSet that is closest in brightness
     * to the image represented by subImg
     */
    private char findClosestCharacter(ArrayList<ArrayList<Color>> subImg, Character[] charSet) {
        // Sort charSet alphabetically
        Arrays.sort(charSet, new CharComparatorByBrightness());

        // Initialize brightnessArr to store the brightness of each character in charSet
        float[] brightnessArr = new float[charSet.length];
        for (int i = 0; i < brightnessArr.length; i++) {
            // Calculate the brightness of each character and store it in brightnessArr
            brightnessArr[i] = getBrightnessOfChar(charSet[i]);
        }
        // Normalize the brightness of the characters in charSet
        normalImageBrightness(brightnessArr);

        // Calculate the average brightness of the image represented by subImg
        float imgBrightness = calculateAverageBrightness(subImg);

        // Initialize closestIndex to -1
        int closestIndex = -1;
        // Iterate through brightnessArr to find the first character whose brightness
        // is greater than or equal to imgBrightness
        for (int i = 0; i < charSet.length; i++) {
            if (brightnessArr[i] >= imgBrightness) {
                // If found, store the index in closestIndex and break out of the loop
                closestIndex = i;
                break;
            }
        }
        // If closestIndex is 0, return the character at index 0 of charSet
        if (closestIndex == 0) {
            return charSet[0];
        }
        // If the difference between the brightness of the character at index closestIndex and
        // imgBrightness is smaller
        // than the difference between imgBrightness and the brightness of
        // the character at index closestIndex - 1,
        // return the character at index closestIndex
        if (brightnessArr[closestIndex] - imgBrightness < imgBrightness - brightnessArr[closestIndex - 1]) {
            return charSet[closestIndex];
        }
        // Otherwise, return the character at index closestIndex - 1
        return charSet[closestIndex - 1];
    }

    /**
     * This class is a comparator that compares characters based on their brightness.
     * Characters with higher brightness are considered "greater" than characters with lower brightness.
     */

    private class CharComparatorByBrightness implements Comparator<Character> {

        // Implement the compare method of the Comparator interface
        // It compares the brightness of the two characters passed as arguments
        @Override
        public int compare(Character character1, Character character2) {

            // Calculate the brightness of each character using the getBrightnessOfChar method
            float brightness1 = getBrightnessOfChar(character1);
            float brightness2 = getBrightnessOfChar(character2);

            // Use the Float.compare method to compare the brightness of the two characters
            // It returns a negative number if brightness1 is less than brightness2,
            // a positive number if brightness1 is greater than brightness2,
            // and 0 if brightness1 is equal to brightness2
            return Float.compare(brightness1, brightness2);
        }
    }


}


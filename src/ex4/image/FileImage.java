package image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A package-private class of the package image.
 *
 * @author Dan Nirel
 */
class FileImage implements Image {
    private static final Color DEFAULT_COLOR = Color.WHITE;

    private final Color[][] pixelArray;
    ArrayList<Color[][]> subimages;

    public FileImage(String filename) throws IOException {
        java.awt.image.BufferedImage im = ImageIO.read(new File(filename));

        int origWidth = im.getWidth(), origHeight = im.getHeight();
        int newWidth = roundNumberToPower2(origWidth); //TODO: change
        int newHeight = roundNumberToPower2(origHeight); //TODO: change


        pixelArray = new Color[newHeight][newWidth];

        int topLeftCornerX = (newHeight - origHeight) / 2;
        int topLeftCornerY = (newWidth - origWidth) / 2;

        for (int i = 0; i < newHeight; i++) {
            for (int j = 0; j < newWidth; j++) {
                if (topLeftCornerX <= i && i < origHeight + topLeftCornerX &&
                        topLeftCornerY <= j && j < origWidth + topLeftCornerY) {
                    pixelArray[i][j] = new Color(im.getRGB(j - topLeftCornerY, i - topLeftCornerX));

                } else {
                    pixelArray[i][j] = DEFAULT_COLOR;
                }
            }
        }

    }

    /**
     * This method rounds a given number to the nearest power of 2.
     *
     * @param num
     * @return
     */
    private int roundNumberToPower2(int num) {
        // Use the logarithm function to find the power of 2 that is closest to the given number.
        // The logarithm function returns the exponent to which the base (in this case 2) must be raised
        // to produce a given number. We use Math.log() to find the logarithm of the given number with base 2.
        // We then divide this value by the logarithm of 2 with base 2 (which is equal to 1) to get the exponent.
        // Finally, we round this value using Math.round().
        // We then raise 2 to this exponent using Math.pow() and cast the result to an integer.
        // This is the rounded number that is a power of 2.
        return (int) Math.pow(2, Math.round(Math.log(num) / Math.log(2)));
    }


    @Override
    public int getWidth() {
        //TODO: implement the function
        return this.pixelArray[0].length;
    }

    @Override
    public int getHeight() {
        //TODO: implement the function
        return this.pixelArray.length;
    }

    @Override
    public Color getPixel(int x, int y) {
        //TODO: implement the function
        return this.pixelArray[y][x];
    }

}

package by.vshkl.pxlsrt.core.mode;

import android.graphics.Color;

public class Brightness {

    private static final int brightnessValue = 60;

    public static int getFirstBrightX(int[] pixels, int x, int y, int width) {
        while (brightness(pixels[x + y * width]) < brightnessValue) {
            x++;
            if (x >= width) {
                return -1;
            }
        }
        return x;
    }

    public static int getNextDarkX(int[] pixels, int x, int y, int width) {
        x++;
        while (brightness(pixels[x + y * width]) > brightnessValue) {
            x++;
            if (x >= width) {
                return width - 1;
            }
        }
        return x - 1;
    }

    public static int getFirstBrightY(int[] pixels, int x, int y, int width, int height) {
        if (y < height) {
            while (brightness(pixels[x + y * width]) < brightnessValue) {
                y++;
                if (y >= height) {
                    return -1;
                }
            }
        }
        return y;
    }

    public static int getNextDarkY(int[] pixels, int x, int y, int width, int height) {
        y++;
        if (y < height) {
            while (brightness(pixels[x + y * width]) > brightnessValue) {
                y++;
                if (y >= height) {
                    return height - 1;
                }
            }
        }
        return y - 1;
    }

    private static int brightness(int pixelColor) {
        int r = Color.red(pixelColor);
        int g = Color.green(pixelColor);
        int b = Color.blue(pixelColor);
        return (r + r + r + b + g + g + g + g) >> 3;
    }
}

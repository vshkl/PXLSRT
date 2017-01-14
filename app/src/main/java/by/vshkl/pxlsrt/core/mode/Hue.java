package by.vshkl.pxlsrt.core.mode;

import android.graphics.Color;

public class Hue {

    private static final int hueValue = 180;

    public static int getFirstHueX(int[] pixels, int x, int y, int width) {
        while (getHue(pixels[x + y * width]) < hueValue) {
            x++;
            if (x >= width) {
                return -1;
            }
        }
        return x;
    }

    public static int getNextNotHueX(int[] pixels, int x, int y, int width) {
        x++;
        while (getHue(pixels[x + y * width]) > hueValue) {
            x++;
            if (x >= width) {
                return width - 1;
            }
        }
        return x - 1;
    }

    public static int getFirstHueY(int[] pixels, int x, int y, int width, int height) {
        if (y < height) {
            while (getHue(pixels[x + y * width]) < hueValue) {
                y++;
                if (y >= height) {
                    return -1;
                }
            }
        }
        return y;
    }

    public static int getNextNotHueY(int[] pixels, int x, int y, int width, int height) {
        y++;
        if (y < height) {
            while (getHue(pixels[x + y * width]) > hueValue) {
                y++;
                if (y >= height) {
                    return height - 1;
                }
            }
        }
        return y;
    }

    private static int getHue(int pixelColor) {
        int r = Color.red(pixelColor);
        int g = Color.green(pixelColor);
        int b = Color.blue(pixelColor);
        double hue = 100 / Math.PI * Math.atan2(Math.sqrt(3) * (g - b), 2 * r - g - b);
        return hue < 0 ? (int) (hue + 360) : (int) hue;
    }
}

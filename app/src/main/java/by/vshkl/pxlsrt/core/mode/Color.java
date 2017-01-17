package by.vshkl.pxlsrt.core.mode;

public class Color {

    public static int getFirstColorX(int[] pixels, int x, int y, int width, int colorValue) {
        while (pixels[x + y * width] < colorValue) {
            x++;
            if (x >= width) {
                return -1;
            }
        }
        return x;
    }

    public static int getNextNotColorX(int[] pixels, int x, int y, int width, int colorValue) {
        x++;
        while (pixels[x + y * width] > colorValue) {
            x++;
            if (x >= width) {
                return width - 1;
            }
        }
        return x - 1;
    }

    public static int getFirstColorY(int[] pixels, int x, int y, int width, int height, int colorValue) {
        if (y < height) {
            while (pixels[x + y * width] < colorValue) {
                y++;
                if (y >= height) {
                    return -1;
                }
            }
        }
        return y;
    }

    public static int getNextNotColorY(int[] pixels, int x, int y, int width, int height, int colorValue) {
        y++;
        if (y < height) {
            while (pixels[x + y * width] > colorValue) {
                y++;
                if (y >= height) {
                    return height - 1;
                }
            }
        }
        return y;
    }
}

package by.vshkl.pxlsrt.core.mode;

public class Black {

    private static final int blackValue = -16000000;

    public static int getFirstNotBlackX(int[] pixels, int x, int y, int width) {
        while (pixels[x + y * width] < blackValue) {
            x++;
            if (x >= width) {
                return -1;
            }
        }
        return x;
    }

    public static int getNextBlackX(int[] pixels, int x, int y, int width) {
        x++;
        while (pixels[x + y * width] > blackValue) {
            x++;
            if (x >= width) {
                return width - 1;
            }
        }
        return x - 1;
    }

    public static int getFirstNotBlackY(int[] pixels, int x, int y, int width, int height) {
        if (y < height) {
            while (pixels[x + y * width] < blackValue) {
                y++;
                if (y >= height) {
                    return -1;
                }
            }
        }
        return y;
    }

    public static int getNextBlackY(int[] pixels, int x, int y, int width, int height) {
        y++;
        if (y < height) {
            while (pixels[x + y * width] > blackValue) {
                y++;
                if (y >= height) {
                    return height - 1;
                }
            }
        }
        return y;
    }
}

package by.vshkl.pxlsrt.core.mode;

public class White {

    private static final int whiteValue = -13000000;

    public static int getFirstNotWhiteX(int[] pixels, int x, int y, int width) {
        while (pixels[x + y * width] < whiteValue) {
            x++;
            if (x >= width) {
                return -1;
            }
        }
        return x;
    }

    public static int getNextWhiteX(int[] pixels, int x, int y, int width) {
        x++;
        while (pixels[x + y * width] > whiteValue) {
            x++;
            if (x >= width) {
                return width - 1;
            }
        }
        return x - 1;
    }

    public static int getFirstNotWhiteY(int[] pixels, int x, int y, int width, int height) {
        if (y < height) {
            while (pixels[x + y * width] < whiteValue) {
                y++;
                if (y >= height) {
                    return -1;
                }
            }
        }
        return y;
    }

    public static int getNextWhiteY(int[] pixels, int x, int y, int width, int height) {
        y++;
        if (y < height) {
            while (pixels[x + y * width] > whiteValue) {
                y++;
                if (y >= height) {
                    return height - 1;
                }
            }
        }
        return y;
    }
}

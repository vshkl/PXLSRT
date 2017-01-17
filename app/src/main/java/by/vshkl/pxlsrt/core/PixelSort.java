package by.vshkl.pxlsrt.core;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileInputStream;
import java.nio.IntBuffer;
import java.util.Arrays;

import by.vshkl.pxlsrt.core.mode.Black;
import by.vshkl.pxlsrt.core.mode.Brightness;
import by.vshkl.pxlsrt.core.mode.Color;
import by.vshkl.pxlsrt.core.mode.White;
import by.vshkl.pxlsrt.mvp.model.SortingMode;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

import static by.vshkl.pxlsrt.core.mode.Brightness.getFirstBrightY;

public class PixelSort {

    public static Observable<Bitmap> sort(final FileInputStream fis, final SortingMode sortingMode, final int color) {
        return Observable.create(new ObservableOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(ObservableEmitter<Bitmap> emitter) throws Exception {
                int currentRow = 0;
                int currentColumn = 0;

                Bitmap bitmap = BitmapFactory.decodeStream(fis);
                int height = bitmap.getHeight();
                int width = bitmap.getWidth();

                IntBuffer buffer = IntBuffer.allocate(width * height);
                bitmap.copyPixelsToBuffer(buffer);
                int[] pixels = buffer.array();

                bitmap.recycle();

                switch (sortingMode) {
                    case BLACK:
                        while (currentColumn < width - 1) {
                            pixels = sortColumnBlack(pixels, currentColumn, width, height);
                            currentColumn++;
                        }
                        while (currentRow < height - 1) {
                            pixels = sortRowBlack(pixels, currentRow, width);
                            currentRow++;
                        }
                        break;
                    case WHITE:
                        while (currentColumn < width - 1) {
                            pixels = sortColumnWhite(pixels, currentColumn, width, height);
                            currentColumn++;
                        }
                        while (currentRow < height - 1) {
                            pixels = sortRowWhite(pixels, currentRow, width);
                            currentRow++;
                        }
                        break;
                    case BRIGHTNESS:
                        while (currentColumn < width - 1) {
                            pixels = sortColumnBrightness(pixels, currentColumn, width, height);
                            currentColumn++;
                        }
                        while (currentRow < height - 1) {
                            pixels = sortRowBrightness(pixels, currentRow, width);
                            currentRow++;
                        }
                        break;
                    case COLOR:
                        while (currentColumn < width - 1) {
                            pixels = sortColumnColor(pixels, currentColumn, width, height, color);
                            currentColumn++;
                        }
                        while (currentRow < height - 1) {
                            pixels = sortRowColor(pixels, currentRow, width, color);
                            currentRow++;
                        }
                        break;
                    default:
                        break;
                }

                // Set sorted pixels to bitmap
                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                buffer.clear();
                IntBuffer.wrap(pixels);
                bitmap.copyPixelsFromBuffer(buffer);

                // Emit resulting bitmap
                emitter.onNext(bitmap);
            }
        });
    }

    //---[ Sorting black ]----------------------------------------------------------------------------------------------

    static int[] sortRowBlack(int[] pixels, int currentRow, int width) {
        int y = currentRow;
        int x = 0;
        int xe = 0;
        while (xe < width - 1) {
            x = Black.getFirstNotBlackX(pixels, x, y, width);
            xe = Black.getNextBlackX(pixels, x, y, width);
            if (x < 0) {
                break;
            }
            int sortedLength = xe - x;
            int[] sorted = new int[sortedLength];
            for (int i = 0; i < sortedLength; i++) {
                sorted[i] = pixels[x + i + y * width];
            }
            Arrays.sort(sorted);
            for (int i = 0; i < sortedLength; i++) {
                pixels[x + i + y * width] = sorted[i];
            }
            x = xe + 1;
        }
        return pixels;
    }

    static int[] sortColumnBlack(int[] pixels, int currentColumn, int width, int height) {
        int x = currentColumn;
        int y = 0;
        int ye = 0;
        while (ye < height - 1) {
            y = Black.getFirstNotBlackY(pixels, x, y, width, height);
            ye = Black.getNextBlackY(pixels, x, y, width, height);
            if (y < 0) {
                break;
            }
            int sortedLength = ye - y;
            int[] sorted = new int[sortedLength];
            for (int i = 0; i < sortedLength; i++) {
                sorted[i] = pixels[x + (i + y) * width];
            }
            Arrays.sort(sorted);
            for (int i = 0; i < sortedLength; i++) {
                pixels[x + (i + y) * width] = sorted[i];
            }
            y = ye + 1;
        }
        return pixels;
    }

    //---[ Sorting white ]----------------------------------------------------------------------------------------------

    static int[] sortRowWhite(int[] pixels, int currentRow, int width) {
        int y = currentRow;
        int x = 0;
        int xe = 0;
        while (xe < width - 1) {
            x = White.getFirstNotWhiteX(pixels, x, y, width);
            xe = White.getNextWhiteX(pixels, x, y, width);
            if (x < 0) {
                break;
            }
            int sortedLength = xe - x;
            int[] sorted = new int[sortedLength];
            for (int i = 0; i < sortedLength; i++) {
                sorted[i] = pixels[x + i + y * width];
            }
            Arrays.sort(sorted);
            for (int i = 0; i < sortedLength; i++) {
                pixels[x + i + y * width] = sorted[i];
            }
            x = xe + 1;
        }
        return pixels;
    }

    static int[] sortColumnWhite(int[] pixels, int currentColumn, int width, int height) {
        int x = currentColumn;
        int y = 0;
        int ye = 0;
        while (ye < height - 1) {
            y = White.getFirstNotWhiteY(pixels, x, y, width, height);
            ye = White.getNextWhiteY(pixels, x, y, width, height);
            if (y < 0) {
                break;
            }
            int sortedLength = ye - y;
            int[] sorted = new int[sortedLength];
            for (int i = 0; i < sortedLength; i++) {
                sorted[i] = pixels[x + (i + y) * width];
            }
            Arrays.sort(sorted);
            for (int i = 0; i < sortedLength; i++) {
                pixels[x + (i + y) * width] = sorted[i];
            }
            y = ye + 1;
        }
        return pixels;
    }

    //---[ Sorting brightness ]-----------------------------------------------------------------------------------------

    static int[] sortRowBrightness(int[] pixels, int currentRow, int width) {
        int y = currentRow;
        int x = 0;
        int xe = 0;
        while (xe < width - 1) {
            x = Brightness.getFirstBrightX(pixels, x, y, width);
            xe = Brightness.getNextDarkX(pixels, x, y, width);
            if (x < 0) {
                break;
            }
            int sortedLength = xe - x;
            int[] sorted = new int[sortedLength];
            for (int i = 0; i < sortedLength; i++) {
                sorted[i] = pixels[x + i + y * width];
            }
            Arrays.sort(sorted);
            for (int i = 0; i < sortedLength; i++) {
                pixels[x + i + y * width] = sorted[i];
            }
            x = xe + 1;
        }
        return pixels;
    }

    static int[] sortColumnBrightness(int[] pixels, int currentColumn, int width, int height) {
        int x = currentColumn;
        int y = 0;
        int ye = 0;
        while (ye < height - 1) {
            y = getFirstBrightY(pixels, x, y, width, height);
            ye = Brightness.getNextDarkY(pixels, x, y, width, height);
            if (y < 0) {
                break;
            }
            int sortedLength = ye - y;
            int[] sorted = new int[sortedLength];
            for (int i = 0; i < sortedLength; i++) {
                sorted[i] = pixels[x + (i + y) * width];
            }
            Arrays.sort(sorted);
            for (int i = 0; i < sortedLength; i++) {
                pixels[x + (i + y) * width] = sorted[i];
            }
            y = ye + 1;
        }
        return pixels;
    }

    //---[ Sorting color ]----------------------------------------------------------------------------------------------

    static int[] sortRowColor(int[] pixels, int currentRow, int width, int color) {
        int y = currentRow;
        int x = 0;
        int xe = 0;
        while (xe < width - 1) {
            x = Color.getFirstColorX(pixels, x, y, width, color);
            xe = Color.getNextNotColorX(pixels, x, y, width, color);
            if (x < 0) {
                break;
            }
            int sortedLength = xe - x;
            int[] sorted = new int[sortedLength];
            for (int i = 0; i < sortedLength; i++) {
                sorted[i] = pixels[x + i + y * width];
            }
            Arrays.sort(sorted);
            for (int i = 0; i < sortedLength; i++) {
                pixels[x + i + y * width] = sorted[i];
            }
            x = xe + 1;
        }
        return pixels;
    }

    static int[] sortColumnColor(int[] pixels, int currentColumn, int width, int height, int color) {
        int x = currentColumn;
        int y = 0;
        int ye = 0;
        while (ye < height - 1) {
            y = Color.getFirstColorY(pixels, x, y, width, height, color);
            ye = Color.getNextNotColorY(pixels, x, y, width, height, color);
            if (y < 0) {
                break;
            }
            int sortedLength = ye - y;
            int[] sorted = new int[sortedLength];
            for (int i = 0; i < sortedLength; i++) {
                sorted[i] = pixels[x + (i + y) * width];
            }
            Arrays.sort(sorted);
            for (int i = 0; i < sortedLength; i++) {
                pixels[x + (i + y) * width] = sorted[i];
            }
            y = ye + 1;
        }
        return pixels;
    }
}

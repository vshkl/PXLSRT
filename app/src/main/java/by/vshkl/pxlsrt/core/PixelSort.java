package by.vshkl.pxlsrt.core;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import java.io.FileInputStream;
import java.util.Arrays;

import by.vshkl.pxlsrt.mvp.model.SortingMode;
import by.vshkl.pxlsrt.mvp.presenter.CameraPresenter;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class PixelSort {

    public static Observable<Bitmap> sort(final FileInputStream fis, final SortingMode sortingMode) {
        return Observable.create(new ObservableOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(ObservableEmitter<Bitmap> emitter) throws Exception {
                // Sorting modes 0 -> black, 1 -> brightness, 2 -> white
                int mode = 1;

//                int blackValue = -16000000;
                int brightnessValue = 60;
//                int whiteValue = -13000000;

                int height = CameraPresenter.IMAGE_SIZE_PX;
                int width = CameraPresenter.IMAGE_SIZE_PX;

                int currentRow = 0;
                int currentColumn = 0;

                // Get array of pixels
                Bitmap bitmap = BitmapFactory.decodeStream(fis);
                int[] pixels = new int[width * height];
                int k = 0;
                while (k < width * height) {
                    for (int row = 0; row < height; row++) {
                        for (int col = 0; col < width; col++) {
                            pixels[k] = bitmap.getPixel(row, col);
                            k++;
                        }
                    }
                }
                bitmap.recycle();

                // Loop through columns
                while (currentColumn < width - 1) {
                    pixels = sortColumns(pixels, currentColumn, width, height, brightnessValue);
                    currentColumn++;
                }

                // Loop through rows
                while (currentRow < height - 1) {
                    pixels = sortRow(pixels, currentRow, width, brightnessValue);
                    currentRow++;
                }

                // Set sorted pixels to bitmap
                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                k = 0;
                while (k < width * height) {
                    for (int row = 0; row < height; row++) {
                        for (int col = 0; col < width; col++) {
                            bitmap.setPixel(row, col, pixels[k]);
                            k++;
                        }
                    }
                }

                // Emit resulting bitmap
                emitter.onNext(bitmap);
            }
        });
    }

    //---[ Sorting ]----------------------------------------------------------------------------------------------------

    static int[] sortRow(int[] pixels, int currentRow, int width, int brightnessValue) {
        int y = currentRow;
        int x = 0;
        int xe = 0;
        while (xe < width - 1) {
            x = getFirstBrightX(pixels, x, y, brightnessValue, width);
            xe = getNextDarkX(pixels, x, y, brightnessValue, width);
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

    static int[] sortColumns(int[] pixels, int currentColumn, int width, int height, int brightnessValue) {
        int x = currentColumn;
        int y = 0;
        int ye = 0;
        while (ye < height - 1) {
            y = getFirstBrightY(pixels, x, y, brightnessValue, width, height);
            ye = getNextDarkY(pixels, x, y, brightnessValue, width, height);
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

    //---[ Brightness ]-------------------------------------------------------------------------------------------------

    static int getFirstBrightX(int[] pixels, int x, int y, int brightnessValue, int width) {
        while (brightness(pixels[x + y * width]) < brightnessValue) {
            x++;
            if (x >= width) {
                return -1;
            }
        }
        return x;
    }

    static int getNextDarkX(int[] pixels, int x, int y, int brightnessValue, int width) {
        x++;
        while (brightness(pixels[x + y * width]) > brightnessValue) {
            x++;
            if (x >= width) {
                return width - 1;
            }
        }
        return x - 1;
    }

    static int getFirstBrightY(int[] pixels, int x, int y, int brightnessValue, int width, int height) {
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

    static int getNextDarkY(int[] pixels, int x, int y, int brightnessValue, int width, int height) {
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

    //------------------------------------------------------------------------------------------------------------------

    private static int brightness(int pixelColor) {
        int r = Color.red(pixelColor);
        int g = Color.green(pixelColor);
        int b = Color.blue(pixelColor);
        return (r + r + r + b + g + g + g + g) >> 3;
    }
}

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

    private static final int blackValue = -16000000;
    private static final int brightnessValue = 60;
    private static final int whiteValue = -13000000;

    public static Observable<Bitmap> sort(final FileInputStream fis, final SortingMode sortingMode) {
        return Observable.create(new ObservableOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(ObservableEmitter<Bitmap> emitter) throws Exception {
                int height = CameraPresenter.IMAGE_SIZE_PX;
                int width = CameraPresenter.IMAGE_SIZE_PX;

                int currentRow = 0;
                int currentColumn = 0;

                System.out.println(sortingMode);

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
                    pixels = sortColumns(pixels, currentColumn, width, height, sortingMode);
                    currentColumn++;
                }

                // Loop through rows
                while (currentRow < height - 1) {
                    pixels = sortRow(pixels, currentRow, width, sortingMode);
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

    static int[] sortRow(int[] pixels, int currentRow, int width, SortingMode sortingMode) {
        int y = currentRow;
        int x = 0;
        int xe = 0;
        while (xe < width - 1) {
            switch (sortingMode) {
                case BLACK:
                    x = getFirstNotBlackX(pixels, x, y, width);
                    xe = getNextBlackX(pixels, x, y, width);
                    break;
                case BRIGHTNESS:
                    x = getFirstBrightX(pixels, x, y, width);
                    xe = getNextDarkX(pixels, x, y, width);
                    break;
                case WHITE:
                    x = getFirstNotWhiteX(pixels, x, y, width);
                    xe = getNextWhiteX(pixels, x, y, width);
                    break;
                default:
                    break;
            }
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

    static int[] sortColumns(int[] pixels, int currentColumn, int width, int height, SortingMode sortingMode) {
        int x = currentColumn;
        int y = 0;
        int ye = 0;
        while (ye < height - 1) {
            switch (sortingMode) {
                case BLACK:
                    y = getFirstNotBlackY(pixels, x, y, width, height);
                    ye = getNextBlackY(pixels, x, y, width, height);
                    break;
                case BRIGHTNESS:
                    y = getFirstBrightY(pixels, x, y, width, height);
                    ye = getNextDarkY(pixels, x, y, width, height);
                    break;
                case WHITE:
                    y = getFirstNotWhiteY(pixels, x, y, width, height);
                    ye = getNextWhiteY(pixels, x, y, width, height);
                    break;
                default:
                    break;
            }
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

    static int getFirstBrightX(int[] pixels, int x, int y, int width) {
        while (brightness(pixels[x + y * width]) < brightnessValue) {
            x++;
            if (x >= width) {
                return -1;
            }
        }
        return x;
    }

    static int getNextDarkX(int[] pixels, int x, int y, int width) {
        x++;
        while (brightness(pixels[x + y * width]) > brightnessValue) {
            x++;
            if (x >= width) {
                return width - 1;
            }
        }
        return x - 1;
    }

    static int getFirstBrightY(int[] pixels, int x, int y, int width, int height) {
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

    static int getNextDarkY(int[] pixels, int x, int y, int width, int height) {
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

    //---[ Black ]------------------------------------------------------------------------------------------------------

    static int getFirstNotBlackX(int[] pixels, int x, int y, int width) {
        while (pixels[x + y * width] < blackValue) {
            x++;
            if (x >= width) {
                return -1;
            }
        }
        return x;
    }

    static int getNextBlackX(int[] pixels, int x, int y, int width) {
        x++;
        while (pixels[x + y * width] > blackValue) {
            x++;
            if (x >= width) {
                return width - 1;
            }
        }
        return x - 1;
    }

    static int getFirstNotBlackY(int[] pixels, int x, int y, int width, int height) {
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

    static int getNextBlackY(int[] pixels, int x, int y, int width, int height) {
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

    //---[ White ]------------------------------------------------------------------------------------------------------

    static int getFirstNotWhiteX(int[] pixels, int x, int y, int width) {
        while (pixels[x + y * width] < whiteValue) {
            x++;
            if (x >= width) {
                return -1;
            }
        }
        return x;
    }

    static int getNextWhiteX(int[] pixels, int x, int y, int width) {
        x++;
        while (pixels[x + y * width] > whiteValue) {
            x++;
            if (x >= width) {
                return width - 1;
            }
        }
        return x - 1;
    }

    static int getFirstNotWhiteY(int[] pixels, int x, int y, int width, int height) {
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

    static int getNextWhiteY(int[] pixels, int x, int y, int width, int height) {
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

    //------------------------------------------------------------------------------------------------------------------

    private static int brightness(int pixelColor) {
        int r = Color.red(pixelColor);
        int g = Color.green(pixelColor);
        int b = Color.blue(pixelColor);
        return (r + r + r + b + g + g + g + g) >> 3;
    }
}

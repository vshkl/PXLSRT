package by.vshkl.pxlsrt.mvp.view;

import android.graphics.Bitmap;

import com.arellomobile.mvp.MvpView;

import by.vshkl.pxlsrt.mvp.model.SortingMode;

public interface PreviewView extends MvpView {

    void setPreviewImage(Bitmap bitmap);

    void retakePicture();

    void proceedToProcessing(String filename, SortingMode sortingMode, int color);

    void removeTempFile(String filename);

    void showColorSeekBar();

    void hideColorSeekBar();

    void logSortingMode(SortingMode sortingMode);
}

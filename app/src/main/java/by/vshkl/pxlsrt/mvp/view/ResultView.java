package by.vshkl.pxlsrt.mvp.view;

import android.graphics.Bitmap;

import com.arellomobile.mvp.MvpView;

import by.vshkl.pxlsrt.mvp.model.SortingMode;

public interface ResultView extends MvpView {

    void showProgress();

    void hideProgress();

    void showButtons();

    void hideSaveButton();

    void setResultPicture(Bitmap bitmap);

    void setResultMessage(String timeDelta);

    void retakePicture();

    void editPicture(String filename);

    void savePicture(String directory);

    void removeTempFile(String filename);

    void scanMediaStore(String path);

    void logProcessingTime(SortingMode sortingMode, double processingTime);
}

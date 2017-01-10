package by.vshkl.pxlsrt.mvp.view;

import android.graphics.Bitmap;

import com.arellomobile.mvp.MvpView;

public interface ResultView extends MvpView {

    void showProgress();

    void hideProgress();

    void enableSaveButton();

    void disableSaveButton();

    void setResultPicture(Bitmap bitmap);

    void retakePicture(String filename);

    void savePicture(String filename, String directory);

    void removeTempFile(String filename);
}

package by.vshkl.pxlsrt.mvp.view;

import android.graphics.Bitmap;

import com.arellomobile.mvp.MvpView;

public interface PreviewView extends MvpView {

    void setPreviewImage(Bitmap bitmap);

    void retakePicture();

    void proceedToProcessing(String filename);
}

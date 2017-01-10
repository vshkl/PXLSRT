package by.vshkl.pxlsrt.mvp.presenter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.io.FileInputStream;
import java.io.IOException;

import by.vshkl.pxlsrt.mvp.view.PreviewView;
import io.reactivex.disposables.Disposable;

@InjectViewState
public class PreviewPresenter extends MvpPresenter<PreviewView> {

    private Disposable disposable;

    public void onStop() {
        if (disposable != null) {
            disposable.dispose();
        }
    }

    public void setPreviewImage(FileInputStream fis) {
        Bitmap bitmap = BitmapFactory.decodeStream(fis);
        try {
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        getViewState().setPreviewImage(bitmap);
    }

    public void retakePicture() {
        getViewState().retakePicture();
    }

    public void proceedToProcessing() {
        getViewState().proceedToProcessing();
    }
}

package by.vshkl.pxlsrt.mvp.presenter;

import android.graphics.Bitmap;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.io.FileInputStream;

import by.vshkl.pxlsrt.core.utils.BitmapUtils;
import by.vshkl.pxlsrt.mvp.model.SortingMode;
import by.vshkl.pxlsrt.mvp.view.PreviewView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class PreviewPresenter extends MvpPresenter<PreviewView> {

    private Disposable disposable;
    private SortingMode sortingMode = SortingMode.BRIGHTNESS;

    public void onStop() {
        if (disposable != null) {
            disposable.dispose();
        }
    }

    public void setSortingMode(SortingMode sortingMode) {
        this.sortingMode = sortingMode;
    }

    public void setPreviewImage(FileInputStream fis) {
        disposable = BitmapUtils.getStoredBitmap(fis)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Bitmap>() {
                    @Override
                    public void accept(Bitmap bitmap) throws Exception {
                        getViewState().setPreviewImage(bitmap);
                    }
                });
    }

    public void retakePicture() {
        getViewState().retakePicture();
    }

    public void proceedToProcessing() {
        getViewState().proceedToProcessing();
    }
}

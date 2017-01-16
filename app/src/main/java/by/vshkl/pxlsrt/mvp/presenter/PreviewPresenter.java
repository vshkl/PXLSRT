package by.vshkl.pxlsrt.mvp.presenter;

import android.graphics.Bitmap;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.io.FileInputStream;

import by.vshkl.pxlsrt.core.utils.TempStorageUtils;
import by.vshkl.pxlsrt.mvp.model.SortingMode;
import by.vshkl.pxlsrt.mvp.view.PreviewView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class PreviewPresenter extends MvpPresenter<PreviewView> {

    private Disposable disposable;
    private String filename;
    private SortingMode sortingMode = SortingMode.BRIGHTNESS;
    private int color = 180;

    public void onDestroy() {
        getViewState().removeTempFile(filename);
        if (disposable != null) {
            disposable.dispose();
        }
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setSortingMode(SortingMode sortingMode) {
        this.sortingMode = sortingMode;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setPreviewImage(FileInputStream fis) {
        disposable = TempStorageUtils.getTempBitmap(fis)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Bitmap>() {
                    @Override
                    public void accept(Bitmap bitmap) throws Exception {
                        getViewState().setPreviewImage(bitmap);
                    }
                });
    }

    public void showColorSeekBar() {
        getViewState().showColorSeekBar();
    }

    public void hideColorSeekBar() {
        getViewState().hideColorSeekBar();
    }

    public void retakePicture() {
        getViewState().removeTempFile(filename);
        getViewState().retakePicture();
    }

    public void proceedToProcessing() {
        if (filename != null) {
            getViewState().proceedToProcessing(filename, sortingMode, color);
        }
    }

    public void logSortingMode(SortingMode sortingMode) {
        getViewState().logSortingMode(sortingMode);
    }
}

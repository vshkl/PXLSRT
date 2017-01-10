package by.vshkl.pxlsrt.mvp.presenter;

import android.graphics.Bitmap;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.io.FileInputStream;

import by.vshkl.pxlsrt.core.PixelSort;
import by.vshkl.pxlsrt.mvp.model.SortingMode;
import by.vshkl.pxlsrt.mvp.view.ResultView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class ResultPresenter extends MvpPresenter<ResultView> {

    private Disposable disposable;
    private String filename;
    private SortingMode sortingMode;

    public void onDestroy() {
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

    public void processImage(FileInputStream fis) {
        disposable = PixelSort.sort(fis)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Bitmap>() {
                    @Override
                    public void accept(Bitmap bitmap) throws Exception {
                        getViewState().hideProgress();
                        getViewState().enableSaveButton();
                        getViewState().setResultPicture(bitmap);
                    }
                });

    }

    public void retakePicture() {
        getViewState().retakePicture();
    }

    public void savePicture() {
        getViewState().savePicture();
    }

}

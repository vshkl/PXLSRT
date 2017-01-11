package by.vshkl.pxlsrt.mvp.presenter;

import android.graphics.Bitmap;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import by.vshkl.pxlsrt.core.PixelSort;
import by.vshkl.pxlsrt.core.utils.TempStorageUtils;
import by.vshkl.pxlsrt.mvp.model.SortingMode;
import by.vshkl.pxlsrt.mvp.view.ResultView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class ResultPresenter extends MvpPresenter<ResultView> {

    private static final String OUTPUT_DIRECTORY = "PXLSRT";
    private Disposable disposable;
    private String filename;
    private SortingMode sortingMode;

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

    public void processImage(FileInputStream fis) {
        disposable = PixelSort.sort(fis, sortingMode)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Bitmap>() {
                    @Override
                    public void accept(Bitmap bitmap) throws Exception {
                        getViewState().hideProgress();
                        getViewState().setResultPicture(bitmap);
                        getViewState().showButtons();
                    }
                });
    }

    public void saveResultPicture(FileOutputStream fos, Bitmap bitmap) {
        disposable = TempStorageUtils.saveResultPicture(fos, bitmap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(new Function<Throwable, Boolean>() {
                    @Override
                    public Boolean apply(Throwable throwable) throws Exception {
                        return null;
                    }
                })
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            getViewState().hideSaveButton();
                        }
                    }
                });
    }

    public void retakePicture() {
        getViewState().removeTempFile(filename);
        getViewState().retakePicture(filename);
    }

    public void editPicture() {
        getViewState().editPicture(filename);
    }

    public void savePicture() {
        getViewState().removeTempFile(filename);
        getViewState().savePicture(filename, OUTPUT_DIRECTORY);
    }
}

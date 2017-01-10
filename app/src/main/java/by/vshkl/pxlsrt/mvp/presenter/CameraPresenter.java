package by.vshkl.pxlsrt.mvp.presenter;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.io.FileOutputStream;

import by.vshkl.pxlsrt.core.utils.TempStorageUtils;
import by.vshkl.pxlsrt.mvp.view.CameraView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class CameraPresenter extends MvpPresenter<CameraView> {

    public static final int IMAGE_SIZE_PX = 1000;

    private Disposable disposable;

    public void onDestroy() {
        if (disposable != null) {
            disposable.dispose();
        }
    }

    public void toggleGrid() {
        getViewState().toggleGrid();
    }

    public void toggleCamera() {
        getViewState().toggleCamera();
    }

    public void toggleFlash() {
        getViewState().toggleFlash();
    }

    public void takePicture() {
        getViewState().takePicture();
    }

    public void processPicture(FileOutputStream fos, String fname, byte[] data) {
        disposable = TempStorageUtils.saveTempBitmap(fos, fname, data)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String string) throws Exception {
                        getViewState().sendPictureData(string);
                    }
                });
    }
}

package by.vshkl.pxlsrt.mvp.presenter;

import android.net.Uri;

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

    private Disposable disposable;

    public void onDestroy() {
        if (disposable != null) {
            disposable.dispose();
        }
    }

    public void checkPermissions() {
        getViewState().checkPermissions();
    }

    public void showPermissionsRationale() {
        getViewState().showPermissionsRationale();
    }

    public void requestPermissions() {
        getViewState().requestPermissions();
    }

    public void showPermissionsMessage(int resId) {
        getViewState().showPermissionMessage(resId);
    }

    public void showAppSystemSettings() {
        getViewState().showAppSystemSettings();
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

    public void openGallery() {
        getViewState().openGallery();
    }

    public void openCropper(String image) {
        getViewState().openCropper(image);
    }

    public void takePicture() {
        getViewState().takePicture();
    }

    public void openSettings() {
        getViewState().openSettings();
    }

    public void processPicture(FileOutputStream fos, String fname, byte[] data, int resolution) {
        disposable = TempStorageUtils.saveTempBitmap(fos, fname, data, resolution)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String string) throws Exception {
                        getViewState().sendPictureData(string);
                    }
                });
    }

    public void processPicture(FileOutputStream fos, String fname, Uri uri) {
        disposable = TempStorageUtils.saveTempBitmap(fos, fname, uri)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String string) throws Exception {
                        getViewState().sendPictureData(string);
                    }
                });
    }

    public void cleanTempFiles() {
        getViewState().cleanTempFiles();
    }
}

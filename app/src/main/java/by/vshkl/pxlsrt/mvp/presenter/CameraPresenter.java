package by.vshkl.pxlsrt.mvp.presenter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.io.FileOutputStream;
import java.io.IOException;

import by.vshkl.pxlsrt.mvp.view.CameraView;
import io.reactivex.disposables.Disposable;

@InjectViewState
public class CameraPresenter extends MvpPresenter<CameraView> {

    private static final String TAG = CameraPresenter.class.getSimpleName();
//    private static final int IMAGE_SIZE_PX = 1024;
    public static final int IMAGE_SIZE_PX = 1000;

    private Disposable disposable;

    public void onStop() {
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

    //TODO: move this stuff to separate thread via RxJava2
    public void processPicture(FileOutputStream fos, String filename, byte[] data) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getWidth());
        bitmap = Bitmap.createScaledBitmap(bitmap, IMAGE_SIZE_PX, IMAGE_SIZE_PX, false);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        try {
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        bitmap.recycle();
        getViewState().sendPictureData(filename);
    }
}

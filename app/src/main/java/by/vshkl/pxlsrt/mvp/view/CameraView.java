package by.vshkl.pxlsrt.mvp.view;

import com.arellomobile.mvp.MvpView;

public interface CameraView extends MvpView {

    void checkPermissions();

    void showPermissionMessage(int resId);

    void showPermissionsRationale();

    void requestPermissions();

    void showAppSystemSettings();

    void toggleGrid();

    void toggleCamera();

    void toggleFlash();

    void openGallery();

    void openCropper(String image);

    void takePicture();

    void openSettings();

    void sendPictureData(String filename);

    void cleanTempFiles();
}

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

    void takePicture();

    void sendPictureData(String filename);
}

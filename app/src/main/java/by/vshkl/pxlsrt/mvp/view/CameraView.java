package by.vshkl.pxlsrt.mvp.view;

import com.arellomobile.mvp.MvpView;

public interface CameraView extends MvpView {

    void toggleGrid();

    void toggleCamera();

    void toggleFlash();

    void takePicture();

    void sendPictureData(String filename);
}

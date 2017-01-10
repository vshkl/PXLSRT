package by.vshkl.pxlsrt.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.google.android.cameraview.CameraView;

import java.io.FileNotFoundException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import by.vshkl.pxlsrt.R;
import by.vshkl.pxlsrt.mvp.presenter.CameraPresenter;
import by.vshkl.pxlsrt.ui.customview.GridView;

public class CameraActivity extends MvpAppCompatActivity implements by.vshkl.pxlsrt.mvp.view.CameraView {

    private static final String FNAME_PREFIX = "PXLSRT_";

    @BindView(R.id.cv_camera) CameraView cvCamera;
    @BindView(R.id.gv_grid_overlay) GridView gvGridOverlay;
    @BindView(R.id.iv_grid) ImageView ivGrid;
    @BindView(R.id.iv_camera) ImageView ivCamera;
    @BindView(R.id.iv_flash) ImageView ivFlash;
    @InjectPresenter CameraPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);

        cvCamera.addCallback(new CameraView.Callback() {
            @Override
            public void onPictureTaken(CameraView cameraView, byte[] data) {
                super.onPictureTaken(cameraView, data);
                try {
                    String filename = FNAME_PREFIX + System.currentTimeMillis();
                    presenter.processPicture(openFileOutput(filename, Context.MODE_PRIVATE), filename, data);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        cvCamera.start();
    }

    @Override
    protected void onPause() {
        cvCamera.stop();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    //------------------------------------------------------------------------------------------------------------------

    @OnClick(R.id.iv_grid)
    void onGridToggleClicked() {
        presenter.toggleGrid();
    }

    @OnClick(R.id.iv_camera)
    void onCameraToggleClicked() {
        presenter.toggleCamera();
    }

    @OnClick(R.id.iv_flash)
    void onFlashToggleClicked() {
        presenter.toggleFlash();
    }

    @OnClick(R.id.iv_shutter)
    void onShutterClicked() {
        presenter.takePicture();
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void toggleGrid() {
        if (gvGridOverlay.getVisibility() == View.GONE) {
            gvGridOverlay.setVisibility(View.VISIBLE);
            ivGrid.setImageResource(R.drawable.ic_camera_grid_on);
        } else {
            gvGridOverlay.setVisibility(View.GONE);
            ivGrid.setImageResource(R.drawable.ic_camera_grid_off);
        }
    }

    @Override
    public void toggleCamera() {
        switch (cvCamera.getFacing()) {
            case CameraView.FACING_BACK:
                cvCamera.setFacing(CameraView.FACING_FRONT);
                ivCamera.setImageResource(R.drawable.ic_camera_front);
                break;
            case CameraView.FACING_FRONT:
                cvCamera.setFacing(CameraView.FACING_BACK);
                ivCamera.setImageResource(R.drawable.ic_camera_rear);
                break;
        }
    }

    @Override
    public void toggleFlash() {
        switch (cvCamera.getFlash()) {
            case CameraView.FLASH_OFF:
                cvCamera.setFlash(CameraView.FLASH_AUTO);
                ivFlash.setImageResource(R.drawable.ic_camera_flash_auto);
                break;
            case CameraView.FLASH_AUTO:
                cvCamera.setFlash(CameraView.FLASH_OFF);
                ivFlash.setImageResource(R.drawable.ic_camera_flash_off);
                break;
        }
    }

    @Override
    public void takePicture() {
        cvCamera.takePicture();
    }

    @Override
    public void sendPictureData(String filename) {
        Intent intent = new Intent(this, PreviewActivity.class);
        intent.putExtra(PreviewActivity.EXTRA_FILENAME, filename);
        startActivity(intent);
    }

}

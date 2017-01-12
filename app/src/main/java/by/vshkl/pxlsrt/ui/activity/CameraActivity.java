package by.vshkl.pxlsrt.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.google.android.cameraview.CameraView;
import com.tangxiaolv.telegramgallery.GalleryActivity;
import com.tangxiaolv.telegramgallery.GalleryConfig;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import by.vshkl.pxlsrt.R;
import by.vshkl.pxlsrt.mvp.presenter.CameraPresenter;
import by.vshkl.pxlsrt.ui.customview.GridView;

public class CameraActivity extends MvpAppCompatActivity implements by.vshkl.pxlsrt.mvp.view.CameraView {

    private static final int PERMISSION_REQUEST = 42;
    private static final int PICK_PIC_REQUEST = 1337;
    private static final String FNAME_PREFIX = "PXLSRT_";

    @BindView(R.id.rl_root) RelativeLayout rlRoot;
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

        presenter.cleanTempFiles();

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_PIC_REQUEST) {
                List<String> photos = (List<String>) data.getSerializableExtra(GalleryActivity.PHOTOS);
                presenter.openCropper(photos.get(0).toString());
            }
            if (requestCode == UCrop.REQUEST_CROP) {
                try {
                    String filename = FNAME_PREFIX + System.currentTimeMillis();
                    presenter.processPicture(
                            openFileOutput(filename, Context.MODE_PRIVATE), filename, UCrop.getOutput(data));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                cvCamera.start();
            } else {
                cvCamera.stop();
                presenter.showPermissionsMessage(R.string.permission_denied);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.checkPermissions();
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

    @OnClick(R.id.iv_gallery)
    void onGalleryClicked() {
        presenter.openGallery();
    }

    @OnClick(R.id.iv_shutter)
    void onShutterClicked() {
        presenter.takePicture();
    }

    @OnClick(R.id.iv_settings)
    void onSettingsClicked() {
        presenter.openSettings();
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void checkPermissions() {
        if (checkSelfPermissions()) {
            if (checkShouldShowRationale()) {
                presenter.showPermissionsRationale();
            } else {
                presenter.requestPermissions();
            }
        } else {
            cvCamera.start();
        }
    }

    @Override
    public void showPermissionMessage(int resId) {
        final Snackbar snackbar = Snackbar.make(rlRoot, resId, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.button_settings, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.showAppSystemSettings();
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    @Override
    public void showPermissionsRationale() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.permission_rationale)
                .setPositiveButton(R.string.button_allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissions();
                    }
                })
                .setNegativeButton(R.string.button_deny, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.showPermissionsMessage(R.string.permission_denied);
                    }
                })
                .show();
    }

    @Override
    public void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                PERMISSION_REQUEST);
    }

    @Override
    public void showAppSystemSettings() {
        final Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse("package:" + getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_NO_HISTORY
                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(intent);
    }

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
    public void openGallery() {
        GalleryConfig config = new GalleryConfig.Build()
                .singlePhoto(true)
                .filterMimeTypes(new String[]{"image/*"})
                .build();
        GalleryActivity.openActivity(this, PICK_PIC_REQUEST, config);
    }

    @Override
    public void openCropper(String image) {
        UCrop.Options options = new UCrop.Options();
        options.setToolbarTitle(getString(R.string.title_cropper));
        options.setCompressionQuality(100);
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setHideBottomControls(true);
        options.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        UCrop.of(Uri.fromFile(new File(image)), Uri.fromFile(new File(image)))
                .withOptions(options)
                .withAspectRatio(1, 1)
                .start(this);
    }

    @Override
    public void takePicture() {
        cvCamera.takePicture();
    }

    @Override
    public void openSettings() {

    }

    @Override
    public void sendPictureData(String filename) {
        Intent intent = new Intent(this, PreviewActivity.class);
        intent.putExtra(PreviewActivity.EXTRA_FILENAME, filename);
        startActivity(intent);
    }

    @Override
    public void cleanTempFiles() {
        new Runnable() {
            @Override
            public void run() {
                File[] files = getFilesDir().listFiles();
                for (File f : files) {
                    deleteFile(f.getName());
                }
            }
        }.run();
    }

    //------------------------------------------------------------------------------------------------------------------

    private boolean checkSelfPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkShouldShowRationale() {
        return ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)
                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE);
    }
}

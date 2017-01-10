package by.vshkl.pxlsrt.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import by.vshkl.pxlsrt.R;
import by.vshkl.pxlsrt.mvp.model.SortingMode;
import by.vshkl.pxlsrt.mvp.presenter.ResultPresenter;
import by.vshkl.pxlsrt.mvp.view.ResultView;

public class ResultActivity extends MvpAppCompatActivity implements ResultView {

    public static final String EXTRA_FILENAME = "ResultActivity.filename";
    public static final String EXTRA_SORTING_MODE = "ResultActivity.sorting_mode";

    @BindView(R.id.iv_result) ImageView ivResult;
    @BindView(R.id.pb_loading) AVLoadingIndicatorView pbProgress;
    @BindView(R.id.iv_save) ImageView ivSave;

    @InjectPresenter ResultPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ButterKnife.bind(this);
        processIntentExtra();
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    //------------------------------------------------------------------------------------------------------------------

    @OnClick(R.id.iv_retake)
    void onRetakeClicked() {
        presenter.retakePicture();
    }

    @OnClick(R.id.iv_save)
    void onSavePictureClicked() {
        if (ivSave.isEnabled()) {
            presenter.savePicture();
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void showProgress() {
        pbProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        pbProgress.setVisibility(View.GONE);
    }

    @Override
    public void enableSaveButton() {
        ivSave.setEnabled(true);
    }

    @Override
    public void disableSaveButton() {
        ivSave.setEnabled(false);
    }

    @Override
    public void setResultPicture(Bitmap bitmap) {
        ivResult.setImageBitmap(bitmap);
    }

    @Override
    public void retakePicture(String filename) {
        Intent intent = new Intent(this, CameraActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void savePicture(String filename, String directory) {
        processSavePicture(filename, directory);
    }

    @Override
    public void removeTempFile(String filename) {
        deleteFile(filename);
    }

    //------------------------------------------------------------------------------------------------------------------

    private void processIntentExtra() {
        Intent intent = getIntent();
        if (intent != null) {
            String filename = intent.getStringExtra(EXTRA_FILENAME);
            SortingMode sortingMode = (SortingMode) intent.getSerializableExtra(EXTRA_SORTING_MODE);
            if (filename != null && sortingMode != null) {
                try {
                    presenter.setFilename(filename);
                    presenter.setSortingMode(sortingMode);
                    presenter.processImage(this.openFileInput(filename));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void processSavePicture(String filename, String directory) {
        File file = new File(new StringBuilder()
                .append(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES))
                .append(File.separator)
                .append(directory)
                .append(File.separator)
                .append(filename)
                .append(".png")
                .toString());
        if (!file.getParentFile().exists()) {
            file.mkdirs();
        }
        try {
            presenter.saveResultPicture(
                    new FileOutputStream(file), ((BitmapDrawable) ivResult.getDrawable()).getBitmap());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package by.vshkl.pxlsrt.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.FileNotFoundException;

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
    void onProceedClicked() {
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
        deleteFile(filename);
        Intent intent = new Intent(this, CameraActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void savePicture() {
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
}

package by.vshkl.pxlsrt.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;

import java.io.FileNotFoundException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import by.vshkl.pxlsrt.R;
import by.vshkl.pxlsrt.mvp.model.SortingMode;
import by.vshkl.pxlsrt.mvp.presenter.PreviewPresenter;
import by.vshkl.pxlsrt.mvp.view.PreviewView;

public class PreviewActivity extends MvpAppCompatActivity implements PreviewView, OnCheckedChangeListener {

    public static final String EXTRA_FILENAME = "PreviewActivity.filename";

    @BindView(R.id.iv_preview) ImageView ivPreview;
    @BindView(R.id.rg_settings) RadioGroup rgSettings;
    @BindView(R.id.rb_black) RadioButton rbBlack;
    @BindView(R.id.rb_brightness) RadioButton rbBrightness;
    @BindView(R.id.rb_white) RadioButton rbWhite;

    @InjectPresenter PreviewPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        ButterKnife.bind(this);
        setupTypeface();
        rgSettings.setOnCheckedChangeListener(this);
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

    @OnClick(R.id.iv_proceed)
    void onProceedClicked() {
        presenter.proceedToProcessing();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_black:
                presenter.setSortingMode(SortingMode.BLACK);
                break;
            case R.id.rb_brightness:
                presenter.setSortingMode(SortingMode.BRIGHTNESS);
                break;
            case R.id.rb_white:
                presenter.setSortingMode(SortingMode.WHITE);
                break;
            case R.id.rb_hue:
                presenter.setSortingMode(SortingMode.HUE);
                break;
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void setPreviewImage(Bitmap bitmap) {
        ivPreview.setImageBitmap(bitmap);
    }

    @Override
    public void retakePicture(String filename) {
        Intent intent = new Intent(this, CameraActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void proceedToProcessing(String filename, SortingMode sortingMode) {
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra(ResultActivity.EXTRA_FILENAME, filename);
        intent.putExtra(ResultActivity.EXTRA_SORTING_MODE, sortingMode);
        startActivity(intent);
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
            if (filename != null) {
                try {
                    presenter.setFilename(filename);
                    presenter.setPreviewImage(this.openFileInput(filename));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setupTypeface() {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        rbBlack.setTypeface(typeface);
        rbBrightness.setTypeface(typeface);
        rbWhite.setTypeface(typeface);
    }
}

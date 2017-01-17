package by.vshkl.pxlsrt.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.rtugeek.android.colorseekbar.ColorSeekBar;

import java.io.FileNotFoundException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import by.vshkl.pxlsrt.R;
import by.vshkl.pxlsrt.mvp.model.SortingMode;
import by.vshkl.pxlsrt.mvp.presenter.PreviewPresenter;
import by.vshkl.pxlsrt.mvp.view.PreviewView;

public class PreviewActivity extends MvpAppCompatActivity implements PreviewView, OnCheckedChangeListener {

    public static final String EXTRA_FILENAME = "PreviewActivity.filename";

    @BindView(R.id.rl_root) RelativeLayout rlRoot;
    @BindView(R.id.iv_preview) ImageView ivPreview;
    @BindView(R.id.rg_settings) RadioGroup rgSettings;
    @BindView(R.id.rb_black) RadioButton rbBlack;
    @BindView(R.id.rb_brightness) RadioButton rbBrightness;
    @BindView(R.id.rb_white) RadioButton rbWhite;

    @InjectPresenter PreviewPresenter presenter;

    private ColorSeekBar sbColor;
    private View vSeekBarBacking;

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
                presenter.hideColorSeekBar();
                break;
            case R.id.rb_brightness:
                presenter.setSortingMode(SortingMode.BRIGHTNESS);
                presenter.hideColorSeekBar();
                break;
            case R.id.rb_white:
                presenter.setSortingMode(SortingMode.WHITE);
                presenter.hideColorSeekBar();
                break;
            case R.id.rb_hue:
                presenter.setSortingMode(SortingMode.COLOR);
                presenter.showColorSeekBar();
                break;
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void setPreviewImage(Bitmap bitmap) {
        ivPreview.setImageBitmap(bitmap);
    }

    @Override
    public void retakePicture() {
        Intent intent = new Intent(this, CameraActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void proceedToProcessing(String filename, SortingMode sortingMode, int color) {
        presenter.logSortingMode(sortingMode);
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra(ResultActivity.EXTRA_FILENAME, filename);
        intent.putExtra(ResultActivity.EXTRA_SORTING_MODE, sortingMode);
        intent.putExtra(ResultActivity.EXTRA_COLOR, color);
        startActivity(intent);
    }

    @Override
    public void removeTempFile(String filename) {
        deleteFile(filename);
    }

    @Override
    public void showColorSeekBar() {
        if (sbColor == null && vSeekBarBacking == null) {
            extractColors();
        } else {
            sbColor.setVisibility(View.VISIBLE);
            vSeekBarBacking.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideColorSeekBar() {
        if (sbColor != null && vSeekBarBacking != null) {
            sbColor.setVisibility(View.GONE);
            vSeekBarBacking.setVisibility(View.GONE);
        }
    }

    @Override
    public void logSortingMode(SortingMode sortingMode) {
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.param_sorting_mode), sortingMode.toString());
        FirebaseAnalytics.getInstance(getApplicationContext()).logEvent(getString(R.string.event_sorting_mode), bundle);
    }

    //------------------------------------------------------------------------------------------------------------------

    private void extractColors() {
        Palette.from(((BitmapDrawable) ivPreview.getDrawable()).getBitmap()).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                initializeColorSeekBar(palette);
            }
        });

    }

    private void initializeColorSeekBar(Palette palette) {
        int defaultColor = 0x000000;
        int[] colors = {
                palette.getDominantColor(defaultColor),
                palette.getVibrantColor(defaultColor),
                palette.getMutedColor(defaultColor)

        };

        int px = ivPreview.getHeight() - (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16,
                getResources().getDisplayMetrics());
        sbColor = new ColorSeekBar(PreviewActivity.this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, px, 0, 0);
        sbColor.setLayoutParams(params);
        sbColor.setBarHeight(2);
        sbColor.setColorSeeds(colors);
        sbColor.setBackground(ContextCompat.getDrawable(PreviewActivity.this, R.drawable.transparent));
        sbColor.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int colorBarPosition, int alphaBarPosition, int color) {
                presenter.setColor(color);
            }
        });

        px = ivPreview.getHeight() - (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1,
                getResources().getDisplayMetrics());
        int h = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
        vSeekBarBacking = new View(PreviewActivity.this);
        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, h);
        params.setMargins(0, px, 0, 0);
        vSeekBarBacking.setLayoutParams(params);
        GradientDrawable gradient = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors);
        gradient.setCornerRadius(0f);
        vSeekBarBacking.setBackground(gradient);

        rlRoot.addView(vSeekBarBacking);
        rlRoot.addView(sbColor);
    }

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

    private int[] a2l(List<Integer> list) {
        int[] array = new int[list.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = list.get(i);
        }
        return array;
    }
}

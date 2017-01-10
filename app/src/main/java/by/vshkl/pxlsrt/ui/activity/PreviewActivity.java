package by.vshkl.pxlsrt.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;

import java.io.FileNotFoundException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import by.vshkl.pxlsrt.R;
import by.vshkl.pxlsrt.mvp.presenter.PreviewPresenter;
import by.vshkl.pxlsrt.mvp.view.PreviewView;

public class PreviewActivity extends MvpAppCompatActivity implements PreviewView {

    public static final String EXTRA_FILENAME = "PreviewActivity.filename";
    private String filename;

    @BindView(R.id.iv_preview) ImageView ivPreview;

    @InjectPresenter PreviewPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        ButterKnife.bind(this);
        processIntentExtra();
    }

    @Override
    protected void onStop() {
        presenter.onStop();
        super.onStop();
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

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void setPreviewImage(Bitmap bitmap) {
        ivPreview.setImageBitmap(bitmap);
    }

    @Override
    public void retakePicture() {
        finish();
    }

    @Override
    public void proceedToProcessing() {
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra(ResultActivity.EXTRA_FILENAME, filename);
        startActivity(intent);
    }

    //------------------------------------------------------------------------------------------------------------------

    private void processIntentExtra() {
        Intent intent = getIntent();
        if (intent != null) {
            filename = intent.getStringExtra(EXTRA_FILENAME);
            if (filename != null) {
                try {
                    presenter.setPreviewImage(this.openFileInput(filename));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

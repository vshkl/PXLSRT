package by.vshkl.pxlsrt.ui.customview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

public class GridView extends View {

    private static final int NUM_ROWS = 3;
    private static final int NUM_COLS = 3;

    private Paint blackPaint = new Paint();

    public GridView(Context context) {
        super(context);
        initialize();
    }

    public GridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public GridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public GridView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize();
    }

    private void initialize() {
        blackPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        blackPaint.setStrokeWidth(3.0F);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);

        int width = getWidth();
        int height = getHeight();
        int widthStep = getWidth() / NUM_COLS;
        int heightStep = getHeight() / NUM_ROWS;

        for (int i = 1; i < NUM_COLS; i++) {
            for (int j = 1; j < NUM_ROWS; j++) {
                canvas.drawLine(0, j * heightStep, width, j * heightStep, blackPaint);
            }
            canvas.drawLine(i * widthStep, 0, i * widthStep, height, blackPaint);
        }

        super.onDraw(canvas);
    }
}

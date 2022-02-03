package gsshop.mobile.v2.home.shop.flexible.vip.common;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.style.ReplacementSpan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CustomBackgroundColorSpan extends ReplacementSpan {

    private int mColor = Color.parseColor("#dbf109");
    private float mHeight = 0;

    public CustomBackgroundColorSpan(int backgroundColor, float height) {
        mColor = backgroundColor;
        mHeight = height;
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
        return Math.round(paint.measureText(text, start, end));
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        RectF rect = new RectF(x, bottom - mHeight, x + measureText(paint, text, start, end), bottom);

        Paint paintNew = new Paint(paint);
        paintNew.setColor(mColor);
        canvas.drawRect(rect, paintNew);

        canvas.drawText(text, start, end, x, y, paint);
    }

    private float measureText(Paint paint, CharSequence text, int start, int end) {
        return paint.measureText(text, start, end);
    }
}

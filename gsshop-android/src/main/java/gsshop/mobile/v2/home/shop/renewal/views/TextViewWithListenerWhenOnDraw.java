package gsshop.mobile.v2.home.shop.renewal.views;

import android.content.Context;
import android.graphics.Canvas;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by gsshop on 2016. 9. 7..
 */
public class TextViewWithListenerWhenOnDraw extends AppCompatTextView {
    OnDrawListener mLisnter;
    public interface OnDrawListener {
        void onDraw(View view);
    }

    public TextViewWithListenerWhenOnDraw(Context context) {
        super(context);
    }

    public TextViewWithListenerWhenOnDraw(Context context, OnDrawListener listener) {
        super(context);
        mLisnter = listener;
    }

    public TextViewWithListenerWhenOnDraw(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnDrawListener (OnDrawListener listener) {
        mLisnter = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mLisnter != null) {
            mLisnter.onDraw(this);
        }
    }

}

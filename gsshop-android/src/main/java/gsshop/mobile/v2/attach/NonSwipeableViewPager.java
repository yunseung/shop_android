package gsshop.mobile.v2.attach;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * NonSwipeableViewPager
 */
@SuppressLint("ClickableViewAccessibility")
public class NonSwipeableViewPager extends ViewPager {

    /**
     * NonSwipeableViewPager
     *
     * @param context
     */
    public NonSwipeableViewPager(Context context) {
        super(context);
    }

    /**
     * NonSwipeableViewPager
     * @param context
     * @param attrs
     */
    public NonSwipeableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * onInterceptTouchEvent
     *
     * @param arg0
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        // Never allow swiping to switch between pages
        return false;
    }

    /**
     * onTouchEvent
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Never allow swiping to switch between pages
        return false;
    }
}

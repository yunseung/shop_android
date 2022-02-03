package gsshop.mobile.v2.home;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import fr.castorflex.android.verticalviewpager.VerticalViewPager;

/**
 * Created by azota on 2016-08-24.
 */
public class CustomVerticalViewPager extends VerticalViewPager {

    /**
     * enableSwipe
     */
    public boolean enableSwipe = true;

    /**
     * 생성자
     * @param context context
     */
    public CustomVerticalViewPager(Context context) {
        super(context);
    }

    /**
     * 생성자
     * @param context context
     * @param attrs attrs
     */

    // TODO: 2016. 11. 15.
    public CustomVerticalViewPager(Context context,AttributeSet attrs) {
        super(context,attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (!enableSwipe) {
            return false;
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!enableSwipe) {
            return false;
        }
        return super.onTouchEvent(event);
    }
}

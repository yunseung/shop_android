package gsshop.mobile.v2.menu.navigation;

import android.content.Context;
import androidx.drawerlayout.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import java.util.ArrayList;

/**
 * DrawerLayout에서 Horizontal스크롤시 적용영역을 지정
 */
public class DrawerLayoutHorizontalSupport extends DrawerLayout {

    private ArrayList<View> mArrExceptTouchView = new ArrayList<View>();
    private View mNavigationView;
    private ScrollView scrollView;

    public DrawerLayoutHorizontalSupport(Context context) {
        super(context);
    }

    public DrawerLayoutHorizontalSupport(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawerLayoutHorizontalSupport(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isInside(ev) && isDrawerOpen(mNavigationView))
            return false;
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * 스크롤 하려는영역이 mRecyclerView영역이면 스크롤 이벤트를 넘기지 않는다.
     * @param ev
     * @return
     */
    private boolean isInside(MotionEvent ev) { //check whether user touch recylerView or not
        boolean isInside = false;
        if(scrollView == null ){
            return false;
        }

        for (View item : mArrExceptTouchView) {
            int[] location = new int[2];
            item.getLocationOnScreen(location);
            int height = item.getHeight();
            isInside  = ev.getX() >= item.getLeft() && ev.getX() <= item.getRight() &&
                    location[1] <= ev.getY()
                    && (location[1] + height) >= ev.getY();

            if(isInside){
                break;
            }
        }

        return isInside;
    }

    public void setScrollInsideView(View navigationView,
                                    ScrollView scrollView) {
        this.scrollView = scrollView;
        mNavigationView = navigationView;
    }

    public void addCheckTouchView (View view) {
        mArrExceptTouchView.add(view);
    }

}
package gsshop.mobile.v2.home;

import androidx.viewpager.widget.ViewPager;

/**
 * PageIndicator
 */
public interface PageIndicator extends ViewPager.OnPageChangeListener {
    /**
     * 뷰페이저에 바인딩.
     * @param view view
     */
    void setViewPager(ViewPager view);

    /**
     * 뷰페이저에 바인딩
     * @param view view
     * @param initialPosition initialPosition
     */
    void setViewPager(ViewPager view, int initialPosition);

    /**
     * <p>뷰페이저및 인디케이터 둘다 현재페이지 설정
     * @param item item
     */
    void setCurrentItem(int item);

    /**
     * 전달된 이벤트를 수신하는 페이지 변경 리스너를 설정
     * @param listener listener
     */
    void setOnPageChangeListener(ViewPager.OnPageChangeListener listener);

    /**
     * Fragment 변경 리스너
     */
    void notifyDataSetChanged();
}

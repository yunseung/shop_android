package gsshop.mobile.v2.menu.navigation;

import android.content.Context;
import android.os.Build;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gsshop.mocha.ui.util.ViewUtils;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.library.viewpager.InfinitePagerAdapter;
import gsshop.mobile.v2.library.viewpager.InfiniteViewPager;
import gsshop.mobile.v2.util.DisplayUtils;
import roboguice.util.Ln;

/**
 * 네비게이션 카테고리 레이아웃 뷰
 */
public class ThemeLayout extends LinearLayout {

    private Context mContext;

    /**
     * 테마관 카드 리스트 페이지
     */
    private InfiniteViewPager mViewPagerCardList;

    private LinearLayout mLayoutPage;

    private boolean mIsInfinityPage = true;

    private static int mScrolledPosition = 0;

    // page를 기억하고 있다가 해당 페이지 보여주도록.
//    private static int thisPage;

    public ThemeLayout(Context context) {
        this(context, null);
    }

    public ThemeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ThemeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mViewPagerCardList = (InfiniteViewPager)findViewById(R.id.viewPager_theme);
        mLayoutPage = (LinearLayout) findViewById(R.id.layout_page_img);
    }

    private void initView(Context context) {
        mContext = context;
    }

    public void setView(CateContentList item) {
//        mAdapterGSXBrand = new AdapterGSXBrand(itemList);
        try {
            TextView textTitle = (TextView) findViewById(R.id.txt_navi_theme_title);
            textTitle.setText(item.title);
        }
        catch (NullPointerException e) {
            Ln.e(e.getMessage());
        }

        mViewPagerCardList.requestLayout();

        final ThemePagerAdapter basicPageAdapter = new ThemePagerAdapter(mContext, item.subContentList);
        final InfinitePagerAdapter wrappedAdapter = new InfinitePagerAdapter(
                new ThemePagerAdapter(mContext, item.subContentList));

        if (item.subContentList.size() > ThemePagerAdapter.THEME_MENU_PAGE_ADAPTER_SIZE) {
            mViewPagerCardList.setAdapter(wrappedAdapter);
            ViewUtils.showViews(mLayoutPage);
            mIsInfinityPage = true;
        }
        else {
            mViewPagerCardList.setAdapter(basicPageAdapter);
            ViewUtils.hideViews(mLayoutPage);
            mIsInfinityPage = false;
        }
//        DisplayUtils.resizeAtViewToScreen(context, viewPager);

        mViewPagerCardList.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Ln.d(getClass().getName() + " onPageScrolled : " + position);
            }
            @Override
            public void onPageSelected(int position) {
                Ln.d(getClass().getName() + "onPageSelected : " + position);
                if (mIsInfinityPage) {
                    int virtualPosition = position % wrappedAdapter.getRealCount();
                    // Position이 InfinityPagingView여서 실제 Page와 다르다.
//                    thisPage = virtualPosition;
                    ViewPagerSelected(mContext, virtualPosition, wrappedAdapter.getRealCount());
                }
                else {
                    ViewPagerSelected(mContext, position, basicPageAdapter.getCount());
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
                Ln.d("onPageScrollStateChanged : " + state);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            mViewPagerCardList.setAccessibilityDelegate(new AccessibilityDelegate() {
                @Override
                public void onPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
                    super.onPopulateAccessibilityEvent(host, event);

                    if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_SELECTED ||
                            event.getEventType() == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
                        event.setContentDescription("page" + wrappedAdapter.getRealCount());
                    }
                }
            });
        }

//        mViewPagerCardList.setCurrentItem(thisPage);

//		int virtualPosition = position % wrappedAdapter.getRealCount();
        ViewPagerSelected(mContext, 0, wrappedAdapter.getRealCount());
    }

    private void ViewPagerSelected(Context context, int position, int listCnt) {
        mLayoutPage.removeAllViews();
        for ( int bottomImgNum = 0; bottomImgNum < listCnt; bottomImgNum ++ ) {
            LayoutParams params = new LayoutParams(
                    DisplayUtils.convertDpToPx(context, 7), DisplayUtils.convertDpToPx(context, 7));
            if (bottomImgNum != 0)
                params.leftMargin = DisplayUtils.convertDpToPx(context, 9);

            ImageView imgTemp = new ImageView(context);
            if (bottomImgNum == position)
                imgTemp.setBackgroundResource(R.drawable.bg_navigation_partner_dot_green);
            else
                imgTemp.setBackgroundResource(R.drawable.bg_navigation_partner_dot_dim);

            imgTemp.setLayoutParams(params);
            mLayoutPage.addView(imgTemp);
        }
    }

    public View getPager() {
        return mViewPagerCardList;
    }

    public void setPosition(int position) {
        mScrolledPosition = position;
    }

    public void savePosition() {
        mScrolledPosition = mViewPagerCardList.getCurrentItem();
    }

    public void setSavedPosition() {
        int position = mViewPagerCardList.getRealCurrentItem();

//        InfinitePagerAdapter infAdapter = (InfinitePagerAdapter) mViewPagerCardList.getAdapter();
        int movePosition = mScrolledPosition - mViewPagerCardList.getCurrentItem();

        mViewPagerCardList.setRealCurrentItem(position + movePosition, false);
    }
}

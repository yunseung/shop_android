/**
 * Copyright (C) 2015 nshmura
 * Copyright (C) 2015 The Android Open Source Project
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gsshop.mobile.v2.home;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.util.DisplayUtils;

import static gsshop.mobile.v2.home.RecyclerTabAdapter.tabViews;

/**
 * 상단 탭메뉴 (무한루핑)
 */
public class RecyclerTabLayout extends RecyclerView {

    /**
     * Touch 시 Background가 표시되게 하려면 변수 IS_SET_TOUCH_BACK 을 true로 설정.
     */
    public static final boolean IS_SET_TOUCH_BACK = false;

    protected static final long DEFAULT_SCROLL_DURATION = 200;
    protected static final float DEFAULT_POSITION_THRESHOLD = 0.6f;
    protected static final float POSITION_THRESHOLD_ALLOWABLE = 0.001f;

    protected Paint mIndicatorPaint;
    protected int mTabBackgroundResId;
    protected int mTabOnScreenLimit;
    protected int mTabMinWidth;
    protected int mTabMaxWidth;
    protected int mTabTextAppearance;
    protected int mTabSelectedTextColor;
    protected boolean mTabSelectedTextColorSet;
    protected int mTabPaddingStart;
    protected int mTabPaddingTop;
    protected int mTabPaddingEnd;
    protected int mTabPaddingBottom;
    protected int mIndicatorHeight;

    protected LinearLayoutManager mLinearLayoutManager;
    protected RecyclerOnScrollListener mRecyclerOnScrollListener;
    protected ViewPager mViewPager;
    protected Adapter<?> mAdapter;

    protected int mIndicatorPosition;
    protected int mIndicatorGap;
    protected int mIndicatorScroll;
    private int mOldPosition;
    private int mOldScrollOffset;
    protected float mOldPositionOffset;
    protected float mPositionThreshold;
    protected boolean mRequestScrollToTab;
    protected boolean mScrollEanbled;

    private String mStrSelectedItem;

    /**
     * 사용자가 클릭한 원래 위치
     */
    private int mOriIndicatorPosition;
    private int mOriIndicatorPosition2;

    public RecyclerTabLayout(Context context) {
        this(context, null);
    }

    public RecyclerTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecyclerTabLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setWillNotDraw(false);
        mIndicatorPaint = new Paint();
        getAttributes(context, attrs, defStyle);
        mLinearLayoutManager = new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollHorizontally() {
                return mScrollEanbled;
            }
        };
        mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        setLayoutManager(mLinearLayoutManager);
        setItemAnimator(null);
        mPositionThreshold = DEFAULT_POSITION_THRESHOLD;

        // 페이지를 넘길 때 마다 전체 뷰페이지 크기 (최대 21억~) 및 현재 페이지를 읽어주는 문제가 존재.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            this.setAccessibilityDelegate(new View.AccessibilityDelegate() {
                @Override
                public void onPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
                    super.onPopulateAccessibilityEvent(host, event);
                    event.setContentDescription("");
                }
            });
        }

//        mTabTextColor = context.getResources().getColor(R.color.tab_indicator_text_selected);
    }

    private void getAttributes(Context context, AttributeSet attrs, int defStyle) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.rtl_RecyclerTabLayout,
                defStyle, R.style.rtl_RecyclerTabLayout);
        setIndicatorColor(a.getColor(R.styleable
                .rtl_RecyclerTabLayout_rtl_tabIndicatorColor, 0));
        setIndicatorHeight(a.getDimensionPixelSize(R.styleable
                .rtl_RecyclerTabLayout_rtl_tabIndicatorHeight, 0));

        mTabTextAppearance = a.getResourceId(R.styleable.rtl_RecyclerTabLayout_rtl_tabTextAppearance,
                R.style.rtl_RecyclerTabLayout_Tab);

        mTabPaddingStart = mTabPaddingTop = mTabPaddingEnd = mTabPaddingBottom = a
                .getDimensionPixelSize(R.styleable.rtl_RecyclerTabLayout_rtl_tabPadding, 0);
        mTabPaddingStart = a.getDimensionPixelSize(
                R.styleable.rtl_RecyclerTabLayout_rtl_tabPaddingStart, mTabPaddingStart);
        mTabPaddingTop = a.getDimensionPixelSize(
                R.styleable.rtl_RecyclerTabLayout_rtl_tabPaddingTop, mTabPaddingTop);
        mTabPaddingEnd = a.getDimensionPixelSize(
                R.styleable.rtl_RecyclerTabLayout_rtl_tabPaddingEnd, mTabPaddingEnd);
        mTabPaddingBottom = a.getDimensionPixelSize(
                R.styleable.rtl_RecyclerTabLayout_rtl_tabPaddingBottom, mTabPaddingBottom);

        if (a.hasValue(R.styleable.rtl_RecyclerTabLayout_rtl_tabSelectedTextColor)) {
            mTabSelectedTextColor = a
                    .getColor(R.styleable.rtl_RecyclerTabLayout_rtl_tabSelectedTextColor, 0);
            mTabSelectedTextColorSet = true;
        }

        mTabOnScreenLimit = a.getInteger(
                R.styleable.rtl_RecyclerTabLayout_rtl_tabOnScreenLimit, 0);
        if (mTabOnScreenLimit == 0) {
            mTabMinWidth = a.getDimensionPixelSize(
                    R.styleable.rtl_RecyclerTabLayout_rtl_tabMinWidth, 0);
            mTabMaxWidth = a.getDimensionPixelSize(
                    R.styleable.rtl_RecyclerTabLayout_rtl_tabMaxWidth, 0);
        }

        mTabBackgroundResId = a
                .getResourceId(R.styleable.rtl_RecyclerTabLayout_rtl_tabBackground, 0);
        mScrollEanbled = a.getBoolean(R.styleable.rtl_RecyclerTabLayout_rtl_scrollEnabled, true);
        a.recycle();
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mRecyclerOnScrollListener != null) {
            removeOnScrollListener(mRecyclerOnScrollListener);
            mRecyclerOnScrollListener = null;
        }
        super.onDetachedFromWindow();
    }

    /**
     * 사용자가 클릭한 위치 정보를 세팅한다.
     *
     * @param oriIndicatorPosition 위치정보
     */
    public void setOriIndicatorPosition(int oriIndicatorPosition) {
        mOriIndicatorPosition = oriIndicatorPosition;
    }

    /**
     * 사용자가 터치한 위치 정보를 세팅한다.
     * @param touchedPosition
     */
    public void setTouchedPosition(int touchedPosition) {
        View selectedView = mLinearLayoutManager.findViewByPosition(touchedPosition);
        selectedView.setBackgroundColor(Color.parseColor("#14000000"));
    }

    public void setTouchDown(float touceX) {
        for (int i=0; i<mLinearLayoutManager.getChildCount(); i++) {
            if (mLinearLayoutManager.getChildAt(i).getX() < touceX &&
                    touceX < mLinearLayoutManager.getChildAt(i).getX() + mLinearLayoutManager.getChildAt(i).getWidth()) {
                View selectedView = mLinearLayoutManager.getChildAt(i);
                selectedView.setBackgroundColor(Color.parseColor("#14000000"));
            }
        }
    }

    public void setTouchUp() {
        for (int i=0; i<mLinearLayoutManager.getChildCount(); i++) {
            View selectedView = mLinearLayoutManager.getChildAt(i);
            selectedView.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    public void setIndicatorColor(int color) {
        mIndicatorPaint.setColor(color);
    }

    public void setIndicatorHeight(int indicatorHeight) {
        mIndicatorHeight = indicatorHeight;
    }

    public void setAutoSelectionMode(boolean autoSelect) {
        if (mRecyclerOnScrollListener != null) {
            removeOnScrollListener(mRecyclerOnScrollListener);
            mRecyclerOnScrollListener = null;
        }
        if (autoSelect) {
            mRecyclerOnScrollListener = new RecyclerOnScrollListener(this, mLinearLayoutManager);
            addOnScrollListener(mRecyclerOnScrollListener);
        }
    }

    public void setPositionThreshold(float positionThreshold) {
        mPositionThreshold = positionThreshold;
    }

    public void setUpWithAdapter(RecyclerTabLayout.Adapter<?> adapter) {
        mAdapter = adapter;
        mViewPager = adapter.getViewPager();

        //참조용 리사이클러탭 세팅
        ((RecyclerTabAdapter)adapter).setRecyclerTabLayout(this);
        if (mViewPager.getAdapter() == null) {
            throw new IllegalArgumentException("ViewPager does not have a PagerAdapter set");
        }
        mViewPager.addOnPageChangeListener(new ViewPagerOnPageChangeListener(this));

        setAdapter(adapter);
        scrollToTab(mViewPager.getCurrentItem());

        addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //매장내에서 링크를 통한 탭이동시 바인딩 이전의 아이템은 RecyclerTabLayout 내에 selectedView 값이 null 이기 때문에
                //중앙정렬이 되지 않는 현상이 있음. 해결방안으로 바인딩된 아이템을 oriIndicatorPosition에 저장해두고 사용함.
                mOriIndicatorPosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
            }
        });
    }

    protected void startAnimation(final int position) {

        float distance = 1;

        View view = mLinearLayoutManager.findViewByPosition(position);
        if (view != null) {
            float currentX = view.getX() + view.getMeasuredWidth() / 2.f;
            float centerX = getMeasuredWidth() / 2.f;
            distance = Math.abs(centerX - currentX) / view.getMeasuredWidth();
        }

        ValueAnimator animator;
        if (position < mIndicatorPosition) {
            animator = ValueAnimator.ofFloat(distance, 0);
        } else {
            animator = ValueAnimator.ofFloat(-distance, 0);
        }
        animator.setDuration(DEFAULT_SCROLL_DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                scrollToTab(position, (float) animation.getAnimatedValue(), true);
            }
        });
        animator.start();
    }

    public void scrollToTab(int position) {
        position = position % mAdapter.getItemCount();
        scrollToTab(position, 0, false);
        mAdapter.setCurrentIndicatorPosition(position);
        mAdapter.notifyDataSetChanged();
    }

    protected void scrollToTab(int position, float positionOffset, boolean fitIndicator) {
        position = position % mAdapter.getItemCount();

        int scrollOffset = 0;

        int selectedViewWidth = 0;
        int nextViewWidth = 0;

        View selectedView = mLinearLayoutManager.findViewByPosition(position);
        View nextView = mLinearLayoutManager.findViewByPosition(position + 1);

        if (selectedView != null) {
            try {
                selectedViewWidth = selectedView.getMeasuredWidth();
                nextViewWidth = nextView.getMeasuredWidth();
            } catch (Exception e) {
                //ignore
            }
        } else {
            //해당 position이 바인딩 전인 경우 미리 저장해둔 값을 사용함
            try {
                selectedViewWidth = tabViews.get(position);
                nextViewWidth = tabViews.get(position + 1);
            } catch (Exception e) {
                //ignore
            }
        }

        if (selectedViewWidth > 0) {
            int width = getMeasuredWidth();
            float sLeft = (position == 0) ? 0 : width / 2.f - selectedViewWidth / 2.f; // left edge of selected tab
            float sRight = sLeft + selectedViewWidth; // right edge of selected tab

            if (nextViewWidth > 0) {
                float nLeft = width / 2.f - nextViewWidth / 2.f; // left edge of next tab
                float distance = sRight - nLeft; // total distance that is needed to distance to next tab
                float dx = distance * positionOffset;
                scrollOffset = (int) (sLeft - dx);

                if (position == 0) {
                    float indicatorGap = (nextViewWidth - selectedViewWidth) / 2;
                    mIndicatorGap = (int) (indicatorGap * positionOffset);
                    mIndicatorScroll = (int)((selectedViewWidth + indicatorGap)  * positionOffset);

                } else {
                    float indicatorGap = (nextViewWidth - selectedViewWidth) / 2;
                    mIndicatorGap = (int) (indicatorGap * positionOffset);
                    mIndicatorScroll = (int) dx;
                }

            } else {
                scrollOffset = (int) sLeft;
                mIndicatorScroll = 0;
                mIndicatorGap = 0;
            }
            if (fitIndicator) {
                mIndicatorScroll = 0;
                mIndicatorGap = 0;
            }

        } else {
            if (getMeasuredWidth() > 0 && mTabMaxWidth > 0 && mTabMinWidth == mTabMaxWidth) { //fixed size
                int width = mTabMinWidth;
                int offset = (int) (positionOffset * -width);
                int leftOffset = (int) ((getMeasuredWidth() - width) / 2.f);
                scrollOffset = offset + leftOffset;
            }
            mRequestScrollToTab = true;
        }

        updateCurrentIndicatorPosition(position, positionOffset - mOldPositionOffset, positionOffset);
        mIndicatorPosition = position;

        stopScroll();

        if (position != mOldPosition || scrollOffset != mOldScrollOffset) {
            mLinearLayoutManager.scrollToPositionWithOffset(position, scrollOffset);
        }
        if (mIndicatorHeight > 0) {
            invalidate();
        }

        mOldPosition = position;
        mOldScrollOffset = scrollOffset;
        mOldPositionOffset = positionOffset;
    }

    protected void updateCurrentIndicatorPosition(int position, float dx, float positionOffset) {
        if (mAdapter == null) {
            return;
        }
        int indicatorPosition = -1;
        if (dx > 0 && positionOffset >= mPositionThreshold - POSITION_THRESHOLD_ALLOWABLE) {
            indicatorPosition = position + 1;

        } else if (dx < 0 && positionOffset <= 1 - mPositionThreshold + POSITION_THRESHOLD_ALLOWABLE) {
            indicatorPosition = position;
        }
        if (indicatorPosition >= 0 && indicatorPosition != mAdapter.getCurrentIndicatorPosition()) {
            mAdapter.setCurrentIndicatorPosition(indicatorPosition);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        View view = mLinearLayoutManager.findViewByPosition(mIndicatorPosition);
        if (view == null) {
            if (mRequestScrollToTab) {
                mRequestScrollToTab = false;
                scrollToTab(mViewPager.getCurrentItem());
            }
            for (int i=0; i<mLinearLayoutManager.getChildCount(); i++) {
                View viewChild = mLinearLayoutManager.getChildAt(i);
                TextView textViewBinded = viewChild.findViewById(R.id.psts_tab_title);
                String strBindedItemTitle = textViewBinded.getText().toString().trim();
                if (mStrSelectedItem != null && mStrSelectedItem.trim().equals(strBindedItemTitle)) {
                    view = viewChild;
//                    매장 갯수가 적을때 여러개 나올 수도 있으니 break 안한다.
//                    break;
                }
            }
            if (view == null) {
                return;
            }
        }
        else {
            mRequestScrollToTab = false;
            // child에 해당 뷰가 존재
            TextView textView = view.findViewById(R.id.psts_tab_title);
            if (textView != null) {
                mStrSelectedItem = textView.getText().toString().trim();
            }
        }

        int left;
        int right;
        if (isLayoutRtl()) {
            left = view.getLeft() - mIndicatorScroll - mIndicatorGap;
            right = view.getRight() - mIndicatorScroll + mIndicatorGap;
        } else {
            left = view.getLeft() + mIndicatorScroll - mIndicatorGap;
            right = view.getRight() + mIndicatorScroll + mIndicatorGap;
        }

        // home_tab.xml 에서 view 왼쪽은 15dp 오른쪽은 12dp 만큼 간격이 있다. (양 옆으로 5dp 만큼 간격 주기 위해 10, 7 dp 씩 줄임)
        // 무한 롤링에서 양쪽 끝은 존재 하지 않기때문에 psts_view_last는 계속 gone 상태
        int plusLeftGap = DisplayUtils.convertDpToPx(getContext(), 10);
        int plusRightGap = DisplayUtils.convertDpToPx(getContext(), 7);

        left += plusLeftGap;
        right -= plusRightGap;

        int top = getHeight() - mIndicatorHeight;
        int bottom = getHeight();

        canvas.drawRect(left, top, right, bottom, mIndicatorPaint);
    }

    protected boolean isLayoutRtl() {
        return ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL;
    }

    protected static class RecyclerOnScrollListener extends OnScrollListener {

        protected RecyclerTabLayout mRecyclerTabLayout;
        protected LinearLayoutManager mLinearLayoutManager;

        public RecyclerOnScrollListener(RecyclerTabLayout recyclerTabLayout,
                                        LinearLayoutManager linearLayoutManager) {
            mRecyclerTabLayout = recyclerTabLayout;
            mLinearLayoutManager = linearLayoutManager;
        }

        public int mDx;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            mDx += dx;
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            switch (newState) {
                case SCROLL_STATE_IDLE:
                    if (mDx > 0) {
                        selectCenterTabForRightScroll();
                    } else {
                        selectCenterTabForLeftScroll();
                    }
                    mDx = 0;
                    break;
                case SCROLL_STATE_DRAGGING:
                case SCROLL_STATE_SETTLING:
            }
        }

        protected void selectCenterTabForRightScroll() {
            int first = mLinearLayoutManager.findFirstVisibleItemPosition();
            int last = mLinearLayoutManager.findLastVisibleItemPosition();
            int center = mRecyclerTabLayout.getWidth() / 2;
            for (int position = first; position <= last; position++) {
                View view = mLinearLayoutManager.findViewByPosition(position);
                if (view.getLeft() + view.getWidth() >= center) {
                    view.performClick();
                    break;
                }
            }
        }

        protected void selectCenterTabForLeftScroll() {
            int first = mLinearLayoutManager.findFirstVisibleItemPosition();
            int last = mLinearLayoutManager.findLastVisibleItemPosition();
            int center = mRecyclerTabLayout.getWidth() / 2;
            for (int position = last; position >= first; position--) {
                View view = mLinearLayoutManager.findViewByPosition(position);
                if (view.getLeft() <= center) {
                    view.performClick();
                    break;
                }
            }
        }
    }

    protected static class ViewPagerOnPageChangeListener implements ViewPager.OnPageChangeListener {

        private final RecyclerTabLayout mRecyclerTabLayout;
        private int mScrollState;

        public ViewPagerOnPageChangeListener(RecyclerTabLayout recyclerTabLayout) {
            mRecyclerTabLayout = recyclerTabLayout;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            mRecyclerTabLayout.scrollToTab(position, positionOffset, false);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            mScrollState = state;
        }

        @Override
        public void onPageSelected(int position) {
            if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
                if (mRecyclerTabLayout.mIndicatorPosition != position) {
                    mRecyclerTabLayout.scrollToTab(position);
                }
            }
        }
    }

    public static abstract class Adapter<T extends RecyclerView.ViewHolder>
            extends RecyclerView.Adapter<T> {

        protected ViewPager mViewPager;
        protected int mIndicatorPosition;

        public Adapter(ViewPager viewPager) {
            mViewPager = viewPager;
        }

        public ViewPager getViewPager() {
            return mViewPager;
        }

        public void setCurrentIndicatorPosition(int indicatorPosition) {
            mIndicatorPosition = indicatorPosition;
        }

        public int getCurrentIndicatorPosition() {
            return mIndicatorPosition;
        }
    }

    public static class TabTextView extends AppCompatTextView {

        public TabTextView(Context context) {
            super(context);
        }

        public ColorStateList createColorStateList(int defaultColor, int selectedColor) {
            final int[][] states = new int[2][];
            final int[] colors = new int[2];
            states[0] = SELECTED_STATE_SET;
            colors[0] = selectedColor;
            // Default enabled state
            states[1] = EMPTY_STATE_SET;
            colors[1] = defaultColor;
            return new ColorStateList(states, colors);
        }
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {
        // 플리킹 속도 조절
        velocityX *= .7;
        return super.fling(velocityX, velocityY);
    }
}

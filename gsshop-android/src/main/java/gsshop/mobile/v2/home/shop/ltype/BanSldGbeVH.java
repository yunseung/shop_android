/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.ltype;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.gsshop.mocha.ui.util.ViewUtils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.ModuleList;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolderV2;
import gsshop.mobile.v2.home.shop.renewal.views.CommonTitleLayout;
import gsshop.mobile.v2.library.viewpager.InfinitePagerAdapter;
import gsshop.mobile.v2.library.viewpager.InfiniteViewPager;
import gsshop.mobile.v2.util.ClickUtils;
import gsshop.mobile.v2.util.DisplayUtils;
import roboguice.util.Ln;

import static com.blankj.utilcode.util.EmptyUtils.isEmpty;


/**
 * ad 구좌.
 */
@SuppressLint("NewApi")
public class BanSldGbeVH extends BaseViewHolderV2 {

    private static final long DEFAULT_ROLLING_TIME = TimeUnit.SECONDS.toMillis(3);

    private Context mContext;

    private InfiniteViewPager mViewPager;
    private TextView mTextCount;
    private View mViewRoot;

    private final CommonTitleLayout mCommonTitleLayout;

//    protected TimerTask task;

    private final int COUNT_COLOR_ALL = Color.parseColor("#888888");
    private final int COUNT_COLOR = Color.parseColor("#111111");

    private ViewPager.SimpleOnPageChangeListener mPageChangeListener;

    /**
     * 뷰페이저 좌우플리킹 빨리 수행시 행걸리는 현상 방지를 위한 플래그
     */
    private boolean isTouching = false;

    private TimerTask mTask;
    private class RollingData {
        public boolean isAuto;
        public long rollingDelayMillisecond;
    }

    @Override
    public void onViewAttachedToWindow() {
        super.onViewAttachedToWindow();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onViewDetachedFromWindow() {
        super.onViewDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(Events.FlexibleEvent.StartRollingGSChoiceRollBannerEvent event) {
        //Ln.i("StartRollingNo1BestDealBannerEvent : "+ event.start);
        if (event.start) {
            startTimer();
        } else {
            stopTimer();
        }
    }

    /**
     * @param itemView
     */
    public BanSldGbeVH(View itemView) {
        super(itemView);

        mCommonTitleLayout = itemView.findViewById(R.id.common_title_layout);

        mViewPager = (InfiniteViewPager) itemView.findViewById(R.id.pager_choice_roll);
        mTextCount = (TextView) itemView.findViewById(R.id.text_choice_roll_count);
        mViewRoot = itemView.findViewById(R.id.layout_root);

        DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), mViewRoot);
        DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), mViewPager);
    }

    @Override
    public void onBindViewHolder(Context context, int position, List<ModuleList> moduleList) {
        super.onBindViewHolder(context, position, moduleList);
        this.mContext = context;

        ModuleList thisModuleList = moduleList.get(position);

        if (thisModuleList.productList == null) {
            return;
        }

        mCommonTitleLayout.setCommonTitle(this, thisModuleList);

        final List<SectionContentList> list = thisModuleList.productList;

        if (list == null) {
            return;
        }

        RollingData data = new RollingData();
        if (thisModuleList.rollingDelay > 0 && thisModuleList.subProductList != null) {
            data.rollingDelayMillisecond = (int)(thisModuleList.rollingDelay * 1000);
        }
        else {
            data.rollingDelayMillisecond = DEFAULT_ROLLING_TIME;
        }

        if(list.size() > 1) {
            data.isAuto = true;
            mViewPager.setTag(data);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        startTimer();
                    } catch (Exception e) {
                        Ln.e(e);
                    }
                }
            }, data.rollingDelayMillisecond);
        }else{
            data.isAuto = false;
            data.rollingDelayMillisecond = 0;
            mViewPager.setTag(data);
            stopTimer();
        }

        /**
         * 페이지 이동.
         */

        mPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                int item = mViewPager.getCurrentItem();
                int count = ((InfinitePagerAdapter) mViewPager.getAdapter()).getRealCount();

                String text = (item + 1) + " / " + count;
                Spannable wordtoSpan = new SpannableString(text);

                try {
                    wordtoSpan.setSpan(new ForegroundColorSpan(COUNT_COLOR), 0, text.indexOf("/") - 1, 0);
                    wordtoSpan.setSpan(new ForegroundColorSpan(COUNT_COLOR_ALL), text.indexOf("/"), text.length(), 0);
                }
                catch (IllegalArgumentException e) {
                    Ln.e(e.getMessage());
                }
                mTextCount.setText(wordtoSpan);
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            mViewPager.setAccessibilityDelegate(new View.AccessibilityDelegate() {
                @Override
                public void onPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
                    super.onPopulateAccessibilityEvent(host, event);

                    if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_SELECTED ||
                            event.getEventType() == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
                        if (!TextUtils.isEmpty(thisModuleList.name)) {
                            event.setContentDescription(thisModuleList.name);
                        }
                    }
                }
            });
        }

        if(list.size() == 1){
            mViewPager.setPagingEnabled(false);
            ViewUtils.hideViews(mTextCount);
            mViewPager.setPadding(0, 0, 0, 0);
            mViewPager.setClipToPadding(true);
            mViewPager.setPageMargin(0);
            mViewPager.setPageTransformer(false, null);
        }else{
            mViewPager.setPagingEnabled(true);
            ViewUtils.showViews(mTextCount);

            int preview = context.getResources()
                    .getDimensionPixelOffset(R.dimen.gschoice_preview_width);
            int margin = context.getResources()
                    .getDimensionPixelOffset(R.dimen.gschoice_viewpager_margin);

            preview = DisplayUtils.getResizedPixelSizeToScreenSize(context, preview);
            final int setMargin = DisplayUtils.getResizedPixelSizeToScreenSize(context, margin);
            mViewPager.setPadding(preview, 0, preview, 0);
            mViewPager.setClipToPadding(false);
            mViewPager.setPageMargin(setMargin);

            mViewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
                @Override
                public void transformPage(View page, float position) {

                    page.setTranslationX(-setMargin);
                }
            });

            String text = 1 + " / " + list.size();
            Spannable wordtoSpan = new SpannableString(text);

            try {
                wordtoSpan.setSpan(new ForegroundColorSpan(COUNT_COLOR), 0, text.indexOf("/") - 1, 0);
                wordtoSpan.setSpan(new ForegroundColorSpan(COUNT_COLOR_ALL), text.indexOf("/"), text.length(), 0);
            } catch (IllegalArgumentException e) {
                Ln.e(e.getMessage());
            }
            mTextCount.setText(wordtoSpan);

        }

        final InfinitePagerAdapter wrappedAdapter = new InfinitePagerAdapter(
                new BanSldGbeAdapter(context, thisModuleList.productList, null, null));

        //Ln.i("content.indicator : " + content.indicator);
        mViewPager.setAdapter(wrappedAdapter);
        mViewPager.setCurrentItem(0);

        mViewPager.removeOnPageChangeListener(mPageChangeListener);
        mViewPager.addOnPageChangeListener(mPageChangeListener);

        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        if (!isTouching && ClickUtils.work(800)) {
                            return true;
                        }
                        isTouching = true;
                        stopTimer();
                        break;
                    case MotionEvent.ACTION_UP:
                        isTouching = false;
                        startTimer();
                        break;
                }
                return false;
            }
        });
    }

    /* 자동 롤링 실행. */
    protected void startTimer() {

        RollingData data = null;
        if (mViewPager != null) {
            // 사이즈 확인 후 롤링 하지 않는다.
            if (mViewPager.getAdapter().getCount() < 2) {
                return;
            }
            data = (RollingData)mViewPager.getTag();
        }else {
            return;
        }
        if (data != null && data.isAuto) {
            stopTimer();
            mTask = new TimerTask() {
                @Override
                public void run() {
                    if (isEmpty(mContext)) {
                        return;
                    }
                    ((Activity)mContext).runOnUiThread(() -> {
                        if (mViewPager != null) {
                            if (mViewPager != null && mViewPager.getAdapter() != null) {
                                int nextItem = mViewPager.getRealCurrentItem() + 1;
                                if (nextItem < mViewPager.getAdapter().getCount()) {
                                    mViewPager.setRealCurrentItem(nextItem, true);
                                }
                            }
                        }
                    });
                }
            };
            new Timer().schedule(mTask, data.rollingDelayMillisecond, data.rollingDelayMillisecond);
        }
    }

    /* 자동 롤링 종료. */

    public void stopTimer() {
        // timer
        if (mTask != null) {
            mTask.cancel();
            mTask = null;
        }
    }

}

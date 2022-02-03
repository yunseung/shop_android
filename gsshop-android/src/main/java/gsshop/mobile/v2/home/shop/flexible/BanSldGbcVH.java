/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.flexible;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.gsshop.mocha.ui.util.ViewUtils;

import java.util.List;

import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.ShopInfo.ShopItem;
import gsshop.mobile.v2.library.viewpager.InfinitePagerAdapter;
import gsshop.mobile.v2.library.viewpager.InfiniteViewPager;
import gsshop.mobile.v2.util.CookieUtils;
import gsshop.mobile.v2.util.DisplayUtils;


/**
 * ad 구좌.
 */
@SuppressLint("NewApi")
public class BanSldGbcVH extends BaseRollViewHolder {

    private final TextView countText;

    private final int COUNT_COLOR = Color.parseColor("#999999");

    /**
     * 페이지 이동.
     */

    private ViewPager.SimpleOnPageChangeListener mPageChangeListener;
    private boolean isAdult;

    @Override
    public void onViewAttachedToWindow() {
        /** ignore auto rolling */
    }

    @Override
    public void onViewDetachedFromWindow() {
        /** ignore auto rolling */
    }

    /**
     * @param itemView
     */
    public BanSldGbcVH(View itemView) {
        super(itemView);

        viewPager = (InfiniteViewPager) itemView.findViewById(R.id.pager_ad_roll);
        countText = (TextView) itemView.findViewById(R.id.text_ad_roll_count);

        // resize view
        DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), viewPager);

        String adult = CookieUtils.getAdult(MainApplication.getAppContext());

        if ("true".equals(adult) || "temp".equals(adult)) {
            isAdult = true;
        }

    }

    // 롤링 배너.
    @Override
    public void onBindViewHolder(final Context context, int position, ShopInfo info,
                                 final String action, final String label, String sectionName) {
        final ShopItem content = info.contents.get(position);
        content.sectionContent.rollingDelay = -1;   // auto rolling stop

        super.onBindViewHolder(context, position, info, action, label, sectionName);

        final List<SectionContentList> list = content.sectionContent.subProductList;
        final int indicator = content.indicator;

        if (list == null) {
            return;
        }

        /**
         * 페이지 이동.
         */
        mPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                int item = viewPager.getCurrentItem();
                int count = ((InfinitePagerAdapter) viewPager.getAdapter()).getRealCount();

                String text = Integer.toString(item + 1) + " / " + count;
                Spannable wordtoSpan = new SpannableString(text);
                wordtoSpan.setSpan(new ForegroundColorSpan(COUNT_COLOR), text.indexOf("/"), text.length(), 0);

                countText.setText(wordtoSpan);

                content.indicator = item;
                startTimer();
            }
        };


        int preview = context.getResources()
                .getDimensionPixelOffset(R.dimen.flexible_preview_width);
        int margin = context.getResources()
                .getDimensionPixelOffset(R.dimen.flexible_viewpager_margin);

        preview = DisplayUtils.getResizedPixelSizeToScreenSize(context, preview);
        margin = DisplayUtils.getResizedPixelSizeToScreenSize(context, margin);
        viewPager.setPadding(preview, 0, preview, 0);
        viewPager.setClipToPadding(false);
        viewPager.setPageMargin(margin);

        String text =  (indicator + 1) + " / " + list.size();
        Spannable wordtoSpan = new SpannableString(text);
        wordtoSpan.setSpan(new ForegroundColorSpan(COUNT_COLOR), text.indexOf("/"), text.length(), 0);
        countText.setText(wordtoSpan);

        final InfinitePagerAdapter wrappedAdapter = new InfinitePagerAdapter(
                new RollBannerPagerAdapter(context, content.sectionContent.subProductList, action, label, task, isAdult));

        //Ln.i("content.indicator : " + content.indicator);
        viewPager.setAdapter(wrappedAdapter);
        viewPager.setCurrentItem(indicator);

        viewPager.removeOnPageChangeListener(mPageChangeListener);
        viewPager.addOnPageChangeListener(mPageChangeListener);

        if(list.size() == 1){
            viewPager.setPagingEnabled(false);
            ViewUtils.hideViews(countText);
        }else{
            viewPager.setPagingEnabled(true);
            ViewUtils.showViews(countText);
        }
        setRandom(content);
    }

}

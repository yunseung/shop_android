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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.List;

import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.ShopInfo.ShopItem;
import gsshop.mobile.v2.library.viewpager.InfinitePagerAdapter;
import gsshop.mobile.v2.library.viewpager.InfiniteViewPager;
import gsshop.mobile.v2.support.gtm.GTMAction;
import gsshop.mobile.v2.support.gtm.GTMEnum;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.WebUtils;


/**
 * todays hot 배너.
 */
@SuppressLint("NewApi")
public class BanSldCommonVH extends BaseRollViewHolder {

    private View prevButton;
    private View nextButton;
    private TextView countText;

    enum SCALE_TYPE{
        type_fit,
        type_resize
    }

    private int mBannerHeight = -1;
    private SCALE_TYPE mBannerFitType = SCALE_TYPE.type_resize;

//    private int mResourceId = -1;

    /**
     * 페이지 이동.
     */
    private ViewPager.SimpleOnPageChangeListener mPageChangeListener;

    /**
     * @param itemView
     */
    public BanSldCommonVH(View itemView) {
        super(itemView);

        viewPager = (InfiniteViewPager) itemView.findViewById(R.id.viewPager);
        countText = (TextView) itemView.findViewById(R.id.text_count);
        prevButton = itemView.findViewById(R.id.button_prev);
        nextButton = itemView.findViewById(R.id.button_next);
    }


    // 롤링 배너.
    @Override
    public void onBindViewHolder(final Context context, int position, ShopInfo info,
                                 final String action, final String label, String sectionName) {

        super.onBindViewHolder(context, position, info, action, label, sectionName);

        final ShopItem content = info.contents.get(position);
        final List<SectionContentList> list = content.sectionContent.subProductList;
        final int indicator = content.indicator;

        // null 일 경우 대비.
        if (viewPager == null) return;
        if (countText == null) countText = new TextView(context);
        if (prevButton == null) prevButton = new View(context);
        if (nextButton == null) nextButton = new View(context);

        if(list.size() == 1){
            viewPager.setPagingEnabled(false);
            prevButton.setVisibility(View.GONE);
            nextButton.setVisibility(View.GONE);
            countText.setVisibility(View.GONE);

        }else{
            viewPager.setPagingEnabled(true);
            prevButton.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.VISIBLE);
            countText.setVisibility(View.VISIBLE);
        }

        prevButton.setContentDescription("이전 이미지 버튼");
        prevButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewPager.getRealCurrentItem() - 1;
                viewPager.setRealCurrentItem(position, true);
            }
        });

        nextButton.setContentDescription("다음 이미지 버튼");
        nextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewPager.getRealCurrentItem() + 1;
                viewPager.setRealCurrentItem(position, true);
            }
        });

        /**
         * 페이지 이동.
         */
        mPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                int item = viewPager.getCurrentItem();
                int count = ((InfinitePagerAdapter) viewPager.getAdapter()).getRealCount();

                String text = Integer.toString(item + 1) + "/" + count;

                countText.setText(text);
                countText.setContentDescription("총 " + count + "페이지 중 " + (item + 1) + "페이지");

                content.indicator = item;
                startTimer();
            }

        };

        String text =  (indicator + 1) + "/" + list.size();
        countText.setText(text);

        final InfinitePagerAdapter wrappedAdapter = new InfinitePagerAdapter(
                new RollImageAdapter(context, list, action, label, mBannerHeight, mBannerFitType));

        if (mBannerHeight > 0)
            viewPager.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, mBannerHeight));

        //Ln.i("content.indicator : " + content.indicator);
        viewPager.setAdapter(wrappedAdapter);
        viewPager.setCurrentItem(indicator);

        viewPager.removeOnPageChangeListener(mPageChangeListener);
        viewPager.addOnPageChangeListener(mPageChangeListener);

        setRandom(content);
    }

    public void setBannerHeight(int bannerHeight) {
        mBannerHeight = bannerHeight;
    }

    public void setScaleType(SCALE_TYPE scaleType) {
        mBannerFitType = scaleType;
    }

//    public void setResourceItemResource(int itemResourceId) {
//        mResourceId = itemResourceId;
//    }

    /**
     * 이미지 롤링배너 어댑터 pager adapter
     */
    private static class RollImageAdapter extends PagerAdapter {

        private final Context context;
        private final List<SectionContentList> list;
        private final String action;
        private final String label;
        private final int mRollImgHeight;
        private final SCALE_TYPE mScaleType;

        public RollImageAdapter(Context context, List<SectionContentList> list, String action,
                                String label, int imageHeight, SCALE_TYPE scaleType) {
            this.context = context;

            this.list = list;
            this.action = action;
            this.label = label;
            this.mRollImgHeight = imageHeight;
            this.mScaleType = scaleType;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return (view == (View) obj);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            ImageView view = new ImageView(context);
            if ( mRollImgHeight > 0 ) {
                view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, mRollImgHeight));
            }

            // main image
//            DisplayUtils.resizeHeightAtViewToScreenSize(context, view);

            final SectionContentList item = list.get(position);
            view.setContentDescription(item.productName);

            view.setContentDescription(context.getString(R.string.description_image));

            view.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    WebUtils.goWeb(context, item.linkUrl);

                    String tempLabel = String.format("%s_%s_%s", label, position, item.linkUrl);
                    GTMAction.sendEvent(context, GTMEnum.GTM_AREA_CATEGORY, action, tempLabel);
                }
            });

            if(mScaleType == SCALE_TYPE.type_fit) {
                ImageUtil.loadImageFit(context, item.imageUrl, view, R.drawable.noimage_375_188);
            }
            else {
                ImageUtil.loadImageResize(context, item.imageUrl, view, R.drawable.noimage_375_188);
            }

            container.addView(view);

            return view;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            // must be overridden else throws exception as not overridden.
            collection.removeView((View) view);
        }

    }

}

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
import android.widget.ImageView.ScaleType;

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
import gsshop.mobile.v2.support.ui.IndicatorRecyclerView;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.WebUtils;


/**
 * todays hot 배너.
 */
@SuppressLint("NewApi")
public class BanSldGbhVH extends BaseRollViewHolder {
    private int mBannerHeight = -1; //이미지배너 높이
    private ScaleType mBannerFitType = ScaleType.FIT_XY; //이미지배너 fitType
    private IndicatorRecyclerView indicatorRecycler; //점 indicator
    private ViewPager.SimpleOnPageChangeListener mPageChangeListener;

    private final int separationMargin = 10; //이미지뷰 사이사이 간격
    private final String PMO_T2_IMG_C = "PMO_T2_IMG_C";

    public BanSldGbhVH(View itemView) {
        super(itemView);
        viewPager = (InfiniteViewPager) itemView.findViewById(R.id.viewPager);
        indicatorRecycler = itemView.findViewById(R.id.indicator_layout);

        DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), viewPager);

        viewPager.setClipToPadding(false);
        viewPager.setPageMargin(DisplayUtils.convertDpToPx(MainApplication.getAppContext(), separationMargin)); //뷰 양옆 여백(10dp)
    }

    @Override
    public void onBindViewHolder(final Context context, int position, ShopInfo info, final String action, final String label, String sectionName) {
        super.onBindViewHolder(context, position, info, action, label, sectionName);

        final ShopItem content = info.contents.get(position);
        final List<SectionContentList> list = content.sectionContent.subProductList;
        final int indicator = content.indicator;

        //이미지배너 높이설정
        int bannerHeight = DisplayUtils.getScreenWidth() * (224/300);
        setBannerHeight(bannerHeight);

        //점 indicator 디자인 적용
        indicatorRecycler.setIndicator(R.layout.dot_indicator_adapter_ab, R.id.radio_indicator, list.size());

        if (!content.sectionContent.viewType.equalsIgnoreCase(PMO_T2_IMG_C)) {
            if(list.size() == 1){
                viewPager.setPagingEnabled(false);
            }else{
                viewPager.setPagingEnabled(true);
            }
        } else {
            if(list.size() == 1){
                viewPager.setPagingEnabled(false);
            }else{
                viewPager.setPagingEnabled(true);
            }
        }

        //이미지배너 바뀔때(이동할때) 리스너
        mPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                int item = viewPager.getCurrentItem();
                content.indicator = item;

                //이미지배너 바뀔때마다 인디케이터도 같이 바뀌도록
                indicatorRecycler.setSelectedIndicator(viewPager.getCurrentItem());
                startTimer();
            }
        };

        final InfinitePagerAdapter wrappedAdapter = new InfinitePagerAdapter(new RollImageAdapter(context, list, label, mBannerHeight, mBannerFitType));

        if (mBannerHeight > 0)
            viewPager.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, mBannerHeight));

        viewPager.setAdapter(wrappedAdapter);
        viewPager.setCurrentItem(indicator);

        viewPager.removeOnPageChangeListener(mPageChangeListener);
        viewPager.addOnPageChangeListener(mPageChangeListener);

        setRandom(content);
    }

    public void setBannerHeight(int bannerHeight) {
        mBannerHeight = bannerHeight;
    }

    public void setScaleType(ScaleType scaleType) {
        mBannerFitType = scaleType;
    }

    /**
     * 이미지 롤링배너 어댑터 pager adapter
     */
    private static class RollImageAdapter extends PagerAdapter {

        private final Context context;
        private final List<SectionContentList> list;
        private final String label;
        private int mRollImgHeight;
        private ScaleType mScaleType;

        public RollImageAdapter(Context context, List<SectionContentList> list,
                                String label, int imageHeight, ScaleType scaleType) {
            this.context = context;
            this.list = list;
            this.label = label;
            this.mRollImgHeight = imageHeight;
            this.mScaleType = scaleType;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            ImageView view = new ImageView(context);

            //높이 0보다 작을경우 224dp지정
            if (mRollImgHeight < 0 ) {
                mRollImgHeight = context.getResources()
                        .getDimensionPixelSize(R.dimen.HomeImageBannerHeight);
            }

            //뷰 속성 지정
            view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, mRollImgHeight));
            view.setScaleType(mScaleType);
            DisplayUtils.resizeHeightAtViewToScreenSize(context, view);

            //이미지 리스트
            final SectionContentList item = list.get(position);
            view.setContentDescription(item.productName);

            //이미지뷰 라운드처리
            ImageUtil.loadImageFit(context, item.imageUrl, view, R.drawable.noimg_banner);
            //이미지 그림자 처리 삭제 요청
//            view.setBackgroundResource(R.drawable.ban_sld_gbh_shadow);

            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    WebUtils.goWeb(context, item.linkUrl);
                }
            });

            container.addView(view);
            return view;
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
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

    }
}

/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.bestshop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.List;

import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.library.viewpager.InfinitePagerAdapter;
import gsshop.mobile.v2.library.viewpager.InfiniteViewPager;
import gsshop.mobile.v2.support.gtm.GTMAction;
import gsshop.mobile.v2.support.gtm.GTMEnum;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.WebUtils;

import static com.blankj.utilcode.util.ConvertUtils.dp2px;
/**
 * 기획전 - 빅브랜드 배너.
 */
@SuppressLint("NewApi")
public class MapSldC3GbaVH extends BaseViewHolder {

    private final InfiniteViewPager viewPager;
    private final View prevButton;
    private final View nextButton;
    private final TextView countText;

    private final int COUNT_COLOR = Color.parseColor("#66ffffff");

    /**
     * 페이지 이동.
     */

    private ViewPager.SimpleOnPageChangeListener mPageChangeListener;

    /**
     * @param itemView itemView
     */
    public MapSldC3GbaVH(View itemView) {
        super(itemView);

        viewPager = (InfiniteViewPager) itemView.findViewById(R.id.viewPager);
        countText = (TextView) itemView.findViewById(R.id.text_count);
        prevButton = itemView.findViewById(R.id.button_prev);
        nextButton = itemView.findViewById(R.id.button_next);

        // resize view
        DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), viewPager);
    }

    /*
     * bind
     */
    @Override
    public void onBindViewHolder(final Context context, int position, ShopInfo info, final String action, final String label, String sectionName) {

        final ShopInfo.ShopItem content = info.contents.get(position);
        final List<SectionContentList> list = content.sectionContent.subProductList;
        final int indicator = content.indicator;

        if (list == null) {
            return;
        }

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewPager.getRealCurrentItem() - 1;
                viewPager.setRealCurrentItem(position, true);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
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

                String text = Integer.toString(item + 1) + " / " + count;
                Spannable wordtoSpan = new SpannableString(text);
                wordtoSpan.setSpan(new ForegroundColorSpan(COUNT_COLOR), text.indexOf("/"), text.length(), 0);


                countText.setText(wordtoSpan);

                content.indicator = item;
            }
        };


        String text =  (indicator + 1) + " / " + list.size();
        Spannable wordtoSpan = new SpannableString(text);
        wordtoSpan.setSpan(new ForegroundColorSpan(COUNT_COLOR), text.indexOf("/"), text.length(), 0);
        countText.setText(wordtoSpan);

        final InfinitePagerAdapter wrappedAdapter = new InfinitePagerAdapter(
                new MapSldC3GbaVH.RollImageAdapter(viewPager, context, list, action, label));

        //Ln.i("content.indicator : " + content.indicator);
        viewPager.setAdapter(wrappedAdapter);
        viewPager.setCurrentItem(indicator);

        viewPager.removeOnPageChangeListener(mPageChangeListener);
        viewPager.addOnPageChangeListener(mPageChangeListener);

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

    }

    /**
     * 이미지 롤링배너 어댑터 pager adapter
     */
    private static class RollImageAdapter extends PagerAdapter {

        private final InfiniteViewPager viewPager;
        private final Context context;
        private final List<SectionContentList> list;
        private final String action;
        private final String label;

        public RollImageAdapter(InfiniteViewPager viewPager, Context context, List<SectionContentList> list, String action,
                                String label) {
            this.context = context;
            this.viewPager = viewPager;
            this.list = list;
            this.action = action;
            this.label = label;
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

            View view = ((Activity) context).getLayoutInflater()
                    .inflate(R.layout.home_row_type_fx_planshop_image_item, null);

            ImageView mainImage = (ImageView) view.findViewById(R.id.image_main);

            final View[] sub_layout = new View[3];
            sub_layout[0] = view.findViewById(R.id.sub_layout_first);
            sub_layout[1] = view.findViewById(R.id.sub_layout_second);
            sub_layout[2] = view.findViewById(R.id.sub_layout_third);

            ImageView[] image_sub = new ImageView[3];
            image_sub[0] = (ImageView) view.findViewById(R.id.image_sub_first);
            image_sub[1] = (ImageView) view.findViewById(R.id.image_sub_second);
            image_sub[2] = (ImageView) view.findViewById(R.id.image_sub_third);

            TextView[] title_sub = new TextView[3];
            title_sub[0] = (TextView) view.findViewById(R.id.title_first);
            title_sub[1] = (TextView) view.findViewById(R.id.title_second);
            title_sub[2] = (TextView) view.findViewById(R.id.title_third);

            TextView[] price_sub = new TextView[3];
            price_sub[0] = (TextView) view.findViewById(R.id.price_first);
            price_sub[1] = (TextView) view.findViewById(R.id.price_second);
            price_sub[2] = (TextView) view.findViewById(R.id.price_third);

            TextView[] price_unit_sub = new TextView[3];
            price_unit_sub[0] = (TextView) view.findViewById(R.id.price_unit_first);
            price_unit_sub[1] = (TextView) view.findViewById(R.id.price_unit_second);
            price_unit_sub[2] = (TextView) view.findViewById(R.id.price_unit_third);

            int size = context.getResources()
                    .getDimensionPixelSize(R.dimen.pranshop_sub_img_size);

            for(int i=0; i< sub_layout.length; i++){
                sub_layout[i].setVisibility(View.INVISIBLE);
                image_sub[i].setLayoutParams(new LinearLayout.LayoutParams(size, size));
//                DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), sub_layout[i]);
                DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), image_sub[i]);
                DisplayUtils.resizeWidthAtViewToScreenSize(MainApplication.getAppContext(), image_sub[i]);
            }

            int height = context.getResources()
                    .getDimensionPixelSize(R.dimen.pranshop_main_img_height);
            mainImage.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height));
            mainImage.setScaleType(ImageView.ScaleType.FIT_XY);

            // main image
            DisplayUtils.resizeHeightAtViewToScreenSize(context, mainImage);

            final SectionContentList item = list.get(position);
            mainImage.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    WebUtils.goWeb(context, item.linkUrl);

                    String tempLabel = String.format("%s_%s_%s", label, position, item.linkUrl);
                    GTMAction.sendEvent(context, GTMEnum.GTM_AREA_CATEGORY, action, tempLabel);
                }
            });

            ImageUtil.loadImageFit(context, item.imageUrl, mainImage, R.drawable.noimg_logo);


            if(item.subProductList != null){

                int index = 0;
                for(final SectionContentList subItem : item.subProductList){
                    ImageUtil.loadImageFit(context, subItem.imageUrl, image_sub[index], R.drawable.noimg_list);
                    sub_layout[index].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            WebUtils.goWeb(context, subItem.linkUrl);

                            String tempLabel = String.format("%s_%s_%s", label, position, subItem.linkUrl);
                            GTMAction.sendEvent(context, GTMEnum.GTM_AREA_CATEGORY, action, tempLabel);
                        }
                    });

                    if (DisplayUtils.isValidString(subItem.productName)) {
                        title_sub[index].setText(subItem.productName);
                    }


                    if (DisplayUtils.isValidString(subItem.salePrice)) {
                        price_sub[index].setText(subItem.salePrice);
                        price_sub[index].setVisibility(View.VISIBLE);

                        price_unit_sub[index].setText(subItem.exposePriceText);
                        price_sub[index].setVisibility(View.VISIBLE);
                        price_unit_sub[index].setVisibility(View.VISIBLE);
                    } else {
                        price_sub[index].setVisibility(View.GONE);
                        price_unit_sub[index].setVisibility(View.GONE);
                    }

                    sub_layout[index].setVisibility(View.VISIBLE);

                    index++;
                }
            }

            sub_layout[0].getViewTreeObserver()
                    .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            String tag = (String)viewPager.getTag();
                            if(tag == null) {
                                int height = sub_layout[0].getHeight();

                                if (height > 0) {
                                    sub_layout[0].getViewTreeObserver().removeOnPreDrawListener(this);
                                    int itemHeight = DisplayUtils.resizeHeightAtViewToScreen(MainApplication.getAppContext(), dp2px(245)) + height + dp2px(26);
                                    viewPager.setTag("change");
                                    viewPager.getLayoutParams().height = itemHeight;
                                    viewPager.requestLayout();
                                }
                            }
                            return true;
                        }
                    });


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

package gsshop.mobile.v2.home.shop.flexible.shoppinglive;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.gsshop.mocha.ui.util.ViewUtils;

import java.util.List;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.flexible.BaseRollViewHolder;
import gsshop.mobile.v2.library.viewpager.InfinitePagerAdapter;
import gsshop.mobile.v2.support.gtm.GTMAction;
import gsshop.mobile.v2.support.gtm.GTMEnum;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.WebUtils;

public class BanSldMobileLiveSmallVH extends BaseRollViewHolder {
    private View mBtnCount;
    private ImageView mImgOne;
    private TextView mTvCountText;
    /**
     * 페이지 이동.
     */
    private ViewPager.SimpleOnPageChangeListener mPageChangeListener;

    /**
     * @param itemView
     */
    public BanSldMobileLiveSmallVH(View itemView) {
        super(itemView);
        mBtnCount = itemView.findViewById(R.id.layout_btn_count);
        mImgOne = itemView.findViewById(R.id.imageview_1_img);
        viewPager = itemView.findViewById(R.id.view_pager);
        mTvCountText = itemView.findViewById(R.id.tv_count);
    }

    @Override
    public void onViewAttachedToWindow() {
        EventBus.getDefault().register(this);
    }

    // 롤링 배너.
    @Override
    public void onBindViewHolder(final Context context, int position, final ShopInfo info,
                                 final String action, final String label, String sectionName) {

        super.onBindViewHolder(context, position, info, action, label, sectionName);

        final ShopInfo.ShopItem content = info.contents.get(position);
        final List<SectionContentList> list = content.sectionContent.subProductList;
        final int indicator = content.indicator;

        int bannerHeight = context.getResources().getDimensionPixelSize(R.dimen.mobilelive_roll_banner_item_height);
        viewPager.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, bannerHeight));
        DisplayUtils.resizeHeightAtViewToScreenSize(context, viewPager);
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
                Spannable wordToSpan = new SpannableString(text);
                wordToSpan.setSpan(new ForegroundColorSpan(Color.parseColor("#ffffff")), text.indexOf("/"), text.length(), 0);


                mTvCountText.setText(wordToSpan);
                mTvCountText.setContentDescription("총 " + count + "페이지 중 " + (item + 1) + "페이지");

                content.indicator = item;
                startTimer();
            }

        };

        String text =  (indicator + 1) + " / " + list.size();
        Spannable wordToSpan = new SpannableString(text);
        wordToSpan.setSpan(new ForegroundColorSpan(Color.parseColor("#ffffff")), text.indexOf("/"), text.length(), 0);
        mTvCountText.setText(wordToSpan);

        final InfinitePagerAdapter wrappedAdapter = new InfinitePagerAdapter(
                new RollImageAdapter(context, list, action, label, bannerHeight, ImageView.ScaleType.FIT_XY));

        viewPager.setAdapter(wrappedAdapter);

        viewPager.removeOnPageChangeListener(mPageChangeListener);
        viewPager.addOnPageChangeListener(mPageChangeListener);

        if (list == null || !(list.size() > 0)) {
            return;
        } else if (list.size() == 1) {
            mBtnCount.setVisibility(View.GONE);
            ViewUtils.showViews(mImgOne);
            ImageUtil.loadImageFitWithRound(context, list.get(0).imageUrl, mImgOne, R.dimen.mobilelive_roll_banner_radius, R.drawable.noimage_375_188);
            mImgOne.setOnClickListener(v -> {
                WebUtils.goWeb(context, list.get(0).linkUrl);

                String tempLabel = String.format("%s_%s_%s", label, position, list.get(0).linkUrl);
                GTMAction.sendEvent(context, GTMEnum.GTM_AREA_CATEGORY, action, tempLabel);
            });
            return;
        } else {
            ViewUtils.hideViews(mImgOne);
        }
    }

    /**
     * 이미지 롤링배너 어댑터 pager adapter
     */
    private static class RollImageAdapter extends PagerAdapter {

        private final Context context;
        private final List<SectionContentList> list;
        private final String action;
        private final String label;
        private int mRollImgHeight;
        private ImageView.ScaleType mScaleType;

        public RollImageAdapter(Context context, List<SectionContentList> list, String action,
                                String label, int imageHeight, ImageView.ScaleType scaleType) {
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

            View containerView = ((Activity) context).getLayoutInflater().inflate(R.layout.iv_mobile_live_small_banner, null);
            if (mRollImgHeight < 0) {
                mRollImgHeight = context.getResources()
                        .getDimensionPixelSize(R.dimen.mobilelive_roll_banner_item_height);
            }

            ImageView view = containerView.findViewById(R.id.iv_mobile_live_small_item);
            final SectionContentList item = list.get(position);
            view.setContentDescription(item.productName);

            view.setContentDescription(context.getString(R.string.description_image));

            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    WebUtils.goWeb(context, item.linkUrl);

                    String tempLabel = String.format("%s_%s_%s", label, position, item.linkUrl);
                    GTMAction.sendEvent(context, GTMEnum.GTM_AREA_CATEGORY, action, tempLabel);
                }
            });

            ImageUtil.loadImageFitWithRoundCenterCrop(context, item.imageUrl, view, 0, R.drawable.noimg_logo);
//            view.setBackground(context.getResources().getDrawable(R.drawable.noimage_375_188));

            container.addView(containerView);

            return containerView;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            // must be overridden else throws exception as not overridden.
            collection.removeView((View) view);
        }

    }
}

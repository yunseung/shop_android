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
import android.graphics.drawable.Drawable;
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
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.ShopInfo.ShopItem;
import gsshop.mobile.v2.home.util.IconTabBar;
import gsshop.mobile.v2.home.util.IconTabBar.IconTabListener;
import gsshop.mobile.v2.library.viewpager.InfinitePagerAdapter;
import gsshop.mobile.v2.library.viewpager.InfiniteViewPager;
import gsshop.mobile.v2.support.gtm.GTMAction;
import gsshop.mobile.v2.support.gtm.GTMEnum;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.WebUtils;

/**
 * todays hot 배너.
 *
 */
@SuppressLint("NewApi")
public class FlexibleBannerBrandRollImageViewHolder extends BaseViewHolder {

	private final InfiniteViewPager viewPager;
	private final IconTabBar indicators;

	private final MySimpleOnPageChangeListener mPageChangeListener = new MySimpleOnPageChangeListener();

	/**
	 * @param itemView
	 */
	public FlexibleBannerBrandRollImageViewHolder(View itemView) {
		super(itemView);

		viewPager = (InfiniteViewPager) itemView.findViewById(R.id.viewPager);
		indicators = (IconTabBar) itemView.findViewById(R.id.indicator);

	}

	// 롤링 배너.
	@Override
	public void onBindViewHolder(final Context context, int position, ShopInfo info,
			final String action, final String label, String sectionName) {
		
		int height = context.getResources()
				.getDimensionPixelSize(R.dimen.flexible_brand_rollimage_item_image_height);
		// resize view
		DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), viewPager, height);

		final ShopItem content = info.contents.get(position);
		final List<SectionContentList> list = content.sectionContent.subProductList;
		final int indicator = content.indicator;

		if(list == null){
			return;
		}

		final InfinitePagerAdapter wrappedAdapter = new InfinitePagerAdapter(
				new RollImageAdapter(context, list, action, label));

		//Ln.i("content.indicator : " + content.indicator);
		viewPager.setAdapter(wrappedAdapter);
		viewPager.setCurrentItem(indicator);

		mPageChangeListener.setContent(content);
		viewPager.removeOnPageChangeListener(mPageChangeListener);
		viewPager.addOnPageChangeListener(mPageChangeListener);

		// indicator
		indicators.setIconTabListener(new RollImageBannerIndicatorListener(context, list.size()));
		indicators.setShouldExpand(false);
		int dotPadding = context.getResources()
				.getDimensionPixelOffset(R.dimen.viewpager_dot_padding);
		indicators.setTabPaddingLeftRight(dotPadding);

		indicators.setSelectedTab(indicator);

	}

	/* 페이지 이동. */
	private class MySimpleOnPageChangeListener extends ViewPager.SimpleOnPageChangeListener {
		private ShopItem content;

		@Override
		public void onPageSelected(int position) {
			super.onPageSelected(position);
			int item = viewPager.getCurrentItem();
			//Ln.i("content.indicator : " + item);
			indicators.setSelectedTab(item);

			if (content != null) {
				content.indicator = item;
			}

		}

		public void setContent(ShopItem content) {
			this.content = content;
		}
	}

	/* 이미지 롤링 배너 indicator */
	private class RollImageBannerIndicatorListener implements IconTabListener {

		private final int count;
		private final Context context;

		/**
		 * @param count
		 */
		public RollImageBannerIndicatorListener(Context context, int count) {
			this.context = context;
			this.count = count;
		}


		/**
		 * 
		 * @param position
		 * @param selected
		 * @return
		 */
		@Override
		public Drawable getSelectedTabIconDrawable(int position, boolean selected) {
			int id;
			if (selected) {
				id = R.drawable.dot_green_720;
			} else {
				id = R.drawable.dot_grey_720;
			}
			
			Drawable drawable;
			if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
			    drawable = context.getDrawable(id);
			} else {
				drawable = context.getResources().getDrawable(id);
			}
			
			return drawable;
		}
		
		/**
		 * 
		 * @param position
		 * @param view
		 */
		@Override
		public void onIconTabClicked(int position, View view) {
			//Ln.i("onIconTabClicked : " + position);
		}

		/**
		 * 
		 * @return
		 */
		@Override
		public int tabSize() {
			return count;
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

		public RollImageAdapter(Context context, List<SectionContentList> list, String action,
				String label) {
			this.context = context;

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
			ImageView view = new ImageView(context);

			int height = context.getResources()
					.getDimensionPixelSize(R.dimen.flexible_brand_rollimage_item_image_height);
			view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, height));
			view.setScaleType(ScaleType.FIT_XY);

			// main image
			DisplayUtils.resizeHeightAtViewToScreenSize(context, view);

			final SectionContentList item = list.get(position);
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					WebUtils.goWeb(context, item.linkUrl);

					String tempLabel = String.format("%s_%s_%s", label, position, item.linkUrl);
					GTMAction.sendEvent(context, GTMEnum.GTM_AREA_CATEGORY, action, tempLabel);
				}
			});

			ImageUtil.loadImageFit(context, item.imageUrl, view, R.drawable.noimg_logo);

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

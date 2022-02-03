/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.flexible;

import android.content.Context;
import android.graphics.Color;
import androidx.viewpager.widget.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.TimerTask;

import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.ShopInfo.ShopItem;
import gsshop.mobile.v2.library.viewpager.InfinitePagerAdapter;
import gsshop.mobile.v2.library.viewpager.InfiniteViewPager;
import gsshop.mobile.v2.util.CookieUtils;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;

/**
 * 띠 이미지 배너.
 *
 */
public class DslXVH extends BaseRollViewHolder {

	protected final ImageView titleImage;
	protected final TextView countText;

	protected static TimerTask task;
	protected final MySimpleOnPageChangeListener mPageChangeListener = new MySimpleOnPageChangeListener();
	protected final View divider;
	protected final View topView;
	protected  boolean isAdult;
	private final int COUNT_COLOR = Color.parseColor("#66ffffff");

	/**
	 * @param itemView
	 */
	public DslXVH(View itemView) {
		super(itemView);
		//Ln.i("StartRollingNo1BestDealBannerEvent : create");


		titleImage = (ImageView) itemView.findViewById(R.id.image_title);
		viewPager = (InfiniteViewPager) itemView.findViewById(R.id.viewPager);
		countText = (TextView) itemView.findViewById(R.id.text_count);
		divider = itemView.findViewById(R.id.view_divider);
		topView = itemView.findViewById(R.id.view_top);

		// resize
		DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), titleImage);
		DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), divider);
		DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), topView);

		String adult = CookieUtils.getAdult(MainApplication.getAppContext());

		if ("true".equals(adult) || "temp".equals(adult)) {
			isAdult = true;
		}

	}

	/**
	 * bind
	 */
	@Override
	public void onBindViewHolder(final Context context, int position, ShopInfo info,
								 final String action, final String label, String sectionName) {
		super.onBindViewHolder(context, position, info, action, label, sectionName);
		// 테두리선.
		int borderHeight = DisplayUtils.convertDpToPx(context, 1);
		int imageHeight = context.getResources()
				.getDimensionPixelSize(R.dimen.best_deal_no1_deal_image_height);
		int infoHeight = context.getResources()
				.getDimensionPixelSize(R.dimen.best_deal_no1_deal_info_height);

		int scaleImageHeight = DisplayUtils.getResizedPixelSizeToScreenSize(context, imageHeight);
		viewPager.getLayoutParams().height = scaleImageHeight + infoHeight + borderHeight * 3;
		viewPager.requestLayout();

		final ShopItem content = info.contents.get(position);

		if(content.sectionContent.subProductList == null){
			return;
		}

		if ("DSL_A".equals(content.sectionContent.viewType)) {
			// "딜 슬라이드 상품A" = DSL_A (imageUrl, prdUrl , subProductList -> 일반) (하단 .... 대신 좌측상단 1/N
			// 형태로 표기, 일반베너 타입 타이들, 자동 스크롤)


			countText.setTextColor(
					context.getResources().getColor(R.color.flexible_no1_best_deal_count));

			topView.setVisibility(View.GONE);
		}else if("DSL_A2".equals(content.sectionContent.viewType)) {
			// "딜 슬라이드 상품B" = DSL_B (imageUrl, prdUrl , subProductList -> 일반) (하단 .... 대신 좌측상단 1/N
			// 형태로 표기, 말풍선 타입 타이들)

			countText.setTextColor(
					context.getResources().getColor(R.color.flexible_no1_best_deal_count));
			topView.setVisibility(View.GONE);
		} else {
			// "딜 슬라이드 상품B" = DSL_B (imageUrl, prdUrl , subProductList -> 일반) (하단 .... 대신 좌측상단 1/N
			// 형태로 표기, 말풍선 타입 타이들)

			countText.setTextColor(
					context.getResources().getColor(R.color.flexible_no1_best_deal_hot6_count));
			topView.setVisibility(View.VISIBLE);
		}

		// main image
		ImageUtil.loadImageResize(context, content.sectionContent.imageUrl, titleImage, 0);




		final int indicator = content.indicator;

		mPageChangeListener.setContent(content);
		viewPager.removeOnPageChangeListener(mPageChangeListener);
		viewPager.addOnPageChangeListener(mPageChangeListener);

		final InfinitePagerAdapter wrappedAdapter = new InfinitePagerAdapter(
				new RollBannerPagerAdapter(context, content.sectionContent.subProductList, action, label, task, isAdult));

		//Ln.i("content.indicator : " + content.indicator);
		// viewPager.setMyAdapter(wrappedAdapter, content.indicator);
		viewPager.setAdapter(wrappedAdapter);
		viewPager.setCurrentItem(indicator);

		int preview = context.getResources()
				.getDimensionPixelOffset(R.dimen.flexible_preview_width);
		int margin = context.getResources()
				.getDimensionPixelOffset(R.dimen.flexible_viewpager_margin);

		preview = DisplayUtils.getResizedPixelSizeToScreenSize(context, preview);
		margin = DisplayUtils.getResizedPixelSizeToScreenSize(context, margin);
		viewPager.setPadding(preview, 0, preview, 0);
		viewPager.setClipToPadding(false);
		viewPager.setPageMargin(margin);

		String text = "" + (indicator + 1) + " / " + content.sectionContent.subProductList.size();
		Spannable wordtoSpan = new SpannableString(text);
		wordtoSpan.setSpan(new ForegroundColorSpan(COUNT_COLOR), text.indexOf("/"), text.length(), 0);

		countText.setText(wordtoSpan);

		setRandom(content);

	}

	protected void setPageChangeListenerContent(ShopItem content){
		mPageChangeListener.setContent(content);
	}


	/* 페이지 이동. */
	private class MySimpleOnPageChangeListener extends ViewPager.SimpleOnPageChangeListener {
		private ShopItem content;

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
			if (content != null) {
				content.indicator = item;
				//Ln.i("content.indicator : " + content.indicator);
			}

			startTimer();
		}

		public void setContent(ShopItem content) {
			this.content = content;
		}
	}


}

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
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import gsshop.mobile.v2.util.CookieUtils;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.WebUtils;

/**
 * todays hot 배너.
 *
 */
public class TslVH extends BaseRollViewHolder {

	private final ImageView imageBg;
	private final TextView countText;
	private final View divider;
	private static boolean isAdult;
	private LinearLayout count_layout;

	private final MySimpleOnPageChangeListener mPageChangeListener = new MySimpleOnPageChangeListener();
	private final int COUNT_COLOR = Color.parseColor("#66ffffff");

	/**
	 * @param itemView
	 */
	public TslVH(View itemView) {
		super(itemView);

		imageBg = (ImageView) itemView.findViewById(R.id.image_bg);
		viewPager = (InfiniteViewPager) itemView.findViewById(R.id.viewPager);
		countText = (TextView) itemView.findViewById(R.id.text_count);
		divider = itemView.findViewById(R.id.view_divider);
		count_layout = (LinearLayout)itemView.findViewById(R.id.count_layout);
		// resize
		DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), imageBg);
		DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), divider);
		DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), count_layout);
		String adult = CookieUtils.getAdult(MainApplication.getAppContext());

//		FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) countText.getLayoutParams();
//		lp.setMargins(0, DisplayUtils.getResizedPixelSizeToScreenSize(MainApplication.getAppContext(), DisplayUtils.convertDpToPx(MainApplication.getAppContext(), 44)), DisplayUtils.convertDpToPx(MainApplication.getAppContext(), 10), 0);
//		countText.setLayoutParams(lp);

		if ("true".equals(adult) || "temp".equals(adult)) {
			isAdult = true;
		}
	}

	// .
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
		List<SectionContentList> list = content.sectionContent.subProductList;

		if(list == null){
			return;
		}

		final int indicator = content.indicator;

		// 배경 이미지.
//		ImageUtil.loadImage(context, content.sectionContent.imageUrl, imageBg, 0);
		ImageUtil.loadImageRollBanner(context,
				!TextUtils.isEmpty(content.sectionContent.imageUrl) ? content.sectionContent.imageUrl.trim() : "", imageBg, 0);
//		ImageUtil.loadImage(context, "http://10.53.14.128:8080/app/bg.jpg", imageBg, 0);
		imageBg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String url = content.sectionContent.linkUrl;
				WebUtils.goWeb(context, url);

				String tempLabel = String.format("%s_%s", label, url);
				GTMAction.sendEvent(context, GTMEnum.GTM_AREA_CATEGORY, action, tempLabel);
			}
		});

		String text = (indicator + 1) + " / " + list.size();
		Spannable wordtoSpan = new SpannableString(text);
		wordtoSpan.setSpan(new ForegroundColorSpan(COUNT_COLOR), text.indexOf("/"), text.length(), 0);
		countText.setText(wordtoSpan);

		mPageChangeListener.setContent(content);
		viewPager.removeOnPageChangeListener(mPageChangeListener);
		viewPager.addOnPageChangeListener(mPageChangeListener);

		final InfinitePagerAdapter wrappedAdapter = new InfinitePagerAdapter(
				new RollBannerPagerAdapter(context, list, action, label,null, isAdult));

		//Ln.i("content.indicator : " + content.indicator);
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

		setRandom(content);
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

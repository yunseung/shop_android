/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.flexible;

import android.content.Context;
import android.content.res.Resources;
import androidx.viewpager.widget.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.library.viewpager.InfinitePagerAdapter;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;

import static gsshop.mobile.v2.util.DisplayUtils.convertDpToPx;

/**
 * 띠 이미지 배너.
 *
 */
public class SslVH extends DslXVH {

	protected final TextView baseText;
	protected MySimpleOnPageChangeListener mPageChangeListener = new MySimpleOnPageChangeListener();
	/**
	 * @param itemView
	 */
	public SslVH(View itemView) {
		super(itemView);

		baseText = (TextView) itemView.findViewById(R.id.text_base);
		DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), viewPager);
	}

	/**
	 * bind
	 */
	@Override
	public void onBindViewHolder(final Context context, int position, ShopInfo info,
								 final String action, final String label, String sectionName) {

		topView.setVisibility(View.VISIBLE);

		final ShopInfo.ShopItem content = info.contents.get(position);
		// main image
		ImageUtil.loadImageFit(context, content.sectionContent.imageUrl, titleImage, R.drawable.noimg_logo);

		baseText.setTextColor(
				context.getResources().getColor(R.color.flexible_no1_best_deal_hot6_count));
		final int indicator = content.indicator;

		setPageChangeListenerContent(content);
		viewPager.removeOnPageChangeListener(mPageChangeListener);
		viewPager.addOnPageChangeListener(mPageChangeListener);

		final InfinitePagerAdapter wrappedAdapter = new InfinitePagerAdapter(
				new ShortbangBannerPagerAdapter(context,info, content.sectionContent.subProductList, action, label, task, isAdult));

		//Ln.i("content.indicator : " + content.indicator);
		// viewPager.setMyAdapter(wrappedAdapter, content.indicator);

		baseText.setText("/" + content.sectionContent.subProductList.size());
		viewPager.setAdapter(wrappedAdapter);
		viewPager.setCurrentItem(indicator);

		//이미지 비율을 맞추기 위해 사이즈를 계산한다.
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		int deviceWidth = metrics.widthPixels;
		float previewWidth = deviceWidth - convertDpToPixel(200, context);

		int preview = (int)(previewWidth / 2);

		int margin = context.getResources()
				.getDimensionPixelOffset(R.dimen.shortbang_viewpager_margin);
		margin = DisplayUtils.getResizedPixelSizeToScreenSize(context, margin);
//		preview = DisplayUtils.getResizedPixelSizeToScreenSize(context, preview);

		viewPager.setPadding(preview, 0, preview, 0);
		viewPager.setClipToPadding(false);
		viewPager.setPageMargin(margin);


	}

	public float convertDpToPixel(float dp, Context context){
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);

		// origianl 사이즈(360dp * 640dp) 의 aspect ratio 구하기.
		float baseWidth = convertDpToPx(context, 360);
		float height = px;
		float ratio = height / baseWidth;

		// view의 height 조절하기.

		px = (int) (metrics.widthPixels * ratio);

		return px;

	}

	/* 페이지 이동. */
	private class MySimpleOnPageChangeListener extends ViewPager.SimpleOnPageChangeListener {
		private ShopInfo.ShopItem content;

		@Override
		public void onPageSelected(int position) {
			super.onPageSelected(position);
			int item = viewPager.getCurrentItem();
//			int count = ((InfinitePagerAdapter) viewPager.getAdapter()).getRealCount();
			if (((InfinitePagerAdapter) viewPager.getAdapter()).getRealCount() > 0) {
				countText.setText(Integer.toString(item + 1));
			} else {
				countText.setText(Integer.toString(item));
			}

			if (content != null) {
				content.indicator = item;
				//Ln.i("content.indicator : " + content.indicator);
			}

			startTimer();
		}

		public void setContent(ShopInfo.ShopItem content) {
			this.content = content;
		}
	}

}

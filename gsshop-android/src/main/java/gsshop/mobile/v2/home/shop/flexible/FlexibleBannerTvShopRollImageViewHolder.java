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
import android.os.Handler;
import android.view.View;

import java.util.ArrayList;

import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.CustomViewPager;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.ShopInfo.ShopItem;
import gsshop.mobile.v2.home.shop.tvshoping.TvLivePagerAdapter;
import gsshop.mobile.v2.util.CookieUtils;
import gsshop.mobile.v2.util.DisplayUtils;

/**
 * todays hot 배너.
 *
 */
@SuppressLint("NewApi")
public class FlexibleBannerTvShopRollImageViewHolder extends BaseViewHolder {

	private final CustomViewPager viewPager;
	private  boolean isAdult;

	private TvLivePagerAdapter mAdapter;
	/**
	 * @param itemView
	 */
	public FlexibleBannerTvShopRollImageViewHolder(View itemView) {
		super(itemView);
		viewPager = (CustomViewPager) itemView.findViewById(R.id.viewPager);
		DisplayUtils.resizeHeightAtViewToScreenPageWidth(MainApplication.getAppContext(), viewPager);
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
		final ArrayList<SectionContentList> list = content.sectionContent.subProductList;
//		final int indicator = content.indicator;


		if(list == null){
			return;
		}

		mAdapter = new TvLivePagerAdapter(context, action, label, list);
		viewPager.setAdapter(mAdapter);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				viewPager.setAdapter(mAdapter);
			}
		},300);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				viewPager.requestLayout();
			}
		},800);

//		mPageChangeListener.setContent(content);
//		viewPager.removeOnPageChangeListener(mPageChangeListener);
//		viewPager.addOnPageChangeListener(mPageChangeListener);

	}

	@Override
	public void onViewAttachedToWindow() {
		super.onViewAttachedToWindow();
	}

	@Override
	public void onViewDetachedFromWindow() {
		super.onViewDetachedFromWindow();
	}
}

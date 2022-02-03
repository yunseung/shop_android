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

import com.gsshop.mocha.core.exception.ApplyExceptionHandler;

import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;

/**
 * 카테고리.
 *
 */
@SuppressLint("NewApi")
public class FlexibleBannerBrandDividerViewHolder extends BaseViewHolder {

	/**
	 * @param itemView
	 */
	public FlexibleBannerBrandDividerViewHolder(View itemView) {
		super(itemView);
	}

	/* 카테고리. */
	@Override
	public void onBindViewHolder(Context context, int position, ShopInfo info, String action,
			String label, String sectionName) {
	}


}

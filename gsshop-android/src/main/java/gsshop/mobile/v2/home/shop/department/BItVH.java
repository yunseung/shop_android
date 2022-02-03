/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.department;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import com.gsshop.mocha.core.exception.ApplyExceptionHandler;

import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.flexible.BDhsVH;

/**
 * 이미지 배너.
 *
 */
@SuppressLint("NewApi")
public class BItVH extends BDhsVH {

	/**
	 * @param itemView itemView
	 */
	public BItVH(View itemView) {
		super(itemView);
	}

	/* 이미지 배너. bind */
	@Override
	public void onBindViewHolder(final Context context, int position, ShopInfo info,
								 final String action, final String label, String sectionName) {
		super.onBindViewHolder(context, position, info, action,label, sectionName );
	}

}

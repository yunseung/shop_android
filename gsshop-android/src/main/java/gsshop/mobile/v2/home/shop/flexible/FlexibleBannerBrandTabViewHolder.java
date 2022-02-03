/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.flexible;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.gsshop.mocha.core.exception.ApplyExceptionHandler;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;

/**
 * 
 *
 */
public class FlexibleBannerBrandTabViewHolder extends BaseViewHolder {

	/**
	 * @param itemView
	 */
	public FlexibleBannerBrandTabViewHolder(View itemView) {
		super(itemView);

	}

	/**
	 * 
	 * @param context
	 * @param position
	 * @param info
	 * @param action
	 * @param label
	 * @param sectionName
	 */
	@Override
	public void onBindViewHolder(Context context, int position, ShopInfo info, String action,
			String label, String sectionName) {

		LinearLayout tabsContainer = (LinearLayout) itemView.findViewById(R.id.view_tabs);
		
		Button category_1 = (Button)tabsContainer.findViewById(R.id.category_1);
		
		category_1.setBackgroundColor(context.getResources().getColor(android.R.color.white));
		category_1.setTextColor(context.getResources().getColor(R.color.brand_deal_btn_press));
		category_1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

	}

}
